/* HEADER */
package com.sshtools.appframework.ui;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import com.sshtools.ui.swing.ComboBoxRenderer;

import net.miginfocom.swing.MigLayout;

/**
 * Swing component implementation of a {@link OptionsTab} that can be used to
 * change global settings in the J2SSH Application Framework.
 * 
 * @author $Author: brett $
 */
public class GlobalOptionsTab extends JPanel implements OptionsTab {
	private final static Icon GLOBAL_ICON = IconStore.getInstance().getIcon("applications-internet", 24);
	// Private statics
	private final static Icon LARGE_GLOBAL_ICON = IconStore.getInstance().getIcon("applications-internet", 32);
	private static final long serialVersionUID = 1L;
	private SshToolsApplication application;
	// Private instance variables.
	private JComboBox<UIManager.LookAndFeelInfo> lafChooser;
	private JCheckBox stayRunning = new JCheckBox("Stay running on closing last window");
	private JCheckBox toolBarShowSelectiveText = new JCheckBox(Messages.getString("GlobalOptionsTab.SelectiveText"));
	private JCheckBox toolBarSmallIcons = new JCheckBox(Messages.getString("GlobalOptionsTab.SmallIcons"));
	private JCheckBox useSystemIconTheme = new JCheckBox(Messages.getString("GlobalOptionsTab.UseSystemIconTheme"));
	private JCheckBox wrapToolBar = new JCheckBox("Wrap tool bar icons");

	public GlobalOptionsTab(SshToolsApplication application) {
		super();
		this.application = application;
		setLayout(new MigLayout("wrap 2, fillx", "[][grow]", "[][][][][][]"));
		add(new JLabel(Messages.getString("GlobalOptionsTab.LAF")));
		lafChooser = new JComboBox<UIManager.LookAndFeelInfo>(new LookAndFeelModel());
		lafChooser.setRenderer(new ComboBoxRenderer<UIManager.LookAndFeelInfo>(lafChooser) {
			@Override
			protected void decorate(JLabel label, JList<? extends LookAndFeelInfo> list, LookAndFeelInfo value, int index,
					boolean isSelected, boolean cellHasFocus) {
				label.setText(value.getName());
			}
		});
		add(lafChooser, "growx");
		toolBarSmallIcons.setMnemonic('i');
		add(toolBarSmallIcons, "span 2, gapleft 32");
		useSystemIconTheme.setMnemonic('y');
		add(useSystemIconTheme, "span 2, gapleft 32");
		toolBarShowSelectiveText.setMnemonic('s');
		add(toolBarShowSelectiveText, "span 2, gapleft 32");
		wrapToolBar.setMnemonic('w');
		add(wrapToolBar, "span 2, gapleft 32");
		stayRunning.setMnemonic('r');
		add(stayRunning, "span 2, gapleft 32");
		reset();
	}

	@Override
	public void applyTab() {
		UIManager.LookAndFeelInfo laf = (UIManager.LookAndFeelInfo) lafChooser.getSelectedItem();
		String newLaf = laf == null ? "" : laf.getClassName();
		boolean changed = false;
		if (!newLaf.equals(PreferencesStore.get(SshToolsApplication.PREF_LAF, ""))) {
			PreferencesStore.put(SshToolsApplication.PREF_LAF, newLaf);
			changed = true;
		}
		PreferencesStore.putBoolean(SshToolsApplication.PREF_TOOLBAR_SHOW_SELECTIVE_TEXT, toolBarShowSelectiveText.isSelected());
		PreferencesStore.putBoolean(SshToolsApplication.PREF_TOOLBAR_SMALL_ICONS, toolBarSmallIcons.isSelected());
		PreferencesStore.putBoolean(SshToolsApplication.PREF_USE_SYSTEM_ICON_THEME, useSystemIconTheme.isSelected());
		PreferencesStore.putBoolean(SshToolsApplication.PREF_TOOLBAR_WRAP, wrapToolBar.isSelected());
		PreferencesStore.putBoolean(SshToolsApplication.PREF_STAY_RUNNING, stayRunning.isSelected());
		if (changed) {
			application.setLookAndFeel(laf);
		}
	}

	@Override
	public String getTabCategory() {
		return "General";
	}

	@Override
	public Component getTabComponent() {
		return this;
	}

	@Override
	public Icon getTabIcon() {
		return GLOBAL_ICON;
	}

	@Override
	public Icon getTabLargeIcon() {
		return LARGE_GLOBAL_ICON;
	}

	@Override
	public int getTabMnemonic() {
		return 'g';
	}

	@Override
	public String getTabTitle() {
		return Messages.getString("GlobalOptionsTab.Title");
	}

	@Override
	public String getTabToolTipText() {
		return Messages.getString("GlobalOptionsTab.Tooltip");
	}

	@Override
	public void reset() {
		LookAndFeelModel model = (LookAndFeelModel) lafChooser.getModel();
		lafChooser.setSelectedItem(model.getElementForName(
				PreferencesStore.get(SshToolsApplication.PREF_LAF, UIManager.getLookAndFeel().getClass().getName())));
		toolBarShowSelectiveText
				.setSelected(PreferencesStore.getBoolean(SshToolsApplication.PREF_TOOLBAR_SHOW_SELECTIVE_TEXT, true));
		toolBarSmallIcons.setSelected(PreferencesStore.getBoolean(SshToolsApplication.PREF_TOOLBAR_SMALL_ICONS, false));
		useSystemIconTheme.setSelected(PreferencesStore.getBoolean(SshToolsApplication.PREF_USE_SYSTEM_ICON_THEME, true));
		wrapToolBar.setSelected(PreferencesStore.getBoolean(SshToolsApplication.PREF_TOOLBAR_WRAP, false));
		stayRunning.setSelected(PreferencesStore.getBoolean(SshToolsApplication.PREF_STAY_RUNNING, false));
	}

	@Override
	public void tabSelected() {
	}

	@Override
	public boolean validateTab() {
		return true;
	}
}