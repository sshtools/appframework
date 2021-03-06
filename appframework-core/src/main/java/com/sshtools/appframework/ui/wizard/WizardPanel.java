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
package com.sshtools.appframework.ui.wizard;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import com.sshtools.appframework.ui.TextBox;
import com.sshtools.ui.swing.AppAction;
import com.sshtools.ui.swing.UIUtil;

public abstract class WizardPanel extends JPanel {
	class BackAction extends AppAction {
		private static final long serialVersionUID = 1L;

		BackAction() {
			putValue(Action.NAME, Messages.getString("WizardPanel.Back"));
			putValue(Action.SHORT_DESCRIPTION, Messages.getString("WizardPanel.BackDesc"));
			putValue(Action.LONG_DESCRIPTION, Messages.getString("WizardPanel.BackDesc"));
			putValue(Action.MNEMONIC_KEY, new Integer('b'));
			putValue(Action.ACTION_COMMAND_KEY, "back");
		}

		@Override
		public void actionPerformed(ActionEvent evt) {
			backPage();
		}
	}

	class CancelAction extends AppAction {
		private static final long serialVersionUID = 1L;

		CancelAction() {
			putValue(Action.NAME, Messages.getString("WizardPanel.Cancel"));
			putValue(Action.SHORT_DESCRIPTION, Messages.getString("WizardPanel.CancelDesc"));
			putValue(Action.LONG_DESCRIPTION, Messages.getString("WizardPanel.CancelDesc"));
			putValue(Action.MNEMONIC_KEY, new Integer('c'));
			putValue(Action.ACTION_COMMAND_KEY, "cancel");
		}

		@Override
		public void actionPerformed(ActionEvent evt) {
			cancel();
		}
	}

	class NextAction extends AppAction {
		private static final long serialVersionUID = 1L;

		NextAction() {
			putValue(Action.NAME, Messages.getString("WizardPanel.Next"));
			putValue(Action.SHORT_DESCRIPTION, Messages.getString("WizardPanel.NextDesc"));
			putValue(Action.LONG_DESCRIPTION, Messages.getString("WizardPanel.NextDesc"));
			putValue(Action.MNEMONIC_KEY, new Integer('n'));
			putValue(Action.ACTION_COMMAND_KEY, "next");
		}

		@Override
		public void actionPerformed(ActionEvent evt) {
			nextPage();
		}
	}

	protected static final int BACK = 2;
	protected static final int NEXT = 1;
	static final Dimension SCREEN_SIZE = Toolkit.getDefaultToolkit().getScreenSize();
	private static final long serialVersionUID = 1L;
	private final String ALTERNATIVE_FINISH_PAGE = "Alternative Finish Page";
	private final JButton back = new JButton();
	private final JButton cancel = new JButton();
	private WizardPage currentPage = null;
	private boolean finish = false;
	private final String FINISHED_PAGE = "Finished Page";
	private final WizardFinishPanel finishedPage;
	private JPanel finishPanel;
	private final JPanel info = new JPanel();
	private final JPanel navigate = new JPanel();
	private final String NAVIGATION_PAGE = "Navigation Page";
	private final JPanel navigationPage = new JPanel();
	private final JButton next = new JButton();
	private TextBox pageDescription;
	private final CardLayout pageLayout = new CardLayout();
	private final JPanel pagesContainer = new JPanel();
	private final JLabel pageTitle = new JLabel();
	private final String WELCOME_PAGE = "Welcome Page";
	private String welcomeDescription;
	private final WizardWelcomePanel welcomePage;
	private String welcomeTitle;
	private Icon wizardIcon;
	private final CardLayout wizardLayout = new CardLayout();
	private final JPanel wizardPanel = new JPanel();

	/**
	 * Creates a new WizardPanel object.
	 * 
	 * @param wizardTitle title
	 * @param wizardDescription description
	 * @param wizardIcon wizard icon
	 * @param welcomeIcon welcome icon
	 * @param wizarddimensions size
	 */
	public WizardPanel(String wizardTitle, String wizardDescription, Icon wizardIcon, Icon welcomeIcon,
			Dimension wizarddimensions) {
		this(wizardTitle, wizardDescription, wizardIcon, welcomeIcon, wizarddimensions, null);
	}

