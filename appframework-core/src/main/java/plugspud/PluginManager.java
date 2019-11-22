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
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.cli.Options;
import org.apache.commons.lang3.StringUtils;

public class PluginManager<T extends PluginHostContext> {
	public class MutableURLClassLoader extends URLClassLoader {
		public MutableURLClassLoader(URL[] arg0) {
			super(arg0);
		}

		@Override
		public void addURL(URL arg0) {
			super.addURL(arg0);
		}
	}

	public class PluginStatus {
		Throwable exception;
		int status;

		public PluginStatus() {
		}

		public Throwable getException() {
			return exception;
		}

		public int getStatus() {
			return status;
		}
	}

	//
	public class PluginWrapper<P extends Plugin<T>> implements Comparable<PluginWrapper<P>> {
		int order;
		P plugin;
		Properties properties;
		PluginStatus status = null;
		PluginVersion version;

		PluginWrapper(P plugin, Properties properties) {
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

		@Override
		public int compareTo(PluginWrapper<P> arg0) {
			return new Integer(getOrder()).compareTo(new Integer((arg0).getOrder()));
		}

		public String getName() {
			return properties.getProperty(PLUGIN_NAME);
		}

		public int getOrder() {
			return order;
		}

		public PluginStatus getStatus() {
			return status;
		}

		public PluginVersion getVersion() {
			return version;
		}

		@Override
		public String toString() {
			return "PluginWrapper [order=" + order + ", plugin=" + plugin + ", properties=" + properties + ", status=" + status
					+ ", version=" + version + "]";
		}
	}

	public final static String PLUGIN_AUTHOR = "author";
	public final static String PLUGIN_CLASSNAME = "className";
	public final static String PLUGIN_DEPENDENCIES = "dependsOn";
	public final static String PLUGIN_INFORMATION = "information";
	public final static String PLUGIN_JARS = "jars";
	public final static String PLUGIN_NAME = "name";
	public final static String PLUGIN_ORDER = "order";
	public final static String PLUGIN_REQUIRED_HOST_VERSION = "requiredHostVersion";
	public final static String PLUGIN_RESOURCE = "resource";
	public final static String PLUGIN_SHORT_DESCRIPTION = "shortDescription";
	public final static String PLUGIN_URL = "url";
	// Plugin properties
	public final static String PLUGIN_VERSION = "version";
	public final static int STATUS_ERRORED = 3;
	public final static int STATUS_STARTED = 1;
	public final static int STATUS_STOPPED = 2;
	public final static int STATUS_ACTIVATED = 4;
	// Plugin statis
	public final static int STATUS_UNINITIALIZED = 0;
	private Set<PluginWrapper<Plugin<T>>> activatedPlugins;
	private ClassLoader classLoader;
	private T context;
	private boolean initialised;
	private ScheduledExecutorService loadQueue;
	private Object lock = new Object();
	private Map<String, Set<PluginWrapper<Plugin<T>>>> onLoad;
	private ClassLoader parentClassLoader;
	// Private instance variables
	private File pluginDir;
	private HashMap<String, PluginWrapper<Plugin<T>>> pluginMap;
	private List<PluginWrapper<Plugin<T>>> plugins;
	private Set<PluginWrapper<Plugin<T>>> processedPlugins;
	private Set<PluginWrapper<Plugin<T>>> startedPlugins;

	/**
	 * Activate all plugins
	 */
	public void activate() {
		processedPlugins.clear();
		buildDependencyTree();
		/*
		 * First start all plugins that do not have any dependencies, these can
		 * all be started in their own thread (well as many as are available)
		 */
		int started = 0;
		for (PluginWrapper<Plugin<T>> w : plugins) {
			String deps = w.properties.getProperty(PLUGIN_DEPENDENCIES);
			if (StringUtils.isBlank(deps)) {
				started++;
				context.log(PluginHostContext.LOG_DEBUG,
						"Activating root plugin " + w.getName() + " [" + w.plugin.getClass() + "]");
				synchronized (lock) {
					loadQueue.execute(() -> activatePlugin(w));
				}
			}
		}
		if (plugins.size() > 0 && started == 0) {
			context.log(PluginHostContext.LOG_ERROR,
					"While there are plugins to load, a circular dependency configuration was discovered preventing any being used. Please contact the developer.");
		}
		try {
			waitForPlugins();
		} catch (InterruptedException ie) {
			context.log(PluginHostContext.LOG_ERROR, "Interrupted activating plugins", ie);
		}
	}

