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
package com.sshtools.appframework.ui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;

import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import com.sshtools.appframework.actions.AboutAction;
import com.sshtools.appframework.actions.ExitAction;
import com.sshtools.appframework.actions.NewWindowAction;
import com.sshtools.appframework.api.SshToolsApplicationException;
import com.sshtools.appframework.api.ui.ActionMenu;
import com.sshtools.appframework.api.ui.SshToolsApplicationContainer;
import com.sshtools.appframework.api.ui.SshToolsApplicationPanel;
import com.sshtools.appframework.ui.MessagePanel.Type;
import com.sshtools.appframework.util.GeneralUtil;
import com.sshtools.ui.swing.AppAction;

/**
 * @author $author$
 */
public class SshToolsApplicationInternalFrame extends JInternalFrame implements SshToolsApplicationContainer {
	// Preference names
	/**  */
	public final static String PREF_LAST_FRAME_GEOMETRY = "application.lastFrameGeometry";
	/**  */
	protected AppAction aboutAction;
	/**  */
	protected AppAction exitAction;
	protected MessagePanel messagePanel;
	/**  */
	protected AppAction newWindowAction;
	/**  */
	protected JSeparator toolSeparator;
	private SshToolsApplication application;
	//
	private SshToolsApplicationPanel panel;
	private boolean showAboutBox = true;
	private boolean showExitAction = true;
	private boolean showMenu = true;
	private boolean showNewWindowAction = true;

	@Override
	public boolean canCloseContainer() {
		return panel == null || panel.canClose();
	}

	@Override
	public boolean closeContainer() {
		boolean closedOk = getApplicationPanel().close();
		if (closedOk) {
			/*
			 * If this is the last frame to close, then store its geometry for
			 * use
			 */
			if (application != null && application.getContainerCount() == 1) {
				PreferencesStore.putRectangle(PREF_LAST_FRAME_GEOMETRY, getBounds());
			}
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

	public MessagePanel getMessagePanel() {
		return messagePanel;
	}

	@Override
	public void init(final SshToolsApplication application, SshToolsApplicationPanel panel) throws SshToolsApplicationException {
		this.panel = panel;
		this.application = application;
		if (application != null) {
			setTitle(GeneralUtil.getVersionString(application.getApplicationName(), getClass()));
		}
		// Register the File menu
		panel.registerActionMenu(new ActionMenu("File", Messages.getString("SshToolsApplicationInternalFrame.File"), 'f', 0));
		// Register the Exit action
		if (showExitAction && application != null) {
			panel.registerAction(exitAction = new ExitAction(application, this));
			// Register the New Window Action
		}
		if (showNewWindowAction && application != null) {
			panel.registerAction(newWindowAction = new NewWindowAction(application));
			// Register the Help menu
		}
		panel.registerActionMenu(new ActionMenu("Help", Messages.getString("SshToolsApplicationInternalFrame.Help"), 'h', 99));
		// Register the About box action
		if (showAboutBox && application != null) {
			panel.registerAction(aboutAction = new AboutAction(this, application));
		}
		getApplicationPanel().rebuildActionComponents();
		JPanel p = new JPanel(new BorderLayout());
		if (panel.getJMenuBar() != null) {
			setJMenuBar(panel.getJMenuBar());
		}
		if (panel.getToolBar() != null) {
			JPanel t = new JPanel(new BorderLayout());
			t.add(panel.getToolBar(), BorderLayout.NORTH);
			t.add(toolSeparator = new JSeparator(SwingConstants.HORIZONTAL), BorderLayout.SOUTH);
			toolSeparator.setVisible(panel.getToolBar().isVisible());
			final SshToolsApplicationPanel pnl = panel;
			panel.getToolBar().addComponentListener(new ComponentAdapter() {
				@Override
				public void componentHidden(ComponentEvent evt) {
					toolSeparator.setVisible(pnl.getToolBar().isVisible());
				}
			});
			p.add(t, BorderLayout.NORTH);
		}
		p.add(panel, BorderLayout.CENTER);
		messagePanel = new MessagePanel(Type.hidden);
		p.add(messagePanel, BorderLayout.SOUTH);
		getContentPane().setLayout(new GridLayout(1, 1));
		getContentPane().add(p);
		// Watch for the frame closing
		// setDefaultCloseOperation(JInternalFrame.DO_NOTHING_ON_CLOSE);
		addVetoableChangeListener(new VetoableChangeListener() {
			@Override
			public void vetoableChange(PropertyChangeEvent evt) throws PropertyVetoException {
				if (application != null) {
					application.closeContainer(SshToolsApplicationInternalFrame.this);
				} else {
					if (evt.getPropertyName().equals(IS_CLOSED_PROPERTY)) {
						boolean changed = ((Boolean) evt.getNewValue()).booleanValue();
						if (changed) {
							int confirm = JOptionPane.showOptionDialog(SshToolsApplicationInternalFrame.this,
									Messages.getString("SshToolsApplicationInternalFrame.Close") + " " + getTitle() + "?",
									Messages.getString("SshToolsApplicationInternalFrame.CloseOp"), JOptionPane.YES_NO_OPTION,
									JOptionPane.QUESTION_MESSAGE, null, null, null);
							if (confirm == 0) {
								SshToolsApplicationInternalFrame.this.getDesktopPane()
										.remove(SshToolsApplicationInternalFrame.this);
							}
						}
					}
				}
			}
		});
		/*
		 * this.addWindowListener(new WindowAdapter() { public void
		 * windowClosing(WindowEvent evt) { if(application!=null)
		 * application.closeContainer(SshToolsApplicationFrame.this); else
		 * hide(); } }); // If this is the first frame, center the window on the
		 * screen Dimension screenSize =
		 * Toolkit.getDefaultToolkit().getScreenSize(); boolean found = false;
		 * if (application!=null && application.getContainerCount() != 0) { for
		 * (int i = 0; (i < application.getContainerCount()) && !found; i++) {
		 * SshToolsApplicationContainer c = application.getContainerAt(i); if (c
		 * instanceof SshToolsApplicationFrame) { SshToolsApplicationFrame f =
		 * (SshToolsApplicationFrame) c; setSize(f.getSize()); Point newLocation
		 * = new Point(f.getX(), f.getY()); newLocation.x += 48; newLocation.y
		 * += 48; if (newLocation.x > (screenSize.getWidth() - 64)) {
		 * newLocation.x = 0; } if (newLocation.y > (screenSize.getHeight() -
		 * 64)) { newLocation.y = 0; } setLocation(newLocation); found = true; }
		 * } } if (!found) { // Is there a previous stored geometry we can use?
		 * if (PreferencesStore.preferenceExists(PREF_LAST_FRAME_GEOMETRY)) {
		 * setBounds(PreferencesStore.getRectangle( PREF_LAST_FRAME_GEOMETRY,
		 * getBounds())); } else { pack();
		 * UIUtil.positionComponent(SwingConstants.CENTER, this); }
		 */
		pack();
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
}