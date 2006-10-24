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

import java.util.Vector;

/**
 * Split the treemap by slice
 * 
 * @author Laurent DUTHEIL
 */
public class SplitBySlice extends SplitStrategy {

    /**
     * 
     */
    private static final long serialVersionUID = 8484486418097321160L;

    /**
     * Calculate the dimension of the elements of the Vector.
     * 
     * @param x0
     *            x-coordinate
     * @param y0
     *            y-coordinate
     * @param w0
     *            width
     * @param h0
     *            height
     * @param v
     *            elements to split in the dimensions before
     * @param sumWeight
     *            sum of the weights
     */
    public static void splitInSlice(final int x0, final int y0, final int w0, final int h0, final Vector<TreeMapNode> v,
            final double sumWeight) {
        int offset = 0;
        boolean vertical = h0 > w0;

        for (final TreeMapNode node : v) {
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
            final TreeMapNode node = v.lastElement();
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
    public void splitElements(final Vector<TreeMapNode> v, final Vector<TreeMapNode> v1, final Vector<TreeMapNode> v2) {
        // ignore

    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.jtreemap.swing.SplitStrategy#calculatePositionsRec(int, int,
     *      int, int, double, java.util.Vector)
     */
    @Override
    protected void calculatePositionsRec(final int x0, final int y0, final int w0, final int h0, final double weight0,
            final Vector<TreeMapNode> v) {

        SplitBySlice.splitInSlice(x0, y0, w0, h0, v, weight0);

        for (final TreeMapNode node : v) {
            if (node.isLeaf()) {
                node.setX(node.getX() + TreeMapNode.getBorder());
                node.setY(node.getY() + TreeMapNode.getBorder());
                node.setHeight(node.getHeight() - TreeMapNode.getBorder());
                node.setWidth(node.getWidth() - TreeMapNode.getBorder());
            } else {
                // if this is not a leaf, calculation for the children
                if (TreeMapNode.getBorder() > 1) {
                    TreeMapNode.setBorder(TreeMapNode.getBorder() - 2);
                    calculatePositionsRec(node.getX() + 2, node.getY() + 2, node.getWidth() - 2, node.getHeight() - 2, node
                            .getWeight(), node.getChildren());
                    TreeMapNode.setBorder(TreeMapNode.getBorder() + 2);
                } else if (TreeMapNode.getBorder() == 1) {
                    TreeMapNode.setBorder(0);
                    calculatePositionsRec(node.getX() + 1, node.getY() + 1, node.getWidth() - 1, node.getHeight() - 1, node
                            .getWeight(), node.getChildren());
                    TreeMapNode.setBorder(1);
                } else {
                    calculatePositionsRec(node.getX(), node.getY(), node.getWidth(), node.getHeight(), node.getWeight(), node
                            .getChildren());
                }
            }
        }

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
