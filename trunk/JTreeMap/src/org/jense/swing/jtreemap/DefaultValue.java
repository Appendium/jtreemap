/*
 * Created on 10 oct. 2005
 */
package org.jense.swing.jtreemap;

/**
 * Default Value <BR>
 * The getLabel() method returns the "" + getValue()
 * 
 * @author Laurent DUTHEIL
 */
public class DefaultValue extends Value {
  private double value;

  /**
   * Constructor.
   */
  public DefaultValue() {
  //nothing to do
  }

  /**
   * Constructor.
   * 
   * @param value double value
   */
  public DefaultValue(double value) {
    this.value = value;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jense.swing.jtreemap.Value#getValue()
   */
  @Override
  public double getValue() {
    return this.value;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jense.swing.jtreemap.Value#getLabel()
   */
  @Override
  public String getLabel() {
    return "" + this.value;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jense.swing.jtreemap.Value#setValue(double)
   */
  @Override
  public void setValue(double value) {
    this.value = value;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jense.swing.jtreemap.Value#setLabel(java.lang.String)
   */
  @Override
  public void setLabel(String newLabel) {
  // ignore

  }

}
