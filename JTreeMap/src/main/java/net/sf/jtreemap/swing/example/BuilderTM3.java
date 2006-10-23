/*
 * Created on 22 nov. 2005
 */
package org.jense.swing.jtreemap.example;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.StringTokenizer;
import java.util.TreeSet;

import org.jense.swing.jtreemap.DefaultValue;
import org.jense.swing.jtreemap.TreeMapNode;
import org.jense.swing.jtreemap.TreeMapNodeBuilder;

/**
 * Parse a TM3 file to build the tree. <BR>
 * See <a href=http://www.cs.umd.edu/hcil/treemap/doc4.1/create_TM3_file.html>
 * how to create your own TM3 data file </a> from hcil Treemap site.
 * 
 * @author Laurent DUTHEIL
 */
public class BuilderTM3 {
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
  private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
      "MM/dd/yyyy");
  private static LinkedList<String> fieldNames = new LinkedList<String>();
  private static LinkedList<String> fieldTypes = new LinkedList<String>();
  private static HashMap<TreeMapNode, HashMap<String, Object>> values = new HashMap<TreeMapNode, HashMap<String, Object>>();
  private TreeMapNodeBuilder builder;

  /**
   * Constructor
   * 
   * @param tm3File tm3 file
   * @throws IOException
   */
  public BuilderTM3(File tm3File) throws IOException {
    this.builder = new TreeMapNodeBuilder();
    parse(tm3File);
  }

  /**
   * @return the number fields (ie INTEGER and FLOAT)
   */
  public String[] getNumberFields() {
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
   * get the build root.
   * 
   * @return the build root
   */
  public TreeMapNode getRoot() {
    return this.builder.getRoot();
  }

  /**
   * Set the values of all the JTreeMapNode with the values of the fieldName.
   * @param fieldName name of the field to set the values
   */
  public void setValues(String fieldName) {
    if ("".equals(fieldName)) {
      for (TreeMapNode node : values.keySet()) {
        node.setValue(new DefaultValue(0));
      }
    } else {
      for (TreeMapNode node : values.keySet()) {
        HashMap<String, Object> mapNodeValues = values.get(node);
        Object value = mapNodeValues.get(fieldName);
        if (value instanceof Number) {
          Number number = (Number) value;
          node.setValue(new DefaultValue(number.doubleValue()));
        } else if (value instanceof Date) {
          Date date = (Date) value;
          node.setValue(new DefaultValue(date.getTime()));
        }
      }
    }
  }

  /**
   * Set the weights of all the JTreeMapNode with the values of the fieldName.
   * @param fieldName name of the field to set the weights
   */
  public void setWeights(String fieldName) {
    if ("".equals(fieldName)) {
      for (TreeMapNode node : values.keySet()) {
        node.setWeight(1);
      }
    } else {
      for (TreeMapNode node : values.keySet()) {
        HashMap<String, Object> mapNodeValues = values.get(node);
        Object value = mapNodeValues.get(fieldName);
        if (value instanceof Number) {
          Number number = (Number) value;
          node.setWeight(number.doubleValue());
        } else if (value instanceof Date) {
          Date date = (Date) value;
          node.setWeight(date.getTime());
        }
      }
    }
  }

  /**
   * @param st StringTokenizer which contains the hierarchy path
   * @param mapNodeValues HashMap with fields and their values
   */
  private void createNodes(StringTokenizer st, HashMap<String, Object> mapNodeValues) {
    //read the hierarchy path
    LinkedList<String> hierarchyPath = new LinkedList<String>();
    while (st.hasMoreTokens()) {
      hierarchyPath.add(st.nextToken());
    }

    TreeMapNode node = this.builder.getRoot();
    if (node == null) {
      node = this.builder.buildBranch(hierarchyPath.get(0), null);
    }
    for (int i = 1; i < hierarchyPath.size() - 1; i++) {
      //looking for the child
      boolean found = false;
      TreeMapNode child = null;
      for (Enumeration iter = node.children(); iter.hasMoreElements();) {
        child = (TreeMapNode) iter.nextElement();
        if (child.getLabel().equals(hierarchyPath.get(i))) {
          found = true;
          break;
        }
      }
      if (found) {
        node = child;
      } else {
        node = this.builder.buildBranch(hierarchyPath.get(i), node);
      }
    }

    //create the leaf
    TreeMapNode leaf = this.builder.buildLeaf(hierarchyPath.getLast(),
        1, new DefaultValue(), node);
    //each leaf is associated to their values
    values.put(leaf, mapNodeValues);
  }

  /**
   * @param tm3File TM3 file
   * @throws IOException
   */
  private void parse(File tm3File) throws IOException {

    BufferedReader in = new BufferedReader(new FileReader(tm3File));
    String line = "";
    //read the field names
    line = in.readLine();
    StringTokenizer st = new StringTokenizer(line, "\t");
    fieldNames.clear();
    while (st.hasMoreTokens()) {
      fieldNames.add(st.nextToken());
    }

    //read the field types
    line = in.readLine();
    st = new StringTokenizer(line, "\t");
    fieldTypes.clear();
    while (st.hasMoreTokens()) {
      fieldTypes.add(st.nextToken());
    }

    //read the values
    values.clear();
    while ((line = in.readLine()) != null) {
      st = new StringTokenizer(line, "\t");
      HashMap<String, Object> mapNodeValues = new HashMap<String, Object>();
      //the values are formated
      for (int i = 0; i < fieldNames.size(); i++) {
        Object value;
        if (FLOAT.equals(fieldTypes.get(i))) {
          value = new Double(Double.parseDouble(st.nextToken()));
        } else if (INTEGER.equals(fieldTypes.get(i))) {
          value = new Integer(Integer.parseInt(st.nextToken()));
        } else if (DATE.equals(fieldTypes.get(i))) {
          try {
            value = DATE_FORMAT.parse(st.nextToken());
          } catch (ParseException e) {
            value = null;
          }
        } else {
          value = st.nextToken();
        }
        mapNodeValues.put(fieldNames.get(i), value);
      }

      //if we have not the path (the node names of parents)
      if (!st.hasMoreTokens()) {
        //we throw an exception, because we can't build the treemap
        throw new IOException("the file didn't contains the hierarchy path");
      }

      //create the nodes
      createNodes(st, mapNodeValues);
    }

  }

}
