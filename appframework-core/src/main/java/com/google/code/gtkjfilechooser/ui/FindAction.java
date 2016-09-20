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

import static java.awt.Color.BLACK;
import static java.awt.Color.RED;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Position;

/**
 * <p>
 * FindAction
 * </p>
 * 
 * Search for files or folders.
 * 
 * @author Costantino Cerbo
 * @author s.tannenbaum@psi.de (patch Issue 58)
 * 
 */
public abstract class FindAction extends AbstractAction implements
		DocumentListener, KeyListener {

	private JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 3,
			3));
	protected JTextField searchField;
	private JPopupMenu popup = new JPopupMenu();
	static final private int TIMEOUT = 5;
	private long lastKeyPressed;

	/**
	 * Interrupt the timeout thread for the popup
	 */
	private boolean stop = false;

	public FindAction() {
		super("Incremental Search"); // NOI18N
		searchField = new JTextField(11);
		searchPanel.add(searchField);

		popup.add(searchPanel);

		// when the window containing the "comp" has registered Esc key
		// then on pressing Esc instead of search popup getting closed
		// the event is sent to the window. to overcome this we
		// register an action for Esc.
		searchField.registerKeyboardAction(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				popup.setVisible(false);
			}
		}, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
				JComponent.WHEN_FOCUSED);
	}

	public String getName() {
		return (String) getValue(Action.NAME);
	}

	protected JComponent comp = null;

	/*-------------------------------------------------[ ActionListener ]---------------------------------------------------*/

	public void actionPerformed(ActionEvent ae) {
		if (ae.getSource() == searchField) {
			popup.setVisible(false);
		} else {
			comp = (JComponent) ae.getSource();

			searchField.removeActionListener(this);
			searchField.removeKeyListener(this);
			searchField.getDocument().removeDocumentListener(this);
			initSearch(ae);
			searchField.addActionListener(this);
			searchField.addKeyListener(this);
			searchField.getDocument().addDocumentListener(this);

			JComponent parent = (JComponent) comp.getParent();
			Rectangle rect = parent.getVisibleRect();

			int x = rect.x + rect.width - popup.getPreferredSize().width;
			int y = rect.y + rect.height - popup.getPreferredSize().height;
			popup.show(comp, x, y);
			searchField.requestFocus();
		}
	}

	/**
	 * Can be overridden by subclasses to change initial search text etc.
	 */
	protected void initSearch(ActionEvent ae) {
		searchField.setText(ae.getActionCommand()); // NOI18N
		changed(null);
	}

	private void changed(Position.Bias bias) {
		popup.pack();

		searchField.requestFocus();
		Color color = changed(comp, searchField.getText(), bias) ? BLACK : RED;
		searchField.setForeground(color);
		lastKeyPressed = System.currentTimeMillis();
	}

	/**
	 * Should search for given text and select item and
	 * 
	 * @return true if search is successful
	 */
	protected abstract boolean changed(JComponent comp, String text,
			Position.Bias bias);

	/*-------------------------------------------------[ DocumentListener ]---------------------------------------------------*/

	public void insertUpdate(DocumentEvent e) {
		changed(null);
	}

	public void removeUpdate(DocumentEvent e) {
		changed(null);
	}

	public void changedUpdate(DocumentEvent e) {
	}

	/*-------------------------------------------------[ KeyListener ]---------------------------------------------------*/

	protected boolean shiftDown = false;
	protected boolean controlDown = false;

	public void keyPressed(KeyEvent ke) {
		shiftDown = ke.isShiftDown();
		controlDown = ke.isControlDown();

		switch (ke.getKeyCode()) {
		case KeyEvent.VK_UP:
			changed(Position.Bias.Backward);
			break;
		case KeyEvent.VK_DOWN:
			changed(Position.Bias.Forward);
			break;
		}
	}

	public void keyTyped(KeyEvent e) {
	}

	public void keyReleased(KeyEvent e) {
	}

	/*-------------------------------------------------[ Installation ]---------------------------------------------------*/

	public void install(final JComponent comp) {
		comp.addKeyListener(new KeyAdapter() {
			private static final int ALT_KEY = 65535;
			private boolean isSearchEnabled = true;

			@Override
			public void keyPressed(KeyEvent e) {
				char ch = e.getKeyChar();
				if (ALT_KEY == ch) {
					// Workaround to disable incremental search when a key is
					// pressed with ALT.
					isSearchEnabled = false;
				}

				if (Character.isLetterOrDigit(ch) && isSearchEnabled) {
					ActionEvent ae = new ActionEvent(comp, 1001, String
							.valueOf(ch));
					actionPerformed(ae);
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				char ch = e.getKeyChar();
				if (ALT_KEY == ch) {
					// enable again the incremental search: the key ALT was
					// released.
					isSearchEnabled = true;
				}
			}
		});

		// Thread to close the popup after the timeout (5 seconds)
		// FIX Issue 58
		new Thread("GTK-File-Chooser Popup-Monitor") {
			@Override
			public void run() {
				try {
					while (!stop) {
						if (popup.isShowing()) {
							long timeWaited = (System.currentTimeMillis() - lastKeyPressed);
							long timeRemaining = Math.max(TIMEOUT * 1000
									- timeWaited, 0);

							if (timeRemaining == 0) {
								popup.setVisible(false);
							} else {
								sleep(timeRemaining);
							}
						} else {
							sleep(TIMEOUT * 1000);
						}
					}
				} catch (InterruptedException exc) {
					throw new RuntimeException(exc);
				}
			}
		}.start();

		comp.addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				// When die ancestor component for the table becomes null, stop
				// the timeout thread.
				if ("ancestor".equals(evt.getPropertyName())
						&& evt.getNewValue() == null) {
					stop = true;
				}
			}

		});
	}
}
