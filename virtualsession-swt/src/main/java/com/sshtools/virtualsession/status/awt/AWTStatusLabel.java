/* HEADER */
package com.sshtools.virtualsession.status.awt;

import java.awt.Image;
import java.awt.Insets;
import java.awt.Label;

import com.sshtools.ui.awt.ImageCanvas;
import com.sshtools.ui.awt.ImageTextLabel;
import com.sshtools.virtualsession.status.StatusElement;

/**
 * Implementation of a {@link StatusElement} that uses a AWT to paint status element
 * as a label.
 *
 * @author $Author: brett $
 */
public class AWTStatusLabel
    extends ImageTextLabel
    implements StatusElement {

  private double weight = 1.0;
  private ImageCanvas imageCanvas;
  private Label textLabel;

  /**
   * Construct a new status label with the default weight of 1.0
   * 
   * @param borderType border type
   */
  public AWTStatusLabel(int borderType) {
    this(borderType, 1.0);
  }

  /**
   * Construct a new status label with a given weight
   *
   * @param borderType border type
   * @param weight weight
   */
  public AWTStatusLabel(int borderType, double weight) {
    this(borderType, null, weight);
  }

  /**
   * Construct a new status label with a given weight and some text
   *
   * @param borderType border type
   * @param text text
   * @param weight weight
   */
  public AWTStatusLabel(int borderType, String text, double weight) {
    this(borderType, text, null, weight);
  }

  /**
   * Construct a new status label with a given weight, some text and an image
   *
   * @param borderType border type
   * @param text text
   * @param image image
   * @param weight weight
   */
  public AWTStatusLabel(int borderType, String text, Image image, double weight) {
    super(image, text);
    setBorderType(borderType);
    setMargin(new Insets(1, 2, 1, 2));
    setWeight(weight);
  }

  /**
   * Set the weight
   *
   * @param weight weight
   */
  private void setWeight(double weight) {
    this.weight = weight;
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
