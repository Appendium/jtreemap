package net.sf.jtreemap.swing;

import java.util.Enumeration;
import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Node of a JTreeMap.<BR>
 *
 * If the node is a branch, only the label is set.<BR>
 * If the node is a leaf, we need a label, a weight and a value.
 * <p>
 * You can also use a TreeMapNode in a JTree.
 *
 * @author Laurent Dutheil
 */

public class TreeMapNode extends DefaultMutableTreeNode {
  private static final long serialVersionUID = 742372833853976103L;
  //max border between two nodes of the same level
  private static int border = 3;
  private int height;
  private Value value;
  private double weight = 0.0;
  private int width;
  private int x;
  private int y;

  /**
   * Get the max border between two nodes of the same level.
   *
   * @return Returns the border.
   */
  public static int getBorder() {
    return TreeMapNode.border;
  }

  /**
   * Set the max border between two nodes of the same level.
   *
   * @param border The border to set.
   */
  public static void setBorder(int border) {
    TreeMapNode.border = border;
  }


  /**
   * Constructor for a branch.
   *
   * @param label label of the branch.
   */
  public TreeMapNode(String label) {
    super(label);
    super.allowsChildren = true;
  }

  /**
   * Constructor for a leaf.
   *
   * @param label label of the leaf.
   * @param weight weight of the leaf (if negative, we take the absolute value).
   * @param value Value associée à la feuille
   */
  public TreeMapNode(String label, double weight, Value value) {
    super(label);
    //the weight must be positive
    this.weight = Math.abs(weight);
    this.value = value;
    super.allowsChildren = false;
  }

  /**
   * add a new child to the node.
   *
   * @param newChild new child
   */
  public void add(TreeMapNode newChild) {
    super.add(newChild);
    this.setWeight(this.weight + newChild.getWeight());
  }

  /**
   * get the active leaf.<BR>
   * null if the passed position is not in this tree.
   *
   * @param x x-coordinate
   * @param y y-coordinate
   * @return active leaf
   */
  public TreeMapNode getActiveLeaf(int x, int y) {

    if (this.isLeaf()) {
      if ((x >= this.getX()) && (x <= this.getX() + this.getWidth())
          && (y >= this.getY()) && (y <= this.getY() + this.getHeight())) {
        return this;
      }
    } else {
      for (Enumeration e = this.children(); e.hasMoreElements();) {
        TreeMapNode node = (TreeMapNode) (e.nextElement());
        if ((x >= node.getX()) && (x <= node.getX() + node.getWidth())
            && (y >= node.getY()) && (y <= node.getY() + node.getHeight())) {
          return node.getActiveLeaf(x, y);
        }
      }
    }
    return null;
  }

  /**
   * get the first child which fits the position.<BR>
   * null if the passed position is not in this tree.
   *
   * @param x x-coordinate
   * @param y y-coordinate
   * @return the first child which fits the position.
   */
  public TreeMapNode getChild(int x, int y) {
    if (! this.isLeaf()) {
      for (Enumeration e = this.children(); e.hasMoreElements();) {
        TreeMapNode node = (TreeMapNode) (e.nextElement());
        if ((x >= node.getX()) && (x <= node.getX() + node.getWidth())
            && (y >= node.getY()) && (y <= node.getY() + node.getHeight())) {
          return node;
        }
      }

    }
    return null;
  }

  /**
   * get a Vector with the children.
   *
   * @return Vector with the children
   */
  @SuppressWarnings("unchecked")
  public Vector<TreeMapNode> getChildren() {
    return this.children;
  }

  /**
   * get the height.
   *
   * @return the height
   */
  public int getHeight() {
    return this.height;
  }

  /**
   * get the label.
   *
   * @return the label
   */
  public String getLabel() {
    return this.getUserObject().toString();
  }

  /**
   * get the label of the Value.
   *
   * @return the label of the Value
   */
  public String getLabelValue() {
    return this.value.getLabel();
  }

  /**
   * get the Value.
   *
   * @return the value
   */
  public Value getValue() {
    return this.value;
  }

  /**
   * get the double Value.
   *
   * @return the double value
   */
  public double getDoubleValue() {
    return this.value.getValue();
  }

  /**
   * get the weight.
   *
   * @return the weight
   */
  public double getWeight() {
    return this.weight;
  }

  /**
   * get the width.
   *
   * @return the width
   */
  public int getWidth() {
    return this.width;
  }

  /**
   * get the x-coordinate.
   *
   * @return the x-coordinate
   */
  public int getX() {
    return this.x;
  }

  /**
   * get the y-coordinate.
   *
   * @return the y-coordinate
   */
  public int getY() {
    return this.y;
  }

  /**
   * set the position and the size.
   *
   * @param x x-coordinate
   * @param y y-coordinate
   * @param width the new width
   * @param height the new height
   */
  public void setDimension(int x, int y, int width, int height) {
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
  }

  /**
   * set the height.
   *
   * @param height la nouvelle valeur de height
   */
  public void setHeight(int height) {
    this.height = height;
  }

  /**
   * set the label.
   *
   * @param label the new label
   */
  public void setLabel(String label) {
    this.userObject = label;
  }

  /**
   * set the position.
   *
   * @param x x-coordinate
   * @param y y-coordinate
   */
  public void setPosition(int x, int y) {
    this.x = x;
    this.y = y;
  }

  /**
   * set size.
   *
   * @param width the new width
   * @param height the new height
   */
  public void setSize(int width, int height) {
    this.width = width;
    this.height = height;
  }

  /**
   * set the Value.
   *
   * @param value the new Value
   */
  public void setValue(Value value) {
    this.value = value;
  }

  /**
   * set the weight of the node and update the parents.
   *
   * @param weight the new weight
   */
  public void setWeight(double weight) {
    double newWeight = Math.abs(weight);
    if (this.parent != null) {
      ((TreeMapNode) this.parent).setWeight(((TreeMapNode) this.parent).weight
                                      - this.weight + newWeight);
    }
    this.weight = newWeight;
  }

  /**
   * set the width.
   *
   * @param width la nouvelle valeur de width
   */
  public void setWidth(int width) {
    this.width = width;
  }

  /**
   * set the x-coordinate.
   *
   * @param x the new x-coordinate
   */
  public void setX(int x) {
    this.x = x;
  }

  /**
   * set the y-coordinate.
   *
   * @param y the new y-coordinate
   */
  public void setY(int y) {
    this.y = y;
  }
}
