package net.sf.jtreemap.swing;


/**
 * Class who permits to associate a double value to a label
 *
 * @author Laurent DUTHEIL
 */

public abstract class Value implements Comparable {
  /**
   * get the double value.
   *
   *@return the double value
   */
  public abstract double getValue();

  /**
   * get the formatedValue.
   *
   *@return the label of the value
   */
  public abstract String getLabel();

  /**
   * set the double value.
   *
   *@param value the new double value
   */
  public abstract void setValue(double value);

  /**
   * set the new label.
   *
   *@param newLabel the new label
   */
  public abstract void setLabel(String newLabel);

  /* (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object value) {
    if (value != null && value instanceof Value) {
      Value value2 = (Value) value;
      return value2.getValue() == this.getValue();
    }
    return false;
  }

  /* (non-Javadoc)
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  public int compareTo(Object value) {
    if (value != null && value instanceof Value) {
      Value value2 = (Value) value;
      if (this.getValue() < value2.getValue()) {
        return -1;
      }
      return this.getValue() > value2.getValue() ? 1 : 0;
    }
    throw new IllegalArgumentException();
  }
}
