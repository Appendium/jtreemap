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
 * $Id: SplitByWeight.java 75 2006-10-24 23:00:51Z benoitx $
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
package net.sf.jtreemap.swttreemap;

import java.util.Iterator;
import java.util.List;

/**
 * Strategy who split the elements in 2 groups of equivalent weight.
 *
 * @author Laurent Dutheil
 */

public class SplitByWeight extends SplitStrategy {

  @Override
  public void splitElements(List<TreeMapNode2> list, List<TreeMapNode2> group1,
      List<TreeMapNode2> group2) {
    double memWeight = 0.0;
    double sumWeight = sumWeight(list);
    double elemWeight = 0.0;

    for (Iterator<TreeMapNode2> i = list.iterator(); i.hasNext();) {
      TreeMapNode2 tmn = i.next();
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
        // then we fill the second Vector with the rest of elements
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
