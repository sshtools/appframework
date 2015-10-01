/* HEADER */
package com.sshtools.virtualsession.ui.commonawt;

import java.awt.Component;

import com.sshtools.virtualsession.ui.VirtualSessionComponent;

/**
 * <p>
 * All components that are to be added to the <code>TerminalDisplay</code>
 * implementation must implement this interface.
 * </p>
 * 
 * <p>
 * A terminal component need not actually be <i>visual</i>, i.e. provide an AWT
 * or Swing component for display, but if it is it must return a
 * {@link Component} from the <code>getComponent()</code> method.
 * 
 * <p>
 * Each component can be added to any position (north, south, east or west) in
 * any <i>ring</i>.
 * 
 * @author $Author: brett $
 */

public interface CommonAWTVirtualSessionComponent extends VirtualSessionComponent {

	/**
	 * Return the AWT / Swing component this terminal component provides. If the
	 * terminal component is not visual it will just return <code>null</code>
	 * 
	 * @return component
	 */
	public Component getComponent();
}