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
package com.sshtools.appframework.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilePermission;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.AccessControlException;
import java.security.AccessController;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sshtools.appframework.api.SshToolsApplicationException;
import com.sshtools.appframework.api.ui.MultilineLabel;
import com.sshtools.appframework.api.ui.SshToolsApplicationContainer;
import com.sshtools.appframework.api.ui.SshToolsApplicationPanel;
import com.sshtools.appframework.mru.MRUList;
import com.sshtools.appframework.mru.MRUListModel;
import com.sshtools.appframework.prefs.FilePreferencesFactory;
import com.sshtools.appframework.util.GeneralUtil;
import com.sshtools.appframework.util.IOUtil;
import com.sshtools.ui.swing.EmptyIcon;
import com.sshtools.ui.swing.OptionDialog;
import com.sshtools.ui.swing.UIUtil;

import plugspud.PluginException;
import plugspud.PluginHostContext;
import plugspud.PluginManager;
import plugspud.PluginVersion;

/**
 * An abstract application class that provides container management, look and
 * feel configuration and most recently used menus.
 */
public abstract class SshToolsApplication implements PluginHostContext {
	public final static String PREF_LAF = "apps.laf";
	public final static String PREF_SKIN = "apps.skin";
	public final static String PREF_STAY_RUNNING = "apps.stayRunning";
	public final static String PREF_TOOLBAR_SHOW_SELECTIVE_TEXT = "apps.toolBar.showSelectiveText";
	public final static String PREF_TOOLBAR_SMALL_ICONS = "apps.toolBar.smallIcons";
	public static final String PREF_TOOLBAR_WRAP = "apps.toolBar.wrap";
	public final static String PREF_USE_SYSTEM_ICON_THEME = "apps.toolBar.useSystemIconTheme";
	final static Logger log = LoggerFactory.getLogger(IconStore.class);
	private static List<SshToolsApplicationContainer> containers = new ArrayList<SshToolsApplicationContainer>();
	private static SshToolsApplication instance;
	// Private statics
	private final static List<UIManager.LookAndFeelInfo> LAFS = new ArrayList<UIManager.LookAndFeelInfo>();
	private static MRUListModel mruModel;
	private static FileSystemManager vfs;
	static {
		// Add the LAFS already known to Java
		addLAF(new UIManager.LookAndFeelInfo("Default", UIManager.getLookAndFeel().getClass().getName()));
		UIManager.LookAndFeelInfo[] i;
		try {
			i = UIManager.getInstalledLookAndFeels();
		} catch (Throwable t) {
			i = new UIManager.LookAndFeelInfo[0];
		}
		for (int j = 0; j < i.length; j++) {
			addLAF(i[j]);
		}
		addLAF(new UIManager.LookAndFeelInfo("Darcula", "com.bulenkov.darcula.DarculaLaf"));
		addLAF(new UIManager.LookAndFeelInfo("Metal", "javax.swing.plaf.metal.MetalLookAndFeel"));
		addLAF(new UIManager.LookAndFeelInfo("Native", UIManager.getSystemLookAndFeelClassName()));
		addLAF(new UIManager.LookAndFeelInfo("Cross Platform", UIManager.getCrossPlatformLookAndFeelClassName()));
		OptionDialog.setIconLoader((option) -> {
			if(option.equals(com.sshtools.ui.Option.CHOICE_CANCEL))
				return IconStore.getInstance().getIcon("process-stop", 24);
			else if(option.equals(com.sshtools.ui.Option.CHOICE_CLOSE))
				return IconStore.getInstance().getIcon("window-close", 24);
			else if(option.equals(com.sshtools.ui.Option.CHOICE_SAVE))
				return IconStore.getInstance().getIcon("document-save", 24);
			else
				return new EmptyIcon(1, 24);
		});
	}

	public static void addLAF(UIManager.LookAndFeelInfo laf) {
		for (UIManager.LookAndFeelInfo info : LAFS) {
			if (info.getName().equals(laf.getName())) {
				try {
					laf.getClass().getClassLoader().loadClass(info.getClassName());
				} catch (Throwable t) {
				}
				return;
			}
		}
		LAFS.add(laf);
	}

	public static UIManager.LookAndFeelInfo[] getAllLookAndFeelInfo() {
		UIManager.LookAndFeelInfo[] i = new UIManager.LookAndFeelInfo[LAFS.size()];
		LAFS.toArray(i);
		return i;
	}

