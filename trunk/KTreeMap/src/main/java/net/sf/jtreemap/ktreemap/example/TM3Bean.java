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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

/**
 * Bean that contains the values of a line of a TM3 file
 */
public class TM3Bean {
  /**
   * label "DATE" to identify Date in TM3 data file
   */
  public static final String DATE = "DATE";
  /**
   * label "FLOAT" to identify float in TM3 data file
   */
  public static final String FLOAT = "FLOAT";
  /**
   * label "INTEGER" to identify int in TM3 data file
   */
  public static final String INTEGER = "INTEGER";
  /**
   * label "STRING" to identify String in TM3 data file
   */
  public static final String STRING = "STRING";
  /**
   * The default date format for TM3 file : MM/dd/yyyy
   */
  public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
  "MM/dd/yyyy");

  /**
   * list containing the field names of the TM3 file
   */
  public static ArrayList<String> fieldNames = new ArrayList<String>();
  /**
   * list containing the field type of the TM3 file
   */
  public static ArrayList<String> fieldTypes = new ArrayList<String>();

  private HashMap<String, Object> values = new HashMap<String, Object>();
  private String label;

  /**
   * @return the number fields (ie INTEGER and FLOAT)
   */
  public static String[] getNumberFields() {
    TreeSet<String> result = new TreeSet<String>();
    for (int i = 0; i < fieldNames.size(); i++) {
      String type = fieldTypes.get(i);
      if (INTEGER.equals(type) || FLOAT.equals(type)) {
        result.add(fieldNames.get(i));
      }
    }
    return result.toArray(new String[1]);
  }

  /**
   * set the value of the field name
   * @param fieldName field name
   * @param value value to set
   */
  public void setValue(String fieldName, Object value) {
    values.put(fieldName, value);
  }

  /**
   * get the value of the field
   * @param fieldName field name
   * @return the value of the field
   */
  public Object getValue(String fieldName) {
    return values.get(fieldName);
  }

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
