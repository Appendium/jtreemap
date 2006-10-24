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

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;

/**
 * Widget who represents each element of a tree in a rectangle of more or
 * less big size according to its importance in the tree.
 * <p>
 * A tree structure may includes more or less important elements. For example,
 * in a tree structure of files, there can be files of big size. Then it can be
 * interesting to know which repertory is the most important on a hard disk.
 * <p>
 * Moreover, we can add a code color which makes it possible to introduce new
 * information into the representation of the tree structure.
 * <p>
 * So, in a KTreeMap, you can see the size and the value of an element in a
 * tree.
 *
 * @see net.sf.jtreemap.ktreemap.TreeMapNode
 * @author Laurent Dutheil
 */
public class KTreeMap extends Canvas {
  private static final long serialVersionUID = 7255952672238300249L;
  private Color revealColor = null;
  // active leaf
  private TreeMapNode activeLeaf = null;
  // color provider
  private ITreeMapColorProvider colorProvider = null;
  // treemap provider
  private ITreeMapProvider treeMapProvider = null;
  // displayed root
  private TreeMapNode displayedRoot = null;
  // root of the tree
  private TreeMapNode root = null;
  // divide strategy
  private SplitStrategy strategy = null;
  // zoom
  private Zoom zoom;
  //position of cursor
  private Point cursorPosition = new Point(0,0);

  /**
   * Constructor of JTreeMap. <BR>
   * The chosen strategy is SplitSquarified. <BR>
   * @param parent parent Composite
   * @param style style
   * @param root the root of the tree to display
   *
   * @see SplitSquarified
   */
  public KTreeMap(Composite parent, int style, TreeMapNode root) {
    this(parent, style, root, new SplitSquarified());
  }

  /**
   * Constructor of JTreeMap. <BR>
   * The chosen color provider is UniqueColorProvider.
   * @param parent parent Composite
   * @param style style
   * @param root the root of the tree to display
   * @param strategy the split strategy
   */
  public KTreeMap(Composite parent, int style, TreeMapNode root,
      SplitStrategy strategy) {
    super(parent, style);

    this.zoom = new Zoom();

    this.setRoot(root);
    this.setStrategy(strategy);

    this.addPaintListener(new PaintListener() {
      public void paintControl(PaintEvent e) {
        KTreeMap.this.paintControl(e);
      }
    });

    this.addMouseMoveListener(new HandleMouseMotion());
  }

