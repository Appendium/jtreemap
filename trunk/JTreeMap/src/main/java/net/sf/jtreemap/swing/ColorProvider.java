package net.sf.jtreemap.swing;

import java.awt.Color;
import java.io.Serializable;

import javax.swing.JPanel;

/**
 * Abstract class with the methods who attribute color to the elements of
 * JTreeMap.
 * 
 * @author Laurent DUTHEIL
 */

public abstract class ColorProvider implements Serializable {

    /**
     * get the associated color to the value.
     * 
     * @param value
     *            double value
     * @return the associated color to the value
     */
    public abstract Color getColor(Value value);

    /**
     * get a legend JPanel.
     * 
     * @return a legend JPanel
     */
    public abstract JPanel getLegendPanel();

}
