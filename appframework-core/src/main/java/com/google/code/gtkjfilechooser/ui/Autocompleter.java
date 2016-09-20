/*******************************************************************************
 * Copyright (c) 2010 Costantino Cerbo.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Costantino Cerbo - initial API and implementation
 ******************************************************************************/
package com.google.code.gtkjfilechooser.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;

/**
 * Decorator for {@link JTextComponent}s to add auto completion support. Based
 * on the Santhosh Kumar's <a
 * href="http://www.jroller.com/santhosh/entry/file_path_autocompletion"
 * >original code</a>.
 * 
 * @author Costantino Cerbo
 * 
 */
public abstract class Autocompleter {
	public static final String ACTION_PERFORMED_ACCEPT_SUGGESTION = "accept_suggestion";
	private static final String AUTOCOMPLETER = "AUTOCOMPLETER"; // NOI18N
	private DocumentListener documentListener;
	private JList list;
	private List<ActionListener> listeners = new ArrayList<ActionListener>();

	private JPopupMenu popup;

	private JTextField textComp;

	public Autocompleter(JTextField comp) {
		textComp = comp;
		textComp.putClientProperty(AUTOCOMPLETER, this);
		list = new JList() {
			@Override
			public Dimension getPreferredSize() {
				Dimension dim = super.getPreferredSize();
				//We use always the same width as the underlying JTextField.
				dim.width = textComp.getWidth() - textComp.getInsets().right;
				return dim;
			}
		};

		list.setCellRenderer(getCellRenderer());
		addListListeners();

		JScrollPane scroll = new JScrollPane(list);
		scroll
		.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setBorder(null);

		list.setFocusable(false);
		scroll.getVerticalScrollBar().setFocusable(false);
		scroll.getHorizontalScrollBar().setFocusable(false);

		popup = new JPopupMenu();
		popup.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		popup.add(scroll);

		Action showAction = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				if (textComp.isEnabled()) {
					if (popup.isVisible()) {
						selectNextPossibleValue();
					} else {
						showPopup(true);
					}
				}
			}
		};

		textComp.registerKeyboardAction(showAction, KeyStroke.getKeyStroke(
				KeyEvent.VK_DOWN, 0), JComponent.WHEN_FOCUSED);

		documentListener = new DocumentListener() {

			public void changedUpdate(DocumentEvent e) {
			}

			public void insertUpdate(DocumentEvent e) {
				// to avoid auto completion when the text is
				// programmatically set.
				if (!textComp.hasFocus()) {
					return;
				}

				showPopup(true);
			}

			public void removeUpdate(DocumentEvent e) {
				// to avoid auto completion when the text is
				// programmatically set.
				if (!textComp.hasFocus()) {
					return;
				}

				if (e.getDocument().getLength() > 0) {
					// show the popup for the autocompletion
					// only if the text isn't empty.
					showPopup(false);
				} else {
					popup.setVisible(false);
				}
			}
		};
		textComp.getDocument().addDocumentListener(documentListener);



		Action upAction = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				if (textComp.isEnabled() && popup.isVisible()) {
					selectPreviousPossibleValue();
				}
			}
		};
		textComp.registerKeyboardAction(upAction, KeyStroke.getKeyStroke(KeyEvent.VK_UP,
				0), JComponent.WHEN_FOCUSED);

		// When ESC pressed, hide the popup
		textComp.registerKeyboardAction(new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				if (textComp.isEnabled()) {
					popup.setVisible(false);
				}
			}
		}, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_FOCUSED);

		popup.addPopupMenuListener(new PopupMenuListener() {
			public void popupMenuCanceled(PopupMenuEvent e) {
			}

			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
				textComp.unregisterKeyboardAction(KeyStroke.getKeyStroke(
						KeyEvent.VK_ENTER, 0));
			}

			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
			}
		});
		list.setRequestFocusEnabled(false);
	}

	protected ListCellRenderer getCellRenderer() {
		return new DefaultListCellRenderer();
	}

	public void addActionListener(ActionListener l) {
		listeners.add(l);
	}

	public JTextComponent getTextComponent() {
		return textComp;
	}

	public void removeActionListener(ActionListener l) {
		listeners.remove(l);
	}

	/**
	 * Accept the current selected suggestion in the list.
	 */
	private void acceptSuggestion() {
		/**
		 * Insertion done in a separate thread with SwingUtilities.invokeLater()
		 * because we need to wait until the AbstractDocument#writeUnlock
		 * operation has completed. Otherwise AbstractDocument#writeLock throws
		 * an IllegalStateException ("Attempt to mutate in notification").
		 * 
		 */
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				popup.setVisible(false);
				String selected = (String) list.getSelectedValue();
				if (selected == null) {
					return;
				}

				int caretPosition = textComp.getCaretPosition();
				if (caretPosition >= selected.length()) {
					return;
				}
				try {
					String append = completion(selected, caretPosition);

					// remove the document listener before inserting and then
					// add it again to avoid to fire an undesired
					// DocumentEvent.EventType.INSERT
					textComp.getDocument().removeDocumentListener(documentListener);
					textComp.getDocument().insertString(caretPosition, append, null);
					textComp.getDocument().addDocumentListener(documentListener);
				} catch (BadLocationException e) {
					e.printStackTrace();
				}

				// fire an action in the underlying text field
				// when a suggestion is accepted
				ActionEvent e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED,
						ACTION_PERFORMED_ACCEPT_SUGGESTION);
				fireActionEvent(e);
			}
		});
	}

	/**
	 * Returns the string to append for the completion to the already entered
	 * text.
	 * 
	 * @param selected
	 *            The selected entry.
	 * @param caretPosition
	 *            The position of the cursor in the text field.
	 * @return
	 */
	protected String completion(String selected, int caretPosition) {
		return selected.substring(caretPosition);
	}

	private void addListListeners() {
		list.addMouseMotionListener(new MouseMotionAdapter() {

			@Override
			public void mouseMoved(MouseEvent e) {
				// Highlight list item on mouse over
				int index = list.locationToIndex(e.getPoint());
				list.setSelectedIndex(index);

			}
		});

		list.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				// Accept the current selection
				acceptSuggestion();
			}
		});

	}

	/**
	 * Selects the next item in the list. It won't change the selection if the
	 * currently selected item is already the last item.
	 */
	private void selectNextPossibleValue() {
		int si = list.getSelectedIndex();

		if (si < list.getModel().getSize() - 1) {
			list.setSelectedIndex(si + 1);
			list.ensureIndexIsVisible(si + 1);
		}
	}

	/**
	 * Selects the previous item in the list. It won't change the selection if
	 * the currently selected item is already the first item.
	 */
	private void selectPreviousPossibleValue() {
		int si = list.getSelectedIndex();

		if (si > 0) {
			list.setSelectedIndex(si - 1);
			list.ensureIndexIsVisible(si - 1);
		}
	}

	private void selectText(final int selectionStart, final int selectionEnd) {
		// textComp.select(selectionStart, selectionEnd);
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				textComp.select(selectionStart, selectionEnd);
			}
		});
	}

	private void setSuggestions(List<String> suggestions) {
		list.setListData(suggestions.toArray(new String[suggestions.size()]));
	}

	/**
	 * Show the popup with the suggestions.
	 * 
	 * @param completeSingle
	 *            If true a suggestion that consists of a single result will be
	 *            immediately selected without showing the popup.
	 */
	private void showPopup(boolean completeSingle) {
		if (!textComp.isShowing()) {
			return;
		}

		// set always visible false to force repainting and resizing of the
		// combo
		popup.setVisible(false);
		List<String> suggestions = updateSuggestions(textComp.getText());

		if (textComp.isEnabled() && suggestions != null && suggestions.size() > 0) {
			setSuggestions(suggestions);

			// Register accept action
			Action acceptAction = new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					acceptSuggestion();
				}
			};
			textComp.registerKeyboardAction(acceptAction, KeyStroke.getKeyStroke(
					KeyEvent.VK_ENTER, 0), JComponent.WHEN_FOCUSED);

			int size = list.getModel().getSize();
			if (size == 1 && completeSingle) {
				list.setSelectedIndex(0);
				int selectionStart = textComp.getCaretPosition() + 1;
				int selectionEnd = ((String) list.getSelectedValue()).length();
				acceptSuggestion();
				textComp.unregisterKeyboardAction(KeyStroke.getKeyStroke(
						KeyEvent.VK_ENTER, 0));
				selectText(selectionStart, selectionEnd);

				return;
			}

			list.setVisibleRowCount(size < 10 ? size : 10);

			try {
				int pos = (int) textComp.getAlignmentX();
				int offset = textComp.getInsets().left;
				int x = textComp.getUI().modelToView(textComp, pos).x - offset;
				popup.show(textComp, x, textComp.getHeight());
			} catch (BadLocationException e) {
				// this should never happen!!!
				e.printStackTrace();
			}

		} else {
			popup.setVisible(false);
		}
		textComp.requestFocus();
	}

	protected void fireActionEvent(ActionEvent evt) {
		for (ActionListener l : listeners) {
			l.actionPerformed(evt);
		}
	}

	/**
	 * Update the list that contains the auto completion suggestions depending
	 * on the data in text field.
	 * 
	 * @param value
	 *            The current text in the field.
	 * @return The list of the possible auto completions. Empty list or {@code
	 *         null} for no suggestion.
	 */
	protected abstract List<String> updateSuggestions(String value);

}