	public static SshToolsApplication getInstance() {
		return instance;
	}

	public static UIManager.LookAndFeelInfo getLAF(String className) {
		for (UIManager.LookAndFeelInfo info : LAFS) {
			if (info.getClassName().equals(className)) {
				return info;
			}
		}
		return null;
	}

	public static FileSystemManager getVFS() throws FileSystemException {
		if (vfs == null) {
			vfs = VFS.getManager();
		}
		return vfs;
	}

	public static void initLAF(UIManager.LookAndFeelInfo laf) {
	}

	public static void saveMRU(SshToolsApplication app) {
		File a = app.getApplicationPreferencesDirectory();
		if (a != null) {
			try {
				File f = new File(app.getApplicationPreferencesDirectory(), app.getApplicationName() + ".mru");
				PrintWriter w = new PrintWriter(new OutputStreamWriter(new FileOutputStream(f), "UTF-8"), true);
				try {
					w.println(mruModel.getMRUList().toString());
				} finally {
					w.close();
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}

	public static void setVFS(FileSystemManager vfs) {
		SshToolsApplication.vfs = vfs;
	}

	protected List<OptionsTab> additionalOptionsTabs;
	protected CommandLine cli;
	protected Class<? extends SshToolsApplicationContainer> defaultContainerClass;
	protected Class<? extends SshToolsApplicationPanel> panelClass;
	protected PluginManager pluginManager;
	protected int reusePort = -1;
	protected ServerSocket reuseServerSocket;

	public SshToolsApplication(Class<? extends SshToolsApplicationPanel> panelClass,
			Class<? extends SshToolsApplicationContainer> defaultContainerClass) throws IOException, ParseException {
		this.panelClass = panelClass;
		this.defaultContainerClass = defaultContainerClass;
		// this.lic = new LicenseVerification(this);
	}

	public void addAdditionalOptionsTab(OptionsTab tab) {
		if (!additionalOptionsTabs.contains(tab)) {
			additionalOptionsTabs.add(tab);
		}
	}

	/**
	 * Add any required command line options to be passed to the Options object
	 * supplied as an argument. Concrete Applications should overidde this, call
	 * super.buildCLIOptions() then add any other options they might require.
	 * 
	 * @param options Options list to add option too
	 */
	public void buildCLIOptions(Options options) {
		OptionBuilder.withLongOpt("help");
		OptionBuilder.withDescription("show this help");
		Option help = OptionBuilder.create("?");
		options.addOption(help);
		if (isReuseCapable()) {
			OptionBuilder.withLongOpt("nodaemon");
			OptionBuilder.withDescription("do not start the instance re-use daemon");
			Option noListen = OptionBuilder.create("d");
			options.addOption(noListen);
		}
		OptionBuilder.withLongOpt("--reusePort");
		OptionBuilder.withArgName("port");
		OptionBuilder.hasArg();
		OptionBuilder.withDescription("port on which to listen for re-use requests");
		OptionBuilder.create("r");
		options.addOption("e", "noOptionsAction", false, "dont allow the user to change options.");
	}

	public abstract boolean canUpgrade();

	public void closeContainer(SshToolsApplicationContainer container) {
		boolean canClose = container.canCloseContainer();
		if (canClose) {
			if (container.closeContainer()) {
				containers.remove(container);
				if (!PreferencesStore.getBoolean(PREF_STAY_RUNNING, false) && containers.size() == 0) {
					exit();
				}
			}
		}
	}

	public SshToolsApplicationContainer convertContainer(SshToolsApplicationContainer container,
			Class<SshToolsApplicationContainer> newContainerClass) throws SshToolsApplicationException {
		int idx = containers.indexOf(container);
		if (idx == -1) {
			throw new SshToolsApplicationException("Container is not being manager by the application.");
		}
		SshToolsApplicationContainer newContainer = null;
		try {
			if (container.closeContainer()) {
				SshToolsApplicationPanel panel = container.getApplicationPanel();
				newContainer = newContainerClass.newInstance();
				newContainer.init(this, panel);
				panel.setApplicationContainer(newContainer);
				if (!newContainer.isContainerVisible()) {
					final SshToolsApplicationContainer c = newContainer;
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							c.setContainerVisible(true);
						}
					});
				}
				containers.set(idx, newContainer);
			} else {
				throw new Exception("User cancelled closing of window.");
			}
			return newContainer;
		} catch (Throwable t) {
			throw new SshToolsApplicationException(t);
		}
	}

