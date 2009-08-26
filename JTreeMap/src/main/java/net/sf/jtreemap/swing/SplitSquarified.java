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
 * The Squarified split strategy
 * 
 * @author Laurent DUTHEIL
 */
public class SplitSquarified extends SplitStrategy {
    /**
     * 
     */
    private static final long serialVersionUID = 1711898915283018450L;

    private int w1;

    private int h1;

    private int x;

    private int y;

    private int w;

    private int h;

    private int x2;

    private int y2;

    private int w2;

    private int h2;

    @Override
    public void splitElements(final Vector<TreeMapNode> v, final Vector<TreeMapNode> v1, final Vector<TreeMapNode> v2) {
        int mid = 0;
        final double weight0 = sumWeight(v);
        final double a = v.get(mid).getWeight() / weight0;
        double b = a;

        if (this.w < this.h) {
            // height/width
            while (mid < v.size()) {
                final double aspect = normAspect(this.h, this.w, a, b);
                final double q = v.get(mid).getWeight() / weight0;
                if (normAspect(this.h, this.w, a, b + q) > aspect) {
                    break;
                }
                mid++;
                b += q;
            }
            int i = 0;
            for (; i <= mid && i < v.size(); i++) {
                v1.add(v.get(i));
            }
            for (; i < v.size(); i++) {
                v2.add(v.get(i));
            }
            this.h1 = (int) Math.round(this.h * b);
            this.w1 = this.w;
            this.x2 = this.x;
            this.y2 = (int) Math.round(this.y + this.h * b);
            this.w2 = this.w;
            this.h2 = this.h - this.h1;
        } else {
            // width/height
            while (mid < v.size()) {
                final double aspect = normAspect(this.w, this.h, a, b);
                final double q = v.get(mid).getWeight() / weight0;
                if (normAspect(this.w, this.h, a, b + q) > aspect) {
                    break;
                }
                mid++;
                b += q;
            }
            int i = 0;
            for (; i <= mid && i < v.size(); i++) {
                v1.add(v.get(i));
            }
            for (; i < v.size(); i++) {
                v2.add(v.get(i));
            }
            this.h1 = this.h;
            this.w1 = (int) Math.round(this.w * b);
            this.x2 = (int) Math.round(this.x + this.w * b);
            this.y2 = this.y;
            this.w2 = this.w - this.w1;
            this.h2 = this.h;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.jtreemap.swing.SplitStrategy#calculatePositionsRec(int, int,
     *      int, int, double, java.util.Vector)
     */
    @Override
    protected void calculatePositionsRec(final int x0, final int y0, final int w0, final int h0, final double weight0, final Vector<TreeMapNode> v) {
        // 1. don't calculate if the area is too small,
        if (w0 * h0 < 20) {
            return;
        }

        // 2. don't calculate if the candidates are too many to display
        if (w0 * h0 < v.size()) {
            return;
        }

        final Vector<TreeMapNode> vClone = new Vector<TreeMapNode>(v);

        sortVector(vClone);

        if (vClone.size() <= 2) {
            SplitBySlice.splitInSlice(x0, y0, w0, h0, vClone, sumWeight(vClone));
            calculateChildren(vClone);
        } else {
            final Vector<TreeMapNode> v1 = new Vector<TreeMapNode>();
            final Vector<TreeMapNode> v2 = new Vector<TreeMapNode>();
            this.x = x0;
            this.y = y0;
            this.w = w0;
            this.h = h0;
            splitElements(vClone, v1, v2);
            // before the recurence, we have to "save" the values for the 2nd
            // Vector
            final int prevX2 = this.x2;
            final int prevY2 = this.y2;
            final int prevW2 = this.w2;
            final int prevH2 = this.h2;
            SplitBySlice.splitInSlice(x0, y0, this.w1, this.h1, v1, sumWeight(v1));
            calculateChildren(v1);
            calculatePositionsRec(prevX2, prevY2, prevW2, prevH2, sumWeight(v2), v2);
        }

    }

    private double aspect(final double big, final double small, final double a, final double b) {
        return (big * b) / (small * a / b);
    }

    /**
     * Execute the recurence for the children of the elements of the vector.<BR>
     * Add also the borders if necessary
     * 
     * @param v
     *            Vector with the elements to calculate
     */
    private void calculateChildren(final Vector<TreeMapNode> v) {
        for (final TreeMapNode node : v) {
            if (node.isLeaf()) {
                node.setX(node.getX() + TreeMapNode.getBorder());
                node.setY(node.getY() + TreeMapNode.getBorder());
                int width = node.getWidth() - TreeMapNode.getBorder();
                if (width < 0) {
                    width = 0;
                }
                int height = node.getHeight() - TreeMapNode.getBorder();
                if (height < 0) {
                    height = 0;
                }
                node.setHeight(height);
                node.setWidth(width);
            } else {
                // if this is not a leaf, calculation for the children
                int bSub;
                if (TreeMapNode.getBorder() > 1) {
                    bSub = 2;
                } else if (TreeMapNode.getBorder() == 1) {
                    bSub = 1;
                } else {
                    bSub = 0;
                }

                int width = node.getWidth() - bSub;
                if (width < 0) {
                    width = 0;
                }
                int height = node.getHeight() - bSub;
                if (height < 0) {
                    height = 0;
                }

                TreeMapNode.setBorder(TreeMapNode.getBorder() - bSub);
                calculatePositionsRec(node.getX() + bSub, node.getY() + bSub, width, height, node.getWeight(), node.getChildren());
                TreeMapNode.setBorder(TreeMapNode.getBorder() + bSub);
            }

        }
    }

    private double normAspect(final double big, final double small, final double a, final double b) {
        final double xCalc = aspect(big, small, a, b);
        if (xCalc < 1) {
            return 1 / xCalc;
        }
        return xCalc;
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
