package net.sf.jtreemap.swing;

import java.text.NumberFormat;


/**
 * class who can display the values of elements of a JTreeMap with pourcent
 * 
 * @author Laurent Dutheil
 */

public class ValuePercent extends Value {
    /**
     * 
     */
    private static final long serialVersionUID = 1087258219010392928L;

    private double value;

    private NumberFormat nf;

    /**
     * Constructor of ValuePercent
     */
    public ValuePercent() {
        this.nf = NumberFormat.getInstance();
        this.nf.setMaximumFractionDigits(2);
        this.nf.setMinimumFractionDigits(2);
        this.nf.setMinimumIntegerDigits(1);
    }

    /**
     * Constructor of ValuePercent
     * 
     * @param value
     *            double value
     */
    public ValuePercent(final double value) {
        this();
        this.value = value;
    }

    @Override
    public void setValue(final double d) {
        this.value = d;
    }

    @Override
    public void setLabel(final String stLibelle) {
        // ignore
    }

    @Override
    public double getValue() {
        return this.value;
    }

    @Override
    public String getLabel() {
        if (this.value >= 0) {
            return "+" + this.nf.format(this.value) + " %";
        }
        return this.nf.format(this.value) + " %";
    }

}