	public void exit() {
		PreferencesStore.savePreferences();
		saveMRU(this);
		System.exit(0);
	}

	public String getAboutLicenseDetails() {
		return "";
	}

	public String getAboutURL() {
		return "http://sshtools.com";
	}

	public OptionsTab[] getAdditionalOptionsTabs() {
		OptionsTab[] t = new OptionsTab[additionalOptionsTabs.size()];
		additionalOptionsTabs.toArray(t);
		return t;
	}

	public abstract BigInteger getApplicationExponent();

	public abstract Icon getApplicationLargeIcon();

	public abstract BigInteger getApplicationModulus();

	public abstract String getApplicationName();

	public abstract File getApplicationPreferencesDirectory();

	public String getApplicationVendor() {
		return "SSHTools Ltd";
	}

	public String getApplicationVersion() {
		return GeneralUtil.getVersionString(getApplicationName(), getClass());
	}

	/**
	 * Get the CommandLine object that provides access to the user supplied
	 * command line arguments.
	 * 
	 * @return command line
	 */
	public CommandLine getCLI() {
		return cli;
	}

	@Override
	public CommandLine getCommandLine() {
		return cli;
	}

	public SshToolsApplicationContainer getContainerAt(int idx) {
		return containers.get(idx);
	}

	public int getContainerCount() {
		return containers.size();
	}

	public SshToolsApplicationContainer getContainerForPanel(SshToolsApplicationPanel panel) {
		for (SshToolsApplicationContainer c : containers) {
			if (c.getApplicationPanel() == panel) {
				return c;
			}
		}
		return null;
	}

	public String getExpiryInfo() {
		return "";
	}

	public MRUListModel getMRUModel() {
		return mruModel;
	}

	public OptionsTab getOptionsTab(String title) {
		for (Iterator<OptionsTab> i = additionalOptionsTabs.iterator(); i.hasNext();) {
			OptionsTab t = i.next();
			if (t.getTabTitle().equals(title)) {
				return t;
			}
		}
		return null;
	}

	@Override
	public File getPluginDirectory() {
		return new File(getApplicationPreferencesDirectory(), "plugins");
	}

	@Override
	public String getPluginHostName() {
		return getApplicationName();
	}

	@Override
	public PluginVersion getPluginHostVersion() {
		return new PluginVersion(GeneralUtil.getVersionString(getApplicationName(), getClass()));
	}

	public PluginManager getPluginManager() {
		return pluginManager;
	}

	@Override
	public URL getPluginUpdatesResource() {
		try {
			return new URL("http://3sp.com/plugins/" + getPluginHostName() + "/" + getPluginHostVersion());
		} catch (MalformedURLException murle) {
			return null;
		}
	}

	@Override
	public String getPreference(String name, String defaultValue) {
		return PreferencesStore.get(name, defaultValue);
	}

	@Override
	public URL getStandardPluginsResource() {
		String spr = GeneralUtil.checkAndGetProperty("sshtools.builtInPlugins", "");
		if (spr.equals("")) {
			return null;
		}
		try {
			return new URL(spr);
		} catch (MalformedURLException murle) {
			try {
				return new File(spr).toURL();
			} catch (MalformedURLException mrule) {
				return null;
			}
		}
	}

