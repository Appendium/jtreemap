/**
 *
 */
package net.sf.jtreemap.swing;

import javax.swing.JToolTip;

/**
 * Default class to build the DefaultToolTip displayed by the JTreeMap.<BR>
 *
 * @see net.sf.jtreemap.swing.DefaultToolTip
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
   * @see net.sf.jtreemap.swing.IToolTipBuilder#getToolTip()
   */
  public JToolTip getToolTip() {
    if (instance == null) {
      instance = new DefaultToolTip(this.jTreeMap);
    }
    return instance;
  }
}
