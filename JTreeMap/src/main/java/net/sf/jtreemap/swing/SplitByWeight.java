package net.sf.jtreemap.swing;

import java.util.Vector;

/**
 * Strategy who split the elements in 2 groups of equivalent weight.
 * 
 * @author Laurent Dutheil
 */

public class SplitByWeight extends SplitStrategy {

    /**
     * 
     */
    private static final long serialVersionUID = 1136669403687512925L;

    @Override
    public void splitElements(final Vector<TreeMapNode> v, final Vector<TreeMapNode> v1, final Vector<TreeMapNode> v2) {
        final double sumWeight = sumWeight(v);

        workOutWeight(v, v1, v2, sumWeight);
    }

    /*
     * private double workoutWeight(final Vector<TreeMapNode> v, final Vector<TreeMapNode>
     * v1, final Vector<TreeMapNode> v2, double memWeight, final double
     * sumWeight) { double elemWeight; for (final Iterator<TreeMapNode> i =
     * v.iterator(); i.hasNext();) { TreeMapNode tmn = i.next(); elemWeight =
     * tmn.getWeight(); // if adding the current element pass the middle of
     * total weight if (memWeight + elemWeight >= sumWeight / 2) { // we look at
     * the finest split (the nearest of the middle of // weight) if (((sumWeight /
     * 2) - memWeight) > ((memWeight + elemWeight) - (sumWeight / 2))) { // if
     * it is after the add, we add the element to the first // Vector memWeight +=
     * elemWeight; v1.addElement(tmn); } else { // we must have at least 1
     * element in the first vector if (v1.isEmpty()) { v1.addElement(tmn); }
     * else { // if it is before the add, we add the element to the // second
     * Vector v2.addElement(tmn); } } // then we fill the second Vector qith the
     * rest of elements while (i.hasNext()) { tmn = i.next();
     * v2.addElement(tmn); } } else { // we add in the first vector while we
     * don't reach the middle of // weight memWeight += elemWeight;
     * v1.addElement(tmn); } } return memWeight; }
     * 
     */
}