	public void init(String[] args) throws SshToolsApplicationException {
		instance = this;
		boolean listen = isReuseCapable();
		// Do parse 1 of the command line arguments - see if we need to start
		// the daemon
		Options options1 = new Options();
		SshToolsApplication.this.buildCLIOptions(options1);
		pluginManager = new PluginManager();
		try {
			initPluginManager(options1);
		} catch (PluginException e1) {
			log(PluginHostContext.LOG_ERROR, "Failed to initialise plugin manager.", e1);
		}
		CommandLineParser parser1 = new PosixParser();
		CommandLine commandLine1 = null;
		try {
			// parse the command line arguments
			commandLine1 = parser1.parse(options1, args);
			if (commandLine1.hasOption("d")) {
				listen = false;
			}
			if (commandLine1.hasOption('r')) {
				reusePort = Integer.parseInt(commandLine1.getOptionValue('r'));
			}
		} catch (Exception e) {
			// Don't care at the moment
		}
		try {
			parsed(commandLine1);
		} catch (SshToolsApplicationException ae) {
			throw ae;
		} catch (Exception e) {
			throw new SshToolsApplicationException("Failed to parse application properties.", e);
		}
		// Try and message the reuse daemon if possible - saves starting another
		// instance
		if (listen) {
			Socket s = null;
			try {
				String hostname = "localhost";
				if (reusePort == -1) {
					reusePort = getDefaultReusePort();
				}
				log.debug("Attempting connection to reuse server on " + hostname + ":" + reusePort);
				s = new Socket(hostname, reusePort);
				log.debug("Found reuse server on " + hostname + ":" + reusePort + ", sending arguments");
				s.setSoTimeout(5000);
				PrintWriter pw = new PrintWriter(s.getOutputStream(), true);
				for (int i = 0; args != null && i < args.length; i++) {
					pw.println(args[i]);
				}
				pw.println();
				BufferedReader r = new BufferedReader(new InputStreamReader(s.getInputStream()));
				log.debug("Waiting for reuse server reply");
				String error = r.readLine();
				log.debug("Reuse server replied with '" + error + "'");
				if (error != null && !error.equals("")) {
					throw new SshToolsApplicationException(error);
				}
				System.exit(0);
			} catch (SshToolsApplicationException t) {
				throw t;
			} catch (SocketException se) {
				log.debug("No reuse server found.");
			} catch (SocketTimeoutException se) {
				log.debug("Reuse server not responding.", se);
			} catch (Exception e) {
				throw new SshToolsApplicationException(e);
			} finally {
				if (s != null) {
					try {
						s.close();
					} catch (IOException ioe) {
					}
				}
			}
		}
		additionalOptionsTabs = new ArrayList<OptionsTab>();
		log.info("Initialising application");
		File f = getApplicationPreferencesDirectory();
		if (f != null) {
			//
			FilePreferencesFactory.setPreferencesFile(new File(f, "javaprefs.properties"));
			PreferencesStore.init(new File(f, getApplicationName() + ".properties"));
		}
		boolean useSysIcons = PreferencesStore.getBoolean(SshToolsApplication.PREF_USE_SYSTEM_ICON_THEME, true);
		System.setProperty("jfreedesktop.alwaysUseDefaultTheme", String.valueOf(!useSysIcons));
		IconStore store = IconStore.getInstance();
		try {
			preConfigureIconStore(store);
			store.configure(this);
			postConfigureIconStore(store);
		} catch (IOException sshe) {
			throw new SshToolsApplicationException("Failed to setup icons.", sshe);
		} catch (ParseException sshe) {
			throw new SshToolsApplicationException("Failed to setup icons.", sshe);
		}
		configureChooser();
		loadMRU();
		setLookAndFeel(getDefaultLAF());
		log.debug("Plugin manager initialised, adding global preferences tabs");
		postInitialization();
		addAdditionalOptionsTab(new GlobalOptionsTab(this));
		Options options = new Options();
		buildCLIOptions(options);
		log.debug("Parsing command line");
		CommandLineParser parser = new PosixParser();
		try {
			// parse the command line arguments
			cli = parser.parse(options, args);
			if (cli.hasOption("?")) {
				printHelp(options);
				System.exit(0);
			}
		} catch (Exception e) {
			System.err.println("Invalid option: " + e.getMessage());
			printHelp(options);
			System.exit(1);
		}
		log.debug("Parsed command line");
		if (listen) {
			Thread t = new Thread("RemoteCommandLine") {
				@Override
				public void run() {
					Socket s = null;
					try {
						reuseServerSocket = new ServerSocket(reusePort, 1);
						while (true) {
							s = reuseServerSocket.accept();
							BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
							String line = null;
							List<String> args = new ArrayList<String>();
							while ((line = reader.readLine()) != null && !line.equals("")) {
								args.add(line);
							}
							final PrintWriter pw = new PrintWriter(s.getOutputStream());
							String[] a = new String[args.size()];
							args.toArray(a);
							CommandLineParser parser = new PosixParser();
							Options options = new Options();
							buildCLIOptions(options);
							// parse the command line arguments
							final CommandLine remoteCLI = parser.parse(options, a);
							pw.println("");
							SwingUtilities.invokeAndWait(new Runnable() {
								@Override
								public void run() {
									try {
										reuseRequest(remoteCLI);
									} catch (Throwable t) {
										pw.println(t.getMessage());
									}
								}
							});
							s.close();
							s = null;
						}
					} catch (Exception e) {
						/* DEBUG */e.printStackTrace();
					} finally {
						if (s != null) {
							try {
								s.close();
							} catch (IOException ioe) {
							}
						}
					}
				}
			};
			t.setDaemon(true);
			t.start();
		}
	}

