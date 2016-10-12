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
 * Convenient adapter for VirtualSessionManagerListener 
 *
 * @author $Author: brett $
 */

public abstract class VirtualSessionManagerAdapter
    implements VirtualSessionManagerListener {

    /* (non-Javadoc)
     * @see com.sshtools.virtualsession.VirtualSessionManagerListener#virtualSessionChanged(com.sshtools.virtualsession.VirtualSession)
     */
    public void virtualSessionChanged(VirtualSession session) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see com.sshtools.virtualsession.VirtualSessionManagerListener#virtualSessionAdded(com.sshtools.virtualsession.VirtualSession)
     */
    public void virtualSessionAdded(VirtualSession session) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see com.sshtools.virtualsession.VirtualSessionManagerListener#virtualSessionRemoved(com.sshtools.virtualsession.VirtualSession)
     */
    public void virtualSessionRemoved(VirtualSession session) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see com.sshtools.virtualsession.VirtualSessionManagerListener#virtualSessionSelected(com.sshtools.virtualsession.VirtualSession)
     */
    public void virtualSessionSelected(VirtualSession session) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see com.sshtools.virtualsession.VirtualSessionManagerListener#virtualSessionDeselected(com.sshtools.virtualsession.VirtualSession)
     */
    public void virtualSessionDeselected(VirtualSession session) {
        // TODO Auto-generated method stub
        
    }
}