  /**
   * calculate the postitions for the displayed root. <BR>
   * The positions of the root must be calculated first.
   */
  public void calculatePositions() {
    if (this.getStrategy() != null && this.displayedRoot != null) {
      this.getStrategy().calculatePositions(this.displayedRoot);
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.swt.widgets.Widget#dispose()
   */
  @Override
  public void dispose() {
    if (revealColor != null)
      revealColor.dispose();
    super.dispose();
  }

  /**
   * get the active leaf (the one under the mouse).
   *
   * @return Returns the activeLeaf.
   */
  public TreeMapNode getActiveLeaf() {
    return this.activeLeaf;
  }

  /**
   * @return the colorProvider
   */
  public ITreeMapColorProvider getColorProvider() {
    return colorProvider;
  }

  /**
   * @return the cursorPosition
   */
  public Point getCursorPosition() {
    return cursorPosition;
  }

  /**
   * get the displayed root.
   * <p>
   * This may be not the root of the jTreeMap. After a zoom, the displayed root
   * can be the root of an under-tree.
   * </p>
   *
   * @return the displayed root
   */
  public TreeMapNode getDisplayedRoot() {
    return this.displayedRoot;
  }

  /**
   * get the root.
   *
   * @return the root
   */
  public TreeMapNode getRoot() {
    return this.root;
  }

  /**
   * get the SplitStrategy.
   *
   * @return the SplitStrategy
   */
  public SplitStrategy getStrategy() {
    return this.strategy;
  }

  /**
   * get the IColorLabelProvider.
   *
   * @return the IColorLabelProvider
   */
  public ITreeMapProvider getTreeMapProvider() {
    return this.treeMapProvider;
  }

  /**
   * set the active leaf.
   *
   * @param newActiveLeaf the new active leaf
   */
  public void setActiveLeaf(TreeMapNode newActiveLeaf) {
    if (newActiveLeaf == null || newActiveLeaf.isLeaf()) {
      this.activeLeaf = newActiveLeaf;
    }
  }

  /**
   * @param colorProvider the colorProvider to set
   */
  public void setColorProvider(ITreeMapColorProvider colorProvider) {
    this.colorProvider = colorProvider;
    redraw();
  }

  /**
   * set the displayed root.
   * <p>
   * This may be not the root of the jTreeMap. After a zoom, the displayed root
   * can be the root of an under-tree.
   * </p>
   *
   * @param newDisplayedRoot new DiplayedRoot
   */
  public void setDisplayedRoot(TreeMapNode newDisplayedRoot) {
    this.displayedRoot = newDisplayedRoot;
    redraw();
  }

  /**
   * set the new root.
   *
   * @param newRoot the new root to set
   */
  public void setRoot(TreeMapNode newRoot) {
    this.root = newRoot;
    int insets = getBorderWidth();
    this.root.setX(insets);
    this.root.setY(insets);
    this.setDisplayedRoot(this.root);
  }

  /**
   * set the new strategy.
   *
   * @param newStrat the new strategy to set
   */
  public void setStrategy(SplitStrategy newStrat) {
    this.strategy = newStrat;
    redraw();
  }

  /**
   * set the ColorProvider.
   *
   * @param newColorProvider the new ColorPorvider
   */
  public void setTreeMapProvider(ITreeMapProvider newColorProvider) {
    this.treeMapProvider = newColorProvider;
    redraw();
  }

  /**
   * When you zoom the jTreeMap, you have the choice to keep proportions or not.
   *
   * @param keepProportion true if you want to keep proportions, else false
   */
  public void setZoomKeepProportion(boolean keepProportion) {
    this.zoom.setKeepProportion(keepProportion);
  }

  /**
   * Undo the zoom to display the root.
   */
  public void unzoom() {
    this.zoom.undo();
  }

  /**
   * Zoom the JTreeMap to the dest node.
   *
   * @param dest node we want to zoom
   */
  public void zoom(TreeMapNode dest) {
    // undo the last zoom
    unzoom();

    this.zoom.execute(dest);
  }

  void paintControl(PaintEvent e) {
    GC gc = e.gc;
    int width = getBounds().width;
    int height = getBounds().height;
    int insets = getBorderWidth();

    int border = TreeMapNode.getBorder();
    this.root.setBounds(new Rectangle(this.root.getX(), this.root.getY(), width
        - border - insets - insets, height - border - insets - insets));

    if (!this.root.equals(this.displayedRoot)) {
      this.displayedRoot.setBounds(new Rectangle(this.displayedRoot.getX(),
          this.displayedRoot.getY(), width - border - insets - insets, height
              - border - insets - insets));
    }

    this.calculatePositions();

    if (!this.displayedRoot.getChildren().isEmpty()) {
      // the background
      gc.setBackground(this.getBackground());
      gc.fillRectangle(this.displayedRoot.getX(), this.displayedRoot.getY(),
          this.displayedRoot.getWidth() + border, this.displayedRoot
              .getHeight()
              + border);
      // the JTreeMapExample
      draw(gc, this.displayedRoot);
      // reveal the active leaf
      if (this.activeLeaf != null) {
        reveal(gc, this.activeLeaf);
      }
      // the labels
      drawLabels(gc, this.displayedRoot);
    }
  }

  /**
   * draw the item.
   *
   * @param gc Graphics where you have to draw
   * @param item item to draw
   */
  protected void draw(GC gc, TreeMapNode item) {
    if (item.isLeaf()) {
      gc.setBackground(getColorProvider().getBackground(item.getValue()));
      gc.fillRectangle(item.getBounds());
    } else {
      for (TreeMapNode node : item.getChildren()) {
        draw(gc, node);
      }
    }
  }

  /**
   * write the label in the middle of the item. <BR>
   * You have first to define the font of the Graphics. <BR>
   * You may override this method to change the position or the color of the
   * label.
   *
   * @param gc Graphics where you have to draw
   * @param item TreeMapNode to draw
   */
  protected void drawLabel(GC gc, TreeMapNode item) {
    FontMetrics fm = gc.getFontMetrics();
    // if the height of the item is high enough
    if (fm.getHeight() < item.getHeight() - 2) {
      String label = getTreeMapProvider().getLabel(item);

      int y = (item.getHeight() - fm.getAscent() - fm.getLeading() + fm
          .getDescent()) / 2;
      int stringWidth = fm.getAverageCharWidth() * label.length();
      // the width of the label depends on the font :
      // if the width of the label is larger than the item
      if (item.getWidth() - 5 <= stringWidth) {
        // We have to truncate the label
        // number of chars who can be writen in the item
        int nbChar = (label.length() * item.getWidth()) / stringWidth;
        if (nbChar > 3) {
          // and add "..." at the end
          label = label.substring(0, nbChar - 3) + "...";
          stringWidth = (nbChar - 1) * fm.getAverageCharWidth();
        } else {
          // if it is not enough large, we display nothing
          return;
        }
      }
      int x = (item.getWidth() - stringWidth) / 2;

      // background in black
      gc.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));
      gc.drawString(label, item.getX() + x + 1, item.getY() + y + 1, true);
      gc.drawString(label, item.getX() + x - 1, item.getY() + y + 1, true);
      gc.drawString(label, item.getX() + x + 1, item.getY() + y - 1, true);
      gc.drawString(label, item.getX() + x - 1, item.getY() + y - 1, true);
      gc.drawString(label, item.getX() + x + 1, item.getY() + y, true);
      gc.drawString(label, item.getX() + x - 1, item.getY() + y, true);
      gc.drawString(label, item.getX() + x, item.getY() + y + 1, true);
      gc.drawString(label, item.getX() + x, item.getY() + y - 1, true);
      // label in foreground color
      gc.setForeground(getColorProvider().getForeground(item));
      gc.drawString(label, item.getX() + x, item.getY() + y, true);
    }
  }

