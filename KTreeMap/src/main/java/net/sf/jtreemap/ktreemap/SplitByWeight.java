package org.jense.ktreemap;

import java.util.Iterator;
import java.util.List;

/**
 * Strategy who split the elements in 2 groups of equivalent weight.
 * 
 * @author Laurent Dutheil
 */

public class SplitByWeight extends SplitStrategy {

  @Override
  public void splitElements(List<TreeMapNode> list, List<TreeMapNode> group1,
      List<TreeMapNode> group2) {
    double memWeight = 0.0;
    double sumWeight = sumWeight(list);
    double elemWeight = 0.0;

    for (Iterator<TreeMapNode> i = list.iterator(); i.hasNext();) {
      TreeMapNode tmn = i.next();
      elemWeight = tmn.getWeight();
      // if adding the current element pass the middle of total weight
      if (memWeight + elemWeight >= sumWeight / 2) {
        // we look at the finest split (the nearest of the middle of weight)
        if (((sumWeight / 2) - memWeight) > ((memWeight + elemWeight) - (sumWeight / 2))) {
          // if it is after the add, we add the element to the first Vector
          memWeight += elemWeight;
          group1.add(tmn);
        } else {
          // we must have at least 1 element in the first vector
          if (group1.isEmpty()) {
            group1.add(tmn);
          } else {
            // if it is before the add, we add the element to the second Vector
            group2.add(tmn);
          }
        }
        // then we fill the second Vector qith the rest of elements
        while (i.hasNext()) {
          tmn = i.next();
          group2.add(tmn);
        }
      } else {
        // we add in the first vector while we don't reach the middle of weight
        memWeight += elemWeight;
        group1.add(tmn);
      }
    }
  }

}
