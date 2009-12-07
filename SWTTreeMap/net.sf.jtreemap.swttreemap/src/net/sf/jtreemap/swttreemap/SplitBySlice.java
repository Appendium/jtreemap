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
 * $Id: SplitBySlice.java 75 2006-10-24 23:00:51Z benoitx $
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

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;


/**
 * Split the treemap by slice
 *
 * @author Laurent DUTHEIL
 */
public class SplitBySlice extends SplitStrategy {

  /**
   * Calculate the dimension of the elements of the Vector.
   * @param bounds bounds of the KTreeMap
   * @param children elements to split in the dimensions before
   * @param sumWeight sum of the weights
   */
  public static void splitInSlice(Rectangle bounds, List<TreeMapNode2> children,
      double sumWeight) {
    int offset = 0;
    boolean vertical = bounds.height > bounds.width;

    for (TreeMapNode2 node : children) {
    	if (vertical) {
    		Rectangle childBounds = new Rectangle(bounds.x, bounds.y + offset, bounds.width, (int)Math.round(bounds.height * node.getWeight()
    				/ sumWeight));
    		node.getFigure().setBounds(childBounds);
    		offset += node.getFigure().getSize().height;
    	} else {
    		Rectangle childBounds = new Rectangle(bounds.x + offset, bounds.y, (int)Math.round(bounds.width * node.getWeight()
    				/ sumWeight), bounds.height);
    		node.getFigure().setBounds(childBounds);
    		offset += node.getFigure().getSize().width;
    	}
    }

    // Because of the Math.round(), we adjust the last element to fit the
    // correctly the JTreeMap
    if (!children.isEmpty()) {
    	TreeMapNode2 node = children.get(children.size() - 1);
    	Dimension size = node.getFigure().getSize();
    	if (vertical && bounds.height != offset) {
    		size.height = size.height - offset + bounds.height;
    	} else if (!vertical && bounds.width != offset) {
    		size.width = size.width - offset + bounds.width;
    	}
    	node.getFigure().setSize(size);
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see net.sf.jtreemap.swing.SplitStrategy#splitElements(java.util.Vector,
   *      java.util.Vector, java.util.Vector)
   */
  @Override
  public void splitElements(List<TreeMapNode2> v, List<TreeMapNode2> v1,
      List<TreeMapNode2> v2) {
  // ignore

  }

  /*
   * (non-Javadoc)
   *
   * @see net.sf.jtreemap.swing.SplitStrategy#calculatePositionsRec(int, int,
   *      int, int, double, java.util.Vector)
   */
  @Override
  protected void calculatePositionsRec(Rectangle bounds, double weight0,
      List<TreeMapNode2> v) {

    SplitBySlice.splitInSlice(bounds, v, weight0);
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
