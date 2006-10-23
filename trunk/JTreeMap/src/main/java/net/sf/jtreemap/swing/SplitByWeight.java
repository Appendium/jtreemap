package org.jense.swing.jtreemap;

import java.util.Iterator;
import java.util.Vector;

/**
 * Strategy who split the elements in 2 groups of equivalent weight.
 * 
 * @author Laurent Dutheil
 */

public class SplitByWeight extends SplitStrategy {

  @Override
  public void splitElements(Vector<TreeMapNode> v, Vector<TreeMapNode> v1,
      Vector<TreeMapNode> v2) {
    double memWeight = 0.0;
    double sumWeight = sumWeight(v);
    double elemWeight = 0.0;

    for (Iterator<TreeMapNode> i = v.iterator(); i.hasNext();) {
      TreeMapNode tmn = i.next();
      elemWeight = tmn.getWeight();
      // if adding the current element pass the middle of total weight
      if (memWeight + elemWeight >= sumWeight / 2) {
        // we look at the finest split (the nearest of the middle of weight)
        if (((sumWeight / 2) - memWeight) > ((memWeight + elemWeight) - (sumWeight / 2))) {
          // if it is after the add, we add the element to the first Vector
          memWeight += elemWeight;
          v1.addElement(tmn);
        } else {
          // we must have at least 1 element in the first vector
          if (v1.isEmpty()) {
            v1.addElement(tmn);
          } else {
            // if it is before the add, we add the element to the second Vector
            v2.addElement(tmn);
          }
        }
        // then we fill the second Vector qith the rest of elements
        while (i.hasNext()) {
          tmn = i.next();
          v2.addElement(tmn);
        }
      } else {
        // we add in the first vector while we don't reach the middle of weight
        memWeight += elemWeight;
        v1.addElement(tmn);
      }
    }
  }

}
