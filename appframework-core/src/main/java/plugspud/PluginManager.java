/*--

 $Id: PluginManager.java,v 1.1.2.3 2011-10-14 17:26:45 brett Exp $

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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.cli.Options;

public class PluginManager {
	// Plugin statis
	public final static int STATUS_UNINITIALIZED = 0;
	public final static int STATUS_STARTED = 1;
	public final static int STATUS_STOPPED = 2;
	public final static int STATUS_ERRORED = 3;
	// Plugin properties
	public final static String PLUGIN_VERSION = "version";
	public final static String PLUGIN_RESOURCE = "resource";
	public final static String PLUGIN_JARS = "jars";
	public final static String PLUGIN_DEPENDENCIES = "dependencies";
	public final static String PLUGIN_AUTHOR = "author";
	public final static String PLUGIN_URL = "url";
	public final static String PLUGIN_ORDER = "order";
	public final static String PLUGIN_INFORMATION = "information";
	public final static String PLUGIN_NAME = "name";
	public final static String PLUGIN_CLASSNAME = "className";
	public final static String PLUGIN_SHORT_DESCRIPTION = "shortDescription";
	public final static String PLUGIN_REQUIRED_HOST_VERSION = "requiredHostVersion";
	// Private instance variables
	private File pluginDir;
	private ClassLoader classLoader;
	private Vector<PluginWrapper> plugins;
	private PluginHostContext context;
	private HashMap pluginProperties;
	private HashMap<String, PluginWrapper> pluginMap;
	private ClassLoader parentClassLoader;
	private boolean initialised;
	private Vector<PluginWrapper> startedPlugins;

	/**
	 * Initialise the plugin manager
	 * 
	 * @param context context
	 * 
	 * @throws PluginException on any errors
	 */
	public void init(PluginHostContext context) throws PluginException {
		this.context = context;
		pluginMap = new HashMap<String, PluginManager.PluginWrapper>();
		plugins = new Vector<PluginWrapper>();
		startedPlugins = new Vector<PluginWrapper>();
		// Create the plugin directory if it doesn't exist
		pluginDir = context.getPluginDirectory();
		if (pluginDir == null) {
			context.log(PluginHostContext.LOG_ERROR, "No plugin directory has been provided by the plugin host.");
		} else {
			if (!pluginDir.exists() && !pluginDir.mkdirs())
				throw new PluginException("Could not create plugin directory " + pluginDir.getAbsolutePath());
			// First remove any plugins jars that are no longer required
			File removeFile = new File(pluginDir, "ros.list");
			if (removeFile.exists() && removeFile.canRead()) {
				InputStream rin = null;
				try {
					rin = new FileInputStream(removeFile);
					BufferedReader reader = new BufferedReader(new InputStreamReader(rin));
					String line = null;
					while ((line = reader.readLine()) != null) {
						File z = new File(line);
						context.log(PluginHostContext.LOG_INFORMATION, "Deleting plugin library " + z.getAbsolutePath());
						if (!z.delete())
							context.log(PluginHostContext.LOG_ERROR, "Failed to delete " + z.getAbsolutePath());
					}
				} catch (IOException ioe) {
					context.log(PluginHostContext.LOG_ERROR,
							"Failed to read remove-on-startup list file " + removeFile.getAbsolutePath());
				} finally {
					if (rin != null) {
						try {
							rin.close();
						} catch (IOException ioe) {
						}
					}
					if (!removeFile.delete()) {
						context.log(PluginHostContext.LOG_ERROR, "Failed to remove remove-on-startup list file "
								+ removeFile.getAbsolutePath() + ". Further errors may appear.");
					}
				}
			}
			if (!pluginDir.exists() && !pluginDir.mkdirs())
				throw new PluginException("Could not create plugin directory " + pluginDir.getAbsolutePath());
			// First unzip any newly installed plugin archives
			File[] newPlugins = pluginDir.listFiles(new FileFilter() {
				public boolean accept(File f) {
					return f.getName().toLowerCase().endsWith(".tmp");
				}
			});
			for (int i = 0; i < newPlugins.length; i++) {
				try {
					unzip(newPlugins[i], pluginDir);
					if (!newPlugins[i].delete()) {
						context.log(PluginHostContext.LOG_ERROR,
								"New plugin archive " + newPlugins[i].getAbsolutePath() + " could not be "
										+ "deleted. Until this file is removed, this plugin "
										+ "will continue to be installed every time " + context.getPluginHostName() + "starts up.");
					}
				} catch (Exception e) {
					context.log(PluginHostContext.LOG_ERROR, e);
					throw new PluginException("Failed to unzip newly installed plugin " + newPlugins[i].getAbsolutePath() + ". "
							+ e.getMessage() == null ? "<null>" : e.getMessage());
				}
			}
		}
		try {
			// Create a classloader for all of the plugin jars
			Vector<URL> v = new Vector<URL>();
			if (pluginDir != null) {
				URL u = pluginDir.toURL();
				v.addElement(u);
				context.log(PluginHostContext.LOG_INFORMATION, "Added Found plugin directory " + u.toExternalForm());
				findJars(pluginDir, v);
				URL[] urls = new URL[v.size()];
				v.copyInto(urls);
			}
			URL[] urls = (URL[]) v.toArray(new URL[v.size()]);
			classLoader = new URLClassLoader(urls, parentClassLoader == null ? getClass().getClassLoader() : parentClassLoader);
			((URLClassLoader) classLoader).getURLs();
			// Add the standard plugins
			URL url = context.getStandardPluginsResource();
			if (url != null) {
				try {
					loadPlugins(url, classLoader, true);
				} catch (PluginException pe) {
					context.log(PluginHostContext.LOG_ERROR, pe);
				}
			} else {
				// Add the plugins
				for (Enumeration<URL> e = classLoader.getResources("plugins.properties"); e.hasMoreElements();) {
					URL resource = e.nextElement();
					context.log(PluginHostContext.LOG_DEBUG, "Found plugins.properties in " + resource.toExternalForm());
					loadPlugins(resource, classLoader, false);
				}
			}
			Collections.sort(plugins);
			checkDependencies();
		} catch (Throwable t) {
			throw new PluginException("Plugin manager failed to initialise. ", t);
		}
		initialised = true;
	}

	private void findJars(File dir, Vector<URL> list) {
		File[] f = pluginDir.listFiles(new FileFilter() {
			public boolean accept(File file) {
				return file.isDirectory() || file.getName().toLowerCase().endsWith(".jar");
			}
		});
		for (int i = 0; f != null && i < f.length; i++) {
			if (f[i].isDirectory()) {
				findJars(f[i], list);
			} else {
				try {
					list.addElement(f[i].toURL());
				} catch (MalformedURLException e) {
				}
			}
		}
	}

	/**
	 * Set the parent class loader. This must be done before the init() method
	 * is called
	 * 
	 * @param parentClassLoader parent class loader
	 * @throws IllegalStateException if the plugin manager has been initialised
	 */
	public void setParentClassLoader(ClassLoader parentClassLoader) {
		if (initialised) {
			throw new IllegalStateException("Plugin manager has already been initialise, parent class loader cannot be set");
		}
		this.parentClassLoader = parentClassLoader;
	}

	/**
	 * Manually add a plugin.
	 * 
	 * @param plugin plugin
	 * @param properties properties
	 */
	public void addPlugin(Plugin plugin, Properties properties) {
		PluginWrapper w = new PluginWrapper(plugin, properties);
		plugins.addElement(w);
		pluginMap.put(w.getName(), w);
	}

	/**
	 * Unzip a file
	 * 
	 * @param zipFile zip file
	 * @param dir directory to unzip to
	 * @throws IOException on any i/o errors
	 */
	public void unzip(File zipFile, File dir) throws IOException {
		context.log(PluginHostContext.LOG_INFORMATION, "Unzipping " + zipFile.getAbsolutePath() + " to " + dir.getAbsolutePath());
		InputStream in = null;
		try {
			in = new BufferedInputStream(new FileInputStream(zipFile));
			ZipInputStream zin = new ZipInputStream(in);
			ZipEntry e;
			while ((e = zin.getNextEntry()) != null) {
				context.log(PluginHostContext.LOG_INFORMATION, "   Deflating " + e.getName() + " (" + e.getSize() + " bytes");
				OutputStream fout = null;
				File f = new File(dir, e.getName());
				if (!f.getParentFile().exists() && !f.getParentFile().mkdirs())
					throw new IOException("Could not create directory " + f.getParentFile().getAbsolutePath());
				fout = new FileOutputStream(f);
				try {
					PluginUtil.copyStreams(zin, fout, 65536);
				} finally {
					PluginUtil.closeStream(fout);
				}
			}
		} finally {
			PluginUtil.closeStream(in);
		}
	}

	/**
	 * Return the directory where plugins are installed
	 * 
	 * @return plugin directory
	 */
	public File getPluginDirectory() {
		return pluginDir;
	}

	/**
	 * Load a plugins.properties property sheet into separate property sheets,
	 * one for each plugin
	 * 
	 * @param url location of plugins.properties resource
	 * @return <code>HashMap</code> of properties
	 * @throws PluginException on error
	 */
	public HashMap loadPluginProperties(URL url) throws PluginException {
		HashMap props = new HashMap();
		InputStream in = null;
		try {
			in = url.openStream();
			Properties p = new Properties();
			p.load(in);
			for (Enumeration e = p.keys(); e.hasMoreElements();) {
				String key = (String) e.nextElement();
				int idx = key.indexOf('.');
				if (idx == -1)
					throw new PluginException(
							"Invalid property name in " + url.toExternalForm() + ". Must be " + "<pluginName>.<property>=<value>");
				String name = key.substring(0, idx);
				String property = key.substring(idx + 1);
				if (property.length() == 0)
					throw new PluginException(
							"Invalid property name in " + url.toExternalForm() + ". Must be " + "<pluginName>.<property>=<value>");
				Properties h = (Properties) props.get(name);
				if (h == null) {
					h = new Properties();
					props.put(name, h);
				}
				h.put(property, p.getProperty(key));
			}
		} catch (IOException ioe) {
			throw new PluginException("Could not load plugins from " + url.toExternalForm(), ioe);
		} finally {
			PluginUtil.closeStream(in);
		}
		return props;
	}

	/**
	 * Load plugins defined in the plugins properties file
	 * 
	 * @param url location of plugins.properties
	 * @param classLoader the class loader to use to load the plugin
	 * @param standard <code>true</code> if any plugins loaded are standard
	 *            plugins
	 * 
	 * @throws PluginException on any error
	 */
	public void loadPlugins(URL url, ClassLoader classLoader, boolean standard) throws PluginException {
		InputStream in = null;
		try {
			context.log(PluginHostContext.LOG_INFORMATION, "Loading plugins from " + url.toExternalForm());
			HashMap props = loadPluginProperties(url);
			for (Iterator i = props.keySet().iterator(); i.hasNext();) {
				String key = (String) i.next();
				Properties properties = (Properties) props.get(key);
				String name = properties.getProperty(PLUGIN_NAME);
				if (name == null)
					throw new PluginException("<pluginName>.name property " + "not specified in " + url.toExternalForm());
				String shortDescription = properties.getProperty(PLUGIN_SHORT_DESCRIPTION);
				if (shortDescription == null)
					throw new PluginException(
							"<pluginName>.shortDescription property " + "not specified in " + url.toExternalForm());
				String requiredHostVersion = properties.getProperty(PLUGIN_REQUIRED_HOST_VERSION);
				if (requiredHostVersion == null)
					throw new PluginException(
							"<pluginName>.requiredHostVersion property " + "not specified in " + url.toExternalForm());
				String className = properties.getProperty("className");
				if (className == null)
					throw new PluginException("<pluginName>.className property " + "not specified in " + url.toExternalForm());
				String version = properties.getProperty("version");
				if (version == null)
					throw new PluginException("<pluginName>.version property " + "not specified in " + url.toExternalForm());
				String jars = properties.getProperty(PLUGIN_JARS);
				String dependencies = properties.getProperty(PLUGIN_DEPENDENCIES);
				int order = 999;
				try {
					order = Integer.parseInt(properties.getProperty(PLUGIN_ORDER));
				} catch (Exception e) {
				}
				String pluginURL = properties.getProperty(PLUGIN_URL);
				String author = properties.getProperty(PLUGIN_AUTHOR);
				String information = properties.getProperty(PLUGIN_INFORMATION);
				List<PluginWrapper> found = new ArrayList<PluginWrapper>();
				context.log(PluginHostContext.LOG_DEBUG, "Looking for plugin " + name);
				for (Iterator z = plugins.iterator(); z.hasNext();) {
					PluginWrapper pw = (PluginWrapper) z.next();
					context.log(PluginHostContext.LOG_DEBUG, "   Testing againts " + pw.properties.getProperty(PLUGIN_NAME, ""));
					if (pw.properties.getProperty(PLUGIN_NAME, "").equals(name))
						found.add(pw);
				}
				if (found.size() > 0)
					context.log(PluginHostContext.LOG_DEBUG,
							"Plugin " + name + " [" + className + "] has been found more than once " + found);
				else {
					context.log(PluginHostContext.LOG_INFORMATION, "Loading plugin " + name + " [" + className + "]");
					try {
						PluginVersion reqVersion = null;
						if (!requiredHostVersion.equalsIgnoreCase("any")) {
							if (context.getPluginHostVersion() == null) {
								throw new PluginException("Plugin host is not supplying its version number.");
							}
							reqVersion = new PluginVersion(requiredHostVersion);
							int dif = reqVersion.compareTo(context.getPluginHostVersion());
							// System.out.println("Comparing " + reqVersion + "
							// to " + context.getPluginHostVersion() + " = " +
							// dif);
							if (dif > 0)
								throw new PluginException(
										"This plugin requires that " + context.getPluginHostName() + " is at least of version "
												+ reqVersion.toString() + ". The plugin host is current at version "
												+ context.getPluginHostVersion().getVersionString());
						}
						Plugin plugin = (Plugin) Class.forName(className, true, classLoader).newInstance();
						String resn = plugin.getClass().getName().replace('.', '/') + ".class";
						context.log(PluginHostContext.LOG_DEBUG, "Looking for resource " + resn);
						URL res = plugin.getClass().getClassLoader().getResource(resn);
						String resource = "";
						if (res == null)
							context.log(PluginHostContext.LOG_ERROR, "Could not locate resource " + resn);
						else {
							String n = res.toExternalForm();
							if (n.startsWith("jar:file:")) {
								n = n.substring(4);
								int idx = n.lastIndexOf('!');
								if (idx != -1)
									n = n.substring(0, idx);
								n = n.substring(5);
								// Windows path?
								if (n.startsWith("/") && n.length() > 3 && n.charAt(2) == ':' && Character.isLetter(n.charAt(1))
										&& n.charAt(3) == '/')
									n = n.substring(1);
								File f = new File(n);
								resource = f.getAbsolutePath();
							}
							context.log(PluginHostContext.LOG_DEBUG, "Resource is " + resource);
						}
						Properties pr = new Properties();
						pr.put(PLUGIN_NAME, name);
						pr.put(PLUGIN_SHORT_DESCRIPTION, shortDescription);
						pr.put(PLUGIN_CLASSNAME, className);
						pr.put(PLUGIN_VERSION, version);
						pr.put(PLUGIN_RESOURCE, resource);
						if (author != null)
							pr.put(PLUGIN_AUTHOR, author);
						if (jars != null)
							pr.put(PLUGIN_JARS, jars);
						if (dependencies != null)
							pr.put(PLUGIN_DEPENDENCIES, dependencies);
						if (information != null)
							pr.put(PLUGIN_INFORMATION, information);
						pr.put(PLUGIN_ORDER, String.valueOf(order));
						if (pluginURL != null)
							pr.put(PLUGIN_URL, pluginURL);
						addPlugin(plugin, pr);
					} catch (Throwable t) {
						context.log(PluginHostContext.LOG_ERROR,
								"Failed to load plugin " + className + " in " + url.toExternalForm(), t);
					}
				}
			}
		} finally {
			PluginUtil.closeStream(in);
		}
	}

	private void checkDependencies() {
		List toRemove = new ArrayList();
		for (Iterator i = plugins.iterator(); i.hasNext();) {
			PluginWrapper w = (PluginWrapper) i.next();
			context.log(PluginHostContext.LOG_INFORMATION,
					"Checking dependencies for " + w.getName() + " (order " + w.getOrder() + ")");
			String deps = w.properties.getProperty(PLUGIN_DEPENDENCIES);
			if (deps != null) {
				context.log(PluginHostContext.LOG_INFORMATION, "Dependencies for " + w.getName() + " are " + deps);
				StringTokenizer t = new StringTokenizer(deps, ",");
				while (t.hasMoreTokens()) {
					String depName = t.nextToken();
					int idx = depName.indexOf("/");
					PluginVersion reqDepVersion = null;
					if (idx != -1) {
						String depVersionText = depName.substring(idx + 1);
						if (depVersionText.length() > 0) {
							reqDepVersion = new PluginVersion(depVersionText);
						}
						depName = depName.substring(0, idx);
					}
					// First check the plugin exists
					PluginWrapper dep = getPluginWrapper(depName);
					if (dep == null) {
						context.log(PluginHostContext.LOG_ERROR,
								"Plugin " + w.getName() + " depends on " + depName + " "
										+ (reqDepVersion == null ? "" : ("(version " + reqDepVersion.toString() + ") "))
										+ "which is not installed. This plugin will not be loaded");
						toRemove.add(w);
					} else {
						// Now check the version if correct (if required)
						if (reqDepVersion != null) {
							PluginVersion depVersion = dep.getVersion();
							if (depVersion.compareTo(reqDepVersion) < 0) {
								context.log(PluginHostContext.LOG_ERROR,
										"Plugin " + w.getName() + " depends on " + depName + " (version " + reqDepVersion.toString()
												+ "), but only version " + depVersion
												+ " is installed. Please upgrade the dependency");
								toRemove.add(w);
							}
						}
					}
				}
			}
		}
		for (Iterator i = toRemove.iterator(); i.hasNext();) {
			PluginWrapper w = (PluginWrapper) i.next();
			plugins.remove(w);
			pluginMap.remove(w.getName());
		}
	}

	/**
	 * Return the properties for the specified plugin, or <code>null</code> if
	 * no such plugin exists
	 * 
	 * @param name plugin name
	 * 
	 * @return properties
	 */
	public Properties getPluginProperties(String name) {
		PluginWrapper w = getPluginWrapper(name);
		return (w == null) ? null : w.properties;
	}

	/**
	 * Return the properties for the specified plugin
	 * 
	 * @param plugin plugin name
	 * 
	 * @return properties
	 */
	public Properties getPluginProperties(Plugin plugin) {
		return getPluginWrapper(plugin).properties;
	}

	public PluginWrapper getPluginWrapper(String name) {
		return (PluginWrapper) pluginMap.get(name);
	}

	public PluginWrapper getPluginWrapper(Class<? extends Plugin<?>> clazz) {
		for (PluginWrapper w : pluginMap.values()) {
			if (w.plugin.getClass().equals(clazz)) {
				return w;
			}
		}
		return null;
	}

	public PluginWrapper getPluginWrapper(Plugin plugin) {
		for (Enumeration e = plugins.elements(); e.hasMoreElements();) {
			PluginWrapper w = (PluginWrapper) e.nextElement();
			if (w.plugin == plugin)
				return w;
		}
		return null;
	}

	public void buildCLIOptions(Options options1) {
		for (Iterator i = plugins.iterator(); i.hasNext();) {
			PluginWrapper w = (PluginWrapper) i.next();
			w.plugin.buildCLIOptions(options1);
		}
	}

	/**
	 * Start all plugins
	 */
	public void start() {
		for (Iterator i = plugins.iterator(); i.hasNext();) {
			PluginWrapper w = (PluginWrapper) i.next();
			try {
				w.plugin.startPlugin(context);
				w.status.status = STATUS_STARTED;
				startedPlugins.add(w);
			} catch (Exception pe) {
				w.status.exception = pe;
				w.status.status = STATUS_ERRORED;
				context.log(PluginHostContext.LOG_ERROR, "Failed to start plugin " + w.properties.getProperty(PLUGIN_NAME), pe);
			}
		}
	}

	/**
	 * Activate all plugins
	 */
	public void activate() {
		for (PluginWrapper w : startedPlugins) {
			try {
				w.plugin.activatePlugin(context);
			} catch (PluginException pe) {
				w.status.exception = pe;
				w.status.status = STATUS_ERRORED;
				context.log(PluginHostContext.LOG_ERROR, "Failed to start plugin " + w.properties.getProperty(PLUGIN_NAME), pe);
			}
		}
	}

	/**
	 * Stop all plugins
	 */
	public void stop() {
		for (PluginWrapper w : startedPlugins) {
			try {
				w.plugin.stopPlugin();
				w.status.status = STATUS_STOPPED;
			} catch (PluginException pe) {
				w.status.exception = pe;
				context.log(PluginHostContext.LOG_ERROR, "Failed to stop plugin " + w.properties.getProperty(PLUGIN_NAME), pe);
			}
		}
	}

	/**
	 * Determine if all of the plugins are allowed to stop
	 * 
	 * @return can stop
	 */
	public boolean canStop() {
		for (PluginWrapper w : startedPlugins) {
			if (!w.plugin.canStopPlugin())
				return false;
		}
		return true;
	}

	/**
	 * Return an enumeration of all the plugins
	 * 
	 * @return enumeration of plugins
	 */
	public Enumeration<Plugin> plugins() {
		Vector<Plugin> v = new Vector<Plugin>();
		for (Enumeration<PluginWrapper> e = plugins.elements(); e.hasMoreElements();) {
			v.addElement(e.nextElement().plugin);
		}
		return v.elements();
	}

	/**
	 * Return the number of plugins loaded
	 * 
	 * @return number of plugins
	 */
	public int getPluginCount() {
		return plugins.size();
	}

	/**
	 * Return the number plugins at a given index
	 * 
	 * @param r index of plugin
	 * @return plugin
	 */
	public Plugin getPluginAt(int r) {
		return plugins.elementAt(r).plugin;
	}

	/**
	 * Return the plugin with the specified name or <code>null</code> if no
	 * plugin can be found
	 * 
	 * @param name name of plugin
	 * 
	 * @return plugin
	 */
	public Plugin getPlugin(String name) {
		PluginWrapper wrapper = getPluginWrapper(name);
		return (wrapper == null) ? null : wrapper.plugin;
	}

	/**
	 * Return the plugin with the specified clazz
	 * 
	 * @param clazz class of plugin
	 * @param <P> type of plugin
	 * @return plugin
	 */
	public <P extends Plugin<?>> P getPlugin(Class<P> clazz) {
		PluginWrapper wrapper = getPluginWrapper(clazz);
		return (wrapper == null) ? null : (P) wrapper.plugin;
	}

	/**
	 * Return the classloader used to load plugins
	 * 
	 * @return plugin
	 */
	public ClassLoader getPluginClassLoader() {
		return classLoader;
	}

	/**
	 * Return all plugins are of the specified class
	 * 
	 * @param pluginClass plugin class
	 * 
	 * @return DOCUMENT ME!
	 */
	public Plugin[] getPluginsOfClass(Class pluginClass) {
		Vector v = new Vector();
		for (Iterator i = plugins.iterator(); i.hasNext();) {
			PluginWrapper w = (PluginWrapper) i.next();
			if (pluginClass.isAssignableFrom(w.plugin.getClass()))
				v.addElement(w.plugin);
		}
		Plugin[] p = new Plugin[v.size()];
		v.copyInto(p);
		return p;
	}

	//
	public class PluginWrapper implements Comparable {
		Plugin plugin;
		Properties properties;
		PluginVersion version;
		int order;
		PluginStatus status = null;

		PluginWrapper(Plugin plugin, Properties properties) {
			this.plugin = plugin;
			status = new PluginStatus();
			this.properties = properties;
			version = new PluginVersion(properties.getProperty(PLUGIN_VERSION));
			order = 999;
			try {
				order = Integer.parseInt(properties.getProperty(PLUGIN_ORDER));
			} catch (Exception e) {
			}
		}

		public PluginStatus getStatus() {
			return status;
		}

		public PluginVersion getVersion() {
			return version;
		}

		public String getName() {
			return properties.getProperty(PLUGIN_NAME);
		}

		public int getOrder() {
			return order;
		}

		public int compareTo(Object arg0) {
			return new Integer(getOrder()).compareTo(new Integer(((PluginWrapper) arg0).getOrder()));
		}

		@Override
		public String toString() {
			return "PluginWrapper [order=" + order + ", plugin=" + plugin + ", properties=" + properties + ", status=" + status
					+ ", version=" + version + "]";
		}
	}

	/**
	 * Load a class and instantiate that is accessable by the specified plugins
	 * class loader.
	 * 
	 * @param plugin plugin name
	 * @param className class name to load
	 * @return class
	 * @throws ClassNotFoundException if class cannot be loaded
	 */
	public Class loadClass(String plugin, String className) throws ClassNotFoundException {
		Plugin p = getPlugin(plugin);
		if (p == null) {
			throw new ClassNotFoundException("The plugin " + plugin + " could not be located.");
		}
		return p.getClass().getClassLoader().loadClass(className);
	}

	/**
	 * Remove an plugin from those being currently managed. This would normally
	 * be to prevent a plugin from starting after the initialisation is
	 * complete.
	 * 
	 * @param plugin plugin to remove
	 */
	public void removePlugin(Plugin plugin) {
		PluginWrapper w = getPluginWrapper(plugin);
		plugins.remove(w);
		pluginMap.remove(w.getName());
	}

	public class PluginStatus {
		Throwable exception;
		int status;

		public PluginStatus() {
		}

		public int getStatus() {
			return status;
		}

		public Throwable getException() {
			return exception;
		}
	}

	public class MutableURLClassLoader extends URLClassLoader {
		public MutableURLClassLoader(URL[] arg0) {
			super(arg0);
		}

		public void addURL(URL arg0) {
			super.addURL(arg0);
		}
	}
}