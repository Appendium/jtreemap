/*
 * ObjectLab, http://www.objectlab.co.uk/open is supporting JTreeMap.
 * 
 * Based in London, we are world leaders in the design and development 
 * of bespoke applications for the securities financing markets.
 * 
 * <a href="http://www.objectlab.co.uk/open">Click here to learn more</a>
 *           ___  _     _           _   _          _
 *          / _ \| |__ (_) ___  ___| |_| |    __ _| |__
 *         | | | | '_ \| |/ _ \/ __| __| |   / _` | '_ \
 *         | |_| | |_) | |  __/ (__| |_| |__| (_| | |_) |
 *          \___/|_.__// |\___|\___|\__|_____\__,_|_.__/
 *                   |__/
 *
 *                     www.ObjectLab.co.uk
 *
 * $Id$
 * 
 * Copyright 2006 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
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
/*
 *                 ObjectLab is supporing JTreeMap
 * 
 * Based in London, we are world leaders in the design and development 
 * of bespoke applications for the securities financing markets.
 * 
 * <a href="http://www.objectlab.co.uk/open">Click here to learn more about us</a>
 *           ___  _     _           _   _          _
 *          / _ \| |__ (_) ___  ___| |_| |    __ _| |__
 *         | | | | '_ \| |/ _ \/ __| __| |   / _` | '_ \
 *         | |_| | |_) | |  __/ (__| |_| |__| (_| | |_) |
 *          \___/|_.__// |\___|\___|\__|_____\__,_|_.__/
 *                   |__/
 *
 *                     www.ObjectLab.co.uk
 */
