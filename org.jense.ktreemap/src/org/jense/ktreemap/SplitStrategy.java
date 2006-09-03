package org.jense.ktreemap;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Rectangle;

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
  public void calculatePositions(TreeMapNode root) {
    if (root == null) {
      return;
    }

    List<TreeMapNode> children = root.getChildren();
    if (children != null) {
      calculatePositionsRec(root.getBounds(), this.sumWeight(children),
          children);
    }
  }

  /**
   * split the elements of a JTreeMap.
   * 
   * @param v Vector with the elements to split (arg IN)
   * @param v1 first Vector of the split (arg OUT)
   * @param v2 second Vector of the split (arg OUT)
   */
  public abstract void splitElements(List<TreeMapNode> v, List<TreeMapNode> v1,
      List<TreeMapNode> v2);

  /**
   * Sum the weight of elements. <BR>
   * You can override this method if you want to apply a coef on the weights or
   * to cancel the effect of weight on the strategy.
   * 
   * @param children List with the elements to sum
   * @return the sum of the weight of elements
   */
  public double sumWeight(List<TreeMapNode> children) {
    double d = 0.0;
    if (children != null) {
      for (TreeMapNode node : children) {
        d += node.getWeight();
      }
    }
    return d;
  }

  protected void calculatePositionsRec(Rectangle bounds, double weight0,
      List<TreeMapNode> children) {

    // if the Vector contains only one element
    if (children.size() == 1) {
      TreeMapNode f = children.get(0);
      if (f.isLeaf()) {
        // if this is a leaf, we display with the border
        int w = bounds.width - TreeMapNode.getBorder();
        if (w < 0) {
          w = 0;
        }
        int h = bounds.height - TreeMapNode.getBorder();
        if (h < 0) {
          h = 0;
        }
        Rectangle newBounds = new Rectangle(bounds.x + TreeMapNode.getBorder(),
            bounds.y + TreeMapNode.getBorder(), w, h);
        f.setBounds(newBounds);
      } else {
        // if this is not a leaf, calculation for the children
        f.setBounds(bounds);

        int bSub;
        if (TreeMapNode.getBorder() > 1) {
          bSub = 2;
        } else if (TreeMapNode.getBorder() == 1) {
          bSub = 1;
        } else {
          bSub = 0;
        }

        int w = bounds.width - bSub;
        if (w < 0) {
          w = 0;
        }
        int h = bounds.height - bSub;
        if (h < 0) {
          h = 0;
        }

        TreeMapNode.setBorder(TreeMapNode.getBorder() - bSub);
        Rectangle newBounds = new Rectangle(bounds.x + bSub, bounds.y + bSub,
            w, h);
        calculatePositionsRec(newBounds, weight0, f.getChildren());
        TreeMapNode.setBorder(TreeMapNode.getBorder() + bSub);
      }
    } else {
      // if there is more than one element
      // we split the Vector according to the selected strategy
      List<TreeMapNode> group1 = new ArrayList<TreeMapNode>();
      List<TreeMapNode> group2 = new ArrayList<TreeMapNode>();
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
  protected void sortList(List<TreeMapNode> v) {
    TreeMapNode tmn;
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
