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
package net.sf.jtreemap.swing;

import java.util.Enumeration;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Represents a node in a JTreeMap.
 * <p>
 * If the node is a branch, only the label is set. If the node is a leaf, it
 * requires a label, a weight, and a value.
 * </p>
 * <p>
 * You can also use a TreeMapNode in a JTree.
 * </p>
 *
 * @author Laurent Dutheil
 */
public class TreeMapNode extends DefaultMutableTreeNode {
    private static final int DEFAULT_BORDER_SIZE = 3;

    private static final long serialVersionUID = 742372833853976103L;

    private static int border = DEFAULT_BORDER_SIZE;

    private int height;
    private Value value;
    private double weight;
    private int width;
    private int x;
    private int y;

    /**
     * Gets the maximum border between two nodes of the same level.
     *
     * @return the border size
     */
    public static int getBorder() {
        return TreeMapNode.border;
    }

    /**
     * Sets the maximum border between two nodes of the same level.
     *
     * @param border the border size to set
     */
    public static void setBorder(final int border) {
        TreeMapNode.border = border;
    }

    /**
     * Constructs a new branch node.
     *
     * @param label the label of the branch
     */
    public TreeMapNode(final String label) {
        super(label);
        super.allowsChildren = true;
    }

    /**
     * Constructs a new leaf node.
     *
     * @param label  the label of the leaf
     * @param weight the weight of the leaf (if negative, the absolute value is
     *               taken)
     * @param value  the value associated with the leaf
     */
    public TreeMapNode(final String label, final double weight, final Value value) {
        super(label);
        this.weight = Math.abs(weight);
        this.value = value;
        super.allowsChildren = false;
    }

    /**
     * Adds a new child to the node.
     *
     * @param newChild the new child to add
     */
    public void add(final TreeMapNode newChild) {
        super.add(newChild);
        this.setWeight(this.weight + newChild.getWeight());
    }

    /**
     * Gets the active leaf at the specified position.
     *
     * @param xParam the x-coordinate
     * @param yParam the y-coordinate
     * @return the active leaf, or {@code null} if the position is not in this tree
     */
    public TreeMapNode getActiveLeaf(final int xParam, final int yParam) {
        if (this.isLeaf()) {
            if (xParam >= this.getX() && xParam <= this.getX() + this.getWidth() && yParam >= this.getY()
                    && yParam <= this.getY() + this.getHeight()) {
                return this;
            }
        } else {
            for (final Enumeration<?> e = this.children(); e.hasMoreElements();) {
                final TreeMapNode node = (TreeMapNode) e.nextElement();
                if (xParam >= node.getX() && xParam <= node.getX() + node.getWidth() && yParam >= node.getY()
                        && yParam <= node.getY() + node.getHeight()) {
                    return node.getActiveLeaf(xParam, yParam);
                }
            }
        }
        return null;
    }

    /**
     * Gets the first child that fits the specified position.
     *
     * @param xParam the x-coordinate
     * @param yParam the y-coordinate
     * @return the first child that fits the position, or {@code null} if not found
     */
    public TreeMapNode getChild(final int xParam, final int yParam) {
        if (!this.isLeaf()) {
            for (final Enumeration<?> e = this.children(); e.hasMoreElements();) {
                final TreeMapNode node = (TreeMapNode) e.nextElement();
                if (xParam >= node.getX() && xParam <= node.getX() + node.getWidth() && yParam >= node.getY()
                        && yParam <= node.getY() + node.getHeight()) {
                    return node;
                }
            }
        }
        return null;
    }

    /**
     * Gets a list of the children of this node.
     *
     * @return a list of the children
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public List<TreeMapNode> getChildren() {
        return (List) this.children;
    }

    /**
     * Gets the height of this node.
     *
     * @return the height
     */
    public int getHeight() {
        return this.height;
    }

    /**
     * Gets the label of this node.
     *
     * @return the label
     */
    public String getLabel() {
        return getUserObject() != null ? getUserObject().toString() : "";
    }

    /**
     * Gets the label of the value of this node.
     *
     * @return the label of the value, or an empty string if the value is
     *         {@code null}
     */
    public String getLabelValue() {
        return this.value != null ? this.value.getLabel() : "";
    }

    /**
     * Gets the value of this node.
     *
     * @return the value
     */
    public Value getValue() {
        return this.value;
    }

    /**
     * Gets the double value of this node.
     *
     * @return the double value
     */
    public double getDoubleValue() {
        return this.value.getValue();
    }

    /**
     * Gets the weight of this node.
     *
     * @return the weight
     */
    public double getWeight() {
        return this.weight;
    }

    /**
     * Gets the width of this node.
     *
     * @return the width
     */
    public int getWidth() {
        return this.width;
    }

    /**
     * Gets the x-coordinate of this node.
     *
     * @return the x-coordinate
     */
    public int getX() {
        return this.x;
    }

    /**
     * Gets the y-coordinate of this node.
     *
     * @return the y-coordinate
     */
    public int getY() {
        return this.y;
    }

    /**
     * Sets the position and size of this node.
     *
     * @param xParam      the x-coordinate
     * @param yParam      the y-coordinate
     * @param widthParam  the new width
     * @param heightParam the new height
     */
    public void setDimension(final int xParam, final int yParam, final int widthParam, final int heightParam) {
        this.x = xParam;
        this.y = yParam;
        this.width = widthParam;
        this.height = heightParam;
    }

    /**
     * Sets the height of this node.
     *
     * @param height the new height
     */
    public void setHeight(final int height) {
        this.height = height;
    }

    /**
     * Sets the label of this node.
     *
     * @param label the new label
     */
    public void setLabel(final String label) {
        this.userObject = label;
    }

    /**
     * Sets the position of this node.
     *
     * @param xParam the x-coordinate
     * @param yParam the y-coordinate
     */
    public void setPosition(final int xParam, final int yParam) {
        this.x = xParam;
        this.y = yParam;
    }

    /**
     * Sets the size of this node.
     *
     * @param widthParam  the new width
     * @param heightParam the new height
     */
    public void setSize(final int widthParam, final int heightParam) {
        this.width = widthParam;
        this.height = heightParam;
    }

    /**
     * Sets the value of this node.
     *
     * @param value the new value
     */
    public void setValue(final Value value) {
        this.value = value;
    }

    /**
     * Sets the weight of the node and updates the parents.
     *
     * @param weight the new weight
     */
    public void setWeight(final double weight) {
        final double newWeight = Math.abs(weight);
        if (this.parent != null) {
            ((TreeMapNode) this.parent).setWeight(((TreeMapNode) this.parent).weight - this.weight + newWeight);
        }
        this.weight = newWeight;
    }

    /**
     * Sets the width of this node.
     *
     * @param width the new width
     */
    public void setWidth(final int width) {
        this.width = width;
    }

    /**
     * Sets the x-coordinate of this node.
     *
     * @param x the new x-coordinate
     */
    public void setX(final int x) {
        this.x = x;
    }

    /**
     * Sets the y-coordinate of this node.
     *
     * @param y the new y-coordinate
     */
    public void setY(final int y) {
        this.y = y;
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
