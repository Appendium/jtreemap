package net.sf.jtreemap.ktreemap.example;

import java.util.Date;

import net.sf.jtreemap.ktreemap.ITreeMapProvider;
import net.sf.jtreemap.ktreemap.TreeMapNode;

/**
 * TreeMapProvider for a TM3 file
 *
 */
public class TM3TreeMapProvider implements ITreeMapProvider {
  private static String valueField = "";

  public double getDoubleValue(Object value) {
    if (value instanceof TM3Bean) {
      TM3Bean bean = (TM3Bean)value;
      Object obj = bean.getValue(getValueField());
      if (obj instanceof Number) {
        Number number = (Number)obj;
        return number.doubleValue();
      } else if (obj instanceof Date) {
        Date date = (Date)obj;
        return date.getTime();
      }
    }
    return 0;
  }

  public String getLabel(TreeMapNode node) {
    Object value = node.getValue();
    if (value instanceof TM3Bean) {
      TM3Bean bean = (TM3Bean)value;
      return bean.getLabel();
    }
    return null;
  }

  public String getValueLabel(Object value) {
    return "" + getDoubleValue(value);
  }

  /**
   * @return the valueField
   */
  public static String getValueField() {
    return valueField;
  }

  /**
   * @param valueField the valueField to set
   */
  public static void setValueField(String valueField) {
    TM3TreeMapProvider.valueField = valueField;
  }

}