	/**
	 * Manually add a plugin.
	 * 
	 * @param plugin plugin
	 * @param properties properties
	 */
	public void addPlugin(Plugin<T> plugin, Properties properties) {
		PluginWrapper<Plugin<T>> w = new PluginWrapper<Plugin<T>>(plugin, properties);
		plugins.add(w);
		pluginMap.put(w.getName(), w);
	}

	public void buildCLIOptions(Options options1) {
		for (PluginWrapper<Plugin<T>> w : plugins) {
			w.plugin.buildCLIOptions(options1);
		}
	}

	/**
	 * Determine if all of the plugins are allowed to stop
	 * 
	 * @return can stop
	 */
	public boolean canStop() {
		for (PluginWrapper<Plugin<T>> w : startedPlugins) {
			if (!w.plugin.canStopPlugin())
				return false;
		}
		return true;
	}

	/**
	 * Return the plugin with the specified clazz
	 * 
	 * @param clazz class of plugin
	 * @param <P> type of plugin
	 * @return plugin
	 */
	@SuppressWarnings("unchecked")
	public <P extends Plugin<?>> P getPlugin(Class<P> clazz) {
		PluginWrapper<Plugin<T>> wrapper = getPluginWrapper(clazz);
		return (wrapper == null) ? null : (P) wrapper.plugin;
	}

	/**
	 * Return the plugin with the specified name or <code>null</code> if no
	 * plugin can be found
	 * 
	 * @param name name of plugin
	 * 
	 * @return plugin
	 */
	public Plugin<T> getPlugin(String name) {
		PluginWrapper<Plugin<T>> wrapper = getPluginWrapper(name);
		return (wrapper == null) ? null : wrapper.plugin;
	}

