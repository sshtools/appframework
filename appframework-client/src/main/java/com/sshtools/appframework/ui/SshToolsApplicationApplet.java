/**
 * Appframework
 * Copyright (C) 2003-2016 SSHTOOLS Limited
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 */
package com.sshtools.appframework.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.StringTokenizer;

import javax.swing.BorderFactory;
import javax.swing.JApplet;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSeparator;

import com.sshtools.appframework.actions.AboutAction;
import com.sshtools.appframework.api.SshToolsApplicationException;
import com.sshtools.appframework.api.ui.ActionMenu;
import com.sshtools.appframework.api.ui.SshToolsApplicationContainer;
import com.sshtools.appframework.api.ui.SshToolsApplicationPanel;
import com.sshtools.profile.URI;
import com.sshtools.ui.GlobalUIUtil;
import com.sshtools.ui.swing.AppAction;
import com.sshtools.ui.swing.UIUtil;

/**
 * @author $author$
 */

public abstract class SshToolsApplicationApplet extends JApplet {

	// eurgghh!

	/**  */

	public final static String[][] PARAMETER_INFO = {
		{ "sshapps.log.file", "string",
			"Logging output destination. Defaults to @console@" },
		{ "sshapps.log.level", "string",
			"Logging level. DEBUG,FATAL,ERROR,WARN,INFO,DEBUG or OFF. Defaults to OFF" },
		{ "sshapps.ui.informationPanel.background", "hex background",
			"Set the background background of the 'information panel'" },
		{ "sshapps.ui.informationPanel.foreground", "boolean",
			"Set the foreground background of the 'information panel'" },
		{ "sshapps.ui.informationPanel.borderColor", "boolean",
			"Set the border background of the 'information panel'" },
		{ "sshapps.ui.informationPanel.borderThickness", "integer",
			"Set the border thickness of the 'information panel'" },
		{ "sshapps.ui.toolBar", "boolean", "Enable / Disable the tool bar" },
		{ "sshapps.ui.menuBar", "boolean", "Enable / Disable the menu bar" },
		{ "sshapps.ui.disabledActions", "string",
			"Comma (,) separated list of disable actions" },
		{ "sshapps.ui.statusBar", "boolean", "Enable / Disable the menu bar" } };

	/**  */

	// Private instance variables

	private LoadingPanel loadingPanel;

	private JSeparator toolSeparator;

	private SshToolsApplicationPanel applicationPanel;

	private Color infoForeground;

	private int infoBorderThickness;

	private boolean toolBar;

	private boolean menuBar;

	private boolean statusBar;

	private Color infoBackground;

	private Color infoBorderColor;

	private String disabledActions;

	/**
	 * @param key
	 * @param def
	 * 
	 * @return
	 */

	public String getParameter(String key, String def) {
		String v = getParameter(key);
		return (v != null) ? v : def;

	}

	/**
   *
   */

	public void init() {
		try {
			Runnable r = new Runnable() {

				public void run() {
					try {
						getContentPane().setLayout(new BorderLayout());
						setAppletComponent(loadingPanel = new LoadingPanel());
						initApplet();
						JComponent p = buildAppletComponent();
						startApplet();
						setAppletComponent(p);
					} catch (Throwable t) {
						seriousAppletError(t);
					}

				}

			};
			Thread t = new Thread(r);
			t.start();
		} catch (Throwable t) {
			seriousAppletError(t);
		}

	}

	/**
	 * @throws IOException
	 */

	public void initApplet() throws IOException {
		/*
		 * ConfigurationLoader.setLogfile(getParameter("sshapps.log.file",
		 * "@console@"));
		 * log.getRootLogger().setLevel(org.apache.log4j.Level.toLevel(
		 */
		infoBackground = GlobalUIUtil.stringToColor(getParameter(
			"sshapps.ui.informationPanel.background", GlobalUIUtil
				.colorToString(new Color(255, 255, 204))));
		infoForeground = GlobalUIUtil.stringToColor(getParameter(
			"sshapps.ui.informationPanel.foreground", GlobalUIUtil
				.colorToString(Color.black)));
		infoBorderColor = GlobalUIUtil.stringToColor(getParameter(
			"sshapps.ui.informationPanel.borderColor", GlobalUIUtil
				.colorToString(Color.black)));
		infoBorderThickness = GlobalUIUtil.stringToInt(getParameter(
			"sshapps.ui.informationPanel.borderThickness", "1"), 1);
		toolBar = getParameter("sshapps.ui.toolBar", "true").equalsIgnoreCase(
			"true");
		menuBar = getParameter("sshapps.ui.menuBar", "true").equalsIgnoreCase(
			"true");
		statusBar = getParameter("sshapps.ui.statusBar", "true")
			.equalsIgnoreCase("true");
		disabledActions = getParameter("sshapps.ui.disabledActions", "");

	}

	/**
   *
   */

	public void startApplet() {

	}

	/**
	 * @return
	 * @throws IOException
	 * @throws SshToolsApplicationException
	 */

	public JComponent buildAppletComponent() throws IOException,