  /**
   * Draw all the labels to draw. <BR>
   * You may override this method to draw the labels you want. <BR>
   * For exemples, all the leaves, or all the first level children, or all of
   * them...
   *
   * @param gc Graphics where you have to draw
   * @param item TreeMapNode to draw
   */
  protected void drawLabels(GC gc, TreeMapNode item) {
    // add the labels (level -1)
    gc.setFont(this.getFont());
    if (this.displayedRoot.isLeaf()) {
      drawLabel(gc, this.displayedRoot);
    } else {
      for (TreeMapNode node : this.displayedRoot.getChildren()) {
        drawLabel(gc, node);
      }
    }
  }

  /**
   * reveal the item.
   *
   * @param gc Graphics where you have to draw
   * @param item TreeMapNode to reveal
   */
  protected void reveal(GC gc, TreeMapNode item) {
    if (item.isLeaf()) {
      if (revealColor != null)
        revealColor.dispose();

      Color itemColor = this.colorProvider.getBackground(item.getValue());
      RGB rgb = itemColor.getRGB();
      float[] fs = java.awt.Color.RGBtoHSB(rgb.red, rgb.green, rgb.blue, null);
      java.awt.Color cc = new java.awt.Color(java.awt.Color.HSBtoRGB(fs[0],
          fs[1] / 2, (fs[2] + 1) / 2));
      revealColor = new Color(getDisplay(), cc.getRed(), cc.getGreen(), cc
          .getBlue());

      gc.setBackground(revealColor);
      gc.fillRectangle(item.getX(), item.getY(), item.getWidth(), item
          .getHeight());
    }
  }

  /**
   * Listener who define the active leaf and set the tooltip text.
   *
   * @author Laurent Dutheil
   */
  protected class HandleMouseMotion implements MouseMoveListener {
    public void mouseMove(MouseEvent e) {
      cursorPosition.x = e.x;
      cursorPosition.y = e.y;
      if (!getDisplayedRoot().getChildren().isEmpty()) {
        TreeMapNode t = getDisplayedRoot().getActiveLeaf(e.x, e.y);
        TreeMapNode oldActiveLeaf = getActiveLeaf();
        if (oldActiveLeaf != null && !oldActiveLeaf.equals(t)) {
          Rectangle bounds = oldActiveLeaf.getBounds();
          redraw(bounds.x, bounds.y, bounds.width, bounds.height, false);
        }
        setActiveLeaf(t);
        if (t != null && !t.equals(oldActiveLeaf)) {
          Rectangle bounds = t.getBounds();
          redraw(bounds.x, bounds.y, bounds.width, bounds.height, false);

        }
        if (t != null) {
          String label = KTreeMap.this.getTreeMapProvider().getLabel(t);
          String valueLabel = KTreeMap.this.getTreeMapProvider().getValueLabel(
              t.getValue());
          setToolTipText(label + "\n" + valueLabel);
        } else {
          setToolTipText(null);
        }
      }
    }
  }

  /**
   * Class who zoom and unzoom the JTreeMap.
   *
   * @author Laurent Dutheil
   */
  private class Zoom {
    private boolean enable;
    private boolean keepProportion = false;

    /**
     * Constructor
     */
    public Zoom() {
      this.enable = true;
    }

    /**
     * Execute the zoom.
     *
     * @param dest TreeMapNode where you want to zoom
     */
    public void execute(TreeMapNode dest) {
      if (this.enable) {
        KTreeMap.this.setActiveLeaf(null);

        setNewDimension(dest);

        KTreeMap.this.setDisplayedRoot(dest);

        this.enable = false;
      }
    }

    /**
     * @return Returns the keepProportion.
     */
    public boolean isKeepProportion() {
      return this.keepProportion;
    }

    /**
     * @param keepProportion The keepProportion to set.
     */
    public void setKeepProportion(boolean keepProportion) {
      this.keepProportion = keepProportion;
    }

    /**
     * undo the zoom.
     */
    public void undo() {
      if (!this.enable) {
        KTreeMap.this.setDisplayedRoot(KTreeMap.this.getRoot());
        this.enable = true;
      }
    }

    /**
     * set the new dimensions of the dest root
     *
     * @param dest the root to dimension
     */
    protected void setNewDimension(TreeMapNode dest) {
      dest.setX(KTreeMap.this.getRoot().getX());
      dest.setY(KTreeMap.this.getRoot().getY());

      int rootWidth = KTreeMap.this.getRoot().getWidth();
      int rootHeight = KTreeMap.this.getRoot().getHeight();

      if (isKeepProportion()) {
        int destHeight = dest.getHeight();
        int destWidth = dest.getWidth();
        float divWidth = (float)destWidth / (float)rootWidth;
        float divHeight = (float)destHeight / (float)rootHeight;

        if (divWidth >= divHeight) {
          dest.setHeight(Math.round(destHeight / divWidth));
          dest.setWidth(rootWidth);
        } else {
          dest.setHeight(rootHeight);
          dest.setWidth(Math.round(destWidth / divHeight));
        }

      } else {
        dest.setHeight(rootHeight);
        dest.setWidth(rootWidth);
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
