package net.sf.jtreemap.swing;

import java.util.Vector;

/**
 * Strategy who split the elements in 2 groups of same cardinal.
 *
 * @author Laurent DUTHEIL
 */
public class SplitByNumber extends SplitStrategy {

  @Override
  public void splitElements(Vector<TreeMapNode> v, Vector<TreeMapNode> v1, Vector<TreeMapNode> v2) {
    int size = v.size();
    int middle = size / 2;
    int index = 0;
    //we add first elements to v1
    for (; index < middle; index++) {
      v1.addElement(v.elementAt(index));
    }
    //we add last elements to v2
    for (; index < size; index++) {
      v2.addElement(v.elementAt(index));
    }
  }

  @Override
  public double sumWeight(Vector<TreeMapNode> v) {
    //all the elements must have the same weight
    double weight = 0.0;
    for (TreeMapNode node : v) {
      if (node.isLeaf()) {
        weight += 1;
      } else {
        weight += this.sumWeight(node.getChildren());
      }
    }
    return weight;
  }

}