	/**
	 * Creates a new WizardPanel object.
	 * 
	 * @param wizardTitle title
	 * @param wizardDescription description
	 * @param wizardIcon wizard icon
	 * @param welcomeIcon welcome icon
	 * @param wizarddimensions size
	 * @param welcomeLabel welcome label
	 */
	public WizardPanel(String wizardTitle, String wizardDescription, Icon wizardIcon, Icon welcomeIcon, Dimension wizarddimensions,
			JLabel welcomeLabel) {
		this.wizardIcon = wizardIcon;
		this.welcomeTitle = wizardTitle;
		this.welcomeDescription = wizardDescription;
		welcomePage = new WizardWelcomePanel(welcomeTitle, welcomeDescription, welcomeIcon, welcomeLabel);
		this.finishedPage = new WizardFinishPanel();
		finishedPage.setIcon(welcomeIcon);
		try {
			jbInit();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void addPage(WizardPage page) {
		this.pagesContainer.add(page.getPageComponent(), page.getPageTitle());
	}

	public void backPage() {
		WizardPage previousPage = selectNextPage(currentPage, BACK);
		if (previousPage != null) {
			wizardLayout.show(wizardPanel, NAVIGATION_PAGE);
			finish = false;
			pageLayout.show(pagesContainer, previousPage.getPageTitle());
			pageTitle.setText(previousPage.getPageTitle());
			pageDescription.setText(previousPage.getPageDescription());
			next.setText(Messages.getString("WizardPanel.Next"));
			back.setEnabled(true);
		} else {
			// Show the welcome screen
			wizardLayout.show(wizardPanel, WELCOME_PAGE);
			back.setEnabled(false);
		}
		currentPage = previousPage;
	}

	public abstract void cancel();

	public abstract void finish(String name);

	public abstract String getFinishDescription();

	public abstract String getFinishName();

	public abstract String getFinishNameDescription();

	public WizardFinishPanel getFinishPage() {
		return finishedPage;
	}

	public abstract String getFinishTitle();

	public abstract String getSummary();

	public void nextPage() {
		try {
			if (finish) {
				if (finishPanel == null) {
					if (finishedPage.getSelectedName().trim().equals("")) {
						JOptionPane.showMessageDialog(this, Messages.getString("WizardPanel.SelectName"),
								Messages.getString("WizardPanel.Complete"), JOptionPane.INFORMATION_MESSAGE);
					} else {
						validateComplete(finishedPage.getSelectedName());
						// Do something to finish
						finish(finishedPage.getSelectedName());
					}
				} else {
					// Do something to finish
					System.out.println("Finished");
					finish(finishedPage.getSelectedName());
				}
			} else {
				if (currentPage != null) {
					currentPage.validatePage();
				}
				// Make sure were showing the navigation
				wizardLayout.show(wizardPanel, NAVIGATION_PAGE);
				WizardPage nextPage = selectNextPage(currentPage, NEXT);
				if (nextPage != null) {
					pageLayout.show(pagesContainer, nextPage.getPageTitle());
					pageTitle.setText(nextPage.getPageTitle());
					pageDescription.setText(nextPage.getPageDescription());
					// This is a fix as the WrappingLabel seems to show when
					// first added
					pageDescription.setVisible(true);
					next.setText(Messages.getString("WizardPanel.Next"));
					nextPage.show(this);
				} else {
					// Show the completion screen
					finish = true;
					if (finishPanel != null) {
						wizardLayout.show(wizardPanel, ALTERNATIVE_FINISH_PAGE);
					} else {
						finishedPage.setSummary(getSummary());
						finishedPage.setTitle(getFinishTitle());
						finishedPage.setDescription(getFinishDescription());
						finishedPage.setDefaultName(getFinishName());
						finishedPage.setSelectNameDescription(getFinishNameDescription());
						wizardLayout.show(wizardPanel, FINISHED_PAGE);
					}
					next.setText(Messages.getString("WizardPanel.Finish"));
				}
				currentPage = nextPage;
				back.setEnabled(true);
			}
		} catch (ValidationException ve) {
			JOptionPane.showMessageDialog(this, ve.getMessage(),
					currentPage == null ? Messages.getString("WizardPanel.Finish") : currentPage.getPageTitle(),
					JOptionPane.INFORMATION_MESSAGE);
		}
	}

	public abstract WizardPage selectNextPage(WizardPage previous, int direction);

	public void setAlternativeFinishPage(JPanel p) {
		this.finishPanel = p;
		wizardPanel.add(finishPanel, ALTERNATIVE_FINISH_PAGE);
	}

	public void setBackEnabled(boolean enabled) {
		back.setEnabled(enabled);
	}

	public void setCancelEnabled(boolean enabled) {
		cancel.setEnabled(enabled);
	}

	public void setNextEnabled(boolean enabled) {
		next.setEnabled(enabled);
	}

	@Override
	public void setVisible(boolean b) {
		if (b) {
			setLocation((SCREEN_SIZE.width / 2) - (getWidth() / 2), (SCREEN_SIZE.height / 2) - (getHeight() / 2));
		}
		super.setVisible(b);
	}

	public abstract void validateComplete(String name) throws ValidationException;

	void jbInit() throws Exception {
		// setLayout(null);
		setLayout(new BorderLayout());
		wizardPanel.setLayout(wizardLayout);
		// wizardPanel.setBounds(0, 0, xWidth, yHeight - 74);
		add(wizardPanel, BorderLayout.CENTER);
		navigationPage.setLayout(new BorderLayout());
		// Info panel - This is the panel that holds the page descriptions
		info.setOpaque(false);
		info.setBorder(BorderFactory.createEmptyBorder(8, 8, 0, 0));
		info.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.WEST;
		// Info text - the text that goes in the info panel
		pageTitle.setFont(new java.awt.Font("Dialog", 1, 11));
		pageTitle.setVerifyInputWhenFocusTarget(true);
		pageTitle.setText("Page Title");
		pageDescription = new TextBox();
		gbc.weightx = 1.0;
		gbc.insets = new Insets(0, 24, 0, 0);
		UIUtil.jGridBagAdd(info, pageTitle, gbc, GridBagConstraints.REMAINDER);
		gbc.weighty = 1.0;
		gbc.insets = new Insets(4, 33, 0, 0);
		UIUtil.jGridBagAdd(info, pageDescription, gbc, GridBagConstraints.REMAINDER);
		JPanel top = new JPanel(new BorderLayout());
		top.setBackground(UIManager.getColor("Table.background"));
		top.setForeground(UIManager.getColor("Table.foreground"));
		top.add(info, BorderLayout.CENTER);
		JLabel wizardIconLabel = new JLabel(wizardIcon) {
			private static final long serialVersionUID = 1L;

			@Override
			public Dimension getPreferredSize() {
				return new Dimension(100, 100);
			}
		};
		wizardIconLabel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
		wizardIconLabel.setOpaque(false);
		top.add(wizardIconLabel, BorderLayout.EAST);
		JPanel t = new JPanel(new BorderLayout());
		t.add(top, BorderLayout.NORTH);
		t.add(new JSeparator(), BorderLayout.CENTER);
		navigationPage.add(t, BorderLayout.NORTH);
		pagesContainer.setLayout(pageLayout);
		navigationPage.add(pagesContainer, BorderLayout.CENTER);
		navigate.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
		navigate.setLayout(new FlowLayout(FlowLayout.RIGHT, 4, 0));
		navigate.add(cancel);
		navigate.add(back);
		navigate.add(next);
		JPanel bottom = new JPanel(new BorderLayout());
		bottom.add(new JSeparator(SwingConstants.HORIZONTAL), BorderLayout.NORTH);
		bottom.add(navigate, BorderLayout.CENTER);
		add(bottom, BorderLayout.SOUTH);
		next.setVerifyInputWhenFocusTarget(true);
		next.setText(Messages.getString("WizardPanel.Next"));
		next.setAction(new NextAction());
		cancel.setText(Messages.getString("WizardPanel.Cancel"));
		cancel.setAction(new CancelAction());
		back.setText(Messages.getString("WizardPanel.Back"));
		back.setAction(new BackAction());
		back.setEnabled(false);
		wizardPanel.add(welcomePage, WELCOME_PAGE);
		wizardPanel.add(navigationPage, NAVIGATION_PAGE);
		wizardPanel.add(finishedPage, FINISHED_PAGE);
		wizardLayout.show(wizardPanel, WELCOME_PAGE);
		setOpaque(true);
		setToolTipText("");
	}
}