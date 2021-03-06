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
package com.sshtools.appframework.prefs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.AbstractPreferences;
import java.util.prefs.BackingStoreException;

/**
 * Preferences implementation that stores to a user-defined file. See
 * FilePreferencesFactory.
 * 
 * @author David Croft (<a href="http://www.davidc.net">www.davidc.net</a>)
 * @version $Id: FilePreferences.java 283 2009-06-18 17:06:58Z david $
 * 
 * http://www.davidc.net/programming/java/java-preferences-using-file-backing-store
 */
public class FilePreferences extends AbstractPreferences {
	private static final Logger log = Logger.getLogger(FilePreferences.class
			.getName());

	private Map<String, FilePreferences> children;
	private boolean isRemoved = false;
	private Map<String, String> root;

	public FilePreferences(AbstractPreferences parent, String name) {
		super(parent, name);

		log.finest("Instantiating node " + name);

		root = new TreeMap<String, String>();
		children = new TreeMap<String, FilePreferences>();

		try {
			sync();
		} catch (BackingStoreException e) {
			log.log(Level.SEVERE, "Unable to sync on creation of node " + name,
					e);
		}
	}

	@Override
	protected String[] childrenNamesSpi() throws BackingStoreException {
		return children.keySet().toArray(new String[children.keySet().size()]);
	}

	@Override
	protected FilePreferences childSpi(String name) {
		FilePreferences child = children.get(name);
		if (child == null || child.isRemoved()) {
			child = new FilePreferences(this, name);
			children.put(name, child);
		}
		return child;
	}

	@Override
	protected void flushSpi() throws BackingStoreException {
		final File file = FilePreferencesFactory.getPreferencesFile();

		synchronized (file) {
			Properties p = new Properties();
			try {

				StringBuilder sb = new StringBuilder();
				getPath(sb);
				String path = sb.toString();

				if (file.exists()) {
					p.load(new FileInputStream(file));

					List<String> toRemove = new ArrayList<String>();

					// Make a list of all direct children of this node to be
					// removed
					final Enumeration<?> pnen = p.propertyNames();
					while (pnen.hasMoreElements()) {
						String propKey = (String) pnen.nextElement();
						if (propKey.startsWith(path)) {
							String subKey = propKey.substring(path.length());
							// Only do immediate descendants
							if (subKey.indexOf('.') == -1) {
								toRemove.add(propKey);
							}
						}
					}

					// Remove them now that the enumeration is done with
					for (String propKey : toRemove) {
						p.remove(propKey);
					}
				}

				// If this node hasn't been removed, add back in any values
				if (!isRemoved) {
					for (String s : root.keySet()) {
						p.setProperty(path + s, root.get(s));
					}
				}

				p.store(new FileOutputStream(file), "FilePreferences");
			} catch (IOException e) {
				throw new BackingStoreException(e);
			}
		}
	}

	@Override
	protected String getSpi(String key) {
		return root.get(key);
	}

	@Override
	protected String[] keysSpi() throws BackingStoreException {
		return root.keySet().toArray(new String[root.keySet().size()]);
	}

	@Override
	protected void putSpi(String key, String value) {
		root.put(key, value);
		try {
			flush();
		} catch (BackingStoreException e) {
			log.log(Level.SEVERE, "Unable to flush after putting " + key, e);
		}
	}

	@Override
	protected void removeNodeSpi() throws BackingStoreException {
		isRemoved = true;
		flush();
	}

	@Override
	protected void removeSpi(String key) {
		root.remove(key);
		try {
			flush();
		} catch (BackingStoreException e) {
			log.log(Level.SEVERE, "Unable to flush after removing " + key, e);
		}
	}

	@Override
	protected void syncSpi() throws BackingStoreException {
		if (isRemoved())
			return;

		final File file = FilePreferencesFactory.getPreferencesFile();

		if (!file.exists())
			return;

		synchronized (file) {
			Properties p = new Properties();
			try {
				p.load(new FileInputStream(file));

				StringBuilder sb = new StringBuilder();
				getPath(sb);
				String path = sb.toString();

				final Enumeration<?> pnen = p.propertyNames();
				while (pnen.hasMoreElements()) {
					String propKey = (String) pnen.nextElement();
					if (propKey.startsWith(path)) {
						String subKey = propKey.substring(path.length());
						// Only load immediate descendants
						if (subKey.indexOf('.') == -1) {
							root.put(subKey, p.getProperty(propKey));
						}
					}
				}
			} catch (IOException e) {
				throw new BackingStoreException(e);
			}
		}
	}

	private void getPath(StringBuilder sb) {
		final FilePreferences parent = (FilePreferences) parent();
		if (parent == null)
			return;

		parent.getPath(sb);
		sb.append(name()).append('.');
	}
}