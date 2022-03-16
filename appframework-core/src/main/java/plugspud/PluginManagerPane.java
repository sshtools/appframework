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
/*--

 $Id: PluginManagerPane.java,v 1.1.2.2 2010-12-01 06:47:42 brett Exp $

 Copyright (C) 2003 Brett Smith.
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:

 1. Redistributions of source code must retain the above copyright
    notice, this list of conditions, and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions, and the disclaimer that follows
    these conditions in the documentation and/or other materials
    provided with the distribution.

 3. The name "Plugspud" must not be used to endorse or promote products
    derived from this software without prior written permission.  For
    written permission, please contact t_magicthize@users.sourceforge.net.

 4. Products derived from this software may not be called "Plugspud", nor
    may "Plugspud" appear in their name, without prior written permission.

 In addition, we request (but do not require) that you include in the
 end-user documentation provided with the redistribution and/or in the
 software itself an acknowledgement equivalent to the following:
     "This product includes software developed for the Gruntspud
     "Project (http://gruntspud.sourceforge.net/)."

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED.  IN NO EVENT SHALL THE PLUGSPUD AUTHORS OR THE PROJECT
 CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 SUCH DAMAGE.
 */
package plugspud;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import org.kordamp.ikonli.carbonicons.CarbonIcons;

import com.sshtools.appframework.ui.IconStore;

/**
 * UI component to allow maintenance of installed plugins
 */
public class PluginManagerPane<C extends PluginHostContext> extends JPanel implements ActionListener, ListSelectionListener, Runnable {
	private static final long serialVersionUID = 1L;

	class ConfigureAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		ConfigureAction() {
			super();
			putValue(Action.NAME, "Configure");
			putValue(Action.SMALL_ICON, CONFIGURE_ICON);
			putValue(Action.SHORT_DESCRIPTION, "Configure plugin");
			putValue(Action.LONG_DESCRIPTION, "Configure the selected plugin");
			putValue(Action.MNEMONIC_KEY, Integer.valueOf('c'));
		}

