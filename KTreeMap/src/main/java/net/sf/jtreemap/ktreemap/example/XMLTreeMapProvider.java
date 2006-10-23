package net.sf.jtreemap.ktreemap.example;

import net.sf.jtreemap.ktreemap.ITreeMapProvider;
import net.sf.jtreemap.ktreemap.TreeMapNode;

/**
 * TreeMapProvider for a XML file
 */
public class XMLTreeMapProvider implements ITreeMapProvider {

  public double getDoubleValue(Object value) {
    if (value instanceof XMLBean) {
      XMLBean bean = (XMLBean)value;
      return bean.getValue();
    }
    return 0;
  }

  public String getLabel(TreeMapNode node) {
    Object value = node.getValue();
    if (value instanceof XMLBean) {
      XMLBean bean = (XMLBean)value;
      return bean.getLabel();
    }
    return null;
  }

  public String getValueLabel(Object value) {
      return "" + getDoubleValue(value);
  }

}
