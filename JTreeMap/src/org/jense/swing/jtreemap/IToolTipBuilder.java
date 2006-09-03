/*
 * Created on 6 mars 2006
 *
 */
package org.jense.swing.jtreemap;

import javax.swing.JToolTip;

/**
 * Interface used to build your own JToolTip for the jTreeMap.
 * 
 * @see org.jense.swing.jtreemap.JTreeMap#setToolTipBuilder(IToolTipBuilder)
 * @author Laurent DUTHEIL
 */
public interface IToolTipBuilder {

  /**
   * Return the instance of the JToolTip.<BR>
   * Override this method to build your own JToolTip
   * 
   * @return the instance of the JToolTip
   */
  public abstract JToolTip getToolTip();

}