/**
 * Maverick Virtual Session - Framework for a tabbed user interface of connections to some local or remote resources.
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
/* HEADER*/
package com.sshtools.profile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sshtools.profile.URI.MalformedURIException;

import nanoxml.XMLElement;

/**
 * A ResourceProfile stores everything that is needed to be known for a
 * {@link SchemeHandler} to be able to make a connection to a resource.<br>
 * <br>
 * The most important field in a ResourceProfile is the {@link URI}. The URI
 * will provide the primary details to connect a resource using a specified
 * scheme. The scheme will probably map to some network protocol that performs
 * the actual connection and optionally authentication. <br>
 * <br>
 * Long term persistence is provided for using XML.<br>
 * <br>
 * When used in an application context, it may be useful to store application
 * specific properties along with a profile. A profile provides this using its
 * <i>Application Properties</i>. The simple name / value pairs are will also
 * be persisted in the XML representation of the profile. Several convenience
 * methods for getting primitive types as well as strings are available.<br>
 * <br>
 * Profiles may also hold may implementors of {@link SchemeOptions}. This
 * interface allows scheme specific options that are more suited to an object
 * originated presentation rather than the simple name value pairs of
 * application properties. SchemeOptions implementations are also expected to
 * provide long term persistence using XML and they will be provided with an
 * XMLelement when the profile is read from storage.<br>
 * 
 * @param <T> the type of transport
 * 
 */
public class ResourceProfile<T extends ProfileTransport<?>> {
	// Private instance variables
	private URI uri;
	private String name;
	private Map<String, String> properties = new HashMap<String, String>();
	private Map<Class<? extends SchemeOptions>, SchemeOptions> schemeOptions = new HashMap<Class<? extends SchemeOptions>, SchemeOptions>();
	private Map<String, XMLElement> extensions = new HashMap<String, XMLElement>();
	private boolean needSave;
	private List<ResourceProfileListener> listeners = new ArrayList<ResourceProfileListener>();

	/**
	 * Construct a new profile from an existing one.
	 * 
	 * @param profile the profile to base this one on
	 */
	public ResourceProfile(ResourceProfile<?> profile) {
		setFromProfile(profile);
	}

	/**
	 * Configure this profile from another.
	 * 
	 * @param profile other profile
	 */
	public void setFromProfile(ResourceProfile<?> profile) {
		if (profile == this) {
			throw new IllegalArgumentException("Cannot set profile from itself.");
		}
		try {
			uri = new URI(profile.getURI().toString());
		} catch (MalformedURIException e) {
			throw new Error(e);
		}
		properties.clear();
		properties.putAll(profile.properties);
		extensions.clear();
		schemeOptions.clear();
		needSave = profile.needSave;
		try {
			if (profile.schemeOptions != null) {
				for (Class<? extends SchemeOptions> k : profile.schemeOptions.keySet()) {
					SchemeOptions sopts = profile.schemeOptions.get(k);
					schemeOptions.put(k, (SchemeOptions) sopts.clone());
				}
			}
		} catch (CloneNotSupportedException e) {
			throw new Error(e);
		}
	}

	/**
	 * Construct a new empty profile.
	 */
	public ResourceProfile() {
		this((URI)null);
	}

	/**
	 * Construct a new profile given a URI
	 * 
	 * @param uri uri
	 */
	public ResourceProfile(URI uri) {
		this(null, uri);
	}

	/**
	 * Construct a new profile given a URI and a name
	 * 
	 * @param name name
	 * @param uri uri
	 */
	public ResourceProfile(String name, URI uri) {
		try {
			setURI(uri == null ? new URI("null://") :  uri);
		} catch (MalformedURIException e) {
			throw new IllegalArgumentException("Invalid URI.", e);
		}
		setName(name);
	}

	/**
	 * Get the internet address for this profile (if applicable).
	 * 
	 * @return address
	 * @throws UnknownHostException if host not supplied or known
	 */
	public InetAddress getAddress() throws UnknownHostException {
		if (uri.getHost() == null || uri.getHost().length() == 0) {
			throw new UnknownHostException("Host not supplied in URI");
		}
		return InetAddress.getByName(uri.getHost());
	}

	/**
	 * Add a listener to the list that should be notified when something in the
	 * profile changes, or if the profile is loaded / saved.
	 * 
	 * @param l listener to add
	 */
	public void addResourceProfileListener(ResourceProfileListener l) {
		listeners.add(l);
	}

