/* HEADER */
package com.sshtools.virtualsession.status;

/**
 * <p>
 * Implementations represent a single element on a {@link StatusBar}
 * implementation and typically provide some component that displays the state
 * of terminal.
 * </p>
 * 
 * <p>
 * Every instance should provide a <i>weight</i> value that determines how much
 * space the element should take up on the status bar. This follows the same
 * rules as java.awt.GridBagLayout.weightx.
 * 
 * @author $Author: brett $
 */

public interface StatusElement {

	/**
	 * Get the display weight of element. This will determine how much space the
	 * element should take up on the status bar. This follows the same rules as
	 * java.awt.GridBagLayout.weightx.
	 * 
	 * @return weight
	 */
	public double getWeight();

	/**
	 * Should be invoked by the virtual session manager to clean up any
	 * resources the status element may be using.
	 */
	public void cleanUp();
}