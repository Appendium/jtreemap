package net.sf.jtreemap.swing;

import java.io.Serializable;
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
        if (v != null) {
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
            double weight1, weight2; // poids des 2 vecteurs
            this.splitElements(v, v1, v2);
            weight1 = this.sumWeight(v1);
            weight2 = this.sumWeight(v2);

            int w1, w2, h1, h2;
            int x2, y2;
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

}
