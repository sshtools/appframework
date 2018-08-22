/*
 * SSHTools - Java SSH2 API
 *
 * Copyright (C) 2002 Lee David Painter.
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Library General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at your
 * option) any later version.
 *
 * You may also distribute it and/or modify it under the terms of the Apache
 * style J2SSH Software License. A copy of which should have been provided with
 * the distribution.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the License document supplied with
 * your distribution for more details.
 *
 */
package com.sshtools.appframework.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Iterator;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sshtools.appframework.actions.AboutAction;
import com.sshtools.appframework.actions.ExitAction;
import com.sshtools.appframework.actions.NewWindowAction;
import com.sshtools.appframework.api.SshToolsApplicationException;
import com.sshtools.appframework.api.ui.ActionMenu;
import com.sshtools.appframework.api.ui.SshToolsApplicationContainer;
import com.sshtools.appframework.api.ui.SshToolsApplicationPanel;
import com.sshtools.ui.swing.AppAction;
import com.sshtools.ui.swing.UIUtil;

public class SshToolsApplicationFrame extends JFrame implements SshToolsApplicationContainer {
	// Preference names
	/**  */
	public final static String PREF_LAST_FRAME_GEOMETRY = "application.lastFrameGeometry";
	final static Logger log = LoggerFactory.getLogger(SshToolsApplicationContainer.class);
	/**  */
	protected AppAction aboutAction;
	/**  */
	protected AppAction exitAction;
	/**  */
	// protected JSeparator toolSeparator;
	protected AppAction licensingAction;
	/**  */
	protected AppAction newWindowAction;
	private SshToolsApplication application;
	//
	private SshToolsApplicationPanel panel;
	private boolean showAboutBox = true;
	private boolean showExitAction = true;
	private boolean showMenu = true;
	private boolean showNewWindowAction = false;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sshtools.appframework.ui.SshToolsApplicationContainer#
	 * canCloseContainer ()
	 */
	@Override
	public boolean canCloseContainer() {
		return panel == null || panel.canClose();
	}

	@Override
	public boolean closeContainer() {
		boolean closedOk = getApplicationPanel().close();
		if (closedOk) {
			saveFrameGeometry();
			dispose();
			getApplicationPanel().deregisterAction(newWindowAction);
			getApplicationPanel().deregisterAction(exitAction);
			getApplicationPanel().deregisterAction(aboutAction);
			getApplicationPanel().rebuildActionComponents();
		}
		return closedOk;
	}

	public SshToolsApplication getApplication() {
		return application;
	}

	@Override
	public SshToolsApplicationPanel getApplicationPanel() {
		return panel;
	}

