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
package com.sshtools.appframework.api.ui;

import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Comparator;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import com.sshtools.appframework.api.SshToolsApplicationException;
import com.sshtools.appframework.ui.Messages;
import com.sshtools.appframework.ui.PreferencesStore;
import com.sshtools.appframework.ui.SshToolsApplication;
import com.sshtools.appframework.ui.SshToolsClientApplication;
import com.sshtools.appframework.ui.SshToolsConnectionPanel;
import com.sshtools.appframework.ui.TabValidationHelper;
import com.sshtools.appframework.util.ApplicationException;
import com.sshtools.appframework.util.IOUtil;
import com.sshtools.profile.AuthenticationException;
import com.sshtools.profile.ProfileException;
import com.sshtools.profile.ProfileTransport;
import com.sshtools.profile.ResourceProfile;
import com.sshtools.ui.Option;
import com.sshtools.ui.OptionCallback;
import com.sshtools.ui.OptionChooser;
import com.sshtools.ui.swing.AppAction;
import com.sshtools.ui.swing.OptionDialog;
import com.sshtools.ui.swing.TabValidationException;
import com.sshtools.virtualsession.VirtualSession;

@SuppressWarnings("serial")
public abstract class AbstractSshToolsApplicationClientPanel<S extends VirtualSession<? extends ProfileTransport<?>, ?>>
		extends SshToolsApplicationPanel {
	class ConnectionFileFilter extends javax.swing.filechooser.FileFilter {
		@Override
		public boolean accept(File f) {
			return f.isDirectory() || f.getName().toLowerCase().endsWith(".xml");
		}

		@Override
		public String getDescription() {
			return Messages.getString("AbstractSshToolsApplicationClientPanel.ConnectionFiles") + " (*.xml)";
		}
	}

	class MenuItemActionComparator implements Comparator<AppAction> {
		@Override
		public int compare(AppAction o1, AppAction o2) {
			int i = ((Integer) o1.getValue(AppAction.MENU_ITEM_GROUP)).compareTo((Integer) o2.getValue(AppAction.MENU_ITEM_GROUP));
			return (i == 0) ? ((Integer) o1.getValue(AppAction.MENU_ITEM_WEIGHT))
					.compareTo((Integer) o2.getValue(AppAction.MENU_ITEM_WEIGHT)) : i;
		}
	}

	class ToolBarActionComparator implements Comparator<AppAction> {
		@Override
		public int compare(AppAction o1, AppAction o2) {
			int i = ((Integer) o1.getValue(AppAction.TOOLBAR_GROUP)).compareTo((Integer) o2.getValue(AppAction.TOOLBAR_GROUP));
			return (i == 0)
					? ((Integer) o1.getValue(AppAction.TOOLBAR_WEIGHT)).compareTo((Integer) o2.getValue(AppAction.TOOLBAR_WEIGHT))
					: i;
		}
	}

	//
	public final static int BANNER_TIMEOUT = 2000;
	public final static String PREF_CONNECTION_FILE_DIRECTORY = "sshapps.connectionFile.directory";
	public final static String PREF_DEFAULT_SCHEME_NAME = "sshapps.defaultSchemeName";
	protected javax.swing.filechooser.FileFilter connectionFileFilter = new ConnectionFileFilter();

	public AbstractSshToolsApplicationClientPanel() {
		super();
	}

	public AbstractSshToolsApplicationClientPanel(LayoutManager mgr) {
		super(mgr);
	}

	/**
	 * Close connection
	 * 
	 * @param disconnect
	 * @return closed OK
	 */
	public abstract boolean closeConnection(boolean disconnect);

	/**
	 * Connect.
	 * 
	 * @throws ApplicationException on application error
	 * @throws ProfileException on profile error
	 * @throws IOException on I/O error
	 * @throws AuthenticationException on authentication error
	 */
	public void connect() throws ApplicationException, ProfileException, IOException, AuthenticationException {
		if (getCurrentProfile() == null) {
			throw new ApplicationException("Can't connect, no connection profile have been set.");
		}
		// There isn't anywhere to store this setting yet
		connect(getCurrentProfile(), false);
	}

	public abstract S connect(final ResourceProfile<? extends ProfileTransport<?>> profile, final boolean newProfile)
			throws ApplicationException, ProfileException, IOException, AuthenticationException;

	public abstract S connect(ResourceProfile<? extends ProfileTransport<?>> profile, boolean newProfile, File sourceFile)
			throws ApplicationException, ProfileException, IOException, AuthenticationException;

	public void editConnection() {
		// Create a file chooser with the current directory set to the
		// application home
		JFileChooser fileDialog = new JFileChooser(PreferencesStore.get(PREF_CONNECTION_FILE_DIRECTORY,
				System.getProperty("sshtools.home", System.getProperty("user.home"))));
		fileDialog.setFileFilter(connectionFileFilter);
		// Show it
		int ret = fileDialog.showOpenDialog(this);
		// If we've approved the selection then process
		if (ret == JFileChooser.APPROVE_OPTION) {
			PreferencesStore.put(PREF_CONNECTION_FILE_DIRECTORY, fileDialog.getCurrentDirectory().getAbsolutePath());
			// Get the file
			File f = fileDialog.getSelectedFile();
			// Load the profile
			ResourceProfile<ProfileTransport<?>> p = new ResourceProfile<>();
			InputStream in = null;
			try {
				in = new FileInputStream(f);
				p.load(in);
				if (editConnection(p)) {
					saveConnection(false, f, p);
				}
			} catch (IOException ioe) {
				OptionDialog.error(this, Messages.getString("Error"),
						Messages.getString("AbstractSshToolsApplicationClientPanel.LoadFail"), ioe);
			} finally {
				IOUtil.closeStream(in);
			}
		}
	}

	/**
	 * 
	 * 
	 * @param profile
	 * @return applied
	 */
	public boolean editConnection(ResourceProfile<ProfileTransport<?>> profile) {
		final SshToolsConnectionPanel panel = new SshToolsConnectionPanel(allowConnectionSettingsEditing(),
				getAdditionalConnectionTabs());
		TabValidationHelper validationHelper = new TabValidationHelper();
		panel.setConnectionProfile(profile);
		OptionCallback callback = new OptionCallback() {
			@Override
			public boolean canClose(OptionChooser dialog, Option option) {
				if (Option.CHOICE_SAVE.equals(option)) {
					try {
						if (panel.validateTabs()) {
							panel.applyTabs();
							return true;
						}
					} catch (TabValidationException tve) {
						validationHelper.handleTabValidationException(tve);
					}
				} else
					return true;
				return false;
			}
		};
		JCheckBox advanced = null;
		if (panel.getTabsForSelected().size() > 1) {
			advanced = new JCheckBox(Messages.getString("Advanced"));
			advanced.setOpaque(false);
			advanced.setMnemonic('a');
			advanced.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					panel.setAdvanced(!panel.isAdvanced());
				}
			});
		}
		Option opt = OptionDialog.prompt(AbstractSshToolsApplicationClientPanel.this, OptionChooser.UNCATEGORISED,
				Messages.getString("AbstractSshToolsApplicationClientPanel.ConnSettings"), panel, Option.CHOICES_SAVE_CANCEL,
				Option.CHOICE_SAVE, callback, advanced, null, true, SshToolsConnectionPanel.DEFAULT_SIZE);
		if (Option.CHOICE_SAVE.equals(opt)) {
			return true;
		}
		return false;
	}

	public abstract List<SshToolsConnectionTab<ProfileTransport<?>>> getAdditionalConnectionTabs();

	public abstract File getCurrentFile();

	public abstract ResourceProfile<? extends ProfileTransport<?>> getCurrentProfile();

	@Override
	public void init(SshToolsApplication application) throws SshToolsApplicationException {
		if (!(application instanceof SshToolsClientApplication)) {
			throw new SshToolsApplicationException("Application must extend SshToolsClientApplication.");
		}
		super.init(application);
	}

	/**
	 * Get if connected.
	 * 
	 * @return connected
	 */
	public abstract boolean isConnected();

	public ResourceProfile<ProfileTransport<?>> newConnectionProfile(ResourceProfile<ProfileTransport<?>> profile) {
		return SshToolsConnectionPanel.showConnectionDialog(AbstractSshToolsApplicationClientPanel.this, profile,
				getAdditionalConnectionTabs());
	}

	public void open() {
		// Create a file chooser with the current directory set to the
		// application home
		JFileChooser fileDialog = new JFileChooser(getDefaultChooserDir(PREF_CONNECTION_FILE_DIRECTORY));
		fileDialog.setFileFilter(connectionFileFilter);
		// Show it
		int ret = fileDialog.showOpenDialog(this);
		// If we've approved the selection then process
		if (ret == JFileChooser.APPROVE_OPTION) {
			PreferencesStore.put(PREF_CONNECTION_FILE_DIRECTORY, fileDialog.getCurrentDirectory().getAbsolutePath());
			// Get the file
			File f = fileDialog.getSelectedFile();
			open(f);
		}
	}

	public void open(File f) {
		// Make sure a connection is not already open
		if (isConnected()) {
			Option optNew = new Option(Messages.getString("AbstractSshToolsApplicationClientPanel.New"),
					Messages.getString("AbstractSshToolsApplicationClientPanel.NewDesc"),
					Messages.getString("AbstractSshToolsApplicationClientPanel.NewMnemonic").charAt(0));
			Option opt = OptionDialog.prompt(this, OptionChooser.WARNING,
					Messages.getString("AbstractSshToolsApplicationClientPanel.ExistingConnection"),
					Messages.getString("AbstractSshToolsApplicationClientPanel.ConnectionOpenMsg"),
					new Option[] { optNew, Option.CHOICE_CLOSE, Option.CHOICE_CANCEL }, Option.CHOICE_CANCEL);
			if (optNew.equals(opt)) {
				try {
					SshToolsApplicationContainer c = application.newContainer();
					((AbstractSshToolsApplicationClientPanel<?>) c.getApplicationPanel()).open(f);
					return;
				} catch (SshToolsApplicationException stae) {
					stae.printStackTrace();
				}
			} else {
				if (Option.CHOICE_CLOSE.equals(opt)) {
					closeConnection(true);
				}
			}
		}
		// Save to MRU
		if (getApplication().getMRUModel() != null) {
			getApplication().getMRUModel().add(f);
		}
		// Make sure its not invalid
		if (f != null) {
			// Create a new connection properties object
			ResourceProfile<ProfileTransport<S>> profile = new ResourceProfile<>();
			InputStream in = null;
			try {
				in = new FileInputStream(f);
				// Open the file
				profile.load(in);
				// Connect with the new details.
				connect(profile, false);
			} catch (Exception e) {
				OptionDialog.error(this, Messages.getString("AbstractSshToolsApplicationClientPanel.OpenConnection"), e);
			} finally {
				IOUtil.closeStream(in);
			}
		} else {
			OptionDialog.error(this, Messages.getString("AbstractSshToolsApplicationClientPanel.InvalidFile"),
					Messages.getString("AbstractSshToolsApplicationClientPanel.OpenConnection"));
		}
	}

	/**
	 * Save connection to file.
	 * 
	 * @param saveAs
	 * @param file
	 * @param profile
	 * 
	 * @return file saved to
	 */
	public File saveConnection(boolean saveAs, File file, ResourceProfile<? extends ProfileTransport<?>> profile) {
		if (profile != null) {
			if ((file == null) || saveAs) {
				JFileChooser fileDialog = new JFileChooser(getDefaultChooserDir(PREF_CONNECTION_FILE_DIRECTORY));
				fileDialog.setFileFilter(connectionFileFilter);
				if (file != null) {
					fileDialog.setSelectedFile(file);
				}
				int ret = fileDialog.showSaveDialog(this);
				if (ret == JFileChooser.CANCEL_OPTION) {
					return null;
				}
				file = fileDialog.getSelectedFile();
				PreferencesStore.put(PREF_CONNECTION_FILE_DIRECTORY, fileDialog.getCurrentDirectory().getAbsolutePath());
				;
				if (!file.getName().toLowerCase().endsWith(".xml")) {
					file = new File(file.getAbsolutePath() + ".xml");
				}
			}
			OutputStream out = null;
			try {
				if (saveAs && file.exists()) {
					if (JOptionPane.showConfirmDialog(this,
							Messages.getString("AbstractSshToolsApplicationClientPanel.FileExistsSure"),
							Messages.getString("AbstractSshToolsApplicationClientPanel.FileExists"), JOptionPane.YES_NO_OPTION,
							JOptionPane.WARNING_MESSAGE) == JOptionPane.NO_OPTION) {
						return null;
					}
				}
				// Save the connection details to file
				out = new FileOutputStream(file);
				profile.save(out);
				return file;
			} catch (IOException e) {
				OptionDialog.error(this, Messages.getString("AbstractSshToolsApplicationClientPanel.FileExistsSure"), e);
			}
		}
		return null;
	}

	/**
	 * 
	 * 
	 * @return
	 */
	protected boolean allowConnectionSettingsEditing() {
		return false;
	}

	@Override
	protected String getDefaultChooserDir(String pref) {
		if (getCurrentFile() != null) {
			return getCurrentFile().getParentFile().getAbsolutePath();
		}
		return super.getDefaultChooserDir(pref);
	}
}