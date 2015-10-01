/* HEADER*/
package com.sshtools.profile;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;

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
 * Long term persistance is provided for using XML.<br>
 * <br>
 * When used in an application context, it may be useful to store application
 * specific properties along with a profile. A profile provides this using its
 * <i>Application Properties</code>. The simple name / value pairs are will also
 * be persisted in the XML representation of the profile. Several convenience
 * methods for getting primitive types as well as strings are available.<br>
 * <br>
 * Profiles may also hold an implementor of {@link SchemeOptions}. This
 * interface allows scheme specific options that are more suited to an object
 * orientated presentation rather than the simple name value pairs of
 * application properties. SchemeOptions implemenations are also expected to
 * provide long term persistance using XML and they will be provided with an
 * XMLelement when the profile is read from storage.<br>
 * 
 * <code>
 * <code>
 * 
 * 
 * @author $Author: brett $
 */
public class ResourceProfile {

	// Private instance variables

	private URI uri;
	private String name;
	private Properties properties;
	private SchemeOptions schemeOptions;
	private Hashtable extensions;
	private boolean needSave;
	private Vector listeners;

	/**
	 * Construct a new empty profile.
	 */
	public ResourceProfile() {
		properties = new Properties();
		extensions = new Hashtable();
		needSave = false;
		listeners = new Vector();
	}

	/**
	 * Construct a new profile given a URI
	 * 
	 * @param uri
	 *            uri
	 */
	public ResourceProfile(URI uri) {
		this(null, uri);
	}

	/**
	 * Construct a new profile given a URI and a name
	 * 
	 * @param name
	 *            name
	 * @param uri
	 *            uri
	 */
	public ResourceProfile(String name, URI uri) {
		this();
		setURI(uri);
		setName(name);
	}

	/**
	 * Add a listener to the list that should be notified when something in the
	 * profile changes, or if the profile is loaded / saved.
	 * 
	 * @param l
	 *            listener to add
	 */
	public void addResourceProfileListener(ResourceProfileListener l) {
		listeners.addElement(l);
	}

	/**
	 * Remove a listener from the list that should be notified when something in
	 * the profile changes, or if the profile is loaded / saved.
	 * 
	 * @param l
	 *            listener to remove
	 */
	public void removeResourceProfileListener(ResourceProfileListener l) {
		listeners.removeElement(l);
	}

	/**
	 * Return a connected and authenticated {@link ProfileTransport} appropriate
	 * for this profile
	 * 
	 * @return ProfileTransport
	 * @throws ProfileException
	 *             if there is any problem with the profile
	 * @throws IOException
	 *             if the transport cannot connect
	 * @throws AuthenticationException
	 *             if the transport cannot authenticate
	 */
	public ProfileTransport createProfileTransport() throws ProfileException,
			IOException, AuthenticationException {
		ConnectionManager mgr = ConnectionManager.getInstance();
		if (uri != null) {
			SchemeHandler handler = mgr.getSchemeHandler(uri.getScheme());
			if (handler == null) {
				throw new ProfileException(
					"Could not locate SchemeHandler for scheme name "
						+ uri.getScheme());
			}
			return handler.createProfileTransport(this);
		}
		return null;
	}

	/**
	 * Get the SchemeOptions
	 * 
	 * @return scheme options
	 */
	public SchemeOptions getSchemeOptions() {
		return schemeOptions;
	}

	/**
	 * Set scheme specific options for this profile.
	 * 
	 * @param options
	 *            scheme specific options to add
	 */
	public void setSchemeOptions(SchemeOptions options) {
		schemeOptions = options;
	}

	/**
	 * Set the name for this profile
	 * 
	 * @param name
	 *            name
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
	 * @param uri
	 *            resource URI
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
	 * @param in
	 *            instream providing XML data for profile
	 * @throws IOException
	 *             if profile cannot be loaded
	 */
	public void load(InputStream in) throws IOException {
		loadImpl(in);
	}

