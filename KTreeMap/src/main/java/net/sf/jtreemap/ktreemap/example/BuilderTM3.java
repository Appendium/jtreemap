/*
 * Created on 22 nov. 2005
 */
package net.sf.jtreemap.ktreemap.example;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.StringTokenizer;

import net.sf.jtreemap.ktreemap.TreeMapNode;
import net.sf.jtreemap.ktreemap.TreeMapNodeBuilder;

/**
 * Parse a TM3 file to build the tree. <BR>
 * See <a href=http://www.cs.umd.edu/hcil/treemap/doc4.1/create_TM3_file.html>
 * how to create your own TM3 data file </a> from hcil Treemap site.
 *
 * @author Laurent DUTHEIL
 */
public class BuilderTM3 extends TreeMapNodeBuilder {
  private static String fieldWeight = "";
  private HashMap<TreeMapNode, TM3Bean> leaves = new HashMap<TreeMapNode, TM3Bean>();

  /**
   * Constructor
   *
   * @param tm3File tm3 file
   * @throws IOException
   */
  public BuilderTM3(File tm3File) throws IOException {
    parse(tm3File);
  }

  /**
   * Set the weights of all the TreeMapNode with the values of the fieldName.
   *
   * @param fieldName name of the field to set the weights
   */
  public void setWeights() {
    if ("".equals(getFieldWeight())) {
      for (TreeMapNode node : leaves.keySet()) {
        node.setWeight(1);
      }
    } else {
      for (TreeMapNode node : leaves.keySet()) {
        TM3Bean bean = leaves.get(node);
        node.setWeight(getWeight(bean));
      }
    }
  }

  /**
   * @param st StringTokenizer which contains the hierarchy path
   * @param mapNodeValues HashMap with fields and their values
   */
  private void createNodes(StringTokenizer st, TM3Bean beanLeaf) {
    // read the hierarchy path
    LinkedList<String> hierarchyPath = new LinkedList<String>();
    while (st.hasMoreTokens()) {
      hierarchyPath.add(st.nextToken());
    }

    TreeMapNode node = getRoot();
    if (node == null) {
      TM3Bean bean = new TM3Bean();
      bean.setLabel(hierarchyPath.get(0));
      node = buildBranch(bean, null);
    }
    for (int i = 1; i < hierarchyPath.size() - 1; i++) {
      // looking for the child
      boolean found = false;
      TreeMapNode child = null;
      for (Iterator<TreeMapNode> iter = node.getChildren().iterator(); iter
          .hasNext();) {
        child = iter.next();
        TM3Bean value = (TM3Bean)child.getValue();
        if (value.getLabel().equals(hierarchyPath.get(i))) {
          found = true;
          break;
        }
      }
      if (found) {
        node = child;
      } else {
        TM3Bean bean = new TM3Bean();
        bean.setLabel(hierarchyPath.get(i));
        node = buildBranch(bean, node);
      }
    }

    // create the leaf
    beanLeaf.setLabel(hierarchyPath.getLast());
    TreeMapNode leafNode = buildLeaf(beanLeaf, node);
    leaves.put(leafNode, beanLeaf);
  }

  /**
   * @param tm3File TM3 file
   * @throws IOException
   */
  private void parse(File tm3File) throws IOException {
    BufferedReader in = new BufferedReader(new FileReader(tm3File));
    String line = "";
    // read the field names
    line = in.readLine();
    StringTokenizer st = new StringTokenizer(line, "\t");
    TM3Bean.fieldNames.clear();
    while (st.hasMoreTokens()) {
      TM3Bean.fieldNames.add(st.nextToken());
    }

    // read the field types
    line = in.readLine();
    st = new StringTokenizer(line, "\t");
    TM3Bean.fieldTypes.clear();
    while (st.hasMoreTokens()) {
      TM3Bean.fieldTypes.add(st.nextToken());
    }

    leaves.clear();

    // read the values
    while ((line = in.readLine()) != null) {
      st = new StringTokenizer(line, "\t");
      TM3Bean bean = new TM3Bean();
      // the values are formated
      for (int i = 0; i < TM3Bean.fieldNames.size(); i++) {
        Object value;
        if (TM3Bean.FLOAT.equals(TM3Bean.fieldTypes.get(i))) {
          value = new Double(Double.parseDouble(st.nextToken()));
        } else if (TM3Bean.INTEGER.equals(TM3Bean.fieldTypes.get(i))) {
          value = new Integer(Integer.parseInt(st.nextToken()));
        } else if (TM3Bean.DATE.equals(TM3Bean.fieldTypes.get(i))) {
          try {
            value = TM3Bean.DATE_FORMAT.parse(st.nextToken());
          } catch (ParseException e) {
            value = null;
          }
        } else {
          value = st.nextToken();
        }
        bean.setValue(TM3Bean.fieldNames.get(i), value);
      }

      // if we have not the path (the node names of parents)
      if (!st.hasMoreTokens()) {
        // we throw an exception, because we can't build the treemap
        throw new IOException("the file didn't contains the hierarchy path");
      }

      // create the nodes
      createNodes(st, bean);
    }

  }

  @Override
  public double getWeight(Object value) {
    if (value instanceof TM3Bean) {
      TM3Bean bean = (TM3Bean)value;
      Object weight = bean.getValue(getFieldWeight());
      if (weight instanceof Number) {
        Number number = (Number)weight;
        return number.doubleValue();
      } else if (weight instanceof Date) {
        Date date = (Date)weight;
        return date.getTime();
      }

    }
    return 1;
  }

  /**
   * @return the fieldWeight
   */
  public static String getFieldWeight() {
    return fieldWeight;
  }

  /**
   * @param fieldWeight the fieldWeight to set
   */
  public static void setFieldWeight(String fieldWeight) {
    BuilderTM3.fieldWeight = fieldWeight;
  }

}
