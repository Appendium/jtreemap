package org.jense.swing.jtreemap;

import java.util.Enumeration;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JToolTip;
import javax.swing.ToolTipManager;
import javax.swing.border.Border;

/**
 * JComponent who represents each element of a tree in a rectangle of more or
 * less big size according to its importance in the tree.
 * <p>
 * A tree structure may includes more or less important elements. For example,
 * in a tree structure of files, there can be files of big size. Then it can be
 * interesting to know which repertory is the most important on a hard disk.
 * <p>
 * Moreover, we can add a code color which makes it possible to introduce new
 * information into the representation of the tree structure.
 * <p>
 * So, in a JTreeMap, you can see the size and the value of an element in a
 * tree.
 * 
 * @see org.jense.swing.jtreemap.TreeMapNode
 * @author Laurent Dutheil
 */
public class JTreeMap extends JComponent {
  private static final long serialVersionUID = 7255952672238300249L;
  private static final Color transparencyColor = new Color(204, 204, 204, 128);
  // active leaf
  private TreeMapNode activeLeaf = null;
  // color provider
  private ColorProvider colorProvider = null;
  // displayed root
  private TreeMapNode displayedRoot = null;
  // root of the tree
  private TreeMapNode root = null;
  // divide strategy
  private SplitStrategy strategy = null;
  // tooltip builder
  private IToolTipBuilder toolTipBuilder;
  // zoom
  private Zoom zoom;

  /**
   * Constructor of JTreeMap. <BR>
   * The chosen strategy is SplitSquarified. <BR>
   * The chosen color provider is UniqueColorProvider.
   * 
   * @see SplitSquarified
   * @see UniqueColorProvider
   * @param root the root of the tree to display
   */
  public JTreeMap(TreeMapNode root) {
    this(root, new SplitSquarified());
  }