	/**
	 * Save the profile given an output stream to write the XML data to.
	 * 
	 * @param out
	 *            output stream to write XML data to
	 * @throws IOException
	 *             if profile cannot be written
	 */
	public void save(OutputStream in) throws IOException {
		saveImpl(in);
	}

	private void saveImpl(OutputStream out) {
		XMLElement rootEl = new XMLElement();
		rootEl.setName("resourceProfile");
		rootEl.setAttribute("name", name == null ? "" : name);
		rootEl.setAttribute("uri", uri.toString());
		// XMLElement uriEl = new XMLElement();
		// uriEl.setName("uri");
		// uriEl.setContent(uri.toString());
		// rootEl.addChild(uriEl);
		if (properties.size() > 0) {
			XMLElement propertiesEl = new XMLElement();
			propertiesEl.setName("properties");
			for (Enumeration e = properties.keys(); e.hasMoreElements();) {
				String k = (String) e.nextElement();
				String v = properties.getProperty(k);
				XMLElement propertyEl = new XMLElement();
				propertyEl.setName("property");
				propertyEl.setAttribute("name", k);
				propertyEl.setContent(v);
				propertiesEl.addChild(propertyEl);
			}
			rootEl.addChild(propertiesEl);
		}
		if (schemeOptions != null) {
			XMLElement schemeOptionsEl = schemeOptions.getElement();
			if (schemeOptionsEl != null) {
				schemeOptionsEl.setAttribute("className", schemeOptions
					.getClass().getName());
				rootEl.addChild(schemeOptionsEl);
			}
		}
		if (extensions.size() > 0) {
			Enumeration e = extensions.elements();
			XMLElement el;
			while (e.hasMoreElements()) {
				el = (XMLElement) e.nextElement();
				rootEl.addChild(el);
			}
		}
		PrintWriter writer = new PrintWriter(out);
		writer.println(rootEl.toString());
		writer.flush();
		needSave = false;
		for (int i = listeners.size() - 1; i >= 0; i--) {
			ResourceProfileListener l = (ResourceProfileListener) listeners
				.elementAt(i);
			l.profileSaved();
		}
	}

