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
 * $Id: SplitSquarified.java 75 2006-10-24 23:00:51Z benoitx $
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
import java.util.List;

import org.eclipse.draw2d.geometry.Rectangle;


/**
 * The Squarified split strategy
 *
 * @author Laurent DUTHEIL
 */
public class SplitSquarified extends SplitStrategy {
  private int w1, h1;
  private int x, y, w, h;
  private int x2, y2, w2, h2;

  @Override
  public void splitElements(List<TreeMapNode2> list, List<TreeMapNode2> group1,
		  List<TreeMapNode2> group2) {
    int mid = 0;
    double weight0 = sumWeight(list);
    double a = list.get(mid).getWeight() / weight0;
    double b = a;

    if (this.w < this.h) {
      // height/width
      while (mid < list.size()) {
        double aspect = normAspect(this.h, this.w, a, b);
        double q = list.get(mid).getWeight() / weight0;
        if (normAspect(this.h, this.w, a, b + q) > aspect) {
          break;
        }
        mid++;
        b += q;
      }
      int i = 0;
      for (; i <= mid && i < list.size(); i++) {
        group1.add(list.get(i));
      }
      for (; i < list.size(); i++) {
        group2.add(list.get(i));
      }
      this.h1 = (int)Math.round(this.h * b);
      this.w1 = this.w;
      this.x2 = this.x;
      this.y2 = (int)Math.round(this.y + this.h * b);
      this.w2 = this.w;
      this.h2 = this.h - this.h1;
    } else {
      // width/height
      while (mid < list.size()) {
        double aspect = normAspect(this.w, this.h, a, b);
        double q = list.get(mid).getWeight() / weight0;
        if (normAspect(this.w, this.h, a, b + q) > aspect) {
          break;
        }
        mid++;
        b += q;
      }
      int i = 0;
      for (; i <= mid && i < list.size(); i++) {
        group1.add(list.get(i));
      }
      for (; i < list.size(); i++) {
        group2.add(list.get(i));
      }
      this.h1 = this.h;
      this.w1 = (int)Math.round(this.w * b);
      this.x2 = (int)Math.round(this.x + this.w * b);
      this.y2 = this.y;
      this.w2 = this.w - this.w1;
      this.h2 = this.h;
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see net.sf.jtreemap.swing.SplitStrategy#calculatePositionsRec(int, int,
   *      int, int, double, java.util.Vector)
   */
  @Override
  protected void calculatePositionsRec(Rectangle bounds, double weight0,
		  List<TreeMapNode2> children) {
    List<TreeMapNode2> listClone = new ArrayList<TreeMapNode2>(children);

    sortList(listClone);

    if (listClone.size() <= 2) {
      SplitBySlice.splitInSlice(bounds, listClone, sumWeight(listClone));
    } else {
      List<TreeMapNode2> l1 = new ArrayList<TreeMapNode2>();
      List<TreeMapNode2> l2 = new ArrayList<TreeMapNode2>();
      this.x = bounds.x;
      this.y = bounds.y;
      this.w = bounds.width;
      this.h = bounds.height;
      splitElements(listClone, l1, l2);
      // before the recurence, we have to "save" the values for the 2nd Vector
      int x2 = this.x2;
      int y2 = this.y2;
      int w2 = this.w2;
      int h2 = this.h2;
      Rectangle bounds1 = new Rectangle(bounds.x, bounds.y, this.w1, this.h1);
      SplitBySlice.splitInSlice(bounds1, l1, sumWeight(l1));
      Rectangle bounds2 = new Rectangle(x2, y2, w2, h2);
      calculatePositionsRec(bounds2, sumWeight(l2), l2);
    }

  }

  private double aspect(double big, double small, double a, double b) {
    return (big * b) / (small * a / b);
  }

  private double normAspect(double big, double small, double a, double b) {
    double x = aspect(big, small, a, b);
    if (x < 1) {
      return 1 / x;
    }
    return x;
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
