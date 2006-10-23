/*
 * Created on 3 nov. 2005
 */
package net.sf.jtreemap.swing;

import java.util.Vector;

/**
 * Split the treemap by slice
 *
 * @author Laurent DUTHEIL
 */
public class SplitBySlice extends SplitStrategy {

  /**
   * Calculate the dimension of the elements of the Vector.
   *
   * @param x0 x-coordinate
   * @param y0 y-coordinate
   * @param w0 width
   * @param h0 height
   * @param v elements to split in the dimensions before
   * @param sumWeight sum of the weights
   */
  public static void splitInSlice(int x0, int y0, int w0, int h0,
      Vector<TreeMapNode> v, double sumWeight) {
    int offset = 0;
    boolean vertical = h0 > w0;

    for (TreeMapNode node : v) {
      if (vertical) {
        node.setX(x0);
        node.setWidth(w0);
        node.setY(y0 + offset);
        node.setHeight((int) Math.round(h0 * node.getWeight() / sumWeight));
        offset = offset + node.getHeight();
      } else {
        node.setX(x0 + offset);
        node.setWidth((int) Math.round(w0 * node.getWeight() / sumWeight));
        node.setY(y0);
        node.setHeight(h0);
        offset = offset + node.getWidth();
      }
    }

    // Because of the Math.round(), we adjust the last element to fit the
    // correctly the JTreeMap
    if (!v.isEmpty()) {
      TreeMapNode node = v.lastElement();
      if (vertical && h0 != offset) {
        node.setHeight(node.getHeight() - offset + h0);
      } else if (!vertical && w0 != offset) {
        node.setWidth(node.getWidth() - offset + w0);
      }
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see net.sf.jtreemap.swing.SplitStrategy#splitElements(java.util.Vector,
   *      java.util.Vector, java.util.Vector)
   */
  @Override
  public void splitElements(Vector<TreeMapNode> v, Vector<TreeMapNode> v1,
      Vector<TreeMapNode> v2) {
    // ignore

  }

  /*
   * (non-Javadoc)
   *
   * @see net.sf.jtreemap.swing.SplitStrategy#calculatePositionsRec(int, int,
   *      int, int, double, java.util.Vector)
   */
  @Override
  protected void calculatePositionsRec(int x0, int y0, int w0, int h0,
      double weight0, Vector<TreeMapNode> v) {

    SplitBySlice.splitInSlice(x0, y0, w0, h0, v, weight0);

    for (TreeMapNode node : v) {
      if (node.isLeaf()) {
        node.setX(node.getX() + TreeMapNode.getBorder());
        node.setY(node.getY() + TreeMapNode.getBorder());
        node.setHeight(node.getHeight() - TreeMapNode.getBorder());
        node.setWidth(node.getWidth() - TreeMapNode.getBorder());
      } else {
        // if this is not a leaf, calculation for the children
        if (TreeMapNode.getBorder() > 1) {
          TreeMapNode.setBorder(TreeMapNode.getBorder() - 2);
          calculatePositionsRec(node.getX() + 2, node.getY() + 2, node
              .getWidth() - 2, node.getHeight() - 2, node.getWeight(), node
              .getChildren());
          TreeMapNode.setBorder(TreeMapNode.getBorder() + 2);
        } else if (TreeMapNode.getBorder() == 1) {
          TreeMapNode.setBorder(0);
          calculatePositionsRec(node.getX() + 1, node.getY() + 1, node
              .getWidth() - 1, node.getHeight() - 1, node.getWeight(), node
              .getChildren());
          TreeMapNode.setBorder(1);
        } else {
          calculatePositionsRec(node.getX(), node.getY(), node.getWidth(), node
              .getHeight(), node.getWeight(), node.getChildren());
        }
      }
    }

  }
}
