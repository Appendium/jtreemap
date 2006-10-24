/*
 * Created on 3 nov. 2005
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

    private int w1, h1;

    private int x, y, w, h;

    private int x2, y2, w2, h2;

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
    protected void calculatePositionsRec(final int x0, final int y0, final int w0, final int h0, final double weight0,
            final Vector<TreeMapNode> v) {
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
            final int x2 = this.x2;
            final int y2 = this.y2;
            final int w2 = this.w2;
            final int h2 = this.h2;
            SplitBySlice.splitInSlice(x0, y0, this.w1, this.h1, v1, sumWeight(v1));
            calculateChildren(v1);
            calculatePositionsRec(x2, y2, w2, h2, sumWeight(v2), v2);
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
                int w = node.getWidth() - TreeMapNode.getBorder();
                if (w < 0) {
                    w = 0;
                }
                int h = node.getHeight() - TreeMapNode.getBorder();
                if (h < 0) {
                    h = 0;
                }
                node.setHeight(h);
                node.setWidth(w);
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

                int w = node.getWidth() - bSub;
                if (w < 0) {
                    w = 0;
                }
                int h = node.getHeight() - bSub;
                if (h < 0) {
                    h = 0;
                }

                TreeMapNode.setBorder(TreeMapNode.getBorder() - bSub);
                calculatePositionsRec(node.getX() + bSub, node.getY() + bSub, w, h, node.getWeight(), node.getChildren());
                TreeMapNode.setBorder(TreeMapNode.getBorder() + bSub);
            }

        }
    }

    private double normAspect(final double big, final double small, final double a, final double b) {
        final double x = aspect(big, small, a, b);
        if (x < 1) {
            return 1 / x;
        }
        return x;
    }

}
