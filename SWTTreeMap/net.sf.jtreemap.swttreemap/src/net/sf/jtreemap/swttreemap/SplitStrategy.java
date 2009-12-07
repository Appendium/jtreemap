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
 * $Id: SplitStrategy.java 75 2006-10-24 23:00:51Z benoitx $
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.draw2d.geometry.Rectangle;


/**
 * Abtract class with the method which split the elements of a KTreeMap.
 * <p>
 * The split is done by dichotomy. We split the elements in 2 groups with a
 * defined strategy (for example : take care of the weight of the elements)
 * <p>
 *
 * @author Laurent Dutheil
 */

public abstract class SplitStrategy {
  /**
   * calculate the positions for all the elements of the root.
   *
   * @param root the root to calculate
   */
//  public void calculatePositions(TreeMapNode root) {
//    List<TreeMapNode> children = root.getChildren();
//    if (children != null) {
//      calculatePositionsRec(root.getBounds(), this.sumWeight(children),
//          children);
//    }
//  }

  public void calculatePositions2(Rectangle bounds, double weight0,
	      List<TreeMapNode2> children) {
	      calculatePositionsRec(bounds, weight0, children);
	  }

  /**
   * split the elements of a JTreeMap.
   *
   * @param v Vector with the elements to split (arg IN)
   * @param v1 first Vector of the split (arg OUT)
   * @param v2 second Vector of the split (arg OUT)
   */
  public abstract void splitElements(List<TreeMapNode2> v, List<TreeMapNode2> v1,
		  List<TreeMapNode2> v2);

  /**
   * Sum the weight of elements. <BR>
   * You can override this method if you want to apply a coef on the weights or
   * to cancel the effect of weight on the strategy.
   *
   * @param children List with the elements to sum
   * @return the sum of the weight of elements
   */
  public double sumWeight(List<TreeMapNode2> children) {
    double d = 0.0;
      for (TreeMapNode2 node : children) {
        d += node.getWeight();
      }
    return d;
  }

  protected void calculatePositionsRec(Rectangle bounds, double weight0,
      List<TreeMapNode2> children) {

    // if the Vector contains only one element
    if (children.size() == 1) {
      TreeMapNode2 f = children.iterator().next();
      f.getFigure().setBounds(bounds);
    } else {
      // if there is more than one element
      // we split the Vector according to the selected strategy
      List<TreeMapNode2> group1 = new ArrayList<TreeMapNode2>();
      List<TreeMapNode2> group2 = new ArrayList<TreeMapNode2>();
      double weight1, weight2; // weights of the groups
      this.splitElements(children, group1, group2);
      weight1 = this.sumWeight(group1);
      weight2 = this.sumWeight(group2);

      int w1, w2, h1, h2;
      int x2, y2;
      // if width is greater than height, we split the width
      if (bounds.width > bounds.height) {
        w1 = (int)(bounds.width * weight1 / weight0);
        w2 = bounds.width - w1;
        h1 = bounds.height;
        h2 = bounds.height;
        x2 = bounds.x + w1;
        y2 = bounds.y;
      } else {
        // else we split the height
        w1 = bounds.width;
        w2 = bounds.width;
        h1 = (int)(bounds.height * weight1 / weight0);
        h2 = bounds.height - h1;
        x2 = bounds.x;
        y2 = bounds.y + h1;
      }
      // calculation for the new two Vectors
      Rectangle bounds1 = new Rectangle(bounds.x, bounds.y, w1, h1);
      calculatePositionsRec(bounds1, weight1, group1);
      Rectangle bounds2 = new Rectangle(x2, y2, w2, h2);
      calculatePositionsRec(bounds2, weight2, group2);
    }
  }

  /**
   * Sort the elements by descending weight.
   *
   * @param v Vector with the elements to be sorted
   */
  protected void sortList(List<TreeMapNode2> v) {
    TreeMapNode2 tmn;
    // we use the bubble sort
    for (int i = 0; i < v.size(); i++) {
      for (int j = v.size() - 1; j > i; j--) {
        if ((v.get(j)).getWeight() > (v.get(j - 1)).getWeight()) {
          tmn = (v.get(j));
          v.set(j, v.get(j - 1));
          v.set(j - 1, tmn);
        }
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
