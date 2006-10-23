package net.sf.jtreemap.swing;

import javax.swing.JPanel;
import java.awt.Color;

/**
 * Abstract class with the methods who attribute color to the elements of
 * JTreeMap.
 *
 * @author Laurent DUTHEIL
 */

public abstract class ColorProvider {

  /**
   * get the associated color to the value.
   *
   *@param value  double value
   *@return the associated color to the value
   */
  public abstract Color getColor(Value value);

  /**
   * get a legend JPanel.
   *
   *@return a legend JPanel
   */
  public abstract JPanel getLegendPanel();

}
