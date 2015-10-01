/* HEADER */
package com.sshtools.appframework.api.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sshtools.appframework.actions.AbstractOptionsAction;
import com.sshtools.appframework.api.SshToolsApplicationException;
import com.sshtools.appframework.ui.Messages;
import com.sshtools.appframework.ui.OptionsPanel;
import com.sshtools.appframework.ui.OptionsTab;
import com.sshtools.appframework.ui.PreferencesStore;
import com.sshtools.appframework.ui.SshToolsApplication;
import com.sshtools.appframework.util.GeneralUtil;
import com.sshtools.ui.Option;
import com.sshtools.ui.swing.ActionToolBar;
import com.sshtools.ui.swing.AppAction;
import com.sshtools.ui.swing.GradientPanel;
import com.sshtools.ui.swing.OptionDialog;

public abstract class SshToolsApplicationPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	final static Log log = LogFactory.getLog(SshToolsApplicationPanel.class);

	public final static boolean DETAILED_ERROR_DIALOGS = "true".equalsIgnoreCase(GeneralUtil.checkAndGetProperty(
		"sshtoos.appframework.detailedErrorDialogs", "true"));
	//
	protected SshToolsApplication application;
	protected JMenuBar menuBar;
	protected ActionToolBar toolBar;
	protected JPopupMenu contextMenu;
	protected SshToolsApplicationContainer container;
	protected List<AppAction> actions = new ArrayList<AppAction>();
	protected Map<String, Boolean> actionsVisible = new HashMap<String, Boolean>();
	protected boolean toolsVisible;

	protected boolean toolBarVisible, menuBarVisible;

	/**  */
	protected List<ActionMenu> actionMenus = new ArrayList<ActionMenu>();
	protected List<AppAction> previousActions = new ArrayList<AppAction>();
	protected ActionBuilder actionBuilder;
	private boolean optionsActionAvailable = true;

	/**
	 * Creates a new SshToolsApplicationPanel object.
	 */
	public SshToolsApplicationPanel() {
		super();
		toolsVisible = true;
		toolBarVisible = true;
		menuBarVisible = true;
	}

	/**
	 * Creates a new SshToolsApplicationPanel object.
	 * 
	 * @param mgr
	 */
	public SshToolsApplicationPanel(LayoutManager mgr) {
		super(mgr);
		// setInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, new
		// ComponentInputMap(this));
	}

	/**
	 * Called by the application framework to test the closing state
	 * 
	 * @return
	 */
	public abstract boolean canClose();

	/**
	 * Called by the application framework to close the panel
	 */
	public abstract boolean close();

	/**
	 * Called by the application framework when a change in connection state has
	 * occured. The available actions should be enabled/disabled in this methods
	 * implementation
	 */
	public abstract void setAvailableActions();

	/**
	 * Set an actions visible state
	 * 
	 * @param name
	 * @param visible
	 */
	public void setActionVisible(String name, boolean visible) {
		actionsVisible.put(name, new Boolean(visible));
	}

	/**
	 * Gets the container for this panel.
	 * 
	 * @return
	 */
	public SshToolsApplicationContainer getApplicationContainer() {
		return container;
	}

	/**
	 * Sets the container for this panel
	 * 
	 * @param container
	 */
	public void setApplicationContainer(SshToolsApplicationContainer container) {
		this.container = container;
	}

	/**
	 * Register a new menu
	 * 
	 * @param actionMenu
	 */
	public void registerActionMenu(ActionMenu actionMenu) {
		ActionMenu current = getActionMenu(actionMenu.getName());
		if (current == null) {
			actionMenus.add(actionMenu);
		}
	}

	/**
	 * Get an Iterator of all registered actions
	 */
	public Iterator<AppAction> actions() {
		return actions.iterator();
	}

	/**
	 * Gets a menu by name
	 * 
	 * @param actionMenuName
	 * @return action menu
	 */
	public ActionMenu getActionMenu(String actionMenuName) {
		return getActionMenu(actionMenus.iterator(), actionMenuName);
	}

	/**
	 * Get an action by name
	 * 
	 * @param name
	 * @return action
	 */
	public AppAction getAction(String name) {
		for (AppAction a : actions) {
			if (a.getName().equals(name)) {
				return a;
			}
		}
		return null;
	}

	/**
	 * De-register an action
	 * 
	 * @param action action
	 */
	public void deregisterAction(AppAction action) {
		if (action != null) {
			actions.remove(action);
		}
	}

	/**
	 * Register a new action
	 * 
	 * @param action action
	 */
	public boolean registerAction(AppAction action) {
		if (action == null) {
			throw new IllegalArgumentException("Cannot register null action.");
		}
		if (!actions.contains(action)) {
			log.debug("Registering action");
			actions.add(action);
			return true;
		}
		return false;
	}

	/**
	 * Initialize the panel
	 * 
	 * @param application
	 * 
	 * @throws SshToolsApplicationException
	 */
	public void init(SshToolsApplication application) throws SshToolsApplicationException {
		this.application = application;
		menuBar = new JMenuBar();
		// Create the tool bar
		toolBar = new ActionToolBar();
		toolBar.setFloatable(false);
		toolBar.setBorderPainted(true);
		toolBar.putClientProperty("JToolBar.isRollover", Boolean.TRUE);
		// Create the context menu
		contextMenu = new JPopupMenu();
		registerActionMenu(new ActionMenu("Tools", "Tools", 't', 30));
		if (PreferencesStore.isStoreAvailable()) {
			if (optionsActionAvailable) {
				registerAction(new AbstractOptionsAction() {
					private static final long serialVersionUID = 1L;

					public void actionPerformed(ActionEvent evt) {
						showOptions();
					}
				});
			}
		}
		actionBuilder = new SshToolsApplicationPanelActionBuilder(menuBar, toolBar, contextMenu);
	}

	/**
	 * Show the options dialog
	 */
	public void showOptions() {
		OptionsTab[] tabs = getApplication().getAdditionalOptionsTabs();
		OptionsPanel.showOptionsDialog(this, tabs);
		rebuildActionComponents();
	}

	/**
	 * Return an additional actions as a list
	 * 
	 * @return list of additional actions
	 */
	public abstract Collection<AppAction> getAdditionalActions();

	/**
	 * Return an additional action menus as a list
	 * 
	 * @return list of additional action menus
	 */
	public abstract Collection<ActionMenu> getAdditionalActionMenus();

	/**
	 * Rebuild all the action components such as toobar, context menu
	 */
	public void rebuildActionComponents() {
		actionBuilder.rebuildActionComponents();
	}

	/**
	 * Determine if the toolbar, menu and statusbar are visible
	 * 
	 * @return
	 */
	public boolean isToolsVisible() {
		return toolsVisible;
	}

	/**
	 * Set the visible state of the menu bar
	 * 
	 * @param visible
	 */
	public void setMenuBarVisible(boolean visible) {
		if ((getJMenuBar() != null)) {
			menuBarVisible = visible;
			boolean viz = toolsVisible && menuBarVisible;
			if (viz != getJMenuBar().isVisible()) {
				getJMenuBar().setVisible(visible);
				revalidate();
			}
		}
	}

	/**
	 * Get whether the menu bar is visible.
	 * 
	 * @return visible
	 */
	public boolean isMenuBarVisible() {
		return menuBarVisible;
	}

	/**
	 * Set the visible state of the toolbar
	 * 
	 * @param visible
	 */
	public void setToolBarVisible(boolean visible) {
		if ((getToolBar() != null)) {
			toolBarVisible = visible;
			boolean viz = toolsVisible && toolBarVisible;
			if (viz != getToolBar().isVisible()) {
				getToolBar().setVisible(visible);
				revalidate();
			}
		}
	}

	/**
	 * Get is the tool bar is currently visible
	 * 
	 * @return tool bar visible
	 */
	public boolean isToolBarVisible() {
		return toolBarVisible;
	}

	/**
	 * Display something other that the normal blank screen so our component
	 * looks pretty.
	 */
	protected void showWelcomeScreen() {
		synchronized (getTreeLock()) {
			removeAll();
			setLayout(new BorderLayout());
//			GradientPanel p = new GradientPanel(new BorderLayout());
//			p.setBackground(Color.white);
//			p.setBackground2(new Color(164, 228, 244));
//			p.setForeground(Color.black);
			JPanel p = new JPanel(new BorderLayout());
			JLabel welcomeLabel = new JLabel("", JLabel.CENTER);
			welcomeLabel.setForeground(Color.white);
			welcomeLabel.setFont(welcomeLabel.getFont().deriveFont(72f).deriveFont(Font.BOLD + Font.ITALIC));
			welcomeLabel.setHorizontalAlignment(JLabel.RIGHT);
			p.add(welcomeLabel, BorderLayout.SOUTH);
			add(p, BorderLayout.CENTER);
			validate();
		}
	}

	/**
	 * Set the visible state of all tools. This will set the toolbar, menu and
	 * status bar visible states to the value provided.
	 * 
	 * @param visible
	 */
	public void setToolsVisible(boolean visible) {
		synchronized (getTreeLock()) {
			toolsVisible = visible;
			if (getToolBar() != null) {
				if (isToolBarVisible() && visible) {
					getToolBar().setVisible(true);
				} else {
					getToolBar().setVisible(false);
				}
			}
			if (getJMenuBar() != null) {
				if (isMenuBarVisible() && visible) {
					getJMenuBar().setVisible(true);
				} else {
					getJMenuBar().setVisible(false);
				}
			}
			revalidate();
		}
	}

	private static ActionMenu getActionMenu(Iterator<ActionMenu> actionMenus, String actionMenuName) {
		while (actionMenus.hasNext()) {
			ActionMenu a = actionMenus.next();
			if (a.getName().equals(actionMenuName)) {
				return a;
			}
		}
		return null;
	}

	public static Option doShowMessage(final Component parent, final String title, final String mesg, final Throwable exception,
			final int messageType, Option[] options, Option defaultOption) {
		boolean details = false;
		Option hideDetails = new Option(Messages.getString("SshToolsApplicationPanel.DetailsHide"),
			Messages.getString("SshToolsApplicationPanel.DetailsHide"), 'h');
		Option showDetails = new Option(Messages.getString("SshToolsApplicationPanel.Details"),
			Messages.getString("SshToolsApplicationPanel.Details"), 'd');
		Option ok = Option.CHOICE_OK;
		while (true) {
			List<Option> optlist = new ArrayList<Option>();
			if (options != null) {
				for (int i = 0; i < options.length; i++) {
					optlist.add(options[i]);
				}
			}
			if (exception != null && DETAILED_ERROR_DIALOGS) {
				if (details) {
					optlist.add(hideDetails);
					if (defaultOption == null) {
						defaultOption = hideDetails;
					}
				} else {
					optlist.add(showDetails);
					if (defaultOption == null) {
						defaultOption = showDetails;
					}
				}
			}
			if (options == null) {
				optlist.add(ok);
			}
			if (defaultOption == null) {
				defaultOption = optlist.get(0);
			}
			StringBuffer buf = new StringBuffer();
			if (mesg != null) {
				buf.append(mesg);
			}
			appendException(exception, 0, buf, details);
			Option opt = OptionDialog.prompt(parent, messageType, title, buf.toString(),
				optlist.toArray(new Option[optlist.size()]), defaultOption);
			if (opt == hideDetails || opt == showDetails) {
				details = !details;
			} else {
				return opt;
			}
		}
	}

	protected static void appendException(Throwable exception, int level, StringBuffer buf, boolean details) {
		try {
			if (((exception != null) && (exception.getMessage() != null)) && (exception.getMessage().length() > 0)) {
				if (details && (level > 0)) {
					buf.append("\n \nCaused by ...\n");
				}
				buf.append(exception.getMessage());
			}
			if (details) {
				if (exception != null) {
					if ((exception.getMessage() != null) && (exception.getMessage().length() == 0)) {
						buf.append("\n \nCaused by ...");
					} else {
						buf.append("\n \n");
					}
				}
				StringWriter sw = new StringWriter();
				if (exception != null) {
					exception.printStackTrace(new PrintWriter(sw));
				}
				buf.append(sw.toString());
			}
			try {
				java.lang.reflect.Method method = exception.getClass().getMethod("getCause", new Class[] {});
				Throwable cause = (Throwable) method.invoke((Object) exception, (Object[]) null);
				if (cause != null) {
					appendException(cause, level + 1, buf, details);
				}
			} catch (Exception e) {
			}
		} catch (Throwable ex) {
		}
	}

	/**
	 * Set the title of the container
	 * 
	 * @param file
	 */
	public void setContainerTitle(File file) {
		String verString = "";
		if (application != null) {
			verString = GeneralUtil.getVersionString(application.getApplicationName(), application.getClass());
		}
		if (container != null) {
			container.setContainerTitle((file == null) ? verString : (verString + " [" + file.getName() + "]"));
		}
	}

	/**
	 * Gets the toolbar
	 * 
	 * @return
	 */
	public JToolBar getToolBar() {
		return toolBar;
	}

	/**
	 * Get the context menu
	 * 
	 * @return
	 */
	public JPopupMenu getContextMenu() {
		return contextMenu;
	}

	/**
	 * Get the main menu
	 * 
	 * @return
	 */
	public JMenuBar getJMenuBar() {
		return menuBar;
	}

	/**
	 * Get the application attached to the panel
	 * 
	 * @return
	 */
	public SshToolsApplication getApplication() {
		return application;
	}

	/**
	 * Get the icon for the panel
	 * 
	 * @return
	 */
	public abstract Icon getIcon();

	class ConnectionFileFilter extends javax.swing.filechooser.FileFilter {
		public boolean accept(File f) {
			return f.isDirectory() || f.getName().toLowerCase().endsWith(".xml");
		}

		public String getDescription() {
			return Messages.getString("SshToolsApplicationPanel.ConnFiles") + " (*.xml)";
		}
	}

	public void setOptionsActionAvailable(boolean available) {
		optionsActionAvailable = available;
	}

	protected String getDefaultChooserDir(String pref) {
		return PreferencesStore.get(pref, getApplication().getApplicationPreferencesDirectory().getAbsolutePath());
	}

	class SshToolsApplicationPanelActionBuilder extends ActionBuilder {

		public SshToolsApplicationPanelActionBuilder(JMenuBar menuBar, ActionToolBar toolBar, JPopupMenu contextMenu) {
			super(menuBar, toolBar, contextMenu);
		}

		public Collection<ActionMenu> listActionMenus() {
			Map<String, ActionMenu> map = new HashMap<String, ActionMenu>();
			for (ActionMenu menu : actionMenus) {
				map.put(menu.getName(), menu);

			}
			Collection<ActionMenu> additionalActionMenus = getAdditionalActionMenus();
			if (additionalActionMenus != null) {
				for (ActionMenu menu : additionalActionMenus) {
					if (!map.containsKey(menu.getName())) {
						map.put(menu.getName(), menu);
					}
				}
			}
			return map.values();
		}

		public Collection<AppAction> listActions() {
			List<AppAction> allActions = new ArrayList<AppAction>();
			allActions.addAll(actions);
			Collection<AppAction> additionalActions = getAdditionalActions();
			if (additionalActions != null) {
				allActions.addAll(additionalActions);
			}
			return allActions;
		}

		public boolean isActionVisible(String name) {
			Boolean s = actionsVisible.get(name);
			return s == null || s.booleanValue();
		}

		public void resetActionState() {
			setAvailableActions();
		}
	}
}