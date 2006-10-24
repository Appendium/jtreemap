/**
 *
 */
package net.sf.jtreemap.swing;

import java.io.Serializable;

import javax.swing.JToolTip;

/**
 * Default class to build the DefaultToolTip displayed by the JTreeMap.<BR>
 * 
 * @see net.sf.jtreemap.swing.DefaultToolTip
 * @author Laurent DUTHEIL
 * 
 */
public class DefaultToolTipBuilder implements IToolTipBuilder, Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = -1646772942428571187L;

    private static JToolTip instance = null;

    private JTreeMap jTreeMap;

    /**
     * Constructor.
     * 
     * @param jTreeMap
     *            the linked JTreeMap
     */
    public DefaultToolTipBuilder(final JTreeMap jTreeMap) {
        this.jTreeMap = jTreeMap;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.jtreemap.swing.IToolTipBuilder#getToolTip()
     */
    public JToolTip getToolTip() {
        if (instance == null) {
            instance = new DefaultToolTip(this.jTreeMap);
        }
        return instance;
    }
}
