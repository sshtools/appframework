/* HEADER */

package com.sshtools.appframework.ui.wizard;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import com.sshtools.appframework.ui.TextBox;
import com.sshtools.ui.swing.UIUtil;

public class WizardFinishPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private JLabel icon;
	private JTextField name;
	private TextBox title, description, summary, nameDescription;

	public WizardFinishPanel() {
		try {
			jbInit();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public String getSelectedName() {
		return name.getText();
	}

	public void setDefaultName(String name) {
		this.name.setText(name);
	}

	public void setDescription(String description) {
		this.description.setText(description);
	}

	public void setIcon(Icon icon) {
		this.icon.setIcon(icon);
	}

	public void setNameVisible(boolean nameVisible) {
		name.setVisible(nameVisible);
		nameDescription.setVisible(nameVisible);
	}

	public void setSelectNameDescription(String str) {
		nameDescription.setText(str);
	}

	public void setSummary(String summary) {
		this.summary.setText(summary);
	}

	public void setTitle(String title) {
		this.title.setText(title);
	}

	void jbInit() throws Exception {
		this.setLayout(new BorderLayout());
		JPanel top = new JPanel(new BorderLayout());
		top.setOpaque(false);
		top.setBorder(BorderFactory.createEmptyBorder(16, 16, 0, 8));
		title = new TextBox("");
		title.setFont(new java.awt.Font("Dialog", 1, 16));
		title.setLocale(java.util.Locale.getDefault());
		top.add(title, BorderLayout.CENTER);
		JPanel middle = new JPanel(new BorderLayout());
		middle.setOpaque(false);
		middle.setBorder(BorderFactory.createEmptyBorder(24, 16, 0, 8));
		description = new TextBox("");
		middle.add(description, BorderLayout.CENTER);
		JPanel bottom = new JPanel(new BorderLayout());
		bottom.setOpaque(false);
		bottom.setBorder(BorderFactory.createEmptyBorder(8, 16, 16, 8));
		summary = new TextBox("");
		summary.setBackground(Color.white);
		summary.setOpaque(true);
		JScrollPane summaryScroller = new JScrollPane(summary);
		summaryScroller.setOpaque(false);
		bottom.setBorder(BorderFactory.createEmptyBorder(8, 16, 16, 8));
		bottom.add(summaryScroller, BorderLayout.CENTER);
		JPanel namePanel = new JPanel(new GridBagLayout());
		namePanel.setOpaque(false);
		GridBagConstraints gbc2 = new GridBagConstraints();
		gbc2.anchor = GridBagConstraints.CENTER;
		gbc2.fill = GridBagConstraints.BOTH;
		namePanel.setBorder(BorderFactory.createEmptyBorder(8, 16, 16, 8));
		nameDescription = new TextBox("");
		name = new JTextField();
		gbc2.weightx = 1.0;
		UIUtil.jGridBagAdd(namePanel, nameDescription, gbc2, GridBagConstraints.REMAINDER);
		UIUtil.jGridBagAdd(namePanel, name, gbc2, GridBagConstraints.REMAINDER);
		JPanel left = new JPanel(new BorderLayout()) {
			private static final long serialVersionUID = 1L;

			@Override
			public Dimension getPreferredSize() {
				return new Dimension(120, super.getPreferredSize().height);
			}
		};
		left.setBackground(UIManager.getColor("Table.background"));
		left.setForeground(UIManager.getColor("Table.foreground"));
		left.setOpaque(true);
		icon = new JLabel();
		icon.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		icon.setHorizontalAlignment(SwingConstants.CENTER);
		icon.setVerticalAlignment(SwingConstants.TOP);
		left.add(icon, BorderLayout.NORTH);
		// Right
		JPanel right = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		right.setOpaque(false);
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.weightx = 1.0;
		UIUtil.jGridBagAdd(right, top, gbc, GridBagConstraints.REMAINDER);
		UIUtil.jGridBagAdd(right, namePanel, gbc, GridBagConstraints.REMAINDER);
		UIUtil.jGridBagAdd(right, middle, gbc, GridBagConstraints.REMAINDER);
		gbc.weighty = 1.0;
		UIUtil.jGridBagAdd(right, bottom, gbc, GridBagConstraints.REMAINDER);
		// This panel
		this.setBackground(UIManager.getColor("Scrollpane.background"));
		this.setForeground(UIManager.getColor("Scrollpane.foreground"));
		this.add(left, BorderLayout.WEST);
		this.add(right, BorderLayout.CENTER);
	}
}