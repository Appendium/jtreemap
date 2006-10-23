package net.sf.jtreemap.ktreemap;

/**
 * Provider of the KTreeMap
 * @author dutheil_l
 *
 */
public interface ITreeMapProvider {
  /**
   * get the label of the node
   * @param node TreeMapNode
   * @return the label of the node
   */
  public String getLabel(TreeMapNode node);

  /**
   * Get the label of the value
   * @param value value of the node (TreeMapNode.getValue())
   * @return the label of the value
   */
  public String getValueLabel(Object value);

  /**
   * Get the double value of the value
   * @param value value of the node (TreeMapNode.getValue())
   * @return the double value of the value
   */
  public double getDoubleValue(Object value);
}
