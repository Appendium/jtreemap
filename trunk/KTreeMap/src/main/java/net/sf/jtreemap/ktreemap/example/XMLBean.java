package net.sf.jtreemap.ktreemap.example;

/**
 * Bean that contains values of an element of a XML file
 */
public class XMLBean {
  private String label;
  private double value;
  private double weight;
  /**
   * @return the label
   */
  public String getLabel() {
    return label;
  }
  /**
   * @param label the label to set
   */
  public void setLabel(String label) {
    this.label = label;
  }
  /**
   * @return the value
   */
  public double getValue() {
    return value;
  }
  /**
   * @param value the value to set
   */
  public void setValue(double value) {
    this.value = value;
  }
  /**
   * @return the weight
   */
  public double getWeight() {
    return weight;
  }
  /**
   * @param weight the weight to set
   */
  public void setWeight(double weight) {
    this.weight = weight;
  }
}