	/**
	 * Return the number plugins at a given index
	 * 
	 * @param r index of plugin
	 * @return plugin
	 */
	public Plugin<T> getPluginAt(int r) {
		return plugins.get(r).plugin;
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
	 * Return the number of plugins loaded
	 * 
	 * @return number of plugins
	 */
	public int getPluginCount() {
		return plugins.size();
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
	 * Return the properties for the specified plugin
	 * 
	 * @param plugin plugin name
	 * 
	 * @return properties
	 */
	public Properties getPluginProperties(Plugin<T> plugin) {
		return getPluginWrapper(plugin).properties;
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
		PluginWrapper<Plugin<T>> w = getPluginWrapper(name);
		return (w == null) ? null : w.properties;
	}

	/**
	 * Return all plugins are of the specified class
	 * 
	 * @param pluginClass plugin class
	 * 
	 * @return DOCUMENT ME!
	 */
	@SuppressWarnings("unchecked")
	public Plugin<T>[] getPluginsOfClass(Class<? extends Plugin<T>> pluginClass) {
		List<Plugin<T>> v = new ArrayList<>();
		for (PluginWrapper<Plugin<T>> w : plugins) {
			if (pluginClass.isAssignableFrom(w.plugin.getClass()))
				v.add(w.plugin);
		}
		return v.toArray(new Plugin[0]);
	}

	public PluginWrapper<Plugin<T>> getPluginWrapper(Class<? extends Plugin<?>> clazz) {
		for (PluginWrapper<Plugin<T>> w : plugins) {
			if (w.plugin.getClass().equals(clazz)) {
				return w;
			}
		}
		return null;
	}

	public PluginWrapper<Plugin<T>> getPluginWrapper(Plugin<T> plugin) {
		for (PluginWrapper<Plugin<T>> w : plugins) {
			if (w.plugin == plugin)
				return w;
		}
		return null;
	}

	public PluginWrapper<Plugin<T>> getPluginWrapper(String name) {
		return pluginMap.get(name);
	}

	/**
	 * Initialise the plugin manager
	 * 
	 * @param context context
	 * 
	 * @throws PluginException on any errors
	 */
	public void init(T context) throws PluginException {
		this.context = context;
		loadQueue = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors() * 2);
		pluginMap = new HashMap<String, PluginWrapper<Plugin<T>>>();
		plugins = new ArrayList<PluginWrapper<Plugin<T>>>();
		startedPlugins = new HashSet<PluginWrapper<Plugin<T>>>();
		activatedPlugins = new HashSet<PluginWrapper<Plugin<T>>>();
		processedPlugins = new HashSet<PluginWrapper<Plugin<T>>>();
		onLoad = new HashMap<String, Set<PluginWrapper<Plugin<T>>>>();
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
				try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(removeFile)))) {
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
				@Override
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
			URL[] urls = v.toArray(new URL[v.size()]);
			classLoader = parentClassLoader;
			if (classLoader == null) {
				classLoader = Thread.currentThread().getContextClassLoader();
				if (classLoader == null) {
					classLoader = getClass().getClassLoader();
				}
			}
			classLoader = new URLClassLoader(urls, classLoader);
			((URLClassLoader) classLoader).getURLs();
			// Add the standard plugins
			URL url = context.getStandardPluginsResource();
			if (url != null) {
				context.log(PluginHostContext.LOG_INFORMATION, "Loading only standard plugins from  " + url.toExternalForm());
				try {
					loadPlugins(url, classLoader, true);
				} catch (PluginException pe) {
					context.log(PluginHostContext.LOG_ERROR, pe);
				}
			} else {
				// Add the plugins
				context.log(PluginHostContext.LOG_INFORMATION, "Looking for plugin jars");
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

	/**
	 * Load a class and instantiate that is accessable by the specified plugins
	 * class loader.
	 * 
	 * @param plugin plugin name
	 * @param className class name to load
	 * @return class
	 * @throws ClassNotFoundException if class cannot be loaded
	 */
	public Class<?> loadClass(String plugin, String className) throws ClassNotFoundException {
		Plugin<T> p = getPlugin(plugin);
		if (p == null) {
			throw new ClassNotFoundException("The plugin " + plugin + " could not be located.");
		}
		return p.getClass().getClassLoader().loadClass(className);
	}

	/**
	 * Load a plugins.properties property sheet into separate property sheets,
	 * one for each plugin
	 * 
	 * @param url location of plugins.properties resource
	 * @return <code>HashMap</code> of properties
	 * @throws PluginException on error
	 */
	public HashMap<String, Properties> loadPluginProperties(URL url) throws PluginException {
		HashMap<String, Properties> props = new HashMap<>();
		InputStream in = null;
		try {
			in = url.openStream();
			Properties p = new Properties();
			p.load(in);
			for (Enumeration<Object> e = p.keys(); e.hasMoreElements();) {
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
				Properties h = props.get(name);
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
			HashMap<String, Properties> props = loadPluginProperties(url);
			for (Map.Entry<String, Properties> en : props.entrySet()) {
				Properties properties = en.getValue();
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
				List<PluginWrapper<Plugin<T>>> found = new ArrayList<>();
				context.log(PluginHostContext.LOG_DEBUG, "Looking for plugin " + name);
				for (PluginWrapper<Plugin<T>> pw : plugins) {
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
						@SuppressWarnings("unchecked")
						Plugin<T> plugin = (Plugin<T>) Class.forName(className, true, classLoader).newInstance();
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

	/**
	 * Return an enumeration of all the plugins
	 * 
	 * @return enumeration of plugins
	 */
	public List<Plugin<T>> plugins() {
		List<Plugin<T>> v = new ArrayList<>();
		for (PluginWrapper<Plugin<T>> w : plugins) {
			v.add(w.plugin);
		}
		return v;
	}

	/**
	 * Remove an plugin from those being currently managed. This would normally
	 * be to prevent a plugin from starting after the initialisation is
	 * complete.
	 * 
	 * @param plugin plugin to remove
	 */
	public void removePlugin(Plugin<T> plugin) {
		PluginWrapper<Plugin<T>> w = getPluginWrapper(plugin);
		plugins.remove(w);
		pluginMap.remove(w.getName());
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
	 * Start all plugins
	 */
	public void start() {
		context.log(PluginHostContext.LOG_DEBUG, "Plugins :-");
		int i = 1;
		for(PluginWrapper<Plugin<T>> plugin : plugins) {
			context.log(PluginHostContext.LOG_DEBUG, String.format("    %2d : %s", i++, plugin.getName()));
		}
		
		processedPlugins.clear();
		buildDependencyTree();
		/*
		 * First start all plugins that do not have any dependencies, these can
		 * all be started in their own thread (well as many as are available)
		 */
		int started = 0;
		for (PluginWrapper<Plugin<T>> w : plugins) {
			String deps = w.properties.getProperty(PLUGIN_DEPENDENCIES);
			if (StringUtils.isBlank(deps)) {
				started++;
				context.log(PluginHostContext.LOG_DEBUG,
						"Starting root plugin " + w.getName() + " [" + w.plugin.getClass() + "]");
				synchronized (lock) {
					loadQueue.execute(() -> startPlugin(w));
				}
			}
		}
		if (plugins.size() > 0 && started == 0) {
			context.log(PluginHostContext.LOG_ERROR,
					"While there are plugins to load, a circular dependency configuration was discovered preventing any being used. Please contact the developer.");
		}
		try {
			waitForPlugins();
		} catch (InterruptedException ie) {
			context.log(PluginHostContext.LOG_ERROR, "Interrupted starting plugins", ie);
		}
	}

	/**
	 * Stop all plugins
	 */
	public void stop() {
		for (PluginWrapper<Plugin<T>> w : plugins) {
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

	private void activatePlugin(PluginWrapper<Plugin<T>> w) {
		try {
			context.log(PluginHostContext.LOG_DEBUG, String.format("Activating plugin %2d : %s [%s]", activatedPlugins.size() + 1, w.getName(), w.plugin.getClass()));
			w.plugin.activatePlugin(context);
			context.log(PluginHostContext.LOG_DEBUG, String.format("Activated plugin %2d : %s [%s]", activatedPlugins.size() + 1, w.getName(), w.plugin.getClass()));
			w.status.status = STATUS_ACTIVATED;
			synchronized (lock) {
				processedPlugins.add(w);
				activatedPlugins.add(w);
				for (PluginWrapper<Plugin<T>> en : plugins) {
					if (en != w) {
						String pname = en.properties.getProperty(PLUGIN_NAME);
						Set<PluginWrapper<Plugin<T>>> l = onLoad.get(pname);
						if (l != null) {
							for (PluginWrapper<Plugin<T>> p : new ArrayList<>(l)) {
								if (!activatedPlugins.contains(p)) {
									if (areAllDepsReady(p)) {
										removeFromPlan(p);
										loadQueue.execute(() -> activatePlugin(p));
									}
								}
							}
						}
					}
				}
			}
		} catch (Exception pe) {
			w.status.exception = pe;
			w.status.status = STATUS_ERRORED;
			context.log(PluginHostContext.LOG_ERROR, "Failed to activate plugin " + w.properties.getProperty(PLUGIN_NAME), pe);
		} finally {
			processedPlugins.add(w);
		}
	}

	private boolean areAllDepsReady(PluginWrapper<Plugin<T>> p) {
		boolean allDeps = true;
		String[] deps = p.properties.getProperty(PLUGIN_DEPENDENCIES).split(",");
		if (StringUtils.isNoneBlank(deps)) {
			for (String dep : deps) {
				PluginWrapper<Plugin<T>> depPlugin = getPluginWrapper(dep);
				if (!processedPlugins.contains(depPlugin)) {
					allDeps = false;
				}
			}
		}
		return allDeps;
	}

	private void buildDependencyTree() {
		onLoad.clear();
		for (PluginWrapper<Plugin<T>> w : plugins) {
			String deps = w.properties.getProperty(PLUGIN_DEPENDENCIES);
			if (StringUtils.isNotBlank(deps)) {
				synchronized (startedPlugins) {
					for (String dep : deps.split(",")) {
						dep = dep.trim();
						Set<PluginWrapper<Plugin<T>>> l = onLoad.get(dep);
						if (l == null)
							l = new LinkedHashSet<>();
						l.add(w);
						onLoad.put(dep, l);
					}
				}
			}
		}
	}

	private void checkDependencies() {
		List<PluginWrapper<Plugin<T>>> toRemove = new ArrayList<>();
		for (PluginWrapper<Plugin<T>> w : plugins) {
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
					PluginWrapper<Plugin<T>> dep = getPluginWrapper(depName);
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
		for (PluginWrapper<Plugin<T>> w : toRemove) {
			plugins.remove(w);
			pluginMap.remove(w.getName());
		}
	}

	private String debugPlist(Set<PluginManager<T>.PluginWrapper<Plugin<T>>> value, String key) {
		StringBuilder b = new StringBuilder();
		b.append(key);
		b.append("=");
		for (PluginWrapper<Plugin<T>> pl : value) {
			b.append(pl.getName());
			b.append(",");
		}
		return b.toString();
	}

	private void findJars(File dir, Vector<URL> list) {
		File[] f = pluginDir.listFiles(new FileFilter() {
			@Override
			public boolean accept(File file) {
				return file.isDirectory() || file.getName().toLowerCase().endsWith(".jar");
			}
		});
		for (int i = 0; f != null && i < f.length; i++) {
			if (f[i].isDirectory()) {
				findJars(f[i], list);
			} else {
				try {
					list.addElement(f[i].toURI().toURL());
				} catch (MalformedURLException e) {
				}
			}
		}
	}

	private void removeFromPlan(PluginWrapper<Plugin<T>> p) {
		/*
		 * Remove this plugin from all the other onload triggers too
		 */
		for (Iterator<String> it = onLoad.keySet().iterator(); it.hasNext();) {
			String k = it.next();
			Set<PluginWrapper<Plugin<T>>> ll = onLoad.get(k);
			ll.remove(p);
			if (ll.isEmpty())
				it.remove();
		}
	}

	private void startPlugin(PluginWrapper<Plugin<T>> w) {
		try {
			context.log(PluginHostContext.LOG_DEBUG, String.format("Starting plugin %2d : %s [%s]", startedPlugins.size() + 1, w.getName(), w.plugin.getClass()));
			w.plugin.startPlugin(context);
			context.log(PluginHostContext.LOG_DEBUG, String.format("Started plugin %2d : %s [%s]", startedPlugins.size() + 1, w.getName(), w.plugin.getClass()));
			w.status.status = STATUS_STARTED;
			synchronized (lock) {
				processedPlugins.add(w);
				startedPlugins.add(w);
				for (PluginWrapper<Plugin<T>> en : plugins) {
					if (en != w) {
						String pname = en.properties.getProperty(PLUGIN_NAME);
						Set<PluginWrapper<Plugin<T>>> l = onLoad.get(pname);
						if (l != null) {
							for (PluginWrapper<Plugin<T>> p : new ArrayList<>(l)) {
								if (!startedPlugins.contains(p)) {
									if (areAllDepsReady(p)) {
										removeFromPlan(p);
										loadQueue.execute(() -> startPlugin(p));
									}
								}
							}
						}
					}
				}
			}
		} catch (Exception pe) {
			processedPlugins.add(w);
			w.status.exception = pe;
			w.status.status = STATUS_ERRORED;
			context.log(PluginHostContext.LOG_ERROR, "Failed to start plugin " + w.properties.getProperty(PLUGIN_NAME), pe);
		} finally {
			processedPlugins.add(w);
		}
	}

	private void waitForPlugins() throws InterruptedException {
		do {
			List<PluginWrapper<Plugin<T>>> pp = new ArrayList<>(plugins);
			pp.removeAll(processedPlugins);
			//loadQueue.awaitTermination(50, TimeUnit.MILLISECONDS);
			loadQueue.awaitTermination(50, TimeUnit.MILLISECONDS);

//			System.out.println("------------------------");
//			List<PluginWrapper<Plugin<T>>> nl = new ArrayList<>(plugins);
//			nl.removeAll(processedPlugins);
//			System.out.println(nl);
			
		} while (processedPlugins.size() != plugins.size());
		if (!onLoad.isEmpty()) {
			StringBuilder b = new StringBuilder();
			for (Map.Entry<String, Set<PluginWrapper<Plugin<T>>>> en : onLoad.entrySet()) {
				b.append(debugPlist(en.getValue(), en.getKey()));
				b.append("\n");
			}
			context.log(PluginHostContext.LOG_ERROR, "There were unsatisfied dependencies. " + b.toString());
		}
	}
}