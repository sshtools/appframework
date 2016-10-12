/**
 * Appframework
 * Copyright (C) 2003-2016 SSHTOOLS Limited
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 */
package com.sshtools.appframework.api.ui;

import java.awt.LayoutManager;
import java.awt.Window;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Comparator;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.sshtools.appframework.api.SshToolsApplicationException;
import com.sshtools.appframework.ui.Messages;
import com.sshtools.appframework.ui.PreferencesStore;
import com.sshtools.appframework.ui.SshToolsApplication;
import com.sshtools.appframework.ui.SshToolsClientApplication;
import com.sshtools.appframework.ui.SshToolsConnectionPanel;
import com.sshtools.appframework.util.ApplicationException;
import com.sshtools.appframework.util.IOUtil;
import com.sshtools.profile.AuthenticationException;
import com.sshtools.profile.ProfileException;
import com.sshtools.profile.ResourceProfile;
import com.sshtools.ui.Option;
import com.sshtools.ui.OptionCallback;
import com.sshtools.ui.OptionChooser;
import com.sshtools.ui.swing.AppAction;
import com.sshtools.ui.swing.OptionDialog;
import com.sshtools.virtualsession.VirtualSession;

/**
 * 
 * 
 * @author $author$
 */

@SuppressWarnings("serial")
public abstract class AbstractSshToolsApplicationClientPanel<V extends VirtualSession>
		extends SshToolsApplicationPanel {

	public final static String PREF_CONNECTION_FILE_DIRECTORY = "sshapps.connectionFile.directory";
	public final static String PREF_DEFAULT_SCHEME_NAME = "sshapps.defaultSchemeName";

	//

	public final static int BANNER_TIMEOUT = 2000;

	protected javax.swing.filechooser.FileFilter connectionFileFilter = new

	ConnectionFileFilter();

	/**
	 * Creates a new AbstractSshToolsApplicationClientPanel object.
	 */

	public AbstractSshToolsApplicationClientPanel() {
		super();
	}

	/**
	 * Creates a new AbstractSshToolsApplicationClientPanel object.
	 * 
	 * @param mgr
	 */
	public AbstractSshToolsApplicationClientPanel(LayoutManager mgr) {
		super(mgr);

	}

	/**
	 * 
	 * 
	 * @return
	 */
	public abstract SshToolsConnectionTab[] getAdditionalConnectionTabs();

	/**
	 * 
	 * 
	 * @param application
	 * 
	 * @throws SshToolsApplicationException
	 */
	public void init(SshToolsApplication application) throws

	SshToolsApplicationException {
		if (!(application instanceof SshToolsClientApplication)) {
			throw new SshToolsApplicationException(
					"Application must extend SshToolsClientApplication.");
		}
		super.init(application);
	}

	/**
     * 
     */
	public void editConnection() {
		// Create a file chooser with the current directory set to the
		// application home
		JFileChooser fileDialog = new JFileChooser(PreferencesStore.get(
				PREF_CONNECTION_FILE_DIRECTORY,
				System.getProperty("sshtools.home",
						System.getProperty("user.home"))));
		fileDialog.setFileFilter(connectionFileFilter);
		// Show it
		Window w = (Window) SwingUtilities.getAncestorOfClass(Window.class,
				this);
		int ret = fileDialog.showOpenDialog(this);
		// If we've approved the selection then process
		if (ret == JFileChooser.APPROVE_OPTION) {
			PreferencesStore.put(PREF_CONNECTION_FILE_DIRECTORY, fileDialog
					.getCurrentDirectory().getAbsolutePath());
			// Get the file
			File f = fileDialog.getSelectedFile();
			// Load the profile
			ResourceProfile p = new ResourceProfile();
			InputStream in = null;
			try {
				in = new FileInputStream(f);
				p.load(in);
				if (editConnection(p)) {
					saveConnection(false, f, p);
				}
			} catch (IOException ioe) {
				OptionDialog
						.error(this,
								Messages.getString("Error"),
								Messages.getString("AbstractSshToolsApplicationClientPanel.LoadFail"),
								ioe);
			} finally {
				IOUtil.closeStream(in);
			}
		}

	}

	/**
	 * 
	 * 
	 * @param profile
	 * 
	 * @return
	 */

	public ResourceProfile newConnectionProfile(ResourceProfile profile) {
		return SshToolsConnectionPanel.showConnectionDialog(
				AbstractSshToolsApplicationClientPanel.this, profile,
				getAdditionalConnectionTabs());

	}

	/**
     * 
     */

	public void open() {
		// Create a file chooser with the current directory set to the
		// application home

		JFileChooser fileDialog = new JFileChooser(
				getDefaultChooserDir(PREF_CONNECTION_FILE_DIRECTORY));
		fileDialog.setFileFilter(connectionFileFilter);
		// Show it
		int ret = fileDialog.showOpenDialog(this);
		// If we've approved the selection then process
		if (ret == JFileChooser.APPROVE_OPTION) {
			PreferencesStore.put(PREF_CONNECTION_FILE_DIRECTORY, fileDialog
					.getCurrentDirectory().getAbsolutePath());
			// Get the file
			File f = fileDialog.getSelectedFile();
			open(f);
		}

	}

	/**
	 * 
	 * 
	 * @param f
	 */

	public void open(File f) {
		log.info("Opening file " + f);
		// Make sure a connection is not already open
		if (isConnected()) {
			Option optNew = new Option(
					Messages.getString("AbstractSshToolsApplicationClientPanel.New"),
					Messages.getString("AbstractSshToolsApplicationClientPanel.NewDesc"),
					Messages.getString(
							"AbstractSshToolsApplicationClientPanel.NewMnemonic")
							.charAt(0));
			Option opt = OptionDialog
					.prompt(this,
							OptionDialog.WARNING,
							Messages.getString("AbstractSshToolsApplicationClientPanel.ExistingConnection"),
							Messages.getString("AbstractSshToolsApplicationClientPanel.ConnectionOpenMsg"),
							new Option[] { optNew, Option.CHOICE_CLOSE,
									Option.CHOICE_CANCEL },
							Option.CHOICE_CANCEL);
			if (optNew.equals(opt)) {
				try {
					SshToolsApplicationContainer c = application.newContainer();
					((AbstractSshToolsApplicationClientPanel) c
							.getApplicationPanel()).open(f);
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
			ResourceProfile profile = new ResourceProfile();
			InputStream in = null;
			try {
				in = new FileInputStream(f);
				// Open the file
				profile.load(in);
				// Connect with the new details.
				connect(profile, false);
			} catch (Exception e) {
				OptionDialog
						.error(this,
								Messages.getString("AbstractSshToolsApplicationClientPanel.OpenConnection"),
								e);
			} finally {
				IOUtil.closeStream(in);
			}
		} else {
			OptionDialog
					.error(this,
							Messages.getString("AbstractSshToolsApplicationClientPanel.InvalidFile"),
							Messages.getString("AbstractSshToolsApplicationClientPanel.OpenConnection"));
		}

	}

	public abstract V connect(final ResourceProfile profile,
			final boolean newProfile) throws ApplicationException,
			ProfileException, IOException, AuthenticationException;

	public abstract V connect(ResourceProfile profile, boolean newProfile,
			File sourceFile) throws ApplicationException, ProfileException,
			IOException, AuthenticationException;

	/**
	 * 
	 * 
	 * @return
	 */

	public abstract boolean isConnected();

	public abstract ResourceProfile getCurrentProfile();

	public abstract File getCurrentFile();

	/**
	 * 
	 * 
	 * @throws SshException
	 */

	public void connect() throws ApplicationException, ProfileException,
			IOException, AuthenticationException {
		if (getCurrentProfile() == null) {
			throw new ApplicationException(
					"Can't connect, no connection profile have been set.");
		}
		// There isn't anywhere to store this setting yet
		connect(getCurrentProfile(), false);

	}

	/**
	 * 
	 * 
	 * @param disconnect
	 */

	public abstract boolean closeConnection(boolean disconnect);

	/**
	 * 
	 * 
	 * @return
	 */

	protected boolean allowConnectionSettingsEditing() {
		return false;

	}

	/**
	 * 
	 * 
	 * @param profile
	 * 
	 * @return
	 */

	public boolean editConnection(ResourceProfile profile) {
		final SshToolsConnectionPanel panel = new SshToolsConnectionPanel(
				allowConnectionSettingsEditing(), getAdditionalConnectionTabs());
		panel.setConnectionProfile(profile);
		OptionCallback callback = new OptionCallback() {
			public boolean canClose(OptionChooser dialog, Option option) {
				if (Option.CHOICE_OK.equals(option)) {
					return panel.validateTabs();
				}
				return true;
			}
		};
		Option opt = OptionDialog
				.prompt(AbstractSshToolsApplicationClientPanel.this,
						OptionDialog.UNCATEGORISED,
						Messages.getString("AbstractSshToolsApplicationClientPanel.ConnSettings"),
						panel, Option.CHOICES_OK_CANCEL, Option.CHOICE_CANCEL,
						callback, null, SshToolsConnectionPanel.DEFAULT_SIZE);
		if (Option.CHOICE_OK.equals(opt)) {
			panel.applyTabs();
			return true;
		}
		return false;

	}

	/**
	 * 
	 * 
	 * @param saveAs
	 * @param file
	 * @param profile
	 * 
	 * @return
	 */

	public File saveConnection(boolean saveAs, File file,
			ResourceProfile profile) {
		if (profile != null) {
			if ((file == null) || saveAs) {
				JFileChooser fileDialog = new JFileChooser(
						getDefaultChooserDir(PREF_CONNECTION_FILE_DIRECTORY));
				fileDialog.setFileFilter(connectionFileFilter);
				if (file != null) {
					fileDialog.setSelectedFile(file);
				}
				int ret = fileDialog.showSaveDialog(this);
				if (ret == JFileChooser.CANCEL_OPTION) {
					return null;
				}
				file = fileDialog.getSelectedFile();
				PreferencesStore.put(PREF_CONNECTION_FILE_DIRECTORY, fileDialog
						.getCurrentDirectory().getAbsolutePath());
				;
				if (!file.getName().toLowerCase().endsWith(".xml")) {
					file = new File(file.getAbsolutePath() + ".xml");
				}
			}
			OutputStream out = null;
			try {
				if (saveAs && file.exists()) {
					if (JOptionPane
							.showConfirmDialog(
									this,
									Messages.getString("AbstractSshToolsApplicationClientPanel.FileExistsSure"),
									Messages.getString("AbstractSshToolsApplicationClientPanel.FileExists"),
									JOptionPane.YES_NO_OPTION,
									JOptionPane.WARNING_MESSAGE) == JOptionPane.NO_OPTION) {
						return null;
					}
				}
				// Check to make sure its valid
				if (file != null) {
					// Save the connection details to file
					out = new FileOutputStream(file);
					profile.save(out);
					return file;
				}
				OptionDialog
						.error(this,
								Messages.getString("AbstractSshToolsApplicationClientPanel.InvalidFile"),
								Messages.getString("AbstractSshToolsApplicationClientPanel.SaveCon"));
			} catch (IOException e) {
				OptionDialog
						.error(this,
								Messages.getString("AbstractSshToolsApplicationClientPanel.FileExistsSure"),
								e);
			}
		}
		return null;

	}

	protected String getDefaultChooserDir(String pref) {
		if (getCurrentFile() != null) {
			return getCurrentFile().getParentFile().getAbsolutePath();
		}
		return super.getDefaultChooserDir(pref);
	}

	class ToolBarActionComparator

	implements Comparator {

		public int compare(Object o1, Object o2) {
			int i = ((Integer) ((AppAction) o1)
					.getValue(AppAction.TOOLBAR_GROUP))
					.compareTo((Integer) ((AppAction) o2)
							.getValue(AppAction.TOOLBAR_GROUP));
			return (i == 0) ? ((Integer) ((AppAction) o1)
					.getValue(AppAction.TOOLBAR_WEIGHT))
					.compareTo((Integer) ((AppAction) o2)
							.getValue(AppAction.TOOLBAR_WEIGHT)) : i;

		}

	}

	class MenuItemActionComparator

	implements Comparator {

		public int compare(Object o1, Object o2) {
			int i = ((Integer) ((AppAction) o1)
					.getValue(AppAction.MENU_ITEM_GROUP))
					.compareTo((Integer) ((AppAction) o2)
							.getValue(AppAction.MENU_ITEM_GROUP));
			return (i == 0) ? ((Integer) ((AppAction) o1)
					.getValue(AppAction.MENU_ITEM_WEIGHT))
					.compareTo((Integer) ((AppAction) o2)
							.getValue(AppAction.MENU_ITEM_WEIGHT)) : i;

		}

	}

	class ConnectionFileFilter

	extends javax.swing.filechooser.FileFilter {

		public boolean accept(File f) {
			return f.isDirectory()
					|| f.getName().toLowerCase().endsWith(".xml");

		}

		public String getDescription() {
			return Messages
					.getString("AbstractSshToolsApplicationClientPanel.ConnectionFiles")
					+ " (*.xml)";
		}

	}

}