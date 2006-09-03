/*
 * Created on 3 nov. 2005
 */
package org.jense.ktreemap;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Rectangle;

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
  public void splitElements(List<TreeMapNode> list, List<TreeMapNode> group1,
      List<TreeMapNode> group2) {
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
   * @see org.jense.swing.jtreemap.SplitStrategy#calculatePositionsRec(int, int,
   *      int, int, double, java.util.Vector)
   */
  @Override
  protected void calculatePositionsRec(Rectangle bounds, double weight0,
      List<TreeMapNode> children) {
    List<TreeMapNode> listClone = new ArrayList<TreeMapNode>(children);

    sortList(listClone);

    if (listClone.size() <= 2) {
      SplitBySlice.splitInSlice(bounds, listClone, sumWeight(listClone));
      calculateChildren(listClone);
    } else {
      List<TreeMapNode> l1 = new ArrayList<TreeMapNode>();
      List<TreeMapNode> l2 = new ArrayList<TreeMapNode>();
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
      calculateChildren(l1);
      Rectangle bounds2 = new Rectangle(x2, y2, w2, h2);
      calculatePositionsRec(bounds2, sumWeight(l2), l2);
    }

  }

  private double aspect(double big, double small, double a, double b) {
    return (big * b) / (small * a / b);
  }

  /**
   * Execute the recurence for the children of the elements of the vector.<BR>
   * Add also the borders if necessary
   * 
   * @param v Vector with the elements to calculate
   */
  private void calculateChildren(List<TreeMapNode> v) {
    for (TreeMapNode node : v) {
      if (node.isLeaf()) {
        node.setX(node.getX() + TreeMapNode.getBorder());
        node.setY(node.getY() + TreeMapNode.getBorder());
        int w = node.getWidth() - TreeMapNode.getBorder();
        if (w < 0) {
          w = 0;
        }
        int h = node.getHeight() - TreeMapNode.getBorder();
        if (h < 0) {
          h = 0;
        }
        node.setHeight(h);
        node.setWidth(w);
      } else {
        // if this is not a leaf, calculation for the children
        int bSub;
        if (TreeMapNode.getBorder() > 1) {
          bSub = 2;
        } else if (TreeMapNode.getBorder() == 1) {
          bSub = 1;
        } else {
          bSub = 0;
        }

        int w = node.getWidth() - bSub;
        if (w < 0) {
          w = 0;
        }
        int h = node.getHeight() - bSub;
        if (h < 0) {
          h = 0;
        }

        TreeMapNode.setBorder(TreeMapNode.getBorder() - bSub);
        Rectangle newBounds = new Rectangle(node.getX() + bSub, node.getY()
            + bSub, w, h);
        calculatePositionsRec(newBounds, node.getWeight(), node.getChildren());
        TreeMapNode.setBorder(TreeMapNode.getBorder() + bSub);
      }

    }
  }

  private double normAspect(double big, double small, double a, double b) {
    double x = aspect(big, small, a, b);
    if (x < 1) {
      return 1 / x;
    }
    return x;
  }

}
