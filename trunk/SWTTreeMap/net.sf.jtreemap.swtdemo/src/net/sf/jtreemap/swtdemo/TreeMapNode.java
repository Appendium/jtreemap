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
 * $Id: TreeMapNode.java 75 2006-10-24 23:00:51Z benoitx $
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
package net.sf.jtreemap.swtdemo;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.Figure;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

/**
 * Node of a KTreeMap.<BR>
 * If the node is a branch, only the label is set.<BR>
 * If the node is a leaf, we need a label, a weight and a value.
 * <p>
 * You can also use a TreeMapNode in a JTree.
 *
 * @author Laurent Dutheil
 */

public class TreeMapNode {
  private static final long serialVersionUID = 742372833853976103L;
  // max border between two nodes of the same level
  private static int border = 3;
  private double weight = 0.0;
  private Rectangle bounds = new Rectangle(0, 0, 0, 0);
  private Object value;
  private ArrayList<TreeMapNode> children = new ArrayList<TreeMapNode>();
  private TreeMapNode parent;
private Figure figure = null;

  /**
   * Constructor for a branch
   * @param value value of the TreeMapNode
   */
  public TreeMapNode(Object value) {
    this.value = value;
  }

  /**
   * Constructor for a leaf.
   *
   * @param weight weight of the leaf (if negative, we take the absolute value).
   * @param value Value associée à la feuille
   */
  public TreeMapNode(Object value, double weight) {
    this.value = value;
    // the weight must be positive
    this.weight = Math.abs(weight);
  }

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
   * add a new child to the node.
   *
   * @param newChild new child
   */
  public void add(TreeMapNode newChild) {
    this.children.add(newChild);
    newChild.setParent(this);
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
      for (TreeMapNode node : this.children) {
        if ((x >= node.getX()) && (x <= node.getX() + node.getWidth())
            && (y >= node.getY()) && (y <= node.getY() + node.getHeight())) {
          return node.getActiveLeaf(x, y);
        }
      }
    }
    return null;
  }

  /**
   * get the active leaf.<BR>
   * null if the passed position is not in this tree.
   *
   * @param position position
   * @return active leaf
   */
  public TreeMapNode getActiveLeaf(Point position) {
    if (position != null) {
      return getActiveLeaf(position.x, position.y);
    }
    return null;
  }

  /**
   * @return the bounds of the KTreeMap
   */
  public Rectangle getBounds() {
    return this.bounds;
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
    if (!this.isLeaf()) {
      for (TreeMapNode node : this.children) {
        if ((x >= node.getX()) && (x <= node.getX() + node.getWidth())
            && (y >= node.getY()) && (y <= node.getY() + node.getHeight())) {
          return node;
        }
      }

    }
    return null;
  }

  /**
   * get the first child which fits the position.<BR>
   * null if the passed position is not in this tree.
   *
   * @param position position
   * @return the first child which fits the position.
   */
  public TreeMapNode getChild(Point position) {
    if (position != null) {
      return getChild(position.x, position.y);
    }
    return null;
  }

  /**
   * get a List with the children.
   *
   * @return List with the children
   */
  @SuppressWarnings("unchecked")
  public List<TreeMapNode> getChildren() {
    return this.children;
  }

  /**
   * get the height.
   *
   * @return the height
   */
  public int getHeight() {
    return this.bounds.height;
  }

  /**
   * get the Value.
   *
   * @return the value
   */
  public Object getValue() {
    return this.value;
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
    return this.bounds.width;
  }

  /**
   * get the x-coordinate.
   *
   * @return the x-coordinate
   */
  public int getX() {
    return this.bounds.x;
  }

  /**
   * get the y-coordinate.
   *
   * @return the y-coordinate
   */
  public int getY() {
    return this.bounds.y;
  }

  /**
   * @return true if the TreeMapNode is a leaf
   */
  public boolean isLeaf() {
    return this.children.isEmpty();
  }

  /**
   * set the position and the size.
   * @param bounds bounds
   */
  public void setBounds(Rectangle bounds) {
    this.bounds = bounds;
  }

  /**
   * set the height.
   *
   * @param height la nouvelle valeur de height
   */
  public void setHeight(int height) {
    this.bounds.height = height;
  }

  /**
   * set the position.
   *
   * @param x x-coordinate
   * @param y y-coordinate
   */
  public void setPosition(int x, int y) {
    this.bounds.x = x;
    this.bounds.y = y;
  }

  /**
   * set size.
   *
   * @param width the new width
   * @param height the new height
   */
  public void setSize(int width, int height) {
    this.bounds.width = width;
    this.bounds.height = height;
  }

  /**
   * set the Value.
   *
   * @param value the new value
   */
  public void setValue(Object value) {
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
      this.parent.setWeight(this.parent.weight - this.weight + newWeight);
    }
    this.weight = newWeight;
  }

  /**
   * set the width.
   *
   * @param width la nouvelle valeur de width
   */
  public void setWidth(int width) {
    this.bounds.width = width;
  }

  /**
   * set the x-coordinate.
   *
   * @param x the new x-coordinate
   */
  public void setX(int x) {
    this.bounds.x = x;
  }

  /**
   * set the y-coordinate.
   *
   * @param y the new y-coordinate
   */
  public void setY(int y) {
    this.bounds.y = y;
  }

  /**
   * @return the parent
   */
  public TreeMapNode getParent() {
    return parent;
  }

  /**
   * @param parent the parent to set
   */
  protected void setParent(TreeMapNode parent) {
    this.parent = parent;
  }

public Figure getFigure() {
	return figure;
}

public void setFigure(Figure proc) {
	figure = proc;
	
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
