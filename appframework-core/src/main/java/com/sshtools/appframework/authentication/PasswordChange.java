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
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import com.sshtools.ui.swing.IconWrapperPanel;
import com.sshtools.ui.swing.ResourceIcon;
import com.sshtools.ui.swing.UIUtil;

/**
 *
 *
 * @author $author$
 */
public class PasswordChange {
	class PasswordChangeDialog extends JDialog {
		boolean cancelled;
		JPasswordField confirm = new JPasswordField(15);
		JPasswordField password = new JPasswordField(15);
		JLabel promptLabel = new JLabel();

		PasswordChangeDialog(Dialog dialog, String prompt) {
			super(dialog, Messages.getString("PasswordChange.PasswordChange"), true);
			init(prompt);
		}

		PasswordChangeDialog(Frame frame, String prompt) {
			super(frame, Messages.getString("PasswordChange.PasswordChange"), true);
			init(prompt);
		}

		PasswordChangeDialog(String prompt) {
			super((Frame) null, Messages.getString("PasswordChange.PasswordChange"), true);
			init(prompt);
		}

		char[] getPassword() {
			return (cancelled == true) ? null : password.getPassword();
		}

		void init(String prompt) {
			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			setTitle(prompt + " " + Messages.getString("PasswordChange.Change"));
			JPanel g = new JPanel(new GridBagLayout());
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.insets = new Insets(0, 0, 2, 2);
			gbc.anchor = GridBagConstraints.WEST;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weightx = 0.0;
			UIUtil.jGridBagAdd(g, new JLabel(prompt + ": "), gbc, GridBagConstraints.RELATIVE);
			gbc.weightx = 1.0;
			UIUtil.jGridBagAdd(g, password, gbc, GridBagConstraints.REMAINDER);
			gbc.weightx = 0.0;
			UIUtil.jGridBagAdd(g, new JLabel(Messages.getString("PasswordChange.Confirm") + " "), gbc, GridBagConstraints.RELATIVE);
			gbc.weightx = 1.0;
			UIUtil.jGridBagAdd(g, confirm, gbc, GridBagConstraints.REMAINDER);
			//
			// promptLabel.setText(prompt);
			// promptLabel.setHorizontalAlignment(JLabel.CENTER);
			// Main panel
			JPanel centerPanel = new JPanel(new BorderLayout());
			centerPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
			centerPanel.add(promptLabel, BorderLayout.NORTH);
			centerPanel.add(g, BorderLayout.CENTER);
			// Create the bottom button panel
			JButton ok = new JButton(Messages.getString("PasswordChange.OK"));
			ok.setMnemonic('o');
			ok.setDefaultCapable(true);
			ok.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent evt) {
					if (!new String(password.getPassword()).equals(new String(confirm.getPassword()))) {
						JOptionPane.showMessageDialog(PasswordChangeDialog.this, Messages.getString("PasswordChange.NotMatch2"),
								Messages.getString("PasswordChange.NotMatch"), JOptionPane.ERROR_MESSAGE);
					} else {
						hide();
					}
				}
			});
			getRootPane().setDefaultButton(ok);
			JButton cancel = new JButton(Messages.getString("Cancel"));
			cancel.setMnemonic('c');
			cancel.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent evt) {
					cancelled = true;
					hide();
				}
			});
			JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
			southPanel.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));
			southPanel.add(cancel);
			southPanel.add(ok);
			// Create the center banner panel
			IconWrapperPanel iconPanel = new IconWrapperPanel(new ResourceIcon(PASSWORD_ICON), centerPanel);
			iconPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
			// The main panel contains everything and is surrounded by a border
			JPanel mainPanel = new JPanel(new BorderLayout());
			mainPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
			mainPanel.add(iconPanel, BorderLayout.CENTER);
			mainPanel.add(southPanel, BorderLayout.SOUTH);
			// Build the main panel
			getContentPane().setLayout(new GridLayout(1, 1));
			getContentPane().add(mainPanel);
			pack();
			toFront();
			UIUtil.positionComponent(SwingConstants.CENTER, this);
			setVisible(true);
		}
	}
	//
	/**  */
	public final static String PASSWORD_ICON = "/images/password-32x32.png";
	//
	private static PasswordChange instance;

	public static PasswordChange getInstance() {
		if (instance == null) {
			instance = new PasswordChange();
		}
		return instance;
	}

	//
	private Component parent;

	private PasswordChange() {
	}

	public String changePassword(String prompt) {
		Window w = (parent == null) ? null : (Window) SwingUtilities.getAncestorOfClass(Window.class, parent);
		PasswordChangeDialog dialog = null;
		if (w instanceof Frame) {
			dialog = new PasswordChangeDialog((Frame) w, prompt);
		} else if (w instanceof Dialog) {
			dialog = new PasswordChangeDialog((Dialog) w, prompt);
		} else {
			dialog = new PasswordChangeDialog(prompt);
		}
		char[] p = dialog.getPassword();
		return (p == null) ? null : new String(p);
	}

	public void setParentComponent(Component parent) {
		this.parent = parent;
	}
}