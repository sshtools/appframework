/* HEADER */
package com.sshtools.virtualsession.ui;

import com.sshtools.virtualsession.VirtualSessionManager;

/**
 * <p>
 * All components that are to be added to the <code>TerminalDisplay</code>
 * implementation must implement this interface.
 * </p>
 * 
 * <p>
 * Each component can be added to any position (north, south, east or west) in
 * any <i>ring</i>.
 * 
 * @author $Author: brett $
 */

public interface VirtualSessionComponent {

	/**
	 * Invoked when the terminal adds the component to its UI.
	 * 
	 * @param display
	 *            display implementation
	 */
	public void init(VirtualSessionManager display);

	/**
	 * Return the <code>TerminalDisplay</code> this componet has been added
	 * to. Before <code>init(TerminalDisplay)</code> is called this method
	 * should return <code>null</code>.
	 * 
	 * @return terminal display
	 */
	public VirtualSessionManager getTerminalDisplay();
}