	SshToolsApplicationException {
		loadingPanel.setStatus("Creating application");
		applicationPanel = createApplicationPanel();
		loadingPanel.setStatus("Building action components");
		applicationPanel.rebuildActionComponents();
		StringTokenizer tk = new StringTokenizer((disabledActions == null) ? ""
			: disabledActions, ",");
		while (tk.hasMoreTokens()) {
			String n = tk.nextToken();
			applicationPanel.setActionVisible(n, false);
		}
		JPanel p = new JPanel(new BorderLayout());
		JPanel n = new JPanel(new BorderLayout());
		if (applicationPanel.getJMenuBar() != null) {
			n.add(applicationPanel.getJMenuBar(), BorderLayout.NORTH);
			applicationPanel.setMenuBarVisible(menuBar);
		}
		if (applicationPanel.getToolBar() != null) {
			JPanel t = new JPanel(new BorderLayout());
			t.add(applicationPanel.getToolBar(), BorderLayout.NORTH);
			applicationPanel.setToolBarVisible(toolBar);
			t.add(toolSeparator = new JSeparator(JSeparator.HORIZONTAL),
				BorderLayout.SOUTH);
			toolSeparator.setVisible(applicationPanel.getToolBar().isVisible());
			final SshToolsApplicationPanel pnl = applicationPanel;
			applicationPanel.getToolBar().addComponentListener(
				new ComponentAdapter() {

					public void componentHidden(ComponentEvent evt) {
						toolSeparator.setVisible(pnl.getToolBar().isVisible());

					}

				});
			n.add(t, BorderLayout.SOUTH);
		}
		p.add(n, BorderLayout.NORTH);
		p.add(applicationPanel, BorderLayout.CENTER);
		return p;

	}

	/**
	 * @param name
	 */

	public void doAction(String name) {
		AppAction a = applicationPanel.getAction(name);
		if (a != null) {
			if (a.isEnabled()) {
				a.actionPerformed(new ActionEvent(this,
					ActionEvent.ACTION_PERFORMED, a.getActionCommand()));
			}
		}

	}

	/**
	 * @return
	 * @throws SshToolsApplicationException
	 */

	public abstract SshToolsApplicationPanel createApplicationPanel()

	throws SshToolsApplicationException;

	/**
	 * @param component
	 */

	protected void setAppletComponent(JComponent component) {
		if (getContentPane().getComponentCount() > 0) {
			getContentPane().invalidate();
			getContentPane().removeAll();
		}
		getContentPane().add(component, BorderLayout.CENTER);
		getContentPane().validate();
		getContentPane().repaint();

	}

	/**
	 * @param t
	 */

	protected void seriousAppletError(Throwable t) {
		StringBuffer buf = new StringBuffer();
		buf.append("<html><p>A serious error has occured ...</p><br>");
		buf.append("<p><font size=\"-1\" background=\"#ff0000\"><b>");
		StringWriter writer = new StringWriter();
		t.printStackTrace(new PrintWriter(writer, true));
		StringTokenizer tk = new StringTokenizer(writer.toString(), "\n");
		while (tk.hasMoreTokens()) {
			String msg = tk.nextToken();
			buf.append(msg);
			if (tk.hasMoreTokens()) {
				buf.append("<br>");
			}
		}
		buf.append("</b></font></p><html>");
		SshToolsApplicationAppletPanel p = new SshToolsApplicationAppletPanel();
		p.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.insets = new Insets(0, 0, 8, 0);
		gbc.fill = GridBagConstraints.NONE;
		UIUtil.jGridBagAdd(p, new JLabel(buf.toString()), gbc,
			GridBagConstraints.REMAINDER);
		setAppletComponent(p);

	}

	/**
   *
   */

	public void start() {

	}

	/**
   *
   */

	public void stop() {

	}

	/**
   *
   */

	public void destroy() {

	}

	/**
	 * @return
	 */

	public String[][] getParameterInfo() {
		return PARAMETER_INFO;

	}

	/**
	 * @return Returns the infoBackground.
	 */

	public Color getInfoBackground() {
		return infoBackground;

	}

	/**
	 * @param infoBackground
	 *            The infoBackground to set.
	 */

	public void setInfoBackground(Color infoBackground) {
		this.infoBackground = infoBackground;

	}

	/**
	 * @return Returns the infoBorderColor.
	 */

	public Color getInfoBorderColor() {
		return infoBorderColor;

	}

	/**
	 * @param infoBorderColor
	 *            The infoBorderColor to set.
	 */

	public void setInfoBorderColor(Color infoBorderColor) {
		this.infoBorderColor = infoBorderColor;

	}

	/**
	 * @return Returns the infoBorderThickness.
	 */

	public int getInfoBorderThickness() {
		return infoBorderThickness;

	}

	/**
	 * @param infoBorderThickness
	 *            The infoBorderThickness to set.
	 */

	public void setInfoBorderThickness(int infoBorderThickness) {
		this.infoBorderThickness = infoBorderThickness;

	}

	/**
	 * @return Returns the infoForeground.
	 */

	public Color getInfoForeground() {
		return infoForeground;

	}

	/**
	 * @param infoForeground
	 *            The infoForeground to set.
	 */

