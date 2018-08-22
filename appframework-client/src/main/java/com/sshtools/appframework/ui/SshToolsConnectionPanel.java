/**
 * Maverick Client Application Framework - Framework for 'client' applications.
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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;

import com.sshtools.appframework.api.ui.AbstractSshToolsApplicationClientPanel;
import com.sshtools.appframework.api.ui.SshToolsConnectionTab;
import com.sshtools.appframework.util.IOUtil;
import com.sshtools.profile.ConnectionManager;
import com.sshtools.profile.ProfileTransport;
import com.sshtools.profile.ResourceProfile;
import com.sshtools.profile.SchemeHandler;
import com.sshtools.profile.SchemeOptions;
import com.sshtools.profile.URI;
import com.sshtools.profile.URI.MalformedURIException;
import com.sshtools.ui.Option;
import com.sshtools.ui.swing.FontUtil;
import com.sshtools.ui.swing.SideBarTabber;
import com.sshtools.ui.swing.UIUtil;

import net.miginfocom.swing.MigLayout;
import plugspud.ArrowIcon;
import plugspud.ToolButton;

/**
 * <p>
 * Swing component allows GUIcreation and editing of {@link ResourceProfile}
 * which can then be used to connect to a host.
 * </p>
 * 
 * <p>
 * All of the registered {@link ProfileTransport} implementations are
 * provided so the user can select the scheme to use. If the scheme has any
 * protocol specific options, then an appropriate GUI component will be shown so
 * the user can change those options.
 * </p>
 */
@SuppressWarnings("serial")
public class SshToolsConnectionPanel extends JPanel implements ActionListener {
	abstract class HoverSchemeSelectionPanel extends JPanel {
		private final static String debug = "";
		private ArrowIcon arrowIcon;
		private Map<String, JComponent> categories = new HashMap<String, JComponent>();
		private Map<String, JComponent> categoryButtons = new HashMap<String, JComponent>();
		private JPanel categorySelection;
		private boolean inComponent = false;
		private JPanel schemeSelection;
		private List<SchemeSettings> showSchemes = new ArrayList<SchemeSettings>();
		private Timer timer;

