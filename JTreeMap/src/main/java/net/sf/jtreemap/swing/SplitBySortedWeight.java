package net.sf.jtreemap.swing;

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
        final double sumWeight = sumWeight(v);

        sortVector(vClone);

        workOutWeight(v1, v2, vClone, sumWeight);
    }

}
