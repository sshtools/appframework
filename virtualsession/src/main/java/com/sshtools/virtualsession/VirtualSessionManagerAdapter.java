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