  /**
   * Constructor of JTreeMap. <BR>
   * The chosen color provider is UniqueColorProvider.
   * 
   * @see UniqueColorProvider
   * @param root the root of the tree to display
   * @param strategy the split strategy
   */
  public JTreeMap(TreeMapNode root, SplitStrategy strategy) {
    // ToolTips appears without delay and stay as long as possible
    ToolTipManager ttm = ToolTipManager.sharedInstance();
    ttm.setInitialDelay(0);
    ttm.setReshowDelay(0);
    ttm.setDismissDelay(100000);
    ttm.setEnabled(true);
    ttm.setLightWeightPopupEnabled(true);
    this.setToolTipText("");

    // the default DefaultToolTipBuilder
    this.toolTipBuilder = new DefaultToolTipBuilder(this);

    this.zoom = new Zoom();

    this.setRoot(root);
    this.setStrategy(strategy);
    this.setColorProvider(new UniqueColorProvider());

    this.addMouseMotionListener(new HandleMouseMotion());
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

  @Override
  public JToolTip createToolTip() {
    return this.toolTipBuilder.getToolTip();
  }

  /**
   * draw the item.
   * 
   * @param g Graphics where you have to draw
   * @param item item to draw
   */
  protected void draw(Graphics g, TreeMapNode item) {
    if (item.isLeaf()) {
      g.setColor(this.colorProvider.getColor(item.getValue()));
      g.fillRect(item.getX(), item.getY(), item.getWidth(), item.getHeight());
    } else {
      for (Enumeration e = item.children(); e.hasMoreElements();) {
        draw(g, (TreeMapNode) (e.nextElement()));
      }
    }
  }

  /**
   * write the label in the middle of the item. <BR>
   * You have first to define the font of the Graphics. <BR>
   * You may override this method to change the position or the color of the
   * label.
   * 
   * @param g Graphics where you have to draw
   * @param item TreeMapNode to draw
   */
  protected void drawLabel(Graphics g, TreeMapNode item) {
    FontMetrics fm = g.getFontMetrics(g.getFont());
    // if the height of the item is high enough
    if (fm.getHeight() < item.getHeight() - 2) {
      String label = item.getLabel();

      int y = (item.getHeight() + fm.getAscent() - fm.getDescent()) / 2;
      int stringWidth = fm.stringWidth(label);
      // the width of the label depends on the font :
      // if the width of the label is larger than the item
      if (item.getWidth() - 5 <= stringWidth) {
        // We have to truncate the label
        // number of chars who can be writen in the item
        int nbChar = (label.length() * item.getWidth()) / stringWidth;
        if (nbChar > 3) {
          // and add "..." at the end
          label = label.substring(0, nbChar - 3) + "...";
        } else {
          // if it is not enough large, we display nothing
          label = "";
        }
      }
      int x = (item.getWidth() - fm.stringWidth(label)) / 2;

      // background in black
      g.setColor(Color.black);
      g.drawString(label, item.getX() + x + 1, item.getY() + y + 1);
      g.drawString(label, item.getX() + x - 1, item.getY() + y + 1);
      g.drawString(label, item.getX() + x + 1, item.getY() + y - 1);
      g.drawString(label, item.getX() + x - 1, item.getY() + y - 1);
      g.drawString(label, item.getX() + x + 1, item.getY() + y);
      g.drawString(label, item.getX() + x - 1, item.getY() + y);
      g.drawString(label, item.getX() + x, item.getY() + y + 1);
      g.drawString(label, item.getX() + x, item.getY() + y - 1);
      // label in white
      g.setColor(Color.white);
      g.drawString(label, item.getX() + x, item.getY() + y);
    }
  }

  /**
   * Draw all the labels to draw. <BR>
   * You may override this method to draw the labels you want. <BR>
   * For exemples, all the leaves, or all the first level children, or all of
   * them...
   * 
   * @param g Graphics where you have to draw
   * @param item TreeMapNode to draw
   */
  protected void drawLabels(Graphics g, TreeMapNode item) {
    // add the labels (level -1)
    g.setFont(this.getFont());
    if (this.displayedRoot.isLeaf()) {
      drawLabel(g, this.displayedRoot);
    } else {
      for (Enumeration e = this.displayedRoot.children(); e.hasMoreElements();) {
        drawLabel(g, (TreeMapNode) (e.nextElement()));
      }
    }

    /* uncomment to add the labels of the lowered levels (up to depth > 2) */
    // int depth = item.getLevel() - this.displayedRoot.getLevel();
    // float newSize = Math.max(20, this.getFont().getSize2D());
    // java.awt.Font labelFont = this.getFont().deriveFont(java.awt.Font.BOLD,
    // newSize - 3 * depth);
    // g.setFont(labelFont);
    // if (depth > 2) {
    // drawLabel(g, item);
    // return;
    // }
    // if (item.isLeaf()) {
    // drawLabel(g, item);
    // } else {
    // for (Enumeration e = item.children(); e.hasMoreElements();) {
    // drawLabels(g, (TreeMapNode) (e.nextElement()));
    // }
    // }
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
   * get the ColorProvider.
   * 
   * @return the ColorProvider
   */
  public ColorProvider getColorProvider() {
    return this.colorProvider;
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

  @Override
  public Point getToolTipLocation(MouseEvent event) {
    int posX;
    int posY;
    JToolTip toolTip = this.createToolTip();
    int XMax = this.displayedRoot.getX() + this.displayedRoot.getWidth();
    int YMin = this.displayedRoot.getY();
    if (this.activeLeaf != null) {
      if (this.activeLeaf.getWidth() >= toolTip.getWidth() + 8
          && this.activeLeaf.getHeight() >= toolTip.getHeight() + 8) {
        posX = this.activeLeaf.getX() + 4;
        posY = this.activeLeaf.getY() + 4;
      } else {
        posX = this.activeLeaf.getX() + this.activeLeaf.getWidth() + 4;
        posY = this.activeLeaf.getY() - toolTip.getHeight() - 4;
      }

      if (posY < YMin + 4) {
        posY = YMin + 4;
      }
      if ((posX + toolTip.getWidth() > XMax - 4)
          && (this.activeLeaf.getX() >= toolTip.getWidth() + 4)) {
        posX = this.activeLeaf.getX() - 4 - toolTip.getWidth();
      }

      return new Point(posX, posY);
    }
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
   */
  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    int width = getSize().width;
    int height = getSize().height;
    Insets insets = getInsets();

    int border = TreeMapNode.getBorder();
    this.root.setDimension(this.root.getX(), this.root.getY(), width - border
        - insets.left - insets.right, height - border - insets.top
        - insets.bottom);

    if (!this.root.equals( this.displayedRoot )) {
      this.displayedRoot.setDimension(this.displayedRoot.getX(), this.displayedRoot.getY(), width - border
          - insets.left - insets.right, height - border - insets.top
          - insets.bottom);
    }
    
    this.calculatePositions();

    if (this.displayedRoot.children().hasMoreElements()) {
      // the background
      g.setColor(this.getBackground());
      g.fillRect(this.displayedRoot.getX(), this.displayedRoot.getY(),
          this.displayedRoot.getWidth() + border, this.displayedRoot
              .getHeight()
              + border);
      // the JTreeMapExample
      draw(g, this.displayedRoot);
      // reveal the active leaf
      if (this.activeLeaf != null) {
        reveal(g, this.activeLeaf);
      }
      // the labels
      drawLabels(g, this.displayedRoot);
    }

  }

  /**
   * reveal the item.
   * 
   * @param g Graphics where you have to draw
   * @param item TreeMapNode to reveal
   */
  protected void reveal(Graphics g, TreeMapNode item) {
    if (item.isLeaf()) {
      g.setColor(transparencyColor);
      g.fillRect(item.getX(), item.getY(), item.getWidth(), item.getHeight());
    }
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

  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.JComponent#setBorder(javax.swing.border.Border)
   */
  @Override
  public void setBorder(Border border) {
    // Substract the previous border insets
    Insets insets = getInsets();
    this.displayedRoot.setDimension(this.displayedRoot.getX() - insets.left,
        this.displayedRoot.getY() - insets.top, this.displayedRoot.getWidth()
            + insets.left + insets.right, this.displayedRoot.getHeight()
            + insets.top + insets.bottom);

    super.setBorder(border);

    // Add the new border insets
    insets = getInsets();
    this.displayedRoot.setDimension(this.displayedRoot.getX() + insets.left,
        this.displayedRoot.getY() + insets.top, this.displayedRoot.getWidth()
            - insets.left - insets.right, this.displayedRoot.getHeight()
            - insets.top - insets.bottom);
  }

  /**
   * set the ColorProvider.
   * 
   * @param newColorProvider the new ColorPorvider
   */
  public void setColorProvider(ColorProvider newColorProvider) {
    this.colorProvider = newColorProvider;
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
  }

  /**
   * set the new root.
   * 
   * @param newRoot the new root to set
   */
  public void setRoot(TreeMapNode newRoot) {
    this.root = newRoot;
    Insets insets = getInsets();
    this.root.setX(insets.left);
    this.root.setY(insets.top);
    this.setDisplayedRoot(this.root);

  }

  /**
   * set the new strategy.
   * 
   * @param newStrat the new strategy to set
   */
  public void setStrategy(SplitStrategy newStrat) {
    this.strategy = newStrat;
  }

  /**
   * Set the builder of the toolTip.<BR>
   * 
   * @param toolTipBuilder The toolTipBuilder to set.
   */
  public void setToolTipBuilder(IToolTipBuilder toolTipBuilder) {
    this.toolTipBuilder = toolTipBuilder;
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

  /**
   * Listener who define the active leaf and set the tooltip text.
   * 
   * @author Laurent Dutheil
   */
  protected class HandleMouseMotion extends MouseMotionAdapter {

    @Override
    public void mouseMoved(MouseEvent e) {
      if (getDisplayedRoot().children().hasMoreElements()) {
        TreeMapNode t = getDisplayedRoot().getActiveLeaf(e.getX(), e.getY());
        if (t != null && !t.equals(getActiveLeaf())) {
          setActiveLeaf(t);
          repaint();
        }
        if (t != null) {
          setToolTipText(t.getLabel() + " " + t.getValue().getValue());
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
     * @param dest TreeMapNode where you want to zoom
     */
    public void execute(TreeMapNode dest) {
      if (this.enable) {
        JTreeMap.this.setActiveLeaf(null);

        setNewDimension(dest);

        JTreeMap.this.setDisplayedRoot(dest);

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
     * set the new dimensions of the dest root
     * @param dest the root to dimension
     */
    protected void setNewDimension(TreeMapNode dest) {
      dest.setX(JTreeMap.this.getRoot().getX());
      dest.setY(JTreeMap.this.getRoot().getY());

      int rootWidth = JTreeMap.this.getRoot().getWidth();
      int rootHeight = JTreeMap.this.getRoot().getHeight();

      if (isKeepProportion()) {
        int destHeight = dest.getHeight();
        int destWidth = dest.getWidth();
        float divWidth = (float) destWidth / (float) rootWidth;
        float divHeight = (float) destHeight / (float) rootHeight;

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

    /**
     * undo the zoom.
     */
    public void undo() {
      if (!this.enable) {
        JTreeMap.this.setDisplayedRoot(JTreeMap.this.getRoot());
        this.enable = true;
      }
    }

  }

}
