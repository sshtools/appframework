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
import java.awt.Font;
import java.awt.GridLayout;
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
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;

import org.kordamp.ikonli.carbonicons.CarbonIcons;

import com.sshtools.appframework.api.ui.AbstractSshToolsApplicationClientPanel;
import com.sshtools.appframework.api.ui.SshToolsConnectionTab;
import com.sshtools.appframework.ui.MessagePanel.Type;
import com.sshtools.appframework.util.IOUtil;
import com.sshtools.profile.ConnectionManager;
import com.sshtools.profile.ProfileTransport;
import com.sshtools.profile.ResourceProfile;
import com.sshtools.profile.SchemeHandler;
import com.sshtools.profile.SchemeOptions;
import com.sshtools.profile.URI;
import com.sshtools.profile.URI.MalformedURIException;
import com.sshtools.ui.Option;
import com.sshtools.ui.swing.EmptyIcon;
import com.sshtools.ui.swing.FontUtil;
import com.sshtools.ui.swing.ScrollingPanel;
import com.sshtools.ui.swing.ScrollingPanel.ButtonMode;
import com.sshtools.ui.swing.SideBarTabber;
import com.sshtools.ui.swing.TabValidationException;
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
 * All of the registered {@link ProfileTransport} implementations are provided
 * so the user can select the scheme to use. If the scheme has any protocol
 * specific options, then an appropriate GUI component will be shown so the user
 * can change those options.
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
		// private Flinger schemeSelection;
		private JPanel schemeSelection;
		private List<SchemeSettings> showSchemes = new ArrayList<SchemeSettings>();
		private Timer timer;

		HoverSchemeSelectionPanel() {
			super(new MigLayout(debug + "ins 0, gap 0, hidemode 1", "[fill]", ""));
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
					return 16;
				}

				@Override
				public int getIconWidth() {
					return 16;
				};
			};
			categorySelection = new JPanel();
			categorySelection.setLayout(new MigLayout(debug + "ins 0, gap 0", "push[][][]push", ""));
			schemeSelection = new JPanel();
			// schemeSelection = new Flinger();
			schemeSelection.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseEntered(MouseEvent e) {
					setInComponent(true);
				}

				@Override
				public void mouseExited(MouseEvent e) {
					setInComponent(false);
				}
			});
			schemeSelection.setLayout(new MigLayout(debug + "ins 0, gap 0", "[grow,fill]", "[][8!]"));
			add(categorySelection, "wrap, dock center");
			ScrollingPanel sp = new ScrollingPanel(schemeSelection, SwingConstants.HORIZONTAL);
			sp.setButtonMode(ButtonMode.VISIBILITY);
			sp.setBordersPainted(false);
			add(sp, "wrap, growx");
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
					ToolButton toolButton = new ToolButton(
							new AbstractAction(ssht.handler.getCategory(), ssht.handler.getLargeIcon()) {
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
					toolButton.setHorizontalTextPosition(SwingConstants.CENTER);
					toolButton.setVerticalTextPosition(SwingConstants.BOTTOM);
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
						categorySelection.add(toolButton, "w 160");
					} else {
						categorySelection.add(toolButton, "w 160, wrap");
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
			// schemeSelection.content.invalidate();
			// schemeSelection.content.removeAll();
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
				ToolButton toolButton = new ToolButton(
						new AbstractAction(settings.handler.getDescription(), settings.handler.getMediumIcon()) {
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
					// schemeSelection.content.add(toolButton, "growx");
					schemeSelection.add(toolButton, "growx");
				} else {
					// schemeSelection.content.add(toolButton, "growx, wrap");
					schemeSelection.add(toolButton, "growx, wrap");
				}
				toolButton.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseEntered(MouseEvent e) {
						setInComponent(true);
					}

					@Override
					public void mouseExited(MouseEvent e) {
						setInComponent(false);
					}
				});
			}
			for (Iterator<SchemeSettings> it = showSchemes.iterator(); it.hasNext();) {
				final SchemeSettings settings = it.next();
				JLabel c = settings == sel ? new JLabel(arrowIcon) : new JLabel();
				c.setHorizontalAlignment(SwingConstants.CENTER);
				if (it.hasNext()) {
					// schemeSelection.content.add(c, "growx");
					schemeSelection.add(c, "growx");
				} else {
					schemeSelection.add(c, "growx, wrap");
					// schemeSelection.content.add(c, "growx, wrap");
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
			return i1 == 0 ? Integer.valueOf(handler1.handler.getWeight()).compareTo(Integer.valueOf(handler2.handler.getWeight())) : i1;
		}
	}

	class SchemeSettings {
		SshToolsSchemeHandler<ProfileTransport<?>> handler;
		Map<Class<? extends SchemeOptions>, SchemeOptions> options = new HashMap<Class<? extends SchemeOptions>, SchemeOptions>();
		List<SshToolsConnectionTab<ProfileTransport<?>>> tabs;
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

		public List<SshToolsConnectionTab<ProfileTransport<?>>> getTabs() {
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

	public static final Dimension DEFAULT_SIZE = new Dimension(640, 740);

	public static ResourceProfile<?> showConnectionDialog(Component parent,
			List<SshToolsConnectionTab<? extends ProfileTransport<?>>> optionalTabs) {
		return showConnectionDialog(parent, null, optionalTabs);
	}

	public static ResourceProfile<ProfileTransport<?>> showConnectionDialog(Component parent,
			ResourceProfile<ProfileTransport<?>> profile, List<SshToolsConnectionTab<? extends ProfileTransport<?>>> optionalTabs2) {
		return showConnectionDialog(parent, profile, optionalTabs2, DEFAULT_SIZE);
	}

	public static ResourceProfile<ProfileTransport<?>> showConnectionDialog(Component parent,
			ResourceProfile<ProfileTransport<?>> origProfile, List<SshToolsConnectionTab<? extends ProfileTransport<?>>> optionalTabs,
			Dimension size) {
		final SshToolsConnectionPanel conx = new SshToolsConnectionPanel(true, optionalTabs);
		conx.setConnectionProfile(origProfile);
		ResourceProfile<ProfileTransport<?>> profile = conx.getConnectionProfile();
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
			boolean save;
		}
		final UserAction userAction = new UserAction();
		// Create the bottom button panel
		final JButton connect = new JButton(Messages.getString("Connect"),
				IconStore.getInstance().icon(CarbonIcons.PLUG_FILLED, 24));
		connect.setMnemonic('t');
		connect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				if (conx.validateTabs()) {
					try {
						conx.applyTabs();
						userAction.connect = true;
						if (userAction.save)
							profile.setNeedSave(true);
						dialog.setVisible(false);
					} catch (TabValidationException tve) {
					}
				}
			}
		});
		final JButton save = new JButton(Messages.getString("Save"), IconStore.getInstance().icon(CarbonIcons.SAVE, 24));
		save.setMnemonic('s');
		save.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				if (conx.validateTabs()) {
					try {
						conx.applyTabs();
						userAction.save = true;
						if (userAction.save)
							profile.setNeedSave(true);
						dialog.setVisible(false);
					} catch (TabValidationException tve) {
					}
				}
			}
		});
		JCheckBox advancedSelect = new JCheckBox(Messages.getString("Advanced"));
		final JButton setDefault = new JButton(Messages.getString("SetDefault"), new EmptyIcon(1, 24));
		advancedSelect.setOpaque(false);
		advancedSelect.setMnemonic('a');
		advancedSelect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				conx.setAdvanced(!conx.isAdvanced());
				setDefault.setVisible(conx.isAdvanced());
			}
		});
		setDefault.setVisible(false);
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
		JPanel buttonPanel = new JPanel(new MigLayout("", "[fill, grow][][][]", "[]"));
		buttonPanel.add(advancedSelect, "growx");
		buttonPanel.add(setDefault);
		buttonPanel.add(save);
		buttonPanel.add(connect);
		//
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		mainPanel.add(conx, BorderLayout.CENTER);
		mainPanel.add(buttonPanel, BorderLayout.SOUTH);
		// Show the dialog
		dialog.getContentPane().setLayout(new GridLayout(1, 1));
		dialog.getContentPane().add(mainPanel);
		if(size != null)
			dialog.setSize(size);
		dialog.setResizable(true);
		UIUtil.positionComponent(SwingConstants.CENTER, dialog, SwingUtilities.getWindowAncestor(parent));
		dialog.setVisible(true);
		if (!userAction.connect && !userAction.save) {
			return null;
		}
		return profile;
	}

	private boolean advanced;
	private ConnectionManager mgr;
	private boolean newProfile;
	private List<SshToolsConnectionTab<? extends ProfileTransport<?>>> optionalTabs;
	private ResourceProfile<ProfileTransport<?>> profile;
	private SchemeSettings[] schemes;
	private HoverSchemeSelectionPanel schemeSelector;
	private SchemeSettings sel;
	private SideBarTabber tabber;
	private JPanel container;
	private JPanel content;
	private SshToolsConnectionTab<ProfileTransport<?>> singleTab;
	private TabValidationHelper tabValidationHelper = new TabValidationHelper();
	private MessagePanel messages;

	/**
	 * Creates a new SshToolsConnectionPanel object.
	 * 
	 * @param showConnectionTabs show tabs for editing the connection
	 * @param optionalTabs additional tabs
	 */
	public SshToolsConnectionPanel(boolean showConnectionTabs, List<SshToolsConnectionTab<? extends ProfileTransport<?>>> optionalTabs) {
		super(new BorderLayout());
		this.optionalTabs = optionalTabs;
		mgr = ConnectionManager.getInstance();
		content = new JPanel(new BorderLayout());
		messages = new MessagePanel(Type.hidden);
		if (showConnectionTabs) {
			if (schemeSelector == null)
				schemeSelector = new HoverSchemeSelectionPanel() {
					@Override
					void schemeSelected(SchemeSettings ssht) {
						sel = ssht;
						showTabsForScheme();
					}
				};
			add(schemeSelector, BorderLayout.NORTH);
		}
		add(content, BorderLayout.CENTER);
		resetPanel();
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

	public ResourceProfile<ProfileTransport<?>> getConnectionProfile() {
		return profile;
	}

	public void setConnectionProfile(ResourceProfile<ProfileTransport<?>> profile) {
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
		tabValidationHelper.clearErrors();
		try {
			if (advanced) {
				return tabber.validateTabs();
			} else if (singleTab != null) {
				return singleTab.validateTab();
			}
		} catch (TabValidationException tve) {
			tabValidationHelper.handleTabValidationException(tve);
		}
		return false;
	}

	protected void applyTabs(ResourceProfile<?> profile, SchemeSettings settings) {
		try {
			if (schemeSelector != null) {
				try {
					profile.getURI().setScheme(settings.handler.getName());
				} catch (MalformedURIException e) {
				}
				for (SchemeOptions sopt : settings.getSchemeOptions().values()) {
					profile.setSchemeOptions(sopt);
				}
			}
			if (advanced)
				tabber.applyTabs();
			else if (singleTab != null) {
				singleTab.applyTab();
			}
		} catch (TabValidationException tve) {
			tabValidationHelper.handleTabValidationException(tve);
			throw tve;
		}
	}

	public void setAdvanced(boolean advanced) {
		applyTabs();
		this.advanced = advanced;
		SchemeSettings settings = new SchemeSettings(profile);
		sel = settings;
		resetPanel();
		invalidate();
		doSetProfile(profile);
		if (schemeSelector != null) {
			schemeSelector.changed();
			schemeSelector.setInComponent(true);
			schemeSelector.setCategory(sel.handler.getCategory());
		}
		validate();
		repaint();
	}
	
	public boolean isAdvanced() {
		return advanced;
	}
	
	public List<SshToolsConnectionTab<? extends ProfileTransport<?>>> getTabsForSelected() {

		SchemeSettings selected = getSelectedSchemeSettings();
		List<SshToolsConnectionTab<? extends ProfileTransport<?>>> tabs = new ArrayList<>();
		if (selected != null && selected.getTabs() != null) {
			profile.setURI(selected.uri);
			for (SshToolsConnectionTab<?> tab : selected.getTabs()) {
				tabs.add(tab);
			}
		}
		if (optionalTabs != null) {
			for (SshToolsConnectionTab<? extends ProfileTransport<?>> tab : optionalTabs) {
				tabs.add(tab);
			}
		}
		return tabs;
	}

	void resetPanel() {
		tabValidationHelper.clearErrors();
		content.invalidate();
		content.removeAll();
		
		if (advanced) {
			if (container != null)
				container.removeAll();
			if (tabber == null) {
				tabber = new SideBarTabber(false);
				tabber.setButtonMode(ButtonMode.VISIBILITY_AND_SIZE);
			}
			JPanel p = new JPanel(new GridLayout());
			p.add(tabber.getComponent());
			p.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
			content.add(p, BorderLayout.CENTER);
			if (optionalTabs != null) {
				for (SshToolsConnectionTab<? extends ProfileTransport<?>> tab : optionalTabs) {
					tabber.addTab(tab);
				}
			}
		} else {
			if (tabber != null)
				tabber.removeAllTabs();
			container = new JPanel(new BorderLayout());
			content.add(container, BorderLayout.CENTER);
		}
		content.add(messages, BorderLayout.NORTH);
		content.validate();
		content.repaint();
		if (profile == null)
			setConnectionProfile(profile);
		else {
			showTabsForScheme();
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	void doSetProfile(ResourceProfile<? extends ProfileTransport<?>> profile2) {
		tabValidationHelper.clearErrors();
		List<SchemeSettings> v = new ArrayList<SchemeSettings>();
		sel = null;
		if (newProfile) {
			for (int i = 0; i < mgr.getSchemeHandlerCount(); i++) {
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
			for (SshToolsConnectionTab t : optionalTabs)
				t.setConnectionProfile(profile);
		}
	}

	private SchemeSettings getSelectedSchemeSettings() {
		return sel;
	}

	@SuppressWarnings("unchecked")
	private void showTabsForScheme() {
		tabValidationHelper.clearErrors();
		SchemeSettings selected = getSelectedSchemeSettings();
		invalidate();
		SshToolsSchemeHandler<ProfileTransport<?>> handler = selected == null ? null : sel.handler;
		if(handler != null && handler.isExperimental()) {
			messages.setType(Type.information);
			messages.setMessage("This is an experimental feature. Please report bugs to support@jadaptive.com");
		}
		else
			messages.setType(Type.hidden);
		singleTab = null;
		if (advanced) {
			tabber.removeAllTabs();
			if (profile != null) {
				for (@SuppressWarnings("rawtypes")
				SshToolsConnectionTab t : getTabsForSelected()) {
					tabber.addTab(t);
					t.setConnectionProfile(profile);
				}
				if (tabber.getTabCount() > 0) {
					tabber.getTabAt(0).getTabComponent().requestFocusInWindow();
				}
			}
		} else {
			container.removeAll();
			if (profile != null) {
				if (selected != null && selected.getTabs() != null) {
					profile.setURI(selected.uri);
					List<SshToolsConnectionTab<ProfileTransport<?>>> stabs = selected.getTabs();
					if (!stabs.isEmpty()) {
						singleTab = stabs.get(0);
						container.add(singleTab.getTabComponent());
						singleTab.setConnectionProfile(profile);
						singleTab.getTabComponent().requestFocusInWindow();
					}
				}
			}
		}
		validate();
		repaint();
	}
}