	protected void parsed(CommandLine commandLine) throws Exception {
	}

	/**
	 * Get if this application is capable of responding to "re-use" requests
	 * when the main entry point is called whilst an instance is already. The
	 * second instance will provide its command line over a TCP/IP socket and
	 * expect the first instance to process it (opening another window or
	 * whatever).
	 * 
	 * @return application is capable of re-use
	 */
	public abstract boolean isReuseCapable();

	@Override
	public void log(int level, String message) {
		log(level, message, null);
	}

	@Override
	public void log(int level, String message, Throwable exception) {
		switch (level) {
		case PluginHostContext.LOG_DEBUG:
			log.debug(message, exception);
			break;
		case PluginHostContext.LOG_ERROR:
			log.error(message, exception);
			break;
		case PluginHostContext.LOG_INFORMATION:
			log.info(message);
			break;
		case PluginHostContext.LOG_WARNING:
			log.warn(message, exception);
			break;
		default:
			return;
		}
	}

	@Override
	public void log(int level, Throwable exception) {
		log(level, null, exception);
	}

	public SshToolsApplicationContainer newContainer() throws SshToolsApplicationException {
		SshToolsApplicationContainer container = null;
		try {
			container = defaultContainerClass.newInstance();
			newContainer(container);
			return container;
		} catch (Throwable t) {
			throw new SshToolsApplicationException(t);
		}
	}

	public void newContainer(SshToolsApplicationContainer container) throws SshToolsApplicationException {
		try {
			log.debug("Creating panel " + panelClass.getName());
			SshToolsApplicationPanel panel = panelClass.newInstance();
			configurePanel(panel, container);
		} catch (Throwable t) {
			throw new SshToolsApplicationException(t);
		}
	}

