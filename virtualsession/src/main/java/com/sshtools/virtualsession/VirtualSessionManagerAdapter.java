/* HEADER */
package com.sshtools.virtualsession;

/**
 * Convenient adapter for VirtualSessionManagerListener
 */
public abstract class VirtualSessionManagerAdapter implements VirtualSessionManagerListener {
	public void virtualSessionChanged(VirtualSession<?, ?> session) {
	}

	public void virtualSessionAdded(VirtualSession<?, ?> session) {
	}

	public void virtualSessionRemoved(VirtualSession<?, ?> session) {
	}

	public void virtualSessionSelected(VirtualSession<?, ?> session) {
	}

	public void virtualSessionDeselected(VirtualSession<?, ?> session) {
	}
}