	/**
	 * Remove a listener from the list that should be notified when something in
	 * the profile changes, or if the profile is loaded / saved.
	 * 
	 * @param l listener to remove
	 */
	public void removeResourceProfileListener(ResourceProfileListener l) {
		listeners.remove(l);
	}

	/**
	 * Return a connected and authenticated {@link ProfileTransport} appropriate
	 * for this profile
	 * 
	 * @return ProfileTransport
	 * @throws ProfileException if there is any problem with the profile
	 * @throws IOException if the transport cannot connect
	 * @throws AuthenticationException if the transport cannot authenticate
	 */
	public T createProfileTransport() throws ProfileException, IOException, AuthenticationException {
		ConnectionManager mgr = ConnectionManager.getInstance();
		if (uri != null) {
			@SuppressWarnings("unchecked")
			SchemeHandler<T> handler = (SchemeHandler<T>) mgr.getSchemeHandler(uri.getScheme());
			if (handler == null) {
				throw new ProfileException("Could not locate SchemeHandler for scheme name " + uri.getScheme());
			}
			return handler.createProfileTransport(this);
		}
		return null;
	}

	/**
	 * Get if this profile has any scheme options for the given class.
	 * 
	 * @param clazz class of {@link SchemeOptions}
	 * @return has scheme options
	 */
	public boolean hasSchemeOptions(Class<? extends SchemeOptions> clazz) {
		return schemeOptions.containsKey(clazz);
	}

	/**
	 * Get the SchemeOptions
	 * 
	 * @param clazz the class ofthe scheme options object
	 * @param <C> the type of scheme options
	 * @return scheme options
	 */
	public <C extends SchemeOptions> C getSchemeOptions(Class<C> clazz) {
		@SuppressWarnings("unchecked")
		C sopts = (C) schemeOptions.get(clazz);
		if (sopts == null) {
			try {
				sopts = clazz.newInstance();
			} catch (InstantiationException e) {
				throw new IllegalArgumentException(e);
			} catch (IllegalAccessException e) {
				throw new IllegalArgumentException(e);
			}
			schemeOptions.put(clazz, sopts);
		}
		return sopts;
	}

	/**
	 * Set scheme specific options for this profile.
	 * 
	 * @param options scheme specific options to add
	 */
	public void setSchemeOptions(SchemeOptions options) {
		schemeOptions.put(options.getClass(), options);
	}

	/**
	 * Set the name for this profile
	 * 
	 * @param name name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Get the name for this profile
	 * 
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the URI for this resource
	 * 
	 * @param uri resource URI
	 */
	public void setURI(URI uri) {
		this.uri = uri;
	}

	/**
	 * Get the URI for this resource
	 * 
	 * @return resource URI
	 */
	public URI getURI() {
		return uri;
	}

	/**
	 * Load the profile given an input stream providing the XML data.
	 * 
	 * @param in instream providing XML data for profile
	 * @throws IOException if profile cannot be loaded
	 */
	public void load(InputStream in) throws IOException {
		loadImpl(in);
	}

	/**
	 * Save the profile given an output stream to write the XML data to.
	 * 
	 * @param out output stream to write XML data to
	 * @throws IOException if profile cannot be written
	 */
	public void save(OutputStream out) throws IOException {
		saveImpl(out);
	}

	private void saveImpl(OutputStream out) {
		XMLElement rootEl = new XMLElement();
		rootEl.setName("resourceProfile");
		rootEl.setAttribute("name", name == null ? "" : name);
		rootEl.setAttribute("uri", uri.toString());
		if (properties.size() > 0) {
			XMLElement propertiesEl = new XMLElement();
			propertiesEl.setName("properties");
			for (String k : properties.keySet()) {
				String v = properties.get(k);
				XMLElement propertyEl = new XMLElement();
				propertyEl.setName("property");
				propertyEl.setAttribute("name", k);
				propertyEl.setContent(v);
				propertiesEl.addChild(propertyEl);
			}
			rootEl.addChild(propertiesEl);
		}
		if (schemeOptions != null) {
			for (Class<? extends SchemeOptions> k : schemeOptions.keySet()) {
				SchemeOptions sopts = schemeOptions.get(k);
				XMLElement schemeOptionsEl = sopts.getElement();
				if (schemeOptionsEl != null) {
					schemeOptionsEl.setName("schemeOptions");
					schemeOptionsEl.setAttribute("scheme", sopts.getScheme());
					schemeOptionsEl.setAttribute("className", sopts.getClass().getName());
					rootEl.addChild(schemeOptionsEl);
				}
			}
		}
		if (extensions.size() > 0) {
			for (XMLElement x : extensions.values())
				rootEl.addChild(x);
		}
		PrintWriter writer = new PrintWriter(out);
		writer.println(rootEl.toString());
		writer.flush();
		needSave = false;
		for (int i = listeners.size() - 1; i >= 0; i--) {
			listeners.get(i).profileSaved();
		}
	}