	private void loadImpl(InputStream in) throws IOException {

		// Initialise
		properties.clear();
		schemeOptions = null;
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
		for (Enumeration e = xml.enumerateChildren(); e.hasMoreElements();) {
			el = (XMLElement) e.nextElement();
			/**
			 * Made the URI an attribute of the root element to gaurentee that
			 * we have the information before processing elements which may
			 * require it.
			 */
			if /*
				 * (el.getName().equals("uri")) { uri = new
				 * URI(el.getContent()); } else if
				 */(el.getName().equals("properties")) {
				for (Enumeration c = el.enumerateChildren(); c
					.hasMoreElements();) {
					el = (XMLElement) c.nextElement();
					if (el.getName().equals("property")) {
						n = (String) el.getAttribute("name");
						if (n == null || n.equals("")) {
							throw new IOException(
								"Invalid propert element, name attribute must be specified.");
						}
						properties.put(n, el.getContent());
					} else {
						throw new IOException(
							"Invalid profile properties element "
								+ el.getName() + ".");
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
					schemeOptions = ConnectionManager.getInstance()
						.getSchemeHandler(uri.getScheme())
						.createSchemeOptions();
					schemeOptions.init(el);
				} catch (Throwable t) {
					System.err
						.println("Could not create scheme specific options for "
							+ uri.getScheme());
				}

				/*
				 * String clazzName = (String) el.getAttribute("className"); if
				 * (clazzName != null) { try { schemeOptions = (SchemeOptions)
				 * Class.forName(clazzName). newInstance();
				 * schemeOptions.init(el); } catch (Throwable t) {
				 * System.err.println
				 * ("Could not create scheme specific options " + clazzName +
				 * ". " + t.getMessage()); } } else { throw new IOException(
				 * "Invalid schemeOptions element, must provide className attribute."
				 * ); }
				 */
			} else {
				extensions.put(el.getName(), el);
				// throw new IOException("Invalid profile element " +
				// el.getName() + ".");
			}
		}
		needSave = false;
		for (int i = listeners.size() - 1; i >= 0; i--) {
			ResourceProfileListener l = (ResourceProfileListener) listeners
				.elementAt(i);
			l.profileLoaded();
		}

	}

	/**
	 * Return one of the properties as an <code>int</code> given its property
	 * name and a default value if the property does not exist is or invalid.
	 * 
	 * @param name
	 *            property name
	 * @param defaultValue
	 *            default value
	 * @return value
	 */
	public int getApplicationPropertyInt(String name, int defaultValue) {
		try {
			return Integer.parseInt(getApplicationProperty(name, String
				.valueOf(defaultValue)));
		} catch (NumberFormatException nfe) {
			return defaultValue;
		}
	}

	/**
	 * Return one of the properties as an <code>boolean</code> given its
	 * property name and a default value if the property does not exist is or
	 * invalid.
	 * 
	 * @param name
	 *            property name
	 * @param defaultValue
	 *            default value
	 * @return value
	 */
	public boolean getApplicationPropertyBoolean(String name,
			boolean defaultValue) {
		try {
			return new Boolean(getApplicationProperty(name, String
				.valueOf(defaultValue))).booleanValue();
		} catch (NumberFormatException nfe) {
			return defaultValue;
		}
	}

	/**
	 * Return one of the properties as a <code>String</code> given its property
	 * name and a default value if the property does not exist is or invalid.
	 * 
	 * @param name
	 *            property name
	 * @param defaultValue
	 *            default value
	 * @return value
	 */
	public String getApplicationProperty(String name, String defaultValue) {
		String value = (String) properties.get(name);
		if (value == null) {
			return defaultValue;
		}
		return value;
	}

	/**
	 * Set a property given its name and a value
	 * 
	 * @param name
	 *            property name
	 * @param value
	 *            value
	 */
	public void setApplicationProperty(String name, String value) {
		properties.put(name, value);
	}

	/**
	 * Set a property given its name and a value
	 * 
	 * @param name
	 *            property name
	 * @param value
	 *            value
	 */
	public void setApplicationProperty(String name, int value) {
		properties.put(name, String.valueOf(value));
	}

	/**
	 * Set a property given its name and a value
	 * 
	 * @param name
	 *            property name
	 * @param value
	 *            value
	 */
	public void setApplicationProperty(String name, boolean value) {
		properties.put(name, String.valueOf(value));
	}

	/**
	 * Get an {@link Enumeration} of all the property keys
	 * 
	 * @return Enumeration of property keys
	 */
	public Enumeration getApplicationPropertyKeys() {
		return properties.keys();
	}

	/**
	 * Remove a property givens its name.
	 * 
	 * @param key
	 *            name
	 */
	public void removeApplicationProperty(String key) {
		properties.remove(key);
	}

	/**
	 * Determine whether an application extension XML element is available
	 * within the profile.
	 * 
	 * @param name
	 * @return
	 */
	public boolean hasApplicationExtension(String name) {
		return extensions.containsKey(name);
	}

	/**
	 * Get an application extension to the profile.
	 * 
	 * @param name
	 * @return
	 */
	public XMLElement getApplicationExtension(String name) {
		return (XMLElement) extensions.get(name);
	}

	/**
	 * Add an application extension to the profile.
	 * 
	 * @param name
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
	 * @param needSave
	 *            true if the profile needs saving.
	 */
	public void setNeedSave(boolean needSave) {
		if (this.needSave != needSave) {
			this.needSave = needSave;
		}
	}

	public void fireProfileChanged() {
		for (int i = listeners.size() - 1; i >= 0; i--) {
			ResourceProfileListener l = (ResourceProfileListener) listeners
				.elementAt(i);
			l.profileChanged();
		}
	}

	public String getUsername() {
		if(uri == null || uri.getUserinfo() == null) {
			return null;
		}
		String userinfo = uri.getUserinfo();
		int idx = userinfo.indexOf(':');
		if(idx == -1) {
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

	public String getPassword() {
		if(uri == null || uri.getUserinfo() == null) {
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
}
