/* HEADER */
package com.sshtools.virtualsession;

/**
 * Convenient adapter implementation of a {@link VirtualSessionListener}.
 */
public class VirtualSessionAdapter implements VirtualSessionListener {
	@Override
	public void connected(VirtualSession<?> session) {
	}

	@Override
	public void dataReceived(VirtualSession<?> session, byte[] data, int len) {
	}

	@Override
	public void dataSent(VirtualSession<?> session, byte[] data, int len) {
	}

	@Override
	public void disconnected(VirtualSession<?> session, Throwable exception) {
	}

	@Override
	public void titleChanged(VirtualSession<?> session, String title) {
	}
}
