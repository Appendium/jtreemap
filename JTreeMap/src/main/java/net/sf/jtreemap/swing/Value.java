package net.sf.jtreemap.swing;

import java.io.Serializable;

/**
 * Class who permits to associate a double value to a label
 * 
 * @author Laurent DUTHEIL
 */

public abstract class Value implements Comparable, Serializable {
    private static final int SHIFT = 32;
    private static final int PRIME = 31;

    /**
     * get the double value.
     * 
     * @return the double value
     */
    public abstract double getValue();

    /**
     * get the formatedValue.
     * 
     * @return the label of the value
     */
    public abstract String getLabel();

    /**
     * set the double value.
     * 
     * @param value
     *            the new double value
     */
    public abstract void setValue(double value);

    /**
     * set the new label.
     * 
     * @param newLabel
     *            the new label
     */
    public abstract void setLabel(String newLabel);

    @Override
    public int hashCode() {
        int result = super.hashCode();
        long temp;
        temp = Double.doubleToLongBits(getValue());
        result = PRIME * result + (int) (temp ^ (temp >>> SHIFT));
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Value other = (Value) obj;
        if (Double.doubleToLongBits(getValue()) != Double.doubleToLongBits(other.getValue())) {
            return false;
        }
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(final Object value) {
        if (value != null && value instanceof Value) {
            final Value value2 = (Value) value;
            if (this.getValue() < value2.getValue()) {
                return -1;
            }
            return this.getValue() > value2.getValue() ? 1 : 0;
        }
        throw new IllegalArgumentException();
    }
}
