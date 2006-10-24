/*
 * Created on 10 oct. 2005
 */
package net.sf.jtreemap.swing;

/**
 * Default Value <BR>
 * The getLabel() method returns the "" + getValue()
 * 
 * @author Laurent DUTHEIL
 */
public class DefaultValue extends Value {
    /**
     * 
     */
    private static final long serialVersionUID = 367321198951855282L;

    private double value;

    /**
     * Constructor.
     */
    public DefaultValue() {
        // nothing to do
    }

    /**
     * Constructor.
     * 
     * @param value
     *            double value
     */
    public DefaultValue(final double value) {
        this.value = value;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.jtreemap.swing.Value#getValue()
     */
    @Override
    public double getValue() {
        return this.value;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.jtreemap.swing.Value#getLabel()
     */
    @Override
    public String getLabel() {
        return "" + this.value;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.jtreemap.swing.Value#setValue(double)
     */
    @Override
    public void setValue(final double value) {
        this.value = value;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.jtreemap.swing.Value#setLabel(java.lang.String)
     */
    @Override
    public void setLabel(final String newLabel) {
        // ignore

    }

}
