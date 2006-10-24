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
 * $Id$
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
package net.sf.jtreemap.ktreemap;

import java.util.List;

import org.eclipse.swt.graphics.Rectangle;

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
  public static void splitInSlice(Rectangle bounds, List<TreeMapNode> children,
      double sumWeight) {
    int offset = 0;
    boolean vertical = bounds.height > bounds.width;

    for (TreeMapNode node : children) {
      if (vertical) {
        node.setX(bounds.x);
        node.setWidth(bounds.width);
        node.setY(bounds.y + offset);
        node.setHeight((int)Math.round(bounds.height * node.getWeight()
            / sumWeight));
        offset = offset + node.getHeight();
      } else {
        node.setX(bounds.x + offset);
        node.setWidth((int)Math.round(bounds.width * node.getWeight()
            / sumWeight));
        node.setY(bounds.y);
        node.setHeight(bounds.height);
        offset = offset + node.getWidth();
      }
    }

    // Because of the Math.round(), we adjust the last element to fit the
    // correctly the JTreeMap
    if (!children.isEmpty()) {
      TreeMapNode node = children.get(children.size() - 1);
      if (vertical && bounds.height != offset) {
        node.setHeight(node.getHeight() - offset + bounds.height);
      } else if (!vertical && bounds.width != offset) {
        node.setWidth(node.getWidth() - offset + bounds.width);
      }
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see net.sf.jtreemap.swing.SplitStrategy#splitElements(java.util.Vector,
   *      java.util.Vector, java.util.Vector)
   */
  @Override
  public void splitElements(List<TreeMapNode> v, List<TreeMapNode> v1,
      List<TreeMapNode> v2) {
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
      List<TreeMapNode> v) {

    SplitBySlice.splitInSlice(bounds, v, weight0);

    for (TreeMapNode node : v) {
      if (node.isLeaf()) {
        node.setX(node.getX() + TreeMapNode.getBorder());
        node.setY(node.getY() + TreeMapNode.getBorder());
        node.setHeight(node.getHeight() - TreeMapNode.getBorder());
        node.setWidth(node.getWidth() - TreeMapNode.getBorder());
      } else {
        // if this is not a leaf, calculation for the children
        if (TreeMapNode.getBorder() > 1) {
          TreeMapNode.setBorder(TreeMapNode.getBorder() - 2);
          Rectangle newBounds = new Rectangle(node.getX() + 2, node.getY() + 2,
              node.getWidth() - 2, node.getHeight() - 2);
          calculatePositionsRec(newBounds, node.getWeight(), node.getChildren());
          TreeMapNode.setBorder(TreeMapNode.getBorder() + 2);
        } else if (TreeMapNode.getBorder() == 1) {
          TreeMapNode.setBorder(0);
          Rectangle newBounds = new Rectangle(node.getX() + 1, node.getY() + 1,
              node.getWidth() - 1, node.getHeight() - 1);
          calculatePositionsRec(newBounds, node.getWeight(), node.getChildren());
          TreeMapNode.setBorder(1);
        } else {
          calculatePositionsRec(node.getBounds(), node.getWeight(), node
              .getChildren());
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
