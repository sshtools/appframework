/*
 */
package com.sshtools.profile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * 
 * @author $Author: brett $
 */
public abstract class SchemeHandler {
	private String name;
	private String description;

	/**
	 * Construct a new SchemeHandler
	 * 
	 * @param name
	 *            scheme name
	 * @param description
	 *            description
	 */
	public SchemeHandler(String name, String description) {
		this.name = name;
		this.description = description;
	}

	/**
	 * Create a {@link SchemeOptions} appropriate for this scheme
	 * 
	 * @return scheme options
	 */
	public abstract SchemeOptions createSchemeOptions();

	/**
	 * Create multiple {@link SchemeOptions} appropriate for this scheme.
	 * Schemes that can contain multiple options types can return the additional
	 * schemes objects pre-configured.
	 * 
	 * @return list of scheme options
	 */
	public List createMultipleSchemeOptions() {
		List l = new ArrayList();
		SchemeOptions createSchemeOptions = createSchemeOptions();
		if (createSchemeOptions != null) {
			l.add(createSchemeOptions);
		}
		return l;
	}

	/**
	 * Create the {@link ProfileTransport} appropriate for this scheme
	 * 
	 * @return profile transport
	 */
	public abstract ProfileTransport createProfileTransport(
			ResourceProfile profile) throws ProfileException, IOException,
			AuthenticationException;

	/**
	 * Get the scheme name
	 * 
	 * @return scheme name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get the scheme description
	 * 
	 * @return scheme description
	 */
	public String getDescription() {
		return description;
	}
}