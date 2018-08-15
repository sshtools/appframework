/* HEADER */
package com.sshtools.virtualsession;

/**
 * Convenient adapter for VirtualSessionManagerListener
 */
public abstract class VirtualSessionManagerAdapter implements VirtualSessionManagerListener {
	@Override
	public void virtualSessionChanged(VirtualSession<?, ?> session) {
	}

	@Override
	public void virtualSessionAdded(VirtualSession<?, ?> session) {
	}

	@Override
	public void virtualSessionRemoved(VirtualSession<?, ?> session) {
	}

	@Override
	public void virtualSessionSelected(VirtualSession<?, ?> session) {
	}

	@Override
	public void virtualSessionDeselected(VirtualSession<?, ?> session) {
	}
}