	private void loadImpl(InputStream in) throws IOException {
		// Initialise
		properties.clear();
		schemeOptions.clear();
		extensions.clear();
		// Load the xml data
		XMLElement xml = new XMLElement();
		xml.parseFromReader(new InputStreamReader(in));
		name = (String) xml.getAttribute("name");
		uri = new URI((String) xml.getAttribute("uri"));
		if (name == null) {
			throw new IOException("Profile has no name attribute.");
		}
		XMLElement el;
		String n;
		for (Enumeration<?> e = xml.enumerateChildren(); e.hasMoreElements();) {
			el = (XMLElement) e.nextElement();
			/**
			 * Made the URI an attribute of the root element to gaurentee that
			 * we have the information before processing elements which may
			 * require it.
			 */
			if (el.getName().equals("properties")) {
				for (Enumeration<?> c = el.enumerateChildren(); c.hasMoreElements();) {
					el = (XMLElement) c.nextElement();
					if (el.getName().equals("property")) {
						n = (String) el.getAttribute("name");
						if (n == null || n.equals("")) {
							throw new IOException("Invalid propert element, name attribute must be specified.");
						}
						properties.put(n, el.getContent());
					} else {
						throw new IOException("Invalid profile properties element " + el.getName() + ".");
					}
				}
			} else if (el.getName().equals("schemeOptions")) {
				/**
				 * Using the classname to create a SchemeOptions caused problems
				 * with Obfuscation. This way we get the SchemeHandler to create
				 * a set of SchemeOptions using the createSchemeOptions method
				 * that it supports.
				 */
				try {
					SchemeOptions sopts = ConnectionManager.getInstance()
							.getSchemeHandler((String) el.getAttribute("scheme", uri.getScheme())).createSchemeOptions();
					sopts.init(el);
					schemeOptions.put(sopts.getClass(), sopts);
				} catch (Throwable t) {
					System.err.println("Could not create scheme specific options for " + uri.getScheme());
					t.printStackTrace();
				}
			} else {
				extensions.put(el.getName(), el);
			}
		}
		needSave = false;
		for (int i = listeners.size() - 1; i >= 0; i--) {
			listeners.get(i).profileLoaded();
		}
	}

	/**
	 * Return one of the properties as an <code>int</code> given its property
	 * name and a default value if the property does not exist is or invalid.
	 * 
	 * @param name property name
	 * @param defaultValue default value
	 * @return value
	 */
	public int getApplicationPropertyInt(String name, int defaultValue) {
		try {
			return Integer.parseInt(getApplicationProperty(name, String.valueOf(defaultValue)));
		} catch (NumberFormatException nfe) {
			return defaultValue;
		}
	}

	/**
	 * Return one of the properties as an <code>boolean</code> given its
	 * property name and a default value if the property does not exist is or
	 * invalid.
	 * 
	 * @param name property name
	 * @param defaultValue default value
	 * @return value
	 */
	public boolean getApplicationPropertyBoolean(String name, boolean defaultValue) {
		try {
			return new Boolean(getApplicationProperty(name, String.valueOf(defaultValue))).booleanValue();
		} catch (NumberFormatException nfe) {
			return defaultValue;
		}
	}

	/**
	 * Return one of the properties as a <code>String</code> given its property
	 * name and a default value if the property does not exist is or invalid.
	 * 
	 * @param name property name
	 * @param defaultValue default value
	 * @return value
	 */
	public String getApplicationProperty(String name, String defaultValue) {
		String value = properties.get(name);
		if (value == null) {
			return defaultValue;
		}
		return value;
	}

	/**
	 * Set a property given its name and a value
	 * 
	 * @param name property name
	 * @param value value
	 */
	public void setApplicationProperty(String name, String value) {
		properties.put(name, value);
	}

	/**
	 * Set a property given its name and a value
	 * 
	 * @param name property name
	 * @param value value
	 */
	public void setApplicationProperty(String name, int value) {
		properties.put(name, String.valueOf(value));
	}

