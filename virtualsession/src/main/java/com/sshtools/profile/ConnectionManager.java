/**
 * Appframework
 * Copyright (C) 2003-2016 SSHTOOLS Limited
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 */
/* HEADER*/
package com.sshtools.profile;

import java.util.Vector;

/**
 * The ConnectionManager is responsible for accepting registrations of
 * {@link SchemeHandler} implementations and all them to be accessed by the
 * scheme name.</p>
 * 
 * @author $Author: brett $
 */
public class ConnectionManager {

    private static ConnectionManager instance;

    // Private instance variables
    private Vector handlers;

    /*
     * Prevent instantiation outside of this class
     */
    private ConnectionManager() {
        handlers = new Vector();
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
     * @param idx indenx
     * @return SchemeHandler
     */
    public SchemeHandler getSchemeHandler(int idx) {
        return (SchemeHandler) handlers.elementAt(idx);
    }

    /**
     * Register a SchemeHandler.
     * 
     * @param schemeHandler SchemeHandler
     */
    public void registerSchemeHandler(SchemeHandler schemeHandler) {
        SchemeHandler currentHandler = getSchemeHandler(schemeHandler.getName());
        if (currentHandler != null) {
            throw new IllegalArgumentException("Scheme handler for " + schemeHandler.getName() + " is already regisered ("
                            + currentHandler.getClass() + ")");
        }
        handlers.addElement(schemeHandler);
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
     * @param name scheme name,
     * @return SchemeHandler
     */
    public SchemeHandler getSchemeHandler(String name) {
        for (int i = 0; i < getSchemeHandlerCount(); i++) {
            SchemeHandler h = getSchemeHandler(i);
            if (h.getName().equals(name)) {
                return h;
            }
        }
        return null;
    }
}