	@Override
	public void init(final SshToolsApplication application, SshToolsApplicationPanel panel) throws SshToolsApplicationException {
		log.debug("Initialising frame");
		this.panel = panel;
		this.application = application;
		if (application != null) {
			setTitle(application.getApplicationName() + " - " + application.getApplicationVersion());
		}
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		// Register the File menu
		panel.registerActionMenu(new ActionMenu("File", "File", 'f', 0));
		// Register the Exit action
		if (showExitAction && application != null) {
			panel.registerAction(exitAction = new ExitAction(application, this));
			// Register the New Window Action
		}
		if (showNewWindowAction && application != null) {
			panel.registerAction(newWindowAction = new NewWindowAction(application));
			// Register the Help menu
		}
		panel.registerActionMenu(new ActionMenu("Help", "Help", 'h', 99));
		// Register the About box action
		if (showAboutBox && application != null) {
			panel.registerAction(aboutAction = new AboutAction(this, application));
		}
		getApplicationPanel().rebuildActionComponents();
		// JPanel p = new JPanel(new ToolBarLayout());
		JPanel p = new JPanel(new BorderLayout(0, 0));
		JPanel center = new JPanel(new BorderLayout(0, 0));
		if (panel.getJMenuBar() != null) {
			setJMenuBar(panel.getJMenuBar());
		}
		if (panel.getToolBar() != null) {
			// center.add(toolSeparator = new JSeparator(JSeparator.HORIZONTAL),
			// BorderLayout.NORTH);
			// toolSeparator.setVisible(panel.getToolBar().isVisible());
			// panel.getToolBar().addComponentListener(new ComponentAdapter() {
			// public void componentShown(ComponentEvent e) {
			// toolSeparator.setVisible(SshToolsApplicationFrame.this.panel.getToolBar().isVisible());
			// }
			//
			// public void componentHidden(ComponentEvent e) {
			// toolSeparator.setVisible(SshToolsApplicationFrame.this.panel.getToolBar().isVisible());
			// }
			//
			// });
			final SshToolsApplicationPanel pnl = panel;
			panel.getToolBar().addComponentListener(new ComponentAdapter() {
				@Override
				public void componentHidden(ComponentEvent evt) {
					// toolSeparator.setVisible(pnl.getToolBar().isVisible());
				}
			});
			p.add(panel.getToolBar(), BorderLayout.NORTH);
		}
		center.add(panel, BorderLayout.CENTER);
		p.add(center, BorderLayout.CENTER);
		getContentPane().setLayout(new GridLayout(1, 1, 0, 0));
		getContentPane().add(p);
		// Watch for the frame closing
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent evt) {
				if (application != null) {
					application.closeContainer(SshToolsApplicationFrame.this);
				} else {
					int confirm = JOptionPane.showOptionDialog(SshToolsApplicationFrame.this, "Close " + getTitle() + "?",
							"Close Operation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
					if (confirm == 0) {
						hide();
					}
				}
			}
		});
		// If this is the first frame, center the window on the screen
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		boolean found = false;
		if (application != null && application.getContainerCount() != 0) {
			for (int i = 0; (i < application.getContainerCount()) && !found; i++) {
				SshToolsApplicationContainer c = application.getContainerAt(i);
				if (c instanceof SshToolsApplicationFrame) {
					SshToolsApplicationFrame f = (SshToolsApplicationFrame) c;
					setSize(f.getSize());
					Point newLocation = new Point(f.getX(), f.getY());
					newLocation.x += 48;
					newLocation.y += 48;
					if (newLocation.x > (screenSize.getWidth() - 64)) {
						newLocation.x = 0;
					}
					if (newLocation.y > (screenSize.getHeight() - 64)) {
						newLocation.y = 0;
					}
					setLocation(newLocation);
					found = true;
				}
			}
		}
		if (!found) {
			// Is there a previous stored geometry we can use?
			if (PreferencesStore.preferenceExists(PREF_LAST_FRAME_GEOMETRY)) {
				setBounds(PreferencesStore.getRectangle(PREF_LAST_FRAME_GEOMETRY, getBounds()));
			} else {
				pack();
				setSize(800, 600);
				UIUtil.positionComponent(SwingConstants.CENTER, this);
			}
		}
		log.debug("Initialisation of frame complete");
	}

	@Override
	public boolean isContainerVisible() {
		return isVisible();
	}

	@Override
	public void packContainer() throws SshToolsApplicationException {
		pack();
	}

	@Override
	public void setContainerTitle(String title) {
		setTitle(title);
	}

	@Override
	public void setContainerVisible(boolean visible) {
		setVisible(visible);
	}

	public void showAboutBox(boolean showAboutBox) {
		this.showAboutBox = showAboutBox;
	}

	public void showExitAction(boolean showExitAction) {
		this.showExitAction = showExitAction;
	}

	public void showNewWindowAction(boolean showNewWindowAction) {
		this.showNewWindowAction = showNewWindowAction;
	}

	@Override
	public void updateUI() {
		SwingUtilities.updateComponentTreeUI(this);
		if (getApplication() != null) {
			for (Iterator i = getApplication().additionalOptionsTabs.iterator(); i.hasNext();) {
				SwingUtilities.updateComponentTreeUI(((JComponent) i.next()));
			}
		}
	}

	/*
	 * Protected so SshTerm can overide and not save the geometry when in full
	 * screen mode.
	 */
	protected void saveFrameGeometry() {
		if (application != null && application.getContainerCount() == 1) {
			PreferencesStore.putRectangle(PREF_LAST_FRAME_GEOMETRY, getBounds());
		}
	}
}