	public void setInfoForeground(Color infoForeground) {
		this.infoForeground = infoForeground;

	}

	/**
	 * @return Returns the loadingPanel.
	 */

	public LoadingPanel getLoadingPanel() {
		return loadingPanel;

	}

	/**
	 * @param loadingPanel
	 *            The loadingPanel to set.
	 */

	public void setLoadingPanel(LoadingPanel loadingPanel) {
		this.loadingPanel = loadingPanel;

	}

	/**
	 * @return Returns the menuBar.
	 */

	public boolean isMenuBar() {
		return menuBar;

	}

	/**
	 * @param menuBar
	 *            The menuBar to set.
	 */

	public void setMenuBar(boolean menuBar) {
		this.menuBar = menuBar;

	}

	/**
	 * @return Returns the statusBar.
	 */

	public boolean isStatusBar() {
		return statusBar;

	}

	/**
	 * @param statusBar
	 *            The statusBar to set.
	 */

	public void setStatusBar(boolean statusBar) {
		this.statusBar = statusBar;

	}

	/**
	 * @return Returns the toolBar.
	 */

	public boolean isToolBar() {
		return toolBar;

	}

	/**
	 * @param toolBar
	 *            The toolBar to set.
	 */

	public void setToolBar(boolean toolBar) {
		this.toolBar = toolBar;

	}

	/**
	 * @return Returns the toolSeparator.
	 */

	public JSeparator getToolSeparator() {
		return toolSeparator;

	}

	/**
	 * @param toolSeparator
	 *            The toolSeparator to set.
	 */

	public void setToolSeparator(JSeparator toolSeparator) {
		this.toolSeparator = toolSeparator;

	}

	/**
	 * @return Returns the applicationPanel.
	 */

	public SshToolsApplicationPanel getApplicationPanel() {
		return applicationPanel;

	}

	/**
	 * @param applicationPanel
	 *            The applicationPanel to set.
	 */

	public void setApplicationPanel(SshToolsApplicationPanel applicationPanel) {
		this.applicationPanel = applicationPanel;

	}

	// Supporting classes

	class SshToolsApplicationAppletPanel extends JPanel {

		SshToolsApplicationAppletPanel() {
			super();
			setOpaque(true);
			setBackground(infoBackground);
			setForeground(infoForeground);
			setBorder(BorderFactory.createLineBorder(infoBorderColor,
				infoBorderThickness));

		}

	}

	class LoadingPanel extends SshToolsApplicationAppletPanel {

		private JProgressBar bar;

		LoadingPanel() {
			super();
			setLayout(new GridBagLayout());
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.anchor = GridBagConstraints.CENTER;
			gbc.insets = new Insets(0, 0, 8, 0);
			gbc.fill = GridBagConstraints.NONE;
			UIUtil.jGridBagAdd(this, new JLabel("Loading " + getAppletInfo()),
				gbc, GridBagConstraints.REMAINDER);
			bar = new JProgressBar(0, 100);
			// bar.setIndeterminate(true);
			bar.setStringPainted(true);
			UIUtil.jGridBagAdd(this, bar, gbc, GridBagConstraints.REMAINDER);

		}

		public void setStatus(String status) {
			bar.setString(status);

		}

	}

	public class SshToolsApplicationAppletContainer extends JPanel implements

	SshToolsApplicationContainer {

		// Private instance variables

		private SshToolsApplicationPanel panel;

		private SshToolsApplication application;

		private URI ticketURI;

		// Construct the applet

		public SshToolsApplicationAppletContainer() {

		}

		public void init(SshToolsApplication application,
				SshToolsApplicationPanel panel)

		throws SshToolsApplicationException {
			this.application = application;
			this.panel = panel;
			panel.registerActionMenu(new ActionMenu("Help", "Help", 'h', 99));
			panel.registerAction(new AboutAction(this, application));
			getApplicationPanel().rebuildActionComponents();

		}

		public void setContainerTitle(String title) {
			getAppletContext().showStatus(title);

		}

		public SshToolsApplicationPanel getApplicationPanel() {
			return panel;

		}

		public boolean closeContainer() {
			return getApplicationPanel().close();

		}

		public void setContainerVisible(boolean visible) {
			setVisible(visible);

		}

		public boolean isContainerVisible() {
			return isVisible();

		}

		public void packContainer() throws SshToolsApplicationException {
			throw new SshToolsApplicationException("Cant pack an applet.");

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @seecom.sshtools.appframework.ui.SshToolsApplicationContainer#
		 * canCloseContainer()
		 */

		public boolean canCloseContainer() {
			return panel == null || panel.canClose();

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @seecom.sshtools.appframework.api.ui.SshToolsApplicationContainer#
		 * getTicketURI()
		 */
		public URI getTicketURI() {
			return ticketURI;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @seecom.sshtools.appframework.api.ui.SshToolsApplicationContainer#
		 * setTicketURI(com.sshtools.profile.URI)
		 */
		public void setTicketURI(URI ticketURI) {
			this.ticketURI = ticketURI;
		}

		public MessagePanel getMessagePanel() {
			// TODO Auto-generated method stub
			return null;
		}

	}

}