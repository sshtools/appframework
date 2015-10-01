/* HEADER */

package com.sshtools.appframework.authentication;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import com.sshtools.appframework.ui.IconWrapperPanel;
import com.sshtools.ui.swing.ResourceIcon;
import com.sshtools.ui.swing.UIUtil;

/**
 * Swing dialog that can be used to select an authentication method. The
 * authentication methods should be provide as {@link java.util.List} of
 * strings. <code>
 *      java.util.List auths = new java.util.ArrayList();
 *      auths.add("Password");
 *      auths.add("Key");
 *      java.util.List selected = AuthenticationDialog.showAuthenticationDialog(this, auths);
 * </code>
 * 
 */

public class AuthenticationDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private JList jListAuths = new JList();
	private JLabel messageLabel = new JLabel();
	private boolean cancelled = false;

	/**
	 * Creates a new AuthenticationDialog
	 */
	private AuthenticationDialog() {
		super((Frame) null, Messages.getString("AuthenticationDialog.SelectMethod"), true);
		init();
	}

	/**
	 * Creates a new AuthenticationDialog given a parent frame to make the
	 * dialog modal to
	 * 
	 * @param frame parent frame
	 */
	private AuthenticationDialog(Frame frame) {
		super(frame, Messages.getString("AuthenticationDialog.SelectMethod"), true);
		init();
	}

	/**
	 * Creates a new AuthenticationDialog given a parent dialog to make the
	 * dialog modal to
	 * 
	 * @param dialog parent dialog
	 */

	private AuthenticationDialog(Dialog dialog) {
		super(dialog, Messages.getString("AuthenticationDialog.SelectMethod"), true);
		init();
	}

	private void setMethodList(java.util.List<String> methods) {
		jListAuths.setListData(methods.toArray());
		if (methods.size() > 0) {
			jListAuths.setSelectedIndex(0);
		}
	}

	private void init() {
		setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		messageLabel.setForeground(Color.red);
		messageLabel.setHorizontalAlignment(JLabel.CENTER);

		jListAuths = new JList();
		jListAuths.setVisibleRowCount(5);

		JPanel listPanel = new JPanel(new GridLayout(1, 1));
		listPanel.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));
		listPanel.add(new JScrollPane(jListAuths));

		// Main panel

		JPanel centerPanel = new JPanel(new BorderLayout());
		centerPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		centerPanel.add(new JLabel(Messages.getString("AuthenticationDialog.PleaseSelectMethod")), BorderLayout.NORTH);
		centerPanel.add(listPanel, BorderLayout.CENTER);

		// Create the bottom button panel

		JButton proceed = new JButton(Messages.getString("AuthenticationDialog.Proceed"));
		proceed.setMnemonic('p');
		proceed.setDefaultCapable(true);
		proceed.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent evt) {
				setVisible(false);
			}
		});

		getRootPane().setDefaultButton(proceed);

		JButton cancel = new JButton(Messages.getString("AuthenticationDialog.Cancel"));
		cancel.setMnemonic('c');
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				cancelled = true;
				setVisible(false);
			}
		});

		JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		southPanel.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));
		southPanel.add(cancel);

		southPanel.add(proceed);

		// Create the center banner panel

		IconWrapperPanel iconPanel = new IconWrapperPanel(new ResourceIcon(getClass(), "/images/password-32x32.png"), centerPanel);
		iconPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

		// The main panel contains everything and is surrounded by a border

		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		mainPanel.add(messageLabel, BorderLayout.NORTH);
		mainPanel.add(iconPanel, BorderLayout.CENTER);
		mainPanel.add(southPanel, BorderLayout.SOUTH);

		// Build the main panel

		getContentPane().setLayout(new GridLayout(1, 1));
		getContentPane().add(mainPanel);

	}

	/**
	 * Show an authentication dialog given a parent component (to make modal
	 * against) and a list of supported authentication methods (strings).
	 * 
	 * @param parent parent component
	 * @param authMethods list of authentication methods (strings)
	 * @return list of selected authentication methods
	 */

	public static java.util.List<String> showAuthenticationDialog(Component parent, java.util.List<String> authMethods) {
		return showAuthenticationDialog(parent, authMethods, null);
	}

	/**
	 * Show an authentication dialog given a parent component (to make modal
	 * against) and a list of supported authentication methods (strings).
	 * 
	 * @param parent parent component
	 * @param authMethods list of authentication methods (strings)
	 * @param message message
	 * @return list of selected authentication methods
	 */
	public static java.util.List<String> showAuthenticationDialog(Component parent, java.util.List<String> authMethods,
			String message) {

		Window w = (Window) SwingUtilities.getAncestorOfClass(Window.class, parent);
		AuthenticationDialog dialog = null;
		if (w instanceof Frame) {
			dialog = new AuthenticationDialog((Frame) w);
		} else if (w instanceof Dialog) {
			dialog = new AuthenticationDialog((Dialog) w);
		} else {
			dialog = new AuthenticationDialog();
		}

		UIUtil.positionComponent(SwingConstants.CENTER, dialog);
		return dialog.showAuthenticationMethods(authMethods, message);

	}

	private java.util.List<String> showAuthenticationMethods(java.util.List<String> supported, String message) {

		// Set the list

		this.setMethodList(supported);

		// Show the dialog

		UIUtil.positionComponent(SwingConstants.CENTER, this);

		if (message != null) {
			messageLabel.setVisible(true);
			messageLabel.setText(message);
		} else {
			messageLabel.setVisible(false);
		}

		pack();
		toFront();
		setVisible(true);

		// Put the selected values into a new list and return

		java.util.List<String> list = new ArrayList<String>();
		if (!cancelled) {
			Object[] methods = jListAuths.getSelectedValues();
			if (methods != null) {
				for (int i = 0; i < methods.length; i++) {
					list.add(methods[i].toString());
				}
			}
		}

		return list;

	}

}
