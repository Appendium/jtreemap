package net.sf.jtreemap.swing;

import java.util.Iterator;
import java.util.Vector;

/**
 * Strategy who split the elements in 2 groups of equivalent weight.
 * <p>
 * The elements are first sorted by descending weight. Then they are splitted in
 * 2 groups of equivalent weight.
 * <p>
 * The heaviest elements are on the top left of the JTreeMap. The lightest
 * elements are on the bottom right of the JTreeMap
 * 
 * @author Laurent Dutheil
 */

public class SplitBySortedWeight extends SplitStrategy {

    /**
     * 
     */
    private static final long serialVersionUID = 1600419780258843122L;

    @Override
    public void splitElements(final Vector<TreeMapNode> v, final Vector<TreeMapNode> v1, final Vector<TreeMapNode> v2) {
        final Vector<TreeMapNode> vClone = new Vector<TreeMapNode>(v);
        double memWeight = 0.0;
        final double sumWeight = sumWeight(v);
        double elemWeight = 0.0;

        sortVector(vClone);

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
