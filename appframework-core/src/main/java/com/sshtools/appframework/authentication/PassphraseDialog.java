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
/* HEADER */
package com.sshtools.appframework.authentication;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import com.sshtools.appframework.ui.IconWrapperPanel;
import com.sshtools.ui.swing.ResourceIcon;
import com.sshtools.ui.swing.UIUtil;

/**
 * 
 * Swing dialog that can be used to request the passphrase for a private key.
 * 
 * The dialog will be displayed with an icon and an optional message.
 *
 * 
 * 
 * @author $Author: brett $
 * 
 */
public class PassphraseDialog extends JDialog {
	// Statics
	final static String PASSPHRASE_ICON = "/images/passphrase-32x32.png";

	/**
	 * 
	 * Return a new instance of a PassphraseDialog given a parent component to
	 * make the dialog modal against with "Passphrase" as the dialog title and
	 * the default icon
	 * 
	 * @param parent parent component
	 * @return PassphraseDialog instance
	 * 
	 */
	public static PassphraseDialog getInstance(Component parent) {
		return getInstance(parent, null, null);
	}

	/**
	 * Return a new instance of a PassphraseDialog given a parent component to
	 * make the dialog modal against, a frame title and the panel icon.
	 * <code>null</code> for either of these values means the dialog will use
	 * the default title of "Passphrase" and the standard passphrase icon.
	 *
	 * @param parent parent component
	 * @param title title
	 * @param icon icon for panel
	 * @return PassphraseDialog instance
	 * 
	 */
	public static PassphraseDialog getInstance(Component parent, String title, Icon icon) {
		Window w = parent instanceof Window ? (Window) parent : (Window) SwingUtilities.getAncestorOfClass(Window.class, parent);
		PassphraseDialog dialog = null;
		if (w instanceof Frame) {
			dialog = new PassphraseDialog((Frame) w, title, icon);
		} else if (w instanceof Dialog) {
			dialog = new PassphraseDialog((Dialog) w, title, icon);
		} else {
			dialog = new PassphraseDialog((Dialog) null, title, icon);
		}
		return dialog;
	}

	// Private instance variables
	private JButton jButtonCancel = new JButton();
	private JButton jButtonOK = new JButton();
	private JPasswordField jPasswordField = new JPasswordField(20);
	private JLabel message = new JLabel(Messages.getString("PassphraseDialog.EnterPass"));
	private boolean userCancelled = false;

	/**
	 * 
	 * Creates a new PassphraseDialog.
	 * 
	 */
	public PassphraseDialog() {
		this((Frame) null);
	}

	/**
	 * Creates a new PassphraseDialog given a dialog to make the dialog modal
	 * against. "Passphrase" will be used as the default dialog title.
	 * 
	 * @param parent parent
	 */
	public PassphraseDialog(Dialog parent) {
		super(parent, Messages.getString("PassphraseDialog.Passphrase"));
	}

	/**
	 * Creates a new PassphraseDialog given a dialog to make the dialog modal
	 * against and a title.
	 * 
	 * @param parent parent
	 * @param title title
	 */
	public PassphraseDialog(Dialog parent, String title) {
		this(parent, title, null);
	}

	/**
	 * Creates a new PassphraseDialog given a dialog to make the dialog modal
	 * against, a title and a large icon.
	 *
	 * @param parent parent to derive windows
	 * @param title title
	 * @param icon icon
	 */
	public PassphraseDialog(Dialog parent, String title, Icon icon) {
		super(parent, title, true);
		init(parent, icon);
	}

	/**
	 * Creates a new PassphraseDialog given a frame to make the dialog modal
	 * against. "Passphrase" will be used as the default dialog title.
	 * 
	 * @param parent
	 * 
	 *            parent frame
	 * 
	 */
	public PassphraseDialog(Frame parent) {
		this(parent, Messages.getString("PassphraseDialog.Passphrase"));
	}

	/**
	 * Creates a new PassphraseDialog given a frame to make the dialog modal
	 * against and an identity string that will be used as the title for the
	 * dialog.
	 *
	 * @param parent parent frame
	 * @param title title
	 */
	public PassphraseDialog(Frame parent, String title) {
		this(parent, title, null);
	}

	/**
	 * Creates a new PassphraseDialog given a frame to make the dialog modal
	 * against and an identity string that will be used as the title for the
	 * dialog.
	 *
	 * @param parent parent frame
	 * @param title title
	 * @param icon icon
	 */
	public PassphraseDialog(Frame parent, String title, Icon icon) {
		super(parent, title, true);
		init(parent, icon);
	}

	/**
	 * Get the passphrase that has been entered by the user or <code>null</code>
	 * if cancelled.
	 * 
	 * @return passphrase or <code>null</code> if cancelled
	 */
	public char[] getPassphrase() {
		return userCancelled ? null : jPasswordField.getPassword();
	}

	/**
	 * Set the message to display on the dialog
	 *
	 * @param message message
	 */
	public void setMessage(String message) {
		this.message.setText(message);
	}

	/**
	 * Set the foreground background of the message.
	 * 
	 * @param color background
	 */
	public void setMessageForeground(Color color) {
		message.setForeground(color);
	}

	private void init(Window parent, Icon icon) {
		getContentPane().setLayout(new GridLayout(1, 1));
		if (parent != null) {
			this.setLocationRelativeTo(parent);
		}
		// selecting OK
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent evt) {
				userCancelled = true;
			}
		});
		// Ok button
		jButtonOK.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				userCancelled = false;
				hide();
			}
		});
		jButtonOK.setText(Messages.getString("PassphraseDialog.OK"));
		jButtonOK.setMnemonic('o');
		getRootPane().setDefaultButton(jButtonOK);
		// Cancel button
		jButtonCancel.setText(Messages.getString("Cancel"));
		jButtonCancel.setMnemonic('c');
		jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				userCancelled = true;
				hide();
			}
		});
		// Passphrase panel
		JPanel passphrasePanel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(0, 2, 2, 2);
		gbc.weightx = 1.0;
		UIUtil.jGridBagAdd(passphrasePanel, message, gbc, GridBagConstraints.REMAINDER);
		UIUtil.jGridBagAdd(passphrasePanel, jPasswordField, gbc, GridBagConstraints.REMAINDER);
		// Create the center banner panel
		IconWrapperPanel centerPanel = new IconWrapperPanel(icon == null ? new ResourceIcon(getClass(), PASSPHRASE_ICON) : icon,
				passphrasePanel);
		centerPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		//
		JPanel buttonPanel = new JPanel(new GridBagLayout());
		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.insets = new Insets(6, 6, 0, 0);
		gbc.weighty = 1.0;
		UIUtil.jGridBagAdd(buttonPanel, jButtonOK, gbc, GridBagConstraints.RELATIVE);
		UIUtil.jGridBagAdd(buttonPanel, jButtonCancel, gbc, GridBagConstraints.REMAINDER);
		JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		southPanel.add(buttonPanel);
		// Wrap the whole thing in an empty border
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		mainPanel.add(centerPanel, BorderLayout.CENTER);
		mainPanel.add(southPanel, BorderLayout.SOUTH);
		// Build the main panel
		getContentPane().add(mainPanel);
		addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				jPasswordField.requestFocus();
			}

			@Override
			public void focusLost(FocusEvent e) {
			}
		});
		pack();
		UIUtil.positionComponent(SwingConstants.CENTER, this);
	}
}
