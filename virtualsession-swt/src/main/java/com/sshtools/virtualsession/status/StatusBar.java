/* HEADER */
package com.sshtools.virtualsession.status;

import com.sshtools.virtualsession.ui.VirtualSessionComponent;

/**
 * <p>Suitable for
 * use with components that provide some kind off status bar made up of many
 * different {@link StatusElement} implementations. For example, the Swing terminal toolkit provides a component that will
 * show each element as a label that appears slighly indented.</p>
 *
     * <p>Each element must provide a 'weight' that determines the proportion of the
 * entire StatusBar it will take up. For ease of use, the weight value should follow
 * the same rules as for the standard AWT <code>java.awt.GridBagConstratins.weightx</code>.
 *
 * @author $Author: brett $
 */

public interface StatusBar extends VirtualSessionComponent {

  /**
   * Add an element to the status bar. The status bar may impose restrictions
   * on the type of this element, such as it being an instance of a
   * <code>java.awt.Component</code> in the case of <code>SwingStatusBar</code>
   * and <code>AWTStatusBar</code>.
   *
   * @param element element to add
   * @throws IllegalArgumentException if element is of wrong type
   */
  public void addElement(StatusElement element) throws IllegalArgumentException;
  
  /**
   * Remove all of the status elements from the status bar.
   *
   */
  public void removeAllElements();

  /** 
   * Set whether separators should be placed between each element
   * @param seperators use separators
   */
  public void setSeparators(boolean seperators);
}