	@Override
	public void openURL(URL url) throws IOException {
		try {
			Desktop.getDesktop().browse(url.toURI());
		} catch (URISyntaxException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void putPreference(String name, String val) {
		PreferencesStore.put(name, val);
	}

	public void removeAdditionalOptionsTab(OptionsTab tab) {
		additionalOptionsTabs.remove(tab);
	}

	public void removeAdditionalOptionsTab(String title) {
		OptionsTab t = getOptionsTab(title);
		if (t != null) {
			removeAdditionalOptionsTab(t);
		}
	}

	/**
	 * This method handles "re-use" requests received when the main entry point
	 * is called whilst an instance is already. The second instance will provide
	 * its command line over a TCP/IP socket to this method and expect the first
	 * instance to process it (opening another window or whatever).
	 * 
	 * @param arguments arguments
	 * @throws SshToolsApplicationException on any error processing the command
	 *             line
	 */
	public abstract void reuseRequest(CommandLine arguments) throws SshToolsApplicationException;

	public void setLookAndFeel(UIManager.LookAndFeelInfo laf) {
		if (laf != null) {
			log.info("Setting Look and Feel to " + laf.getClassName());
			try {
				@SuppressWarnings("unchecked")
				LookAndFeel l = createLookAndFeel((Class<LookAndFeel>) Class.forName(laf.getClassName()));
				UIManager.setLookAndFeel(l);
				Icon checkIcon = UIManager.getIcon("CheckBoxMenuItem.checkIcon");
				Icon radioIcon = UIManager.getIcon("RadioButtonMenuItem.checkIcon");
				UIManager.put("MenuItem.checkIcon",
						new EmptyIcon(Math.max(checkIcon.getIconWidth(), radioIcon.getIconWidth()), checkIcon.getIconHeight()));
				UIManager.put("Menu.checkIcon",
						new EmptyIcon(Math.max(checkIcon.getIconWidth(), radioIcon.getIconWidth()), checkIcon.getIconHeight()));
				for (SshToolsApplicationContainer container : containers) {
					container.updateUI();
				}
				for (OptionsTab tab : additionalOptionsTabs) {
					SwingUtilities.updateComponentTreeUI(tab.getTabComponent());
				}
			} catch (Throwable t) {
				/* DEBUG */t.printStackTrace();
			}
		}
	}

	/**
	 * Show an 'About' dialog
	 * 
	 * @param parent parent
	 */
	public void showAbout(Component parent) {
		JPanel p = new JPanel(new GridBagLayout());
		p.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		GridBagConstraints gBC = new GridBagConstraints();
		gBC.anchor = GridBagConstraints.CENTER;
		gBC.fill = GridBagConstraints.HORIZONTAL;
		gBC.insets = new Insets(1, 1, 1, 1);
		JLabel a = new JLabel(getApplicationName());
		a.setFont(a.getFont().deriveFont(24f));
		UIUtil.jGridBagAdd(p, a, gBC, GridBagConstraints.REMAINDER);
		JLabel v = new JLabel("Version " + getApplicationVersion());
		v.setFont(v.getFont().deriveFont(10f));
		UIUtil.jGridBagAdd(p, v, gBC, GridBagConstraints.REMAINDER);
		MultilineLabel x = new MultilineLabel(getAboutLicenseDetails());
		x.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
		x.setFont(x.getFont().deriveFont(12f));
		UIUtil.jGridBagAdd(p, x, gBC, GridBagConstraints.REMAINDER);
		MultilineLabel c = new MultilineLabel(getExpiryInfo());
		c.setFont(c.getFont().deriveFont(10f));
		UIUtil.jGridBagAdd(p, c, gBC, GridBagConstraints.REMAINDER);
		final JLabel h = new JLabel(getAboutURL());
		h.setForeground(Color.blue);
		h.setFont(new Font(h.getFont().getName(), Font.BOLD, 10));
		h.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		h.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent evt) {
				try {
					openURL(new URL(getAboutURL()));
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
		});
		UIUtil.jGridBagAdd(p, h, gBC, GridBagConstraints.REMAINDER);
		JOptionPane.showMessageDialog(parent, p, "About", JOptionPane.PLAIN_MESSAGE, getApplicationLargeIcon());
	}

	protected void configureChooser() {
		XFileChooser.Chooser.addChoserImpl(File.class, XFileSelector.class);
		XFileChooser.Chooser.addChoserImpl(FileObject.class, VFSFileSelector.class);
	}

	protected void configurePanel(SshToolsApplicationPanel panel, SshToolsApplicationContainer container)
			throws SshToolsApplicationException {
		log.debug("Initialising panel " + panelClass.getName());
		panel.setOptionsActionAvailable(!getCLI().hasOption('e'));
		panel.init(this);
		log.debug("Initialised panel " + panelClass.getName());
		panel.rebuildActionComponents();
		container.init(this, panel);
		panel.setApplicationContainer(container);
		containers.add(container);
		if (!container.isContainerVisible()) {
			final SshToolsApplicationContainer c = container;
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					log.debug("Making container visible");
					c.setContainerVisible(true);
				}
			});
		}
	}

	protected LookAndFeel createLookAndFeel(Class<LookAndFeel> clazz) throws Exception {
		return clazz.newInstance();
	}

	protected LookAndFeelInfo getDefaultLAF() {
		return getLAF(PreferencesStore.get(PREF_LAF, UIManager.getSystemLookAndFeelClassName()));
	}

	protected abstract int getDefaultReusePort();

	protected abstract String getUnlicensedDescription();

	protected void initPluginManager(Options options1) throws PluginException {
		log.debug("Initialising plugin manager");
		pluginManager.init(this);
		pluginManager.buildCLIOptions(options1);
	}

	protected void postConfigureIconStore(IconStore store) throws IOException {
		// Subclasses can override to configure the icon store
	}

	protected abstract void postInitialization();

	protected void preConfigureIconStore(IconStore store) throws IOException {
		// Subclasses can override to configure the icon store
	}

	@SuppressWarnings("serial")
	protected void loadMRU() {
		try {
			if (System.getSecurityManager() != null) {
				AccessController.checkPermission(new FilePermission("<<ALL FILES>>", "write"));
			}
			File a = getApplicationPreferencesDirectory();
			if (a == null) {
				throw new AccessControlException("Application preferences directory not specified.");
			}
			InputStream in = null;
			MRUList mru = new MRUList();
			try {
				File f = new File(a, getApplicationName() + ".mru");
				if (f.exists()) {
					in = new FileInputStream(f);
					mru.reload(in);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				IOUtil.closeStream(in);
			}
			mruModel = new MRUListModel() {
				@Override
				public void add(File f) {
					super.add(f);
					saveMRU(SshToolsApplication.this);
				}

				@Override
				public void setMRUList(MRUList mru) {
					super.setMRUList(mru);
				}
			};
			mruModel.setMRUList(mru);
		} catch (AccessControlException ace) {
			ace.printStackTrace();
		}
	}

	private void printHelp(Options options) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp(getClass().getName(), options, true);
	}
}