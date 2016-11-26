/* HEADER*/
package com.sshtools.profile;

import java.util.ArrayList;
import java.util.List;

/**
 * The ConnectionManager is responsible for accepting registrations of
 * {@link SchemeHandler} implementations and all them to be accessed by the
 * scheme name.
 * </p>
 */
public class ConnectionManager {

	private static ConnectionManager instance;

	// Private instance variables
	private List<SchemeHandler<? extends ProfileTransport<?>>> handlers;

	/*
	 * Prevent instantiation outside of this class
	 */
	private ConnectionManager() {
		handlers = new ArrayList<>();
	}

	/**
	 * Return the number of the SchemeHandlers this connection manager is
	 * managing.
	 * 
	 * @return scheme count
	 */
	public int getSchemeHandlerCount() {
		return handlers.size();
	}

	/**
	 * Return the SchemeHandler at the specified index.
	 * 
	 * @param idx
	 *            indenx
	 * @return SchemeHandler
	 */
	public SchemeHandler<? extends ProfileTransport<?>> getSchemeHandler(int idx) {
		return handlers.get(idx);
	}

	/**
	 * Register a SchemeHandler.
	 * 
	 * @param schemeHandler
	 *            SchemeHandler
	 */
	public void registerSchemeHandler(SchemeHandler<? extends ProfileTransport<?>> schemeHandler) {
		SchemeHandler<? extends ProfileTransport<?>> currentHandler = getSchemeHandler(schemeHandler.getName());
		if (currentHandler != null) {
			throw new IllegalArgumentException("Scheme handler for " + schemeHandler.getName()
					+ " is already regisered (" + currentHandler.getClass() + ")");
		}
		handlers.add(schemeHandler);
	}

	/**
	 * Return an instance of the connection manager, creating it if required
	 * 
	 * @return connection mananger
	 */
	public static ConnectionManager getInstance() {
		if (instance == null) {
			instance = new ConnectionManager();
		}
		return instance;
	}

	/**
	 * Get a SchemeHandler given the scheme name
	 * 
	 * @param name
	 *            scheme name,
	 * @return SchemeHandler
	 */
	public SchemeHandler<? extends ProfileTransport<?>> getSchemeHandler(String name) {
		for (int i = 0; i < getSchemeHandlerCount(); i++) {
			SchemeHandler<? extends ProfileTransport<?>> h = getSchemeHandler(i);
			if (h.getName().equals(name)) {
				return h;
			}
		}
		return null;
	}
}
