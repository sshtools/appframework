/* HEADER */
package com.sshtools.appframework.authentication;

import java.awt.BorderLayout;
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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import com.sshtools.appframework.ui.IconWrapperPanel;
import com.sshtools.ui.swing.ResourceIcon;
import com.sshtools.ui.swing.UIUtil;
import com.sshtools.ui.swing.XTextField;

public class PasswordAuthenticationDialog extends JDialog {
	// Statics
	final static String KEY_ICON = "/images/password-32x32.png";
	/**
	 * Return a new instance of a PasswordAuthenticationDialog given a parent
	 * component to make the dialog modal against.
	 * 
	 * @param parent parent component
	 * @return PasswordAuthenticationDialog instance
	 */
	public static PasswordAuthenticationDialog getInstance(Component parent) {
		Window w = (Window) SwingUtilities.getAncestorOfClass(Window.class, parent);
		PasswordAuthenticationDialog dialog = null;
		if (w instanceof Frame) {
			dialog = new PasswordAuthenticationDialog((Frame) w);
		} else if (w instanceof Dialog) {
			dialog = new PasswordAuthenticationDialog((Dialog) w);
		} else {
			dialog = new PasswordAuthenticationDialog((Dialog) null);
		}
		return dialog;
	}
	private IconWrapperPanel centerPanel;
	// Private instance variables
	private JButton jButtonCancel = new JButton();
	private JButton jButtonOK = new JButton();
	private JPasswordField jPasswordField = new JPasswordField(20);
	private XTextField jTextUsername = new XTextField(null, null, 20, false);
	private JLabel message;

	private boolean userCancelled = false;

	/**
	 * Creates a new PasswordAuthenticationDialog.
	 */
	public PasswordAuthenticationDialog() {
		super((Frame) null, Messages.getString("PasswordAuthenticationDialog.PasswordAuth"), true);
		init();
	}

	/**
	 * 
	 * Creates a new PasswordAuthenticationDialog given a dialog to make the
	 * dialog modal against
	 * 
	 * @param parent parent dialog
	 */
	public PasswordAuthenticationDialog(Dialog parent) {
		super(parent, Messages.getString("PasswordAuthenticationDialog.PasswordAuth"), true);
		init();
	}

	/**
	 * 
	 * Creates a new PasswordAuthenticationDialog given a frame to make the
	 * dialog modal against.
	 * 
	 * @param parent parent frame
	 */
	public PasswordAuthenticationDialog(Frame parent) {
		super(parent, Messages.getString("PasswordAuthenticationDialog.PasswordAuth"), true);
		init();
	}

	/**
	 * Get the password the user has entered or <code>null</code> if the user
	 * cancelled.
	 * 
	 * @return password or <code>null</code> if cancelled
	 */
	public char[] getPassword() {
		return userCancelled ? null : jPasswordField.getPassword();
	}

	public String getUsername() {
		return jTextUsername.getText();
	}

	/**
	 * Set the icon
	 * 
	 * @param icon icon
	 */
	public void setIcon(Icon icon) {
		centerPanel.setIcon(icon);
	}

	public void setIconPosition(String position) {
		centerPanel.setIconPosition(position);
		invalidate();
		validate();
		repaint();
	}

	public void setMessageText(String messageText) {
		if (message.getText() == null) {
			invalidate();
			message.setText(messageText);
			validate();
			repaint();
		} else {
			message.setText(messageText);
		}
	}

	public void setPassword(char[] password) {
		jPasswordField.setText(new String(password));
	}

	public void setUserEditable(boolean b) {
		jTextUsername.setEditable(false);
		if (b) {
			jTextUsername.requestFocusInWindow();
		} else {
			jPasswordField.requestFocusInWindow();
		}
	}

	/**
	 * Set the user name
	 * 
	 * @param username user name
	 */
	public void setUsername(String username) {
		if (username != null) {
			jTextUsername.setText(username);
		}
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible) {
			if (jTextUsername.isEnabled()) {
				jTextUsername.requestFocusInWindow();
			} else {
				jPasswordField.requestFocusInWindow();
			}
		}
	}

	private void init() {
		getContentPane().setLayout(new GridLayout(1, 1));
		// Add a window listener to see when the window closes without
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
				if (jTextUsername.getText().trim().equals("")) {
					JOptionPane.showMessageDialog(PasswordAuthenticationDialog.this,
							Messages.getString("PasswordAuthenticationDialog.EnterUsername"),
							Messages.getString("PasswordAuthenticationDialog.PasswordAuth"), JOptionPane.OK_OPTION);
					return;
				}
				hide();
			}
		});
		jButtonOK.setText("OK");
		jButtonOK.setMnemonic('o');
		getRootPane().setDefaultButton(jButtonOK);
		jButtonCancel.setText(Messages.getString("Cancel"));
		jButtonCancel.setMnemonic('c');
		jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				userCancelled = true;
				hide();
			}
		});
		// User / password panel
		JPanel userPasswordPanel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(0, 2, 2, 2);
		gbc.weightx = 1.0;
		// Username
		UIUtil.jGridBagAdd(userPasswordPanel, new JLabel(Messages.getString("PasswordAuthenticationDialog.User")), gbc,
				GridBagConstraints.REMAINDER);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		UIUtil.jGridBagAdd(userPasswordPanel, jTextUsername, gbc, GridBagConstraints.REMAINDER);
		gbc.fill = GridBagConstraints.NONE;
		// Password
		UIUtil.jGridBagAdd(userPasswordPanel, new JLabel(Messages.getString("Password")), gbc, GridBagConstraints.REMAINDER);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		UIUtil.jGridBagAdd(userPasswordPanel, jPasswordField, gbc, GridBagConstraints.REMAINDER);
		// Message
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.insets = new Insets(8, 2, 2, 2);
		UIUtil.jGridBagAdd(userPasswordPanel, message = new JLabel(), gbc, GridBagConstraints.REMAINDER);
		// Create the center banner panel
		centerPanel = new IconWrapperPanel(new ResourceIcon(PasswordAuthenticationDialog.class, KEY_ICON), userPasswordPanel);
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