		@Override
		public void actionPerformed(ActionEvent evt) {
			PluginDefinition def = model.getPluginDefinitionAt(table.getSelectedRow());
			ConfigurablePlugin<?> sel = (ConfigurablePlugin<?>) def.getPlugin();
			sel.configure(PluginManagerPane.this);
		}
	}

	// Supporting classes
	@SuppressWarnings("serial")
	class InstallAction extends AbstractAction {
		InstallAction() {
			super();
			putValue(Action.NAME, "Install");
			putValue(Action.SMALL_ICON, INSTALL_ICON);
			putValue(Action.SHORT_DESCRIPTION, "Install plugin");
			putValue(Action.LONG_DESCRIPTION, "Install the selected plugin");
			putValue(Action.MNEMONIC_KEY, Integer.valueOf('i'));
		}

		@Override
		public void actionPerformed(ActionEvent evt) {
			/** @todo install should warn about dependencies */
			final PluginDefinition def = model.getPluginDefinitionAt(table.getSelectedRow());
			status1Text.setText("Installing " + def.getName());
			statusIcon.setIcon(LARGE_INSTALL_ICON);
			install(def);
		}
	}

	class PluginDefinition {
		Properties localProperties, remoteProperties;
		Plugin<?> plugin;

		PluginDefinition(Plugin<?> plugin, Properties localProperties) {
			this(plugin, localProperties, null);
		}

		PluginDefinition(Plugin<?> plugin, Properties localProperties, Properties remoteProperties) {
			this.plugin = plugin;
			this.localProperties = localProperties;
			this.remoteProperties = remoteProperties;
		}

		PluginDefinition(Properties remoteProperties) {
			this(null, null, remoteProperties);
		}

		public String getAuthor() {
			return getLocalProperties() != null ? getLocalProperties().getProperty(PluginManager.PLUGIN_AUTHOR)
					: getRemoteProperties().getProperty(PluginManager.PLUGIN_AUTHOR);
		}

		public String getDownloadLocation() {
			return getRemoteProperties() != null ? getRemoteProperties().getProperty(PLUGIN_DOWNLOAD_LOCATION) : null;
		}

		public String getInformation() {
			return getLocalProperties() != null ? getLocalProperties().getProperty(PluginManager.PLUGIN_INFORMATION)
					: getRemoteProperties().getProperty(PluginManager.PLUGIN_INFORMATION);
		}

		public String getLocalVersion() {
			return getLocalProperties() != null ? getLocalProperties().getProperty(PluginManager.PLUGIN_VERSION) : null;
		}

		public String getName() {
			return getLocalProperties() != null ? getLocalProperties().getProperty(PluginManager.PLUGIN_NAME, "")
					: getRemoteProperties().getProperty(PluginManager.PLUGIN_NAME, "");
		}

		public String getRemoteVersion() {
			return getRemoteProperties() != null ? getRemoteProperties().getProperty(PluginManager.PLUGIN_VERSION) : null;
		}

		public PluginVersion getRequiredHostVersion() {
			String ver = getLocalProperties() != null
					? getLocalProperties().getProperty(PluginManager.PLUGIN_REQUIRED_HOST_VERSION, "")
					: getRemoteProperties().getProperty(PluginManager.PLUGIN_REQUIRED_HOST_VERSION, "");
			try {
				if (ver.equalsIgnoreCase("any"))
					return null;
				return new PluginVersion(ver);
			} catch (IllegalArgumentException iae) {
				return null;
			}
		}

		public String getShortDescription() {
			return getLocalProperties() != null ? getLocalProperties().getProperty(PluginManager.PLUGIN_SHORT_DESCRIPTION)
					: getRemoteProperties().getProperty(PluginManager.PLUGIN_SHORT_DESCRIPTION);
		}

		public int getStatus() {
			if (getRemoteProperties() == null) {
				return INSTALLED;
			}
			if (getLocalProperties() != null) {
				if (!getLocalProperties().getProperty(PluginManager.PLUGIN_VERSION)
						.equals(getRemoteProperties().getProperty(PluginManager.PLUGIN_VERSION)))
					return UPDATE_AVAILABLE;
				return INSTALLED;
			}
			return NOT_INSTALLED;
		}

		public String getURL() {
			return getLocalProperties() != null ? getLocalProperties().getProperty(PluginManager.PLUGIN_URL)
					: getRemoteProperties().getProperty(PluginManager.PLUGIN_URL);
		}

		public boolean isStandard() {
			return getPlugin() != null && getLocalProperties() != null
					&& getPlugin().getClass().getClassLoader() != manager.getPluginClassLoader();
		}

		Properties getLocalProperties() {
			return localProperties;
		}

		Plugin<?> getPlugin() {
			return plugin;
		}

		Properties getRemoteProperties() {
			return remoteProperties;
		}

		void setRemoteProperties(Properties remoteProperties) {
			this.remoteProperties = remoteProperties;
		}
	}

	class PluginManagerTable extends JTable {
		private static final long serialVersionUID = 1L;

		public PluginManagerTable(PluginManagerTableModel model) {
			super(model);
			setShowGrid(false);
			setAutoResizeMode(0);
			setRowHeight(18);
			setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			restoreTableMetrics(this, TABLE_GEOMETRY, new int[] { 70, 70, 78, 78, 140, 70 });
		}

		@Override
		public boolean getScrollableTracksViewportHeight() {
			Component parent = getParent();
			if (parent instanceof JViewport)
				return parent.getHeight() > getPreferredSize().height;
			return false;
		}
	};

	class PluginManagerTableModel extends AbstractTableModel {
		private static final long serialVersionUID = 1L;
		private Vector<PluginDefinition> definitions;

		PluginManagerTableModel() {
			definitions = new Vector<>();
			reload();
		}

		@Override
		public Class<?> getColumnClass(int c) {
			switch (c) {
			default:
				return String.class;
			}
		}

		@Override
		public int getColumnCount() {
			return 6;
		}

		@Override
		public String getColumnName(int c) {
			switch (c) {
			case 0:
				return "Status";
			case 1:
				return "Name";
			case 2:
				return "Version";
			case 3:
				return "Available";
			case 4:
				return "Description";
			default:
				return "Author";
			}
		}

		public PluginDefinition getPluginDefinitionAt(int r) {
			return definitions.elementAt(r);
		}

		@Override
		public int getRowCount() {
			return definitions.size();
		}

		@Override
		public Object getValueAt(int r, int c) {
			PluginDefinition def = getPluginDefinitionAt(r);
			switch (c) {
			case 0:
				switch (def.getStatus()) {
				case NOT_INSTALLED:
					return "Not installed";
				case UPDATE_AVAILABLE:
					return "Update available";
				default:
					return "Installed";
				}
			case 1:
				return def.getName();
			case 2:
				String local = def.getLocalVersion();
				return local == null ? "<N/A>" : local;
			case 3:
				String remote = def.getRemoteVersion();
				return remote == null ? "<Unknown>" : remote;
			case 4:
				return def.getShortDescription();
			default:
				return def.getAuthor();
			}
		}

		public void reload() {
			definitions.removeAllElements();
			int c = manager.getPluginCount();
			for (int i = 0; i < c; i++) {
				Plugin<C> p = manager.getPluginAt(i);
				PluginDefinition def = new PluginDefinition(p, manager.getPluginProperties(p), null);
				Properties selPr = def.getLocalProperties();
				String selRes = selPr.getProperty(PluginManager.PLUGIN_RESOURCE);
				if (showBuiltInPlugins || (selRes != null && !selRes.equals(""))) {
					definitions.addElement(def);
				}
			}
			fireTableDataChanged();
		}

		public void setRemoteProperties(String name, Properties p) {
			context.log(PluginHostContext.LOG_INFORMATION, "Looking if " + name + " is already installed");
			for (int i = 0; i < getRowCount(); i++) {
				PluginDefinition def = getPluginDefinitionAt(i);
				context.log(PluginHostContext.LOG_DEBUG, "Found " + def.getName());
				if (name.equals(def.getName())) {
					context.log(PluginHostContext.LOG_DEBUG, name + " is installed");
					def.setRemoteProperties(p);
					fireTableRowsUpdated(i, i);
					return;
				}
			}
			context.log(PluginHostContext.LOG_DEBUG, name + " is not installed");
			int r = getRowCount();
			definitions.addElement(new PluginDefinition(p));
			fireTableRowsInserted(r, r);
		}
	}

	// Supporting classes
	class RemoveAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		/**
		 * Constructor for the DeleteAction object
		 */
		RemoveAction() {
			super();
			putValue(Action.NAME, "Remove");
			putValue(Action.SMALL_ICON, REMOVE_ICON);
			putValue(Action.SHORT_DESCRIPTION, "Remove plugin");
			putValue(Action.LONG_DESCRIPTION, "Remove the selected plugin");
			putValue(Action.MNEMONIC_KEY, Integer.valueOf('r'));
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, InputEvent.CTRL_DOWN_MASK));
		}

		@Override
		public void actionPerformed(ActionEvent evt) {
			PluginDefinition def = model.getPluginDefinitionAt(table.getSelectedRow());
			Properties selPr = def.getLocalProperties();
			String selRes = selPr.getProperty(PluginManager.PLUGIN_RESOURCE);
			if (selRes == null || selRes.equals("")) {
				JOptionPane.showMessageDialog(PluginManagerPane.this,
						"Plugin cannot be removed in this version of " + context.getPluginHostName(), "Error",
						JOptionPane.ERROR_MESSAGE, LARGE_REMOVE_ICON);
				return;
			}
			// Check what other plugins are in the same archive
			Vector<Plugin<C>> allPlugins = getAllPluginsInResource(selRes);
			StringBuffer buf = new StringBuffer();
			if (allPlugins.size() > 1) {
				buf.append("This plugin is 1 of " + allPlugins.size() + " that are contained in the\n"
						+ "same archive. Removal of this plugin will also\n" + "cause the removal of ...\n\n");
				for (int i = 1; i < allPlugins.size(); i++) {
					buf.append("    ");
					Plugin<C> p = allPlugins.elementAt(i);
					Properties pr = manager.getPluginProperties(p);
					buf.append(pr.getProperty(PluginManager.PLUGIN_SHORT_DESCRIPTION));
					buf.append("\n");
				}
			}
			// Get the jars to remove
			HashMap<String, String> removeJars = getJarsToRemove(allPlugins);
			//
			buf.append("\nThe following files will be removed from your\n");
			buf.append("plugin directory .. \n\n");
			for (Iterator<String> i = removeJars.keySet().iterator(); i.hasNext();) {
				String key = i.next();
				buf.append("    ");
				buf.append(key.substring(key.lastIndexOf(File.separator) + 1));
				buf.append("\n");
			}
			buf.append("\n");
			buf.append("Are you sure you wish to continue?");
			// The jars are not actually removed until the application restarts
			if (JOptionPane.showConfirmDialog(PluginManagerPane.this, buf.toString(), "Remove plugin", JOptionPane.YES_NO_OPTION,
					JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
				try {
					removeJars(removeJars);
					JOptionPane.showMessageDialog(PluginManagerPane.this, "You should now restart " + context.getPluginHostName(),
							"Information", JOptionPane.INFORMATION_MESSAGE, LARGE_REMOVE_ICON);
				} catch (IOException ioe) {
					JOptionPane.showMessageDialog(PluginManagerPane.this, "Could not create remove list. " + ioe.getMessage(),
							"Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}

	@SuppressWarnings("serial")
	class ToolBarTablePane extends JPanel {
		ToolBarTablePane(JToolBar toolBar, JTable table) {
			super(new BorderLayout());
			setOpaque(false);
			JPanel t = new JPanel(new BorderLayout());
			t.setOpaque(false);
			toolBar.setOpaque(false);
			t.add(toolBar, BorderLayout.NORTH);
			JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
			sep.setOpaque(false);
			sep.setBorder(BorderFactory.createEmptyBorder(3, 0, 3, 0));
			t.add(sep, BorderLayout.CENTER);
			JScrollPane scroller = new JScrollPane(table) {
				@Override
				public Dimension getPreferredSize() {
					return new Dimension(super.getPreferredSize().width, 240);
				}
			};
			;
			scroller.setOpaque(false);
			scroller.setBorder(BorderFactory.createLineBorder(UIManager.getColor("Label.foreground")));
			add(t, BorderLayout.NORTH);
			add(scroller, BorderLayout.CENTER);
		}
	};

	class UpdateAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		UpdateAction() {
			super();
			putValue(Action.NAME, "Update");
			putValue(Action.SMALL_ICON, UPDATE_ICON);
			putValue(Action.SHORT_DESCRIPTION, "Update plugin");
			putValue(Action.LONG_DESCRIPTION, "Update the selected plugin");
			putValue(Action.MNEMONIC_KEY, Integer.valueOf('u'));
		}

		@Override
		public void actionPerformed(ActionEvent evt) {
			final PluginDefinition def = model.getPluginDefinitionAt(table.getSelectedRow());
			status1Text.setText("Updating " + def.getName());
			statusIcon.setIcon(LARGE_UPDATE_ICON);
			Properties selPr = def.getLocalProperties();
			String selRes = selPr.getProperty(PluginManager.PLUGIN_RESOURCE);
			if (selRes.equals("")) {
				JOptionPane.showMessageDialog(PluginManagerPane.this,
						"Plugin cannot be updated in this version of " + context.getPluginHostName(), "Error",
						JOptionPane.ERROR_MESSAGE, LARGE_REMOVE_ICON);
				return;
			}
			// Check what other plugins are in the same archive
			Vector<Plugin<C>> allPlugins = getAllPluginsInResource(selRes);
			HashMap<String, String> removeJars = getJarsToRemove(allPlugins);
			try {
				removeJars(removeJars);
				install(def);
			} catch (IOException ioe) {
				JOptionPane.showMessageDialog(PluginManagerPane.this, "Could not create remove list. " + ioe.getMessage(), "Error",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	public final static Icon ACTIVE_ICON = IconStore.getInstance().icon(CarbonIcons.CLOUD_DOWNLOAD, 32);
	public final static Icon CONFIGURE_ICON = IconStore.getInstance().icon(CarbonIcons.SETTINGS, 24);
	public final static Icon ERROR_ICON = IconStore.getInstance().icon(CarbonIcons.ERROR_FILLED, 32);
	// Icons
	public final static Icon IDLE_ICON = IconStore.getInstance().icon(CarbonIcons.UPGRADE, 32);
	public final static Icon INFORMATION_ICON = IconStore.getInstance().icon(CarbonIcons.INFORMATION_FILLED, 32);
	public final static Icon INSTALL_ICON = IconStore.getInstance().icon(CarbonIcons.ADD_FILLED, 24);
	// Plugin states
	public final static int INSTALLED = 0;
	public final static Icon LARGE_INSTALL_ICON = IconStore.getInstance().icon(CarbonIcons.ADD_FILLED, 48);
	public final static Icon LARGE_REMOVE_ICON = IconStore.getInstance().icon(CarbonIcons.TRASH_CAN, 48);
	public final static Icon LARGE_UPDATE_ICON = IconStore.getInstance().icon(CarbonIcons.RENEW, 48);
	public final static int NOT_INSTALLED = 2;
	public final static String PLUGIN_DOWNLOAD_LOCATION = "downloadLocation";
	public final static String PROGRESS_DIALOG_GEOMETRY = "pluginManager.progressDialog.geometry";
	public final static Icon REMOVE_ICON = IconStore.getInstance().icon(CarbonIcons.TRASH_CAN, 24);
	public final static String TABLE_GEOMETRY = "pluginManager.table.geometry";
	public final static int UPDATE_AVAILABLE = 1;
	public final static Icon UPDATE_ICON = IconStore.getInstance().icon(CarbonIcons.RENEW, 24);

	public static void centerComponent(Component c) {
		Rectangle r = c.getGraphicsConfiguration().getBounds();
		c.setLocation(((r.x + r.width) - c.getSize().width) / 2, ((r.y + r.height) - c.getSize().height) / 2);
	}

	private static void jGridBagAdd(JComponent parent, JComponent componentToAdd, GridBagConstraints constraints, int pos) {
		if (!(parent.getLayout() instanceof GridBagLayout))
			throw new IllegalArgumentException("parent must have a GridBagLayout");
		GridBagLayout layout = (GridBagLayout) parent.getLayout();
		constraints.gridwidth = pos;
		layout.setConstraints(componentToAdd, constraints);
		parent.add(componentToAdd);
	}

	private JButton cancelDownload;
	private PluginHostContext context;
	private JTextArea info;
	private PluginManager<C> manager;
	private PluginManagerTableModel model;
	private JProgressBar progress;
	private JDialog progressDialog;
	private JButton refresh;
	// Private instance variables
	private Action removeAction, updateAction, installAction, configureAction;
	private boolean showBuiltInPlugins;
	private JLabel status;
	private JLabel status1Text, status2Text, statusIcon;
	private PluginManagerTable table;
	private Thread updateThread;
	private boolean updating;
	// private void gotoSelectedURL() {
	// }
	private JLabel url;

	/**
	 * Creates a new PluginManagerPane object.
	 * 
	 * @param manager the plugin mananager
	 * @param context context
	 */
	public PluginManagerPane(PluginManager<C> manager, PluginHostContext context) {
		this(manager, context, true);
	}

	/**
	 * Creates a new PluginManagerPane object.
	 * 
	 * @param manager the plugin mananager
	 * @param context context
	 * @param showBuiltInPlugins show built-in plugins
	 */
	@SuppressWarnings("serial")
	public PluginManagerPane(PluginManager<C> manager, PluginHostContext context, boolean showBuiltInPlugins) {
		super(new BorderLayout());
		// Initialise
		this.context = context;
		this.showBuiltInPlugins = showBuiltInPlugins;
		this.manager = manager;
		// Connection state
		JPanel sp = new JPanel(new BorderLayout());
		sp.setOpaque(false);
		sp.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Plugin updates"),
				BorderFactory.createEmptyBorder(4, 4, 4, 4)));
		status = new JLabel("Click on Refresh to check for new plugins and updates", SwingConstants.LEFT);
		status.setIcon(IDLE_ICON);
		sp.add(status, BorderLayout.CENTER);
		refresh = new JButton("Refresh");
		refresh.setMnemonic('r');
		refresh.addActionListener(this);
		sp.add(refresh, BorderLayout.EAST);
		// Create the toolbar
		JToolBar toolBar = new JToolBar("Plugin manager tools");
		toolBar.putClientProperty("JToolBar.isRollover", Boolean.TRUE);
		toolBar.setBorder(null);
		toolBar.setFloatable(false);
		toolBar.add(new ToolButton(installAction = new InstallAction(), false));
		toolBar.add(new ToolButton(updateAction = new UpdateAction(), false));
		toolBar.add(new ToolButton(removeAction = new RemoveAction(), false));
		toolBar.add(new ToolButton(configureAction = new ConfigureAction(), false));
		// Create the text area
		table = new PluginManagerTable(model = new PluginManagerTableModel()) {
			@Override
			public Dimension getPreferredSize() {
				return new Dimension(super.getPreferredSize().width, 180);
			}
		};
		table.setBorder(null);
		table.getSelectionModel().addListSelectionListener(this);
		// Information panel
		JPanel ip = new JPanel(new BorderLayout());
		ip.setOpaque(false);
		// ip.setBorder(BorderFactory.createTitledBorder("Information"));
		ip.add(new JLabel(INFORMATION_ICON), BorderLayout.WEST);
		JPanel tp = new JPanel(new BorderLayout()) {
			@Override
			public Dimension getPreferredSize() {
				return new Dimension(super.getPreferredSize().width, 72);
			}
		};
		tp.setOpaque(false);
		tp.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
		url = new JLabel(" ", SwingConstants.CENTER);
		url.setFont(url.getFont().deriveFont(Font.BOLD).deriveFont(12f));
		url.setForeground(Color.blue);
		url.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		url.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
		url.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent evt) {
				try {
					PluginManagerPane.this.context.openURL(new URL(url.getText()));
				} catch (Exception e) {
				}
			}
		});
		tp.add(url, BorderLayout.SOUTH);
		info = new JTextArea(" ") {
			@Override
			public Dimension getPreferredSize() {
				return new Dimension(400, 130);
			}
		};
		info.setOpaque(false);
		info.setWrapStyleWord(true);
		info.setLineWrap(true);
		info.setEditable(false);
		info.setFont(UIManager.getFont("Label.font"));
		tp.add(info, BorderLayout.CENTER);
		ip.add(tp, BorderLayout.CENTER);
		// Build this
		add(sp, BorderLayout.NORTH);
		add(new ToolBarTablePane(toolBar, table) {
			@Override
			public Dimension getPreferredSize() {
				return new Dimension(480, 260);
			}
		}, BorderLayout.CENTER);
		add(ip, BorderLayout.SOUTH);
		// Create the progress dialog
		JPanel progressPanel = new JPanel(new BorderLayout());
		progressPanel.setOpaque(false);
		Window parentWindow = (Window) SwingUtilities.getAncestorOfClass(Window.class, this);
		if (parentWindow instanceof JFrame)
			progressDialog = new JDialog((JFrame) parentWindow, "Downloading", true);
		else if (parentWindow instanceof JDialog)
			progressDialog = new JDialog((JDialog) parentWindow, "Downloading", true);
		else
			progressDialog = new JDialog((JFrame) null, "Downloading", true);
		JPanel progressDetailPanel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		jGridBagAdd(progressDetailPanel, status1Text = new JLabel(), gbc, GridBagConstraints.REMAINDER);
		jGridBagAdd(progressDetailPanel, status2Text = new JLabel(), gbc, GridBagConstraints.REMAINDER);
		jGridBagAdd(progressDetailPanel, progress = new JProgressBar(), gbc, GridBagConstraints.REMAINDER);
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.fill = GridBagConstraints.NONE;
		gbc.weighty = 1.0;
		jGridBagAdd(progressDetailPanel, cancelDownload = new JButton("Cancel"), gbc, GridBagConstraints.REMAINDER);
		cancelDownload.addActionListener(this);
		cancelDownload.setMnemonic('c');
		progressPanel.add(statusIcon = new JLabel(), BorderLayout.WEST);
		progressPanel.add(progressDetailPanel, BorderLayout.CENTER);
		progressDialog.getContentPane().setLayout(new GridLayout(1, 1));
		progressDialog.getContentPane().add(progressPanel);
		progressDialog.setSize(220, 160);
		progressDialog.setResizable(false);
		centerComponent(progressDialog);
		// Set the intially available actions
		setAvailableActions();
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param evt DOCUMENT ME!
	 */
	@Override
	public void actionPerformed(ActionEvent evt) {
		if (evt.getSource() == refresh) {
			refreshUpdates();
		}
	}

	/**
	 * Clean up. This should be called to save dialog positions, table column
	 * widths etc.
	 * 
	 */
	public void cleanUp() {
		saveTableMetrics(table, TABLE_GEOMETRY);
	}

	public synchronized void refreshUpdates() {
		if (updateThread == null || !updateThread.isAlive()) {
			updateThread = new Thread(this);
			updateThread.start();
		}
	}

	/**
	 * Restore table column positions and sizes. Note, the table must have its
	 * auto resize mode set to off, i.e.
	 * 
	 * @param table table
	 * @param propertyName registry prefix
	 * @param defaultWidths default column widths
	 */
	public void restoreTableMetrics(JTable table, String propertyName, int[] defaultWidths) {
		if (table.getAutoResizeMode() != JTable.AUTO_RESIZE_OFF)
			throw new IllegalArgumentException("Table AutoResizeMode must be JTable.AUTO_RESIZE_OFF");
		for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
			try {
				table.moveColumn(
						table.convertColumnIndexToView(Integer
								.parseInt(context.getPreference(propertyName + ".column." + i + ".position", String.valueOf(i)))),
						i);
				int w = Integer.parseInt(context.getPreference(propertyName + ".column." + i + ".width", String.valueOf(
						(defaultWidths == null) ? table.getColumnModel().getColumn(i).getPreferredWidth() : defaultWidths[i])));
				table.getColumnModel().getColumn(i).setPreferredWidth(w);
			} catch (NumberFormatException nfe) {
			}
		}
	}

	@Override
	public void run() {
		status.setIcon(ACTIVE_ICON);
		updating = true;
		setAvailableActions();
		status.setText("Contacting plugin updates site");
		InputStream in = null;
		try {
			URL url = context.getPluginUpdatesResource();
			URL listURL = new URL(url + "/pluginlist.properties");
			in = listURL.openStream();
			status.setText("Downloading list of available plugins");
			Properties pluginList = new Properties();
			pluginList.load(in);
			HashMap<String, String> plugins = new HashMap<>();
			for (Iterator<Object> i = pluginList.keySet().iterator(); i.hasNext();) {
				String key = (String) i.next();
				int idx = key.indexOf('.');
				if (idx != -1) {
					String n = key.substring(0, idx);
					plugins.put(n, n);
				} else
					throw new PluginException(
							"pluginlist.properties is in incorrect format. Please " + "contact site administrator.");
			}
			for (Iterator<String> i = plugins.keySet().iterator(); i.hasNext();) {
				String plugin = (String) i.next();
				String propertiesLocation = pluginList.getProperty(plugin + ".properties", "");
				if (propertiesLocation.length() == 0)
					throw new PluginException("Each plugin in pluginlist.properties must have an entry "
							+ "<plugin-name>.properties specifying the location of the " + "properties file.");
				String archiveLocation = pluginList.getProperty(plugin + ".archive", "");
				if (archiveLocation.length() == 0)
					throw new PluginException("Each plugin in pluginlist.properties must have an entry "
							+ "<plugin-name>.archive specifying the location of the " + "archive file.");
				context.log(PluginHostContext.LOG_INFORMATION, "Found plugin " + plugin);
				URL propURL = null;
				try {
					propURL = new URL(propertiesLocation);
				} catch (MalformedURLException murle) {
					propURL = new URL(url + "/" + propertiesLocation);
				}
				context.log(PluginHostContext.LOG_DEBUG, "Properties location is " + propURL.toExternalForm());
				URL archiveURL = null;
				try {
					archiveURL = new URL(archiveLocation);
				} catch (MalformedURLException murle) {
					archiveURL = new URL(url + "/" + archiveLocation);
				}
				context.log(PluginHostContext.LOG_DEBUG, "Archive location is " + archiveURL.toExternalForm());
				try {
					status.setText("Loading properties for archive " + plugin);
					HashMap<String, Properties> props = manager.loadPluginProperties(propURL);
					status.setText("Checking plugin archive " + plugin);
					for (Map.Entry<String, Properties> en : props.entrySet()) {
						status.setText(" Found plugin " + en.getKey());
						en.getValue().setProperty(PLUGIN_DOWNLOAD_LOCATION, archiveURL.toExternalForm());
						model.setRemoteProperties(en.getKey(), en.getValue());
					}
				} catch (PluginException pe) {
					context.log(PluginHostContext.LOG_ERROR, "Could not load plugin.properties for " + plugin, pe);
				}
			}
			status.setText("Update complete");
			status.setIcon(IDLE_ICON);
		} catch (IOException ioe) {
			status.setIcon(ERROR_ICON);
			status.setText("Failed! " + ioe.getMessage());
		} catch (PluginException pe) {
			status.setIcon(ERROR_ICON);
			status.setText("Failed! " + pe.getMessage());
		} finally {
			PluginUtil.closeStream(in);
		}
		updating = false;
		setAvailableActions();
	}

	@Override
	public void valueChanged(ListSelectionEvent evt) {
		setAvailableActions();
	}

	/**
	 * Get a list of all the plugins that are in the same resource (jar?)
	 * 
	 * @param selRes the resource
	 * @return all plugins in the same resource
	 */
	private Vector<Plugin<C>> getAllPluginsInResource(String selRes) {
		// First check to see if this plugin is one of many in the same jar
		Vector<Plugin<C>> allPlugins = new Vector<>();
		context.log(PluginHostContext.LOG_INFORMATION, "Looking for  plugins that use " + selRes);
		for (Plugin<C> p : manager.plugins()) {
			Properties pr = manager.getPluginProperties(p);
			if (pr == null)
				context.log(PluginHostContext.LOG_ERROR, "No properties found for plugin?");
			else if (selRes.equals(pr.getProperty(PluginManager.PLUGIN_RESOURCE)))
				allPlugins.addElement(p);
		}
		return allPlugins;
	}

	/**
	 * Get a list of jars that should be removed when updating or removing a
	 * plugin. Jars that are shared by other currently installed plugins are not
	 * remove
	 * 
	 * @param allPlugins
	 */
	private HashMap<String, String> getJarsToRemove(Vector<Plugin<C>> allPlugins) {
		// Now determine what other jars should be removed as well
		HashMap<String, String> removeJars = new HashMap<>();
		for (Plugin<C> p : allPlugins) {
			Properties pr = manager.getPluginProperties(p);
			String rs = pr.getProperty(PluginManager.PLUGIN_RESOURCE);
			removeJars.put(rs, rs);
			String jars = pr.getProperty(PluginManager.PLUGIN_JARS, "");
			StringTokenizer st = new StringTokenizer(jars, ",");
			while (st.hasMoreTokens()) {
				String jar = st.nextToken();
				removeJars.put(jar, new File(manager.getPluginDirectory(), jar).getAbsolutePath());
			}
		}
		// If any plugins that are not being removed require any of the jars
		// that are going to be removed, dont remove them
		for (Plugin<C> p : manager.plugins()) {
			if (allPlugins.indexOf(p) == -1) {
				Properties pr = manager.getPluginProperties(p);
				String jars = pr.getProperty(PluginManager.PLUGIN_JARS, "");
				StringTokenizer st = new StringTokenizer(jars, ",");
				while (st.hasMoreTokens()) {
					String jar = st.nextToken();
					if (removeJars.containsKey(jar))
						removeJars.remove(jar);
				}
			}
		}
		return removeJars;
	}

	private void install(final PluginDefinition def) {
		Thread t = new Thread() {
			@Override
			public void run() {
				InputStream in = null;
				OutputStream out = null;
				File temp = null;
				try {
					PluginVersion version = def.getRequiredHostVersion();
					if (version == null)
						context.log(PluginHostContext.LOG_WARNING, "This plugin has not supplied the version of "
								+ context.getPluginHostName() + " it requires. Please contact " + "the plugin author.");
					if (version != null && context.getPluginHostVersion() != null
							&& version.compareTo(context.getPluginHostVersion()) > 0)
						throw new IOException(
								"This plugin requires at least version " + version.toString() + " of " + context.getPluginHostName()
										+ ". You " + "currently have version " + context.getPluginHostVersion());
					URL location = new URL(def.getDownloadLocation());
					context.log(PluginHostContext.LOG_INFORMATION, "Downloading plugin from " + location.toExternalForm());
					status2Text.setText("Connecting");
					context.log(PluginHostContext.LOG_INFORMATION, "Connecting");
					URLConnection conx = location.openConnection();
					status2Text.setText("Determining size");
					context.log(PluginHostContext.LOG_INFORMATION, "Determining size");
					progress.setMaximum(conx.getContentLength());
					in = conx.getInputStream();
					String base = location.getFile();
					int idx = base.lastIndexOf('/');
					if (idx > 0)
						base = base.substring(idx + 1);
					temp = new File(manager.getPluginDirectory(), base + ".tmp");
					context.log(PluginHostContext.LOG_INFORMATION, "Creating " + temp.getAbsolutePath());
					out = new FileOutputStream(temp);
					BufferedInputStream bin = new BufferedInputStream(in, 65536);
					BufferedOutputStream bout = new BufferedOutputStream(out, 65536);
					byte[] b = null;
					int r = 0;
					status2Text.setText("Downloading ....");
					while (true) {
						int a = bin.available();
						if (a == -1)
							break;
						else if (a == 0)
							a = 1;
						b = new byte[a];
						a = bin.read(b);
						if (a == -1)
							break;
						r += a;
						progress.setValue(r);
						bout.write(b, 0, a);
					}
					bout.flush();
					context.log(PluginHostContext.LOG_INFORMATION, "Download complete");
					status2Text.setText("Download complete.");
					PluginUtil.closeStream(in);
					PluginUtil.closeStream(out);
					progressDialog.setVisible(false);
					JOptionPane.showMessageDialog(
							PluginManagerPane.this, "Plugin downloaded and installed, you\n" + "will now need to restart "
									+ context.getPluginHostName() + " for\nthe new plugin to be activated.",
							"Information", JOptionPane.INFORMATION_MESSAGE);
				} catch (Exception e) {
					PluginUtil.closeStream(in);
					PluginUtil.closeStream(out);
					context.log(PluginHostContext.LOG_ERROR, e);
					progressDialog.setVisible(false);
					JOptionPane.showMessageDialog(PluginManagerPane.this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		};
		t.start();
		progressDialog.setVisible(true);
	}

	/**
	 * Mark all of the supplied jars for deletion upon the next startup. Each
	 * key in the hash map should contain a jar name, with the value being the
	 * appropriate <code>File</code> object
	 * 
	 * @param jars the jars to remove
	 */
	private void removeJars(HashMap<String, String> removeJars) throws IOException {
		FileOutputStream out = null;
		File removeFile = new File(context.getPluginDirectory(), "ros.list");
		try {
			out = new FileOutputStream(removeFile, true);
			PrintWriter w = new PrintWriter(out, true);
			for (Map.Entry<String, String> en : removeJars.entrySet()) {
				w.println(new File(en.getValue()).getAbsolutePath());
			}
			out.close();
		} finally {
			try {
				if (out != null)
					out.close();
			} catch (IOException ioe2) {
			}
		}
	}

	/**
	 * Save table column positions and sizes. Note, the table must have its auto
	 * resize mode set to off, i.e.
	 * 
	 * @param table
	 * @param registry prefix
	 * @param table name
	 */
	private void saveTableMetrics(JTable table, String propertyName) {
		for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
			int w = table.getColumnModel().getColumn(i).getWidth();
			context.putPreference(propertyName + ".column." + i + ".width", String.valueOf(w));
			context.putPreference(propertyName + ".column." + i + ".position", String.valueOf(table.convertColumnIndexToModel(i)));
		}
	}

	/**
	 * Set what actions are available depending on state
	 */
	private void setAvailableActions() {
		int sel = table.getSelectedRow();
		if (sel != -1) {
			PluginDefinition def = model.getPluginDefinitionAt(sel);
			info.setText(def.getInformation());
			url.setText(def.getURL());
			removeAction.setEnabled(def.getStatus() != NOT_INSTALLED);
			installAction.setEnabled(def.getStatus() == NOT_INSTALLED);
			updateAction.setEnabled(def.getStatus() == UPDATE_AVAILABLE);
			configureAction.setEnabled(def.getStatus() != NOT_INSTALLED && def.getPlugin() instanceof ConfigurablePlugin);
		} else {
			info.setText(" ");
			url.setText(" ");
			removeAction.setEnabled(false);
			installAction.setEnabled(false);
			updateAction.setEnabled(false);
			configureAction.setEnabled(false);
		}
		refresh.setEnabled(!updating);
	}
}