	/**
	 * Set a property given its name and a value
	 * 
	 * @param name property name
	 * @param value value
	 */
	public void setApplicationProperty(String name, boolean value) {
		properties.put(name, String.valueOf(value));
	}

	/**
	 * Get an {@link Enumeration} of all the property keys
	 * 
	 * @return Enumeration of property keys
	 */
	public Collection<String> getApplicationPropertyKeys() {
		return properties.keySet();
	}

	/**
	 * Remove a property givens its name.
	 * 
	 * @param key name
	 */
	public void removeApplicationProperty(String key) {
		properties.remove(key);
	}

	/**
	 * Determine whether an application extension XML element is available
	 * within the profile.
	 * 
	 * @param name name
	 * @return has extension element
	 */
	public boolean hasApplicationExtension(String name) {
		return extensions.containsKey(name);
	}

	/**
	 * Get an application extension to the profile.
	 * 
	 * @param name name
	 * @return extension element
	 */
	public XMLElement getApplicationExtension(String name) {
		return extensions.get(name);
	}

	/**
	 * Add an application extension to the profile.
	 * 
	 * @param name name
	 */
	public void addApplicationExtension(String name) {
		XMLElement xml = new XMLElement();
		xml.setName(name);
		extensions.put(name, xml);
	}

	/**
	 * Get whether anything has changed since the profile was last loaded or
	 * saved.
	 * 
	 * @return Returns true if the profile needs saving.
	 */
	public boolean isNeedSave() {
		return needSave;
	}

	/**
	 * Set whether anything has changed since the profile was last loaded or
	 * saved.
	 * 
	 * @param needSave true if the profile needs saving.
	 */
	public void setNeedSave(boolean needSave) {
		if (this.needSave != needSave) {
			this.needSave = needSave;
		}
	}

	/**
	 * Fire to event listeners the profile has changed.
	 */
	public void fireProfileChanged() {
		for (int i = listeners.size() - 1; i >= 0; i--) {
			listeners.get(i).profileChanged();
		}
	}

	/**
	 * Set the username.
	 * 
	 * @param username username
	 */
	public void setUsername(String username) {
		String password = getPassword();
		try {
			uri.setUserinfo(username == null && password == null ? null
					: ((username == null ? "" : URLEncoder.encode(username, "UTF-8"))
							+ (password == null ? "" : (":" + URLEncoder.encode(password, "UTF-8")))));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Set the password.
	 * 
	 * @param password password
	 */
	public void setPassword(String password) {
		String username = getUsername();
		try {
			uri.setUserinfo(username == null && password == null ? null
					: ((username == null ? "" : URLEncoder.encode(username, "UTF-8"))
							+ (password == null ? "" : (":" + URLEncoder.encode(password, "UTF-8")))));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Get the username.
	 * 
	 * @return username
	 */
	public String getUsername() {
		if (uri == null || uri.getUserinfo() == null) {
			return null;
		}
		String userinfo = uri.getUserinfo();
		int idx = userinfo.indexOf(':');
		if (idx == -1) {
			idx = userinfo.length();
		}
		if (idx != -1) {
			try {
				return URLDecoder.decode(userinfo.substring(0, idx), "UTF-8");
			} catch (UnsupportedEncodingException e) {
			}
		}
		return null;
	}

	/**
	 * Get the password.
	 * 
	 * @return password
	 */
	public String getPassword() {
		if (uri == null || uri.getUserinfo() == null) {
			return null;
		}
		String userinfo = uri.getUserinfo();
		int idx = userinfo.indexOf(':');
		if (idx != -1) {
			try {
				return URLDecoder.decode(userinfo.substring(idx + 1), "UTF-8");
			} catch (UnsupportedEncodingException e) {
			}
		}
		return null;
	}

	/**
	 * Get the list of scheme options in this profile.
	 * 
	 * @return scheme options
	 */
	public List<SchemeOptions> getSchemeOptionsList() {
		return new ArrayList<SchemeOptions>(schemeOptions.values());
	}

	/**
	 * Load a profile from a file.
	 * 
	 * @param file file
	 * @return profile
	 * @throws IOException on any error
	 */
	public static ResourceProfile<ProfileTransport<?>> load(File file) throws IOException {
		FileInputStream fin = new FileInputStream(file);
		try {
			ResourceProfile<ProfileTransport<?>> p = new ResourceProfile<ProfileTransport<?>>();
			p.load(fin);
			return p;
		} finally {
			fin.close();
		}
	}
}
