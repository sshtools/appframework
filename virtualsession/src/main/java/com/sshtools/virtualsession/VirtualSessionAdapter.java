/**
 * Appframework
 * Copyright (C) 2003-2016 SSHTOOLS Limited
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 */
/* HEADER */
package com.sshtools.virtualsession;

/**
 * Convenient adapter implementation of a {@link VirtualSessionListener}.
 *
 * @author $Author: brett $
 *
 */
public class VirtualSessionAdapter
    implements VirtualSessionListener {
    /* (non-Javadoc)
     * @see com.sshtools.virtualsession.VirtualSessionListener#connected(com.sshtools.virtualsession.VirtualSession)
     */
    public void connected(VirtualSession session) {
    }
    /* (non-Javadoc)
     * @see com.sshtools.virtualsession.VirtualSessionListener#dataReceived(com.sshtools.virtualsession.VirtualSession, byte[], int)
     */
    public void dataReceived(VirtualSession session, byte[] data, int len) {
    }
    /* (non-Javadoc)
     * @see com.sshtools.virtualsession.VirtualSessionListener#dataSent(com.sshtools.virtualsession.VirtualSession, byte[], int)
     */
    public void dataSent(VirtualSession session, byte[] data, int len) {
    }
    /* (non-Javadoc)
     * @see com.sshtools.virtualsession.VirtualSessionListener#disconnected(com.sshtools.virtualsession.VirtualSession)
     */
    public void disconnected(VirtualSession session, Throwable exception) {
    }
    /* (non-Javadoc)
     * @see com.sshtools.virtualsession.VirtualSessionListener#titleChanged(com.sshtools.virtualsession.VirtualSession, java.lang.String)
     */
    public void titleChanged(VirtualSession session, String title) {
    }
}
