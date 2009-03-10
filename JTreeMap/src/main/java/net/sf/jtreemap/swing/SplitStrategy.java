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

import java.io.Serializable;
import java.util.Iterator;
import java.util.Vector;

/**
 * Abtract class with the method which split the elements of a JTreeMap.
 * <p>
 * The split is done by dichotomy. We split the elements in 2 groups with a
 * defined strategy (for example : take care of the weight of the elements)
 * <p>
 * 
 * @author Laurent Dutheil
 */

public abstract class SplitStrategy implements Serializable {
    /**
     * calculate the positions for all the elements of the root.
     * 
     * @param root
     *            the root to calculate
     */
    public void calculatePositions(final TreeMapNode root) {
        if (root == null) {
            return;
        }

        final Vector<TreeMapNode> v = root.getChildren();
        if (v != null && !v.isEmpty()) {
            calculatePositionsRec(root.getX(), root.getY(), root.getWidth(), root.getHeight(), this.sumWeight(v), v);
        }
    }

    /**
     * split the elements of a JTreeMap.
     * 
     * @param v
     *            Vector with the elements to split (arg IN)
     * @param v1
     *            first Vector of the split (arg OUT)
     * @param v2
     *            second Vector of the split (arg OUT)
     */
    public abstract void splitElements(Vector<TreeMapNode> v, Vector<TreeMapNode> v1, Vector<TreeMapNode> v2);

    /**
     * Sum the weight of elements. <BR>
     * You can override this method if you want to apply a coef on the weights
     * or to cancel the effect of weight on the strategy.
     * 
     * @param v
     *            Vector with the elements to sum
     * @return the sum of the weight of elements
     */
    public double sumWeight(final Vector<TreeMapNode> v) {
        double d = 0.0;
        if (v != null) {
            final int size = v.size();

            for (int i = 0; i < size; i++) {
                d += (v.elementAt(i)).getWeight();
            }
        }
        return d;
    }

    protected void calculatePositionsRec(final int x0, final int y0, final int w0, final int h0, final double weight0,
            final Vector<TreeMapNode> v) {

        if (v.isEmpty()) {
            return;
        }
        // if the Vector contains only one element
        if (v.size() == 1) {
            final TreeMapNode f = v.elementAt(0);
            if (f.isLeaf()) {
                // if this is a leaf, we display with the border
                int w = w0 - TreeMapNode.getBorder();
                if (w < 0) {
                    w = 0;
                }
                int h = h0 - TreeMapNode.getBorder();
                if (h < 0) {
                    h = 0;
                }
                f.setDimension(x0 + TreeMapNode.getBorder(), y0 + TreeMapNode.getBorder(), w, h);
            } else {
                // if this is not a leaf, calculation for the children
                f.setDimension(x0, y0, w0, h0);

                int bSub;
                if (TreeMapNode.getBorder() > 1) {
                    bSub = 2;
                } else if (TreeMapNode.getBorder() == 1) {
                    bSub = 1;
                } else {
                    bSub = 0;
                }

                int w = w0 - bSub;
                if (w < 0) {
                    w = 0;
                }
                int h = h0 - bSub;
                if (h < 0) {
                    h = 0;
                }

                TreeMapNode.setBorder(TreeMapNode.getBorder() - bSub);
                calculatePositionsRec(x0 + bSub, y0 + bSub, w, h, weight0, f.getChildren());
                TreeMapNode.setBorder(TreeMapNode.getBorder() + bSub);
            }
        } else {
            // if there is more than one element
            // we split the Vector according to the selected strategy
            final Vector<TreeMapNode> v1 = new Vector<TreeMapNode>();
            final Vector<TreeMapNode> v2 = new Vector<TreeMapNode>();
            double weight1;
            double weight2; // poids des 2 vecteurs
            this.splitElements(v, v1, v2);
            weight1 = this.sumWeight(v1);
            weight2 = this.sumWeight(v2);

            int w1;
            int w2;
            int h1;
            int h2;
            int x2;
            int y2;
            // if width is greater than height, we split the width
            if (w0 > h0) {
                w1 = (int) (w0 * weight1 / weight0);
                w2 = w0 - w1;
                h1 = h0;
                h2 = h0;
                x2 = x0 + w1;
                y2 = y0;
            } else {
                // else we split the height
                w1 = w0;
                w2 = w0;
                h1 = (int) (h0 * weight1 / weight0);
                h2 = h0 - h1;
                x2 = x0;
                y2 = y0 + h1;
            }
            // calculation for the new two Vectors
            calculatePositionsRec(x0, y0, w1, h1, weight1, v1);
            calculatePositionsRec(x2, y2, w2, h2, weight2, v2);
        }
    }

    /**
     * Sort the elements by descending weight.
     * 
     * @param v
     *            Vector with the elements to be sorted
     */
    protected void sortVector(final Vector<TreeMapNode> v) {
        TreeMapNode tmn;
        // we use the bubble sort
        for (int i = 0; i < v.size(); i++) {
            for (int j = v.size() - 1; j > i; j--) {
                if ((v.elementAt(j)).getWeight() > (v.elementAt(j - 1)).getWeight()) {
                    tmn = (v.elementAt(j));
                    v.setElementAt(v.elementAt(j - 1), j);
                    v.setElementAt(tmn, j - 1);
                }
            }
        }

    }

    protected void workOutWeight(final Vector<TreeMapNode> v1, final Vector<TreeMapNode> v2, final Vector<TreeMapNode> vClone,
            final double sumWeight) {
        double memWeight = 0.0;
        double elemWeight = 0.0;
        for (final Iterator<TreeMapNode> i = vClone.iterator(); i.hasNext();) {
            TreeMapNode tmn = i.next();
            elemWeight = tmn.getWeight();
            // if adding the current element pass the middle of total weight
            if (memWeight + elemWeight >= sumWeight / 2) {
                // we look at the finest split (the nearest of the middle of
                // weight)
                if (((sumWeight / 2) - memWeight) > ((memWeight + elemWeight) - (sumWeight / 2))) {
                    // if it is after the add, we add the element to the first
                    // Vector
                    memWeight += elemWeight;
                    v1.addElement(tmn);
                } else {
                    // we must have at least 1 element in the first vector
                    if (v1.isEmpty()) {
                        v1.addElement(tmn);
                    } else {
                        // if it is before the add, we add the element to the
                        // second Vector
                        v2.addElement(tmn);
                    }
                }
                // then we fill the second Vector qith the rest of elements
                while (i.hasNext()) {
                    tmn = i.next();
                    v2.addElement(tmn);
                }
            } else {
                // we add in the first vector while we don't reach the middle of
                // weight
                memWeight += elemWeight;
                v1.addElement(tmn);
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
