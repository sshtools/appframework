/*
 *  SSHTools - Java SSH2 API
 *
 *  Copyright (C) 2002 Lee David Painter.
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Library General Public License
 *  as published by the Free Software Foundation; either version 2 of
 *  the License, or (at your option) any later version.
 *
 *  You may also distribute it and/or modify it under the terms of the
 *  Apache style J2SSH Software License. A copy of which should have
 *  been provided with the distribution.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  License document supplied with your distribution for more details.
 *
 */

package com.sshtools.appframework.api.ui;

import java.awt.LayoutManager;
import java.awt.Window;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilePermission;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.AccessControlException;
import java.security.AccessController;
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
import com.sshtools.appframework.util.GeneralUtil;
import com.sshtools.appframework.util.IOUtil;
import com.sshtools.profile.ProfileTransport;
import com.sshtools.profile.ResourceProfile;
import com.sshtools.ui.Option;
import com.sshtools.ui.OptionCallback;
import com.sshtools.ui.OptionChooser;
import com.sshtools.ui.swing.AppAction;
import com.sshtools.ui.swing.OptionDialog;

/**
 * 
 * 
 * @author $author$
 */

public abstract class SshToolsApplicationClientPanel

extends SshToolsApplicationPanel {

	/**  */

	public final static String PREF_CONNECTION_FILE_DIRECTORY =

	"sshapps.connectionFile.directory";

	//

	/**  */

	public final static int BANNER_TIMEOUT = 2000;

	/**  */

	protected File currentConnectionFile;

	/**  */

	protected boolean needSave;

	/**  */

	protected ResourceProfile currentConnectionProfile;

	/**  */

	protected javax.swing.filechooser.FileFilter connectionFileFilter = new

	ConnectionFileFilter();

	// Private instance variables

	private ProfileTransport transport;

	/**
	 * Creates a new SshToolsApplicationClientPanel object.
	 */

	public SshToolsApplicationClientPanel() {
		super();

	}

	/**
	 * Creates a new SshToolsApplicationClientPanel object.
	 * 
	 * @param mgr
	 */

	public SshToolsApplicationClientPanel(LayoutManager mgr) {
		super(mgr);

	}

	/**
	 * 
	 * 
	 * @return
	 */

	public abstract SshToolsConnectionTab[] getAdditionalConnectionTabs();

	public void init(SshToolsApplication application) throws

	SshToolsApplicationException {
		if (!(application instanceof SshToolsClientApplication)) {
			throw new SshToolsApplicationException("Application must extend SshToolsClientApplication.");
		}
		super.init(application);

	}

	/**
     * 
     */

	public void editConnection() {
		// Create a file chooser with the current directory set to the
		// application home
		JFileChooser fileDialog = new JFileChooser(PreferencesStore.get(PREF_CONNECTION_FILE_DIRECTORY,
			System.getProperty("sshtools.home", System.getProperty("user.home"))));
		fileDialog.setFileFilter(connectionFileFilter);
		// Show it
		Window w = (Window) SwingUtilities.getAncestorOfClass(Window.class, this);
		int ret = fileDialog.showOpenDialog(this);
		// If we've approved the selection then process
		if (ret == JFileChooser.APPROVE_OPTION) {
			PreferencesStore.put(PREF_CONNECTION_FILE_DIRECTORY, fileDialog.getCurrentDirectory().getAbsolutePath());
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
				OptionDialog.error(this, Messages.getString("Error"),
					Messages.getString("SshToolsApplicationClientPanel.ProfileFail"), ioe);
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
		return SshToolsConnectionPanel.showConnectionDialog(SshToolsApplicationClientPanel.this, profile,
			getAdditionalConnectionTabs());

	}

	/**
     * 
     */

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

	/**
	 * 
	 * 
	 * @param f
	 */

	public void open(File f) {
		// Make sure a connection is not already open
		if (isConnected()) {
			Option optNew = new Option(Messages.getString("AbstractSshToolsApplicationClientPanel.New"),
				Messages.getString("AbstractSshToolsApplicationClientPanel.NewDesc"), Messages.getString(
					"AbstractSshToolsApplicationClientPanel.NewMnemonic").charAt(0));
			Option opt = OptionDialog.prompt(this, OptionDialog.WARNING,
				Messages.getString("SshToolsApplicationClientPanel.ExistingConnection"),
				Messages.getString("SshToolsApplicationClientPanel.ConnectionOpenMsg"), new Option[] { optNew, Option.CHOICE_CLOSE,
					Option.CHOICE_CANCEL }, Option.CHOICE_CANCEL);
			if ((opt == null) || (opt == Option.CHOICE_CANCEL)) {
				return;
			} else if (opt == optNew) {
				try {
					SshToolsApplicationContainer c = application.newContainer();
					((SshToolsApplicationClientPanel) c.getApplicationPanel()).open(f);
					return;
				} catch (SshToolsApplicationException stae) {
					log.error("Failed to open profile.", stae);
				}
			} else {
				closeConnection(true);
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
				setNeedSave(false);
				currentConnectionFile = f;
				setContainerTitle(f);
				// Connect with the new details.
				connect(profile, false);
			} catch (Exception e) {
				OptionDialog.error(this, Messages.getString("SshToolsApplicationClientPanel.OpenConnection"), e);
			} finally {
				IOUtil.closeStream(in);
			}
		} else {
			OptionDialog.error(this, Messages.getString("SshToolsApplicationClientPanel.OpenConnection"),
				Messages.getString("SshToolsApplicationClientPanel.InvalidFile"));
		}

	}

	public ProfileTransport getTransport() {
		return transport;

	}

	public void setTransport(ProfileTransport transport) {
		this.transport = transport;

	}

	public void connect(final ResourceProfile profile, final boolean newProfile) {
		currentConnectionProfile = profile;

	}

	/**
	 * 
	 * 
	 * @param file
	 */

	public void setContainerTitle(File file) {
		String verString = GeneralUtil.getVersionString(application.getApplicationName(), getClass());
		if (container != null) {
			container.setContainerTitle((file == null) ? verString : (verString + " [" + file.getName() + "]"));
		}

	}

	/**
	 * 
	 * 
	 * @param needSave
	 */

	public void setNeedSave(boolean needSave) {
		if (needSave != this.needSave) {
			this.needSave = needSave;
			setAvailableActions();
		}

	}

	/**
	 * 
	 * 
	 * @return
	 */

	public boolean isNeedSave() {
		return needSave;

	}

	/**
	 * 
	 * 
	 * @return
	 */

	public boolean isConnected() {
		return (transport != null) && transport.isConnected();

	}

	public abstract ResourceProfile getCurrentProfile();

	public abstract File getCurrentFile();

	/**
	 * 
	 * 
	 * @throws SshException
	 */

	public void connect() throws ApplicationException {
		if (getCurrentProfile() == null) {
			throw new ApplicationException(Messages.getString("SshToolsApplicationClientPanel.CantConnect"));
		}
		// There isn't anywhere to store this setting yet
		connect(getCurrentProfile(), false);

	}

	/**
	 * 
	 * 
	 * @param disconnect
	 */

	public void closeConnection(boolean disconnect) {
		//
		if (isNeedSave()) {
			// Only allow saving of files if allowed by the security manager
			try {
				if (System.getSecurityManager() != null) {
					AccessController.checkPermission(new FilePermission("<<ALL FILES>>", "write"));
					if (JOptionPane.showConfirmDialog(
						this,
						Messages.getString("SshToolsApplicationClientPanel.UnsavedChanges1") + " "
							+ ((currentConnectionFile == null) ? "<Untitled>" : currentConnectionFile.getName())
							+ Messages.getString("SshToolsApplicationClientPanel.UnsavedChanges2"),
						Messages.getString("SshToolsApplicationClientPanel.UnsavedChanges3"), JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
						saveConnection(false, getCurrentFile(), getCurrentProfile());
						setNeedSave(false);
					}
				}
			} catch (AccessControlException ace) {
			}
		}
		if (disconnect && transport != null) {
			try {
				transport.disconnect();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}

	/**
	 * 
	 * 
	 * @return
	 */

	protected boolean allowConnectionSettingsEditing() {
		return true;

	}

	/**
	 * Edit the specified profile.
	 * 
	 * @param profile profile
	 * @return true for edit applied or false for edit cancelled.
	 */

	public boolean editConnection(ResourceProfile profile) {
		final SshToolsConnectionPanel panel = new SshToolsConnectionPanel(allowConnectionSettingsEditing(),
			getAdditionalConnectionTabs());
		panel.setConnectionProfile(profile);
		OptionCallback callback = new OptionCallback() {
			public boolean canClose(OptionChooser dialog, Option option) {
				if (Option.CHOICE_OK.equals(option)) {
					return panel.validateTabs();
				}
				return true;
			}
		};
		Option opt = OptionDialog.prompt(SshToolsApplicationClientPanel.this, OptionDialog.UNCATEGORISED,
			Messages.getString("SshToolsApplicationClientPanel.ConnSettings"), panel, Option.CHOICES_OK_CANCEL,
			Option.CHOICE_CANCEL, callback, null, SshToolsConnectionPanel.DEFAULT_SIZE);
		if (Option.CHOICE_OK.equals(opt)) {
			panel.applyTabs();
			if (profile == getCurrentProfile()) {
				setNeedSave(true);
			}
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

	public File saveConnection(boolean saveAs, File file, ResourceProfile profile) {
		if (profile != null) {
			if ((file == null) || saveAs) {
				JFileChooser fileDialog = new JFileChooser(getDefaultChooserDir(PREF_CONNECTION_FILE_DIRECTORY));
				fileDialog.setFileFilter(connectionFileFilter);
				int ret = fileDialog.showSaveDialog(this);
				if (ret == JFileChooser.CANCEL_OPTION) {
					return null;
				}
				PreferencesStore.put(PREF_CONNECTION_FILE_DIRECTORY, fileDialog.getCurrentDirectory().getAbsolutePath());
				file = fileDialog.getSelectedFile();
				if (!file.getName().toLowerCase().endsWith(".xml")) {
					file = new File(file.getAbsolutePath() + ".xml");
				}
			}
			OutputStream out = null;
			try {
				if (saveAs && file.exists()) {
					if (JOptionPane.showConfirmDialog(this, Messages.getString("SshToolsApplicationClientPanel.FileExistsSure"),
						Messages.getString("SshToolsApplicationClientPanel.FileExists"), JOptionPane.YES_NO_OPTION,
						JOptionPane.WARNING_MESSAGE) == JOptionPane.NO_OPTION) {
						return null;
					}
				}
				// Check to make sure its valid
				if (file != null) {
					// Save the connection details to file
					out = new FileOutputStream(file);
					profile.save(out);
					if (profile == getCurrentProfile()) {
						setNeedSave(false);
					}
					return file;
				}
				OptionDialog.error(this, Messages.getString("SshToolsApplicationClientPanel.SaveCon"),
					Messages.getString("SshToolsApplicationClientPanel.InvalidFile"));
			} catch (IOException e) {
				OptionDialog.error(this, Messages.getString("SshToolsApplicationClientPanel.SaveCon"), e);
			}
		}
		return null;

	}

	class ToolBarActionComparator

	implements Comparator {
		public int compare(Object o1, Object o2) {
			int i = ((Integer) ((AppAction) o1).getValue(AppAction.TOOLBAR_GROUP)).compareTo((Integer) ((AppAction) o2)
				.getValue(AppAction.TOOLBAR_GROUP));
			return (i == 0) ? ((Integer) ((AppAction) o1).getValue(AppAction.TOOLBAR_WEIGHT)).compareTo((Integer) ((AppAction) o2)
				.getValue(AppAction.TOOLBAR_WEIGHT)) : i;

		}

	}

	class MenuItemActionComparator

	implements Comparator {

		public int compare(Object o1, Object o2) {
			int i = ((Integer) ((AppAction) o1).getValue(AppAction.MENU_ITEM_GROUP)).compareTo((Integer) ((AppAction) o2)
				.getValue(AppAction.MENU_ITEM_GROUP));
			return (i == 0) ? ((Integer) ((AppAction) o1).getValue(AppAction.MENU_ITEM_WEIGHT))
				.compareTo((Integer) ((AppAction) o2).getValue(AppAction.MENU_ITEM_WEIGHT)) : i;

		}

	}

	class ConnectionFileFilter

	extends javax.swing.filechooser.FileFilter {

		public boolean accept(File f) {
			return f.isDirectory() || f.getName().toLowerCase().endsWith(".xml");

		}

		public String getDescription() {
			return "Connection files (*.xml)";

		}

	}

}