package net.sf.jtreemap.swing;

import java.util.Vector;

/**
 * Strategy who split the elements in 2 groups of same cardinal.
 * 
 * @author Laurent DUTHEIL
 */
public class SplitByNumber extends SplitStrategy {

    /**
     * 
     */
    private static final long serialVersionUID = -6484279333222332702L;

    @Override
    public void splitElements(final Vector<TreeMapNode> v, final Vector<TreeMapNode> v1, final Vector<TreeMapNode> v2) {
        final int size = v.size();
        final int middle = size / 2;
        int index = 0;
        // we add first elements to v1
        for (; index < middle; index++) {
            v1.addElement(v.elementAt(index));
        }
        // we add last elements to v2
        for (; index < size; index++) {
            v2.addElement(v.elementAt(index));
        }
    }

    @Override
    public double sumWeight(final Vector<TreeMapNode> v) {
        // all the elements must have the same weight
        double weight = 0.0;
        for (final TreeMapNode node : v) {
            if (node.isLeaf()) {
                weight += 1;
            } else {
                weight += this.sumWeight(node.getChildren());
            }
        }
        return weight;
    }

}
