/**
 * 
 */
package org.jense.swing.jtreemap;

import javax.swing.JToolTip;

/**
 * Default class to build the DefaultToolTip displayed by the JTreeMap.<BR>
 * 
 * @see org.jense.swing.jtreemap.DefaultToolTip
 * @author Laurent DUTHEIL
 *
 */
public class DefaultToolTipBuilder implements IToolTipBuilder {
  private static JToolTip instance = null;
  private JTreeMap jTreeMap;
  
  /**
   * Constructor.
   * 
   * @param jTreeMap the linked JTreeMap
   */
  public DefaultToolTipBuilder(JTreeMap jTreeMap) {
    this.jTreeMap = jTreeMap;
  }

  /* (non-Javadoc)
   * @see org.jense.swing.jtreemap.IToolTipBuilder#getToolTip()
   */
  public JToolTip getToolTip() {
    if (instance == null) {
      instance = new DefaultToolTip(this.jTreeMap);
    }
    return instance;
  }
}
