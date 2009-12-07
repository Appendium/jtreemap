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
 * $Id: SplitByNumber.java 75 2006-10-24 23:00:51Z benoitx $
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

import java.util.List;

/**
 * Strategy who split the elements in 2 groups of same cardinal.
 *
 * @author Laurent DUTHEIL
 */
public class SplitByNumber extends SplitStrategy {

  @Override
  public void splitElements(List<TreeMapNode2> v, List<TreeMapNode2> v1, List<TreeMapNode2> v2) {
    int size = v.size();
    int middle = size / 2;
    int index = 0;
    //we add first elements to v1
    for (; index < middle; index++) {
      v1.add(v.get(index));
    }
    //we add last elements to v2
    for (; index < size; index++) {
      v2.add(v.get(index));
    }
  }

  @Override
  public double sumWeight(List<TreeMapNode2> v) {
    //all the elements must have the same weight
    double weight = 0.0;
    for (TreeMapNode2 node : v) {
// TODO      if (node.isLeaf()) {
//        weight += 1;
//      } else {
//        weight += this.sumWeight(node.getChildren());
//      }
    }
    return weight;
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