		HoverSchemeSelectionPanel() {
			super(new MigLayout(debug + "ins 0, gap 0, hidemode 1", "[fill,grow]", ""));
			timer = new Timer(2000, new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					if (!inComponent) {
						setCategory(sel.handler.getCategory());
					} else {
						timer.restart();
					}
				}
			});
			addMouseListener(new MouseAdapter() {
				@Override
				public void mouseEntered(MouseEvent e) {
					setInComponent(true);
				}

				@Override
				public void mouseExited(MouseEvent e) {
					setInComponent(false);
				}
			});
			arrowIcon = new ArrowIcon(SwingConstants.SOUTH, UIManager.getColor("Label.foreground"),
					UIManager.getColor("Label.foreground"), UIManager.getColor("Label.foreground")) {
				@Override
				public int getIconHeight() {
					return 12;
				}

				@Override
				public int getIconWidth() {
					return 12;
				};
			};
			categorySelection = new JPanel();
			categorySelection.setLayout(new MigLayout(debug + "ins 0, gap 0", "[grow,fill]", ""));
			schemeSelection = new JPanel();
			schemeSelection.setLayout(new MigLayout(debug + "ins 0, gap 0", "[grow,fill]", "[][8!]"));
			add(categorySelection, "wrap, growx");
			add(schemeSelection, "wrap, growx");
			changed();
		}

		void changed() {
			categorySelection.invalidate();
			categorySelection.removeAll();
			categories.clear();
			categoryButtons.clear();
			if (schemes != null) {
				Map<String, SchemeSettings> map = new HashMap<String, SchemeSettings>();
				for (SchemeSettings s : schemes) {
					SshToolsSchemeHandler<?> ssht = s.handler;
					if (!ssht.isInternal()) {
						map.put(ssht.getCategory(), s);
					}
				}
				List<String> l = new ArrayList<String>(map.keySet());
				Collections.sort(l);
				for (Iterator<String> it = l.iterator(); it.hasNext();) {
					String s = it.next();
					final SchemeSettings ssht = map.get(s);
					ToolButton toolButton = new ToolButton(new AbstractAction(ssht.handler.getCategory(), ssht.handler.getIcon()) {
						@Override
						public void actionPerformed(ActionEvent arg0) {
							String category = ssht.handler.getCategory();
							setSchemesForCategory(category);
							int idx = sel == null ? -1 : showSchemes.indexOf(sel);
							if (idx == -1) {
								idx = 0;
							} else if (idx >= showSchemes.size() - 1) {
								idx = 0;
							} else {
								idx++;
							}
							sel = showSchemes.get(idx);
							setCategory(category);
							schemeSelected(sel);
						}
					});
					toolButton.addMouseListener(new MouseAdapter() {
						@Override
						public void mouseEntered(MouseEvent e) {
							setInComponent(true);
							setCategory(ssht.handler.getCategory());
						}

						@Override
						public void mouseExited(MouseEvent e) {
							setInComponent(false);
						}
					});
					toolButton.setHideText(false);
					if (it.hasNext()) {
						categorySelection.add(toolButton, "growx");
					} else {
						categorySelection.add(toolButton, "growx, wrap");
					}
					categoryButtons.put(s, toolButton);
				}
				for (Iterator<String> it = l.iterator(); it.hasNext();) {
					String s = it.next();
					JLabel jLabel = new JLabel(arrowIcon);
					if (it.hasNext()) {
						categorySelection.add(jLabel, "growx");
					} else {
						categorySelection.add(jLabel, "growx, wrap");
					}
					categories.put(s, jLabel);
				}
				setCategory(((SshToolsSchemeHandler<?>) ConnectionManager.getInstance().getSchemeHandler(0)).getCategory());
			}
			categorySelection.setVisible(categories.size() > 1);
			categorySelection.validate();
			categorySelection.repaint();
		}

		abstract void schemeSelected(SchemeSettings ssht);

		void setCategory(final String category) {
			schemeSelection.invalidate();
			schemeSelection.removeAll();
			for (Map.Entry<String, JComponent> ent : categories.entrySet()) {
				ent.getValue().setVisible(ent.getKey().equals(category));
			}
			for (Map.Entry<String, JComponent> ent : categoryButtons.entrySet()) {
				if (ent.getKey().equals(category)) {
					ent.getValue().setFont(FontUtil.getUIManagerButtonFontOrDefault("Button.Font").deriveFont(Font.BOLD));
				} else {
					ent.getValue().setFont(FontUtil.getUIManagerButtonFontOrDefault("Button.Font"));
				}
			}
			setSchemesForCategory(category);
			for (Iterator<SchemeSettings> it = showSchemes.iterator(); it.hasNext();) {
				final SchemeSettings settings = it.next();
				ToolButton toolButton = new ToolButton(new AbstractAction(settings.handler.getDescription()) {
					@Override
					public void actionPerformed(ActionEvent arg0) {
						schemeSelected(settings);
						setCategory(category);
					}
				}, false);
				if (settings == sel) {
					toolButton.setFont(FontUtil.getUIManagerButtonFontOrDefault("Button.Font").deriveFont(Font.BOLD));
				} else {
					toolButton.setFont(FontUtil.getUIManagerButtonFontOrDefault("Button.Font"));
				}
				if (it.hasNext()) {
					schemeSelection.add(toolButton, "growx");
				} else {
					schemeSelection.add(toolButton, "growx, wrap");
				}
			}
			for (Iterator<SchemeSettings> it = showSchemes.iterator(); it.hasNext();) {
				final SchemeSettings settings = it.next();
				JLabel c = settings == sel ? new JLabel(arrowIcon) : new JLabel();
				c.setHorizontalAlignment(SwingConstants.CENTER);
				if (it.hasNext()) {
					schemeSelection.add(c, "growx");
				} else {
					schemeSelection.add(c, "growx, wrap");
				}
			}
			schemeSelection.setVisible(categories.size() != 1 || showSchemes.size() > 1);
			schemeSelection.validate();
			schemeSelection.repaint();
		}

		void setInComponent(boolean inComponent) {
			this.inComponent = inComponent;
			timer.restart();
		}

		private void setSchemesForCategory(final String category) {
			showSchemes.clear();
			for (final SchemeSettings s : schemes) {
				final SshToolsSchemeHandler<?> ssht = s.handler;
				if (!ssht.isInternal() && ssht.getCategory().equals(category)) {
					showSchemes.add(s);
				}
			}
		}
	}

	class SchemeComparator implements Comparator<SchemeSettings> {
		@Override
		public int compare(SchemeSettings handler1, SchemeSettings handler2) {
			int i1 = handler1.handler.getCategory() == null && handler2.handler.getCategory() != null ? -1
					: (handler2.handler.getCategory() == null && handler1.handler.getCategory() != null ? 1
							: handler1.handler.getCategory().compareTo(handler2.handler.getCategory()));
			return i1 == 0 ? new Integer(handler1.handler.getWeight()).compareTo(new Integer(handler2.handler.getWeight())) : i1;
		}
	}

	class SchemeSettings {
		SshToolsSchemeHandler<ProfileTransport<?>> handler;
		Map<Class<? extends SchemeOptions>, SchemeOptions> options = new HashMap<Class<? extends SchemeOptions>, SchemeOptions>();
		List<SshToolsConnectionTab<? extends ProfileTransport<?>>> tabs;
		URI uri;

		@SuppressWarnings("unchecked")
		SchemeSettings(ResourceProfile<? extends ProfileTransport<?>> profile2) throws IllegalArgumentException {
			handler = (SshToolsSchemeHandler<ProfileTransport<?>>) ConnectionManager.getInstance()
					.getSchemeHandler(profile2.getURI().getScheme());
			uri = profile2.getURI();
			options.clear();
			addProfileSchemes();
			tabs = handler.createTabs();
		}

		SchemeSettings(SshToolsSchemeHandler<ProfileTransport<?>> handler) throws IllegalArgumentException {
			this.handler = handler;
		}

		public Map<Class<? extends SchemeOptions>, SchemeOptions> getSchemeOptions() {
			checkLoaded();
			return options;
		}

		public List<SshToolsConnectionTab<? extends ProfileTransport<?>>> getTabs() {
			checkLoaded();
			return tabs;
		}

		private void addProfileSchemes() {
			for (SchemeOptions s : profile.getSchemeOptionsList()) {
				options.put(s.getClass(), s);
			}
		}

		@SuppressWarnings("unchecked")
		private void checkLoaded() {
			if (tabs == null) {
				tabs = handler.createTabs();
				options.clear();
				try {
					uri = new URI(handler.getName() + "://");
				} catch (MalformedURIException e) {
					throw new RuntimeException(e);
				}
				if (newProfile) {
					File f = new File(SshToolsApplication.getInstance().getApplicationPreferencesDirectory(),
							handler.getName() + "-default.xml");
					if (f.exists()) {
						InputStream in = null;
						try {
							in = new FileInputStream(f);
							profile.load(in);
						} catch (IOException ioe) {
							/* DEBUG */ioe.printStackTrace();
						} finally {
							IOUtil.closeStream(in);
						}
					}
				}
				addProfileSchemes();
				for (SchemeOptions s : handler.createMultipleSchemeOptions()) {
					if (!options.containsKey(s.getClass())) {
						options.put(s.getClass(), s);
						profile.setSchemeOptions(s);
					}
				}
				for (@SuppressWarnings("rawtypes")
				SshToolsConnectionTab t : tabs) {
					t.setConnectionProfile(profile);
				}
			}
		}
	}

	public static final Dimension DEFAULT_SIZE = new Dimension(620, 600);

	public static ResourceProfile<?> showConnectionDialog(Component parent,
			List<SshToolsConnectionTab<ProfileTransport<?>>> optionalTabs) {
		return showConnectionDialog(parent, null, optionalTabs);
	}
	public static ResourceProfile<? extends ProfileTransport<?>> showConnectionDialog(Component parent,
			ResourceProfile<? extends ProfileTransport<?>> profile,
			List<SshToolsConnectionTab<ProfileTransport<?>>> optionalTabs2) {
		return showConnectionDialog(parent, profile, optionalTabs2, DEFAULT_SIZE);
	}
	public static ResourceProfile<? extends ProfileTransport<?>> showConnectionDialog(Component parent,
			ResourceProfile<? extends ProfileTransport<?>> profile, List<SshToolsConnectionTab<ProfileTransport<?>>> optionalTabs2,
			Dimension size) {
		final SshToolsConnectionPanel conx = new SshToolsConnectionPanel(true, optionalTabs2);
		conx.setConnectionProfile(profile);
		profile = conx.getConnectionProfile();
		EscapeDialog d = null;
		Window w = (Window) SwingUtilities.getAncestorOfClass(Window.class, parent);
		if (w instanceof JDialog) {
			d = new EscapeDialog((JDialog) w, Messages.getString("SshToolsConnectionPanel.ConnProfile"), true);
		} else if (w instanceof JFrame) {
			d = new EscapeDialog((JFrame) w, Messages.getString("SshToolsConnectionPanel.ConnProfile"), true);
		} else {
			d = new EscapeDialog((JFrame) null, Messages.getString("SshToolsConnectionPanel.ConnProfile"), true);
		}
		final EscapeDialog dialog = d;
		class UserAction {
			boolean connect;
		}
		final UserAction userAction = new UserAction();
		// Create the bottom button panel
		final JButton cancel = new JButton(Messages.getString("Cancel"));
		cancel.setMnemonic('c');
		cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				dialog.setVisible(false);
			}
		});
		final JButton connect = new JButton(Messages.getString("Connect"));
		connect.setMnemonic('t');
		connect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				if (conx.validateTabs()) {
					userAction.connect = true;
					dialog.setVisible(false);
				}
			}
		});
		final JButton setDefault = new JButton(Messages.getString("SetDefault"));
		setDefault.setMnemonic('s');
		setDefault.setToolTipText(Messages.getString("SetDefault.ToolTip"));
		setDefault.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				Option[] opts = new Option[] { Option.CHOICE_YES, Option.CHOICE_NO };
				SchemeSettings settings = conx.sel;
				String scheme = settings.handler.getName();
				if (TellMeAgainPane.showTellMeAgainDialog(conx, Messages.getString("Connect.SetDefault.CheckBoxText"),
						"sshtools.warnAboutSettingDefault",
						MessageFormat.format(Messages.getString("Connect.SetDefault.Text"), new Object[] { scheme }), opts,
						Messages.getString("Connect.SetDefault.Title"),
						UIManager.getIcon("OptionPane.warningIcon")) != Option.CHOICE_NO) {
					ResourceProfile<?> profile = new ResourceProfile<>();
					File f = new File(SshToolsApplication.getInstance().getApplicationPreferencesDirectory(),
							scheme + "-default.xml");
					OutputStream out = null;
					try {
						URI uri = new URI(scheme, "", null, null, null);
						profile.setURI(uri);
						conx.applyTabs(profile, settings);
						out = new FileOutputStream(f);
						profile.save(out);
					} catch (IOException ioe) {
						/* DEBUG */ioe.printStackTrace();
					} finally {
						IOUtil.closeStream(out);
					}
				}
			}
		});
		dialog.getRootPane().setDefaultButton(connect);
		JPanel buttonPanel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.insets = new Insets(6, 6, 0, 0);
		gbc.weighty = 1.0;
		UIUtil.jGridBagAdd(buttonPanel, setDefault, gbc, 1);
		UIUtil.jGridBagAdd(buttonPanel, connect, gbc, GridBagConstraints.RELATIVE);
		UIUtil.jGridBagAdd(buttonPanel, cancel, gbc, GridBagConstraints.REMAINDER);
		JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		southPanel.add(buttonPanel);
		//
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		mainPanel.add(conx, BorderLayout.CENTER);
		mainPanel.add(southPanel, BorderLayout.SOUTH);
		// Show the dialog
		dialog.getContentPane().setLayout(new GridLayout(1, 1));
		dialog.getContentPane().add(mainPanel);
		dialog.setSize(size);
		dialog.setResizable(true);
		UIUtil.positionComponent(SwingConstants.CENTER, dialog);
		dialog.setVisible(true);
		if (!userAction.connect) {
			return null;
		}
		conx.applyTabs();
		return profile;
	}
	private ConnectionManager mgr;
	private boolean newProfile;
	private List<SshToolsConnectionTab<ProfileTransport<?>>> optionalTabs;
	private ResourceProfile<? extends ProfileTransport<?>> profile;
	private SchemeSettings[] schemes;

	private HoverSchemeSelectionPanel schemeSelector;

	private SchemeSettings sel;

	private SideBarTabber tabber;

	/**
	 * Creates a new SshToolsConnectionPanel object.
	 * 
	 * @param showConnectionTabs show tabs for editing the connection
	 * @param optionalTabs additional tabs
	 */
	public SshToolsConnectionPanel(boolean showConnectionTabs, List<SshToolsConnectionTab<ProfileTransport<?>>> optionalTabs) {
		super(new BorderLayout());
		this.optionalTabs = optionalTabs;
		mgr = ConnectionManager.getInstance();
		if (showConnectionTabs) {
			add(schemeSelector = new HoverSchemeSelectionPanel() {
				@Override
				void schemeSelected(SchemeSettings ssht) {
					sel = ssht;
					showTabsForScheme();
				}
			}, BorderLayout.NORTH);
		}
		tabber = new SideBarTabber();
		tabber.setFixedToolBarWidth(72);
		JPanel p = new JPanel(new GridLayout());
		p.add(tabber.getComponent());
		p.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
		add(p, BorderLayout.CENTER);
		if (optionalTabs != null) {
			for (SshToolsConnectionTab<ProfileTransport<?>> tab : optionalTabs) {
				tabber.addTab(tab);
			}
		}
		setConnectionProfile(null);
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		showTabsForScheme();
	}

	public void addTab(SshToolsConnectionTab<?> tab) {
		tabber.addTab(tab);
	}

	public void applyTabs() {
		SchemeSettings settings = getSelectedSchemeSettings();
		if (settings != null) {
			applyTabs(profile, getSelectedSchemeSettings());
			PreferencesStore.put(AbstractSshToolsApplicationClientPanel.PREF_DEFAULT_SCHEME_NAME, settings.handler.getName());
		}
	}

	public ResourceProfile<? extends ProfileTransport<?>> getConnectionProfile() {
		return profile;
	}

	public void setConnectionProfile(ResourceProfile<? extends ProfileTransport<?>> profile) {
		// If null is supplied, the we need to create a profile
		if (profile == null) {
			profile = new ResourceProfile<>();
			String defaultSchemeName = PreferencesStore.get(AbstractSshToolsApplicationClientPanel.PREF_DEFAULT_SCHEME_NAME,
					"ssh2");
			SchemeHandler<?> defaultHandler = mgr.getSchemeHandler(defaultSchemeName);
			if (defaultHandler == null) {
				defaultHandler = mgr.getSchemeHandlerCount() > 0 ? mgr.getSchemeHandler(0) : null;
			}
			if (defaultHandler != null) {
				try {
					URI uri = new URI(defaultHandler.getName(), "", null, null, null);
					profile.setURI(uri);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (schemeSelector != null) {
				schemeSelector.setEnabled(true);
			}
			newProfile = true;
		} else if (schemeSelector != null) {
			schemeSelector.setEnabled(false);
			newProfile = false;
		} else {
			newProfile = false;
		}
		this.profile = profile;
		doSetProfile(profile);
		if (schemeSelector != null && sel != null) {
			schemeSelector.setCategory(sel.handler.getCategory());
		}
		showTabsForScheme();
	}

	public boolean validateTabs() {
		return tabber.validateTabs();
	}

	protected void applyTabs(ResourceProfile<?> profile, SchemeSettings settings) {
		if (schemeSelector != null) {
			try {
				profile.getURI().setScheme(settings.handler.getName());
			} catch (MalformedURIException e) {
			}
			for (SchemeOptions sopt : settings.getSchemeOptions().values()) {
				profile.setSchemeOptions(sopt);
			}
		}
		tabber.applyTabs();
	}

	void doSetProfile(ResourceProfile<? extends ProfileTransport<?>> profile2) {
		List<SchemeSettings> v = new ArrayList<SchemeSettings>();
		sel = null;
		if (newProfile) {
			for (int i = 0; i < mgr.getSchemeHandlerCount(); i++) {
				@SuppressWarnings("unchecked")
				SchemeHandler<ProfileTransport<?>> handler = (SchemeHandler<ProfileTransport<?>>) mgr.getSchemeHandler(i);
				if (!(handler instanceof SshToolsSchemeHandler)) {
					/* DEBUG */System.err
							.println("WARNING! SchemeHandler is not an instance of SshToolsSchemeHandler. Will be ignored.");
				} else {
					SchemeSettings settings = new SchemeSettings((SshToolsSchemeHandler<ProfileTransport<?>>) handler);
					if (handler.getName().equals(profile2.getURI().getScheme())) {
						sel = settings;
					}
					v.add(settings);
				}
			}
		} else {
			SchemeSettings settings = new SchemeSettings(profile2);
			sel = settings;
			v.add(settings);
		}
		Collections.sort(v, new SchemeComparator());
		schemes = new SchemeSettings[v.size()];
		v.toArray(schemes);
		if (sel == null && schemes.length > 0) {
			sel = schemes[0];
		}
		if (schemeSelector != null) {
			schemeSelector.changed();
		}
		if (optionalTabs != null) {
			for (SshToolsConnectionTab<ProfileTransport<?>> t : optionalTabs) {
				@SuppressWarnings("unchecked")
				// TODO ugh ... no idea
				ResourceProfile<ProfileTransport<?>> p2 = (ResourceProfile<ProfileTransport<?>>) profile;
				t.setConnectionProfile(p2);
			}
		}
	}

	private SchemeSettings getSelectedSchemeSettings() {
		return sel;
	}

	@SuppressWarnings("unchecked")
	private void showTabsForScheme() {
		SchemeSettings selected = getSelectedSchemeSettings();
		invalidate();
		tabber.removeAllTabs();
		if (profile != null) {
			List<SshToolsConnectionTab<? extends ProfileTransport<?>>> tabs = new ArrayList<>();
			if (selected != null && selected.getTabs() != null) {
				profile.setURI(selected.uri);
				for (SshToolsConnectionTab<?> tab : selected.getTabs()) {
					tabs.add(tab);
					tabber.addTab(tab);
				}
			}
			if (optionalTabs != null) {
				for (SshToolsConnectionTab<ProfileTransport<?>> tab : optionalTabs) {
					tabs.add(tab);
					tabber.addTab(tab);
				}
			}
			for (@SuppressWarnings("rawtypes")
			SshToolsConnectionTab t : tabs) {
				t.setConnectionProfile(profile);
			}
			if (tabber.getTabCount() > 0) {
				tabber.getTabAt(0).getTabComponent().requestFocusInWindow();
			}
		}
		validate();
		repaint();
	}
}