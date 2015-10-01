/* HEADER */
package com.sshtools.virtualsession.status.swing;

import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JLabel;

import com.sshtools.virtualsession.status.StatusBar;
import com.sshtools.virtualsession.status.StatusElement;

/**
 * Implementation of a <code>StatusElemet</code> that uses a Swing <code>JLabel</code>
 * as its component.
 *
 * @author $Author: brett $
 */
public class SwingStatusLabel
    extends JLabel
    implements StatusElement {

  private StatusBar statusBar;
  private double weight = 1.0;
  private int fixedWidth;

  /**
   * Construct a new status label with the default weight of 1.0
   */
  public SwingStatusLabel() {
    this(1.0);
  }

  /**
   * Construct a new status label with a given weight
   *
   * @param weight weight
   */
  public SwingStatusLabel(double weight) {
    this(null, weight);
  }

  /**
   * Construct a new status label with a given weight and some text
   *
   * @param text text
   * @param weight weight
   */
  public SwingStatusLabel(String text, double weight) {
    this(text, weight, -1);
  }

  /**
   * @param string
   * @param i
   * @param j
   */
  public SwingStatusLabel(String text, double weight, int fixedWidth) {
    super(text);
    setWeight(weight);
    setBorder(BorderFactory.createLoweredBevelBorder());
    this.fixedWidth = fixedWidth;
  }
  
  public Dimension getPreferredSize() {
    return fixedWidth == -1 ? super.getPreferredSize() : new Dimension(fixedWidth, super.getPreferredSize().height);
  }
  
  public Dimension getMinimumSize() {
    return fixedWidth == -1 ? super.getMinimumSize() : getPreferredSize();
  }
  
  public Dimension getMaximumSize() {
    return fixedWidth == -1 ? super.getMaximumSize() : getPreferredSize();
  }

  /**
   * Set the weight
   *
   * @param weight weight
   */
  private void setWeight(double weight) {
    this.weight = weight;
  }

  /**
   * Get the status bar containing this label. This will only return as not
   * null if <code>init</code> has been invoked.
   *
   * @return status bar
   */
  public StatusBar getStatusBar() {
    return statusBar;
  }

  /* (non-Javadoc)
   * @see com.sshtools.terminal.StatusElement#added(com.sshtools.terminal.StatusBar)
   */
  public void init(StatusBar statusBar) {
    this.statusBar = statusBar;
  }

  /* (non-Javadoc)
   * @see com.sshtools.terminal.StatusElement#getWeight()
   */
  public double getWeight() {
    return weight;
  }

  /* (non-Javadoc)
   * @see com.sshtools.virtualsession.status.StatusElement#cleanUp()
   */
  public void cleanUp() {    
  }
}
