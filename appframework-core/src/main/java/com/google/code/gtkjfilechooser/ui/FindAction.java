/**
 * Maverick Application Framework - Application framework
 * Copyright © ${project.inceptionYear} SSHTOOLS Limited (support@sshtools.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
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

	static final private int TIMEOUT = 5;
	protected JComponent comp = null;
	protected boolean controlDown = false;
	protected JTextField searchField;
	protected boolean shiftDown = false;

	private long lastKeyPressed;

	private JPopupMenu popup = new JPopupMenu();

	private JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 3,
			3));

	/**
	 * Interrupt the timeout thread for the popup
	 */
	private boolean stop = false;

	/*-------------------------------------------------[ ActionListener ]---------------------------------------------------*/

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
			@Override
			public void actionPerformed(ActionEvent e) {
				popup.setVisible(false);
			}
		}, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
				JComponent.WHEN_FOCUSED);
	}

	@Override
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

	@Override
	public void changedUpdate(DocumentEvent e) {
	}

	public String getName() {
		return (String) getValue(Action.NAME);
	}

	/*-------------------------------------------------[ DocumentListener ]---------------------------------------------------*/

	@Override
	public void insertUpdate(DocumentEvent e) {
		changed(null);
	}

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

	@Override
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

	/*-------------------------------------------------[ KeyListener ]---------------------------------------------------*/

	@Override
	public void keyReleased(KeyEvent e) {
	}
	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		changed(null);
	}

	/**
	 * Should search for given text and select item and
	 *
	 * @param comp component
	 * @param text text
	 * @param bias bias
	 * @return true if search is successful
	 */
	protected abstract boolean changed(JComponent comp, String text,
			Position.Bias bias);

	/*
	 * Can be overridden by subclasses to change initial search text etc.
	 */
	protected void initSearch(ActionEvent ae) {
		searchField.setText(ae.getActionCommand()); // NOI18N
		changed(null);
	}

	/*-------------------------------------------------[ Installation ]---------------------------------------------------*/

	private void changed(Position.Bias bias) {
		popup.pack();

		searchField.requestFocus();
		Color color = changed(comp, searchField.getText(), bias) ? BLACK : RED;
		searchField.setForeground(color);
		lastKeyPressed = System.currentTimeMillis();
	}
}
