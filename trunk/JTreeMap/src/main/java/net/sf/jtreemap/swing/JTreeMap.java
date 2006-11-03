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
package net.sf.jtreemap.swing;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.Serializable;
import java.util.Enumeration;

import javax.swing.JComponent;
import javax.swing.JToolTip;
import javax.swing.JTree;
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
 * @see net.sf.jtreemap.swing.TreeMapNode
 * @author Laurent Dutheil
 */
public class JTreeMap extends JComponent {
    private static final int BORDER_FOR_FONT = 5;

    private static final int MAX_NUM_CHAR = 3;

    private static final int INSET = 4;

    private static final int DISMISS_DELAY_MS = 100000;

    private static final long serialVersionUID = 7255952672238300249L;

    private static final Color TRANSPARENCY_COLOR = new Color(204, 204, 204, 128);

    /**
     * The optional tree representation of the hierarchical data.
     */
    private JTree treeView;
    
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
     * @param root
     *            the root of the tree to display
     */
    public JTreeMap(final TreeMapNode root) {
        this(root, new SplitSquarified());
    }
    
    /**
     * Constructor of JTreeMap. <BR>
     * The chosen strategy is SplitSquarified. <BR>
     * The chosen color provider is UniqueColorProvider.
     * 
     * @see SplitSquarified
     * @see UniqueColorProvider
     * @param root the root of the tree to display
     * @param treeView The tree representation of the hierarchical data. 
     */
    public JTreeMap(final TreeMapNode root, JTree treeView) {
    	this(root, new SplitSquarified());
    	this.treeView = treeView;
    }

    /**
     * Constructor of JTreeMap. <BR>
     * The chosen color provider is UniqueColorProvider.
     * 
     * @see UniqueColorProvider
     * @param root
     *            the root of the tree to display
     * @param strategy
     *            the split strategy
     */
    public JTreeMap(final TreeMapNode root, final SplitStrategy strategy) {
        // ToolTips appears without delay and stay as long as possible
        final ToolTipManager ttm = ToolTipManager.sharedInstance();
        ttm.setInitialDelay(0);
        ttm.setReshowDelay(0);
        ttm.setDismissDelay(DISMISS_DELAY_MS);
        ttm.setEnabled(true);
        ttm.setLightWeightPopupEnabled(true);
        setToolTipText("");

        // the default DefaultToolTipBuilder
        toolTipBuilder = new DefaultToolTipBuilder(this);

        zoom = new Zoom();

        setRoot(root);
        setStrategy(strategy);
        setColorProvider(new UniqueColorProvider());

        addMouseMotionListener(new HandleMouseMotion());
        addMouseListener(new HandleMouseClick());
    }

    /**
     * calculate the postitions for the displayed root. <BR>
     * The positions of the root must be calculated first.
     */
    public void calculatePositions() {
        if (this.getStrategy() != null && displayedRoot != null) {
            getStrategy().calculatePositions(this.displayedRoot);
        }
    }

    @Override
    public JToolTip createToolTip() {
        return toolTipBuilder.getToolTip();
    }

    /**
     * draw the item.
     * 
     * @param g
     *            Graphics where you have to draw
     * @param item
     *            item to draw
     */
    protected void draw(final Graphics g, final TreeMapNode item) {
        if (item.isLeaf()) {
            g.setColor(this.colorProvider.getColor(item.getValue()));
            g.fillRect(item.getX(), item.getY(), item.getWidth(), item.getHeight());
        } else {
            for (final Enumeration e = item.children(); e.hasMoreElements();) {
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
     * @param g
     *            Graphics where you have to draw
     * @param item
     *            TreeMapNode to draw
     */
    protected void drawLabel(final Graphics g, final TreeMapNode item) {
        final FontMetrics fm = g.getFontMetrics(g.getFont());
        // if the height of the item is high enough
        if (fm.getHeight() < item.getHeight() - 2) {
            String label = item.getLabel();

            final int y = (item.getHeight() + fm.getAscent() - fm.getDescent()) / 2;
            final int stringWidth = fm.stringWidth(label);
            // the width of the label depends on the font :
            // if the width of the label is larger than the item
            if (item.getWidth() - BORDER_FOR_FONT <= stringWidth) {
                // We have to truncate the label
                // number of chars who can be writen in the item
                final int nbChar = (label.length() * item.getWidth()) / stringWidth;
                if (nbChar > MAX_NUM_CHAR) {
                    // and add "..." at the end
                    label = label.substring(0, nbChar - MAX_NUM_CHAR) + "...";
                } else {
                    // if it is not enough large, we display nothing
                    label = "";
                }
            }
            final int x = (item.getWidth() - fm.stringWidth(label)) / 2;

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
     * @param g
     *            Graphics where you have to draw
     * @param item
     *            TreeMapNode to draw
     */
    protected void drawLabels(final Graphics g, final TreeMapNode item) {
        // add the labels (level -1)
        g.setFont(this.getFont());
        if (this.displayedRoot.isLeaf()) {
            drawLabel(g, displayedRoot);
        } else {
            for (final Enumeration e = displayedRoot.children(); e.hasMoreElements();) {
                drawLabel(g, (TreeMapNode) (e.nextElement()));
            }
        }

        /* uncomment to add the labels of the lowered levels (up to depth > 2) */
        // int depth = item.getLevel() - displayedRoot.getLevel();
        // float newSize = Math.max(20, getFont().getSize2D());
        // java.awt.Font labelFont =
        // getFont().deriveFont(java.awt.Font.BOLD,
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
        return activeLeaf;
    }

    /**
     * get the ColorProvider.
     * 
     * @return the ColorProvider
     */
    public ColorProvider getColorProvider() {
        return colorProvider;
    }

    /**
     * get the displayed root.
     * <p>
     * This may be not the root of the jTreeMap. After a zoom, the displayed
     * root can be the root of an under-tree.
     * </p>
     * 
     * @return the displayed root
     */
    public TreeMapNode getDisplayedRoot() {
        return displayedRoot;
    }

    /**
     * get the root.
     * 
     * @return the root
     */
    public TreeMapNode getRoot() {
        return root;
    }

    /**
     * get the SplitStrategy.
     * 
     * @return the SplitStrategy
     */
    public SplitStrategy getStrategy() {
        return strategy;
    }

    @Override
    public Point getToolTipLocation(final MouseEvent event) {
        int posX;
        int posY;
        final JToolTip toolTip = createToolTip();
        final int xMax = displayedRoot.getX() + displayedRoot.getWidth();
        final int yMin = displayedRoot.getY();
        if (this.activeLeaf != null) {
            if (this.activeLeaf.getWidth() >= toolTip.getWidth() + 2 * INSET
                    && activeLeaf.getHeight() >= toolTip.getHeight() + 2 * INSET) {
                posX = activeLeaf.getX() + INSET;
                posY = activeLeaf.getY() + INSET;
            } else {
                posX = activeLeaf.getX() + activeLeaf.getWidth() + INSET;
                posY = activeLeaf.getY() - toolTip.getHeight() - INSET;
            }

            if (posY < yMin + INSET) {
                posY = yMin + INSET;
            }
            if ((posX + toolTip.getWidth() > xMax - INSET) && (this.activeLeaf.getX() >= toolTip.getWidth() + INSET)) {
                posX = activeLeaf.getX() - INSET - toolTip.getWidth();
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
    protected void paintComponent(final Graphics g) {
        super.paintComponent(g);
        final int width = getSize().width;
        final int height = getSize().height;
        final Insets insets = getInsets();

        final int border = TreeMapNode.getBorder();
        root.setDimension(this.root.getX(), root.getY(), width - border - insets.left - insets.right, height - border
                - insets.top - insets.bottom);

        if (!this.root.equals(this.displayedRoot)) {
            displayedRoot.setDimension(this.displayedRoot.getX(), displayedRoot.getY(), width - border - insets.left
                    - insets.right, height - border - insets.top - insets.bottom);
        }

        calculatePositions();

        if (this.displayedRoot.children().hasMoreElements()) {
            // the background
            g.setColor(this.getBackground());
            g.fillRect(this.displayedRoot.getX(), displayedRoot.getY(), displayedRoot.getWidth() + border, displayedRoot
                    .getHeight()
                    + border);
            // the JTreeMapExample
            draw(g, displayedRoot);
            // reveal the active leaf
            if (this.activeLeaf != null) {
                reveal(g, activeLeaf);
            }
            // the labels
            drawLabels(g, displayedRoot);
        }

    }

    /**
     * reveal the item.
     * 
     * @param g
     *            Graphics where you have to draw
     * @param item
     *            TreeMapNode to reveal
     */
    protected void reveal(final Graphics g, final TreeMapNode item) {
        if (item.isLeaf()) {
            g.setColor(TRANSPARENCY_COLOR);
            g.fillRect(item.getX(), item.getY(), item.getWidth(), item.getHeight());
        }
    }

    /**
     * set the active leaf.
     * 
     * @param newActiveLeaf
     *            the new active leaf
     */
    public void setActiveLeaf(final TreeMapNode newActiveLeaf) {
        if (newActiveLeaf == null || newActiveLeaf.isLeaf()) {
            activeLeaf = newActiveLeaf;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.JComponent#setBorder(javax.swing.border.Border)
     */
    @Override
    public void setBorder(final Border border) {
        // Substract the previous border insets
        Insets insets = getInsets();
        displayedRoot.setDimension(this.displayedRoot.getX() - insets.left, displayedRoot.getY() - insets.top, displayedRoot
                .getWidth()
                + insets.left + insets.right, displayedRoot.getHeight() + insets.top + insets.bottom);

        super.setBorder(border);

        // Add the new border insets
        insets = getInsets();
        displayedRoot.setDimension(this.displayedRoot.getX() + insets.left, displayedRoot.getY() + insets.top, displayedRoot
                .getWidth()
                - insets.left - insets.right, displayedRoot.getHeight() - insets.top - insets.bottom);
    }

    /**
     * set the ColorProvider.
     * 
     * @param newColorProvider
     *            the new ColorPorvider
     */
    public void setColorProvider(final ColorProvider newColorProvider) {
        colorProvider = newColorProvider;
    }

    /**
     * set the displayed root.
     * <p>
     * This may be not the root of the jTreeMap. After a zoom, the displayed
     * root can be the root of an under-tree.
     * </p>
     * 
     * @param newDisplayedRoot
     *            new DiplayedRoot
     */
    public void setDisplayedRoot(final TreeMapNode newDisplayedRoot) {
        displayedRoot = newDisplayedRoot;
    }

    /**
     * set the new root.
     * 
     * @param newRoot
     *            the new root to set
     */
    public void setRoot(final TreeMapNode newRoot) {
        root = newRoot;
        final Insets insets = getInsets();
        root.setX(insets.left);
        root.setY(insets.top);
        setDisplayedRoot(this.root);

    }

    /**
     * set the new strategy.
     * 
     * @param newStrat
     *            the new strategy to set
     */
    public void setStrategy(final SplitStrategy newStrat) {
        strategy = newStrat;
    }

    /**
     * Set the builder of the toolTip.<BR>
     * 
     * @param toolTipBuilder
     *            The toolTipBuilder to set.
     */
    public void setToolTipBuilder(final IToolTipBuilder toolTipBuilder) {
        this.toolTipBuilder = toolTipBuilder;
    }

    /**
     * When you zoom the jTreeMap, you have the choice to keep proportions or
     * not.
     * 
     * @param keepProportion
     *            true if you want to keep proportions, else false
     */
    public void setZoomKeepProportion(final boolean keepProportion) {
        zoom.setKeepProportion(keepProportion);
    }

    /**
     * Undo the zoom to display the root.
     */
    public void unzoom() {
        zoom.undo();
    }

    /**
     * Zoom the JTreeMap to the dest node.
     * 
     * @param dest
     *            node we want to zoom
     */
    public void zoom(final TreeMapNode dest) {
        // undo the last zoom
        unzoom();

        zoom.execute(dest);
    }

    /**
     * Listener who define the active leaf and set the tooltip text.
     * 
     * @author Laurent Dutheil
     */
    protected class HandleMouseMotion extends MouseMotionAdapter {

        @Override
        public void mouseMoved(final MouseEvent e) {
            if (getDisplayedRoot().children().hasMoreElements()) {
                final TreeMapNode t = getDisplayedRoot().getActiveLeaf(e.getX(), e.getY());
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
     * Listener which listens for double click to navigate one level down.
     * 
     * @author Ekin Gulen
     */
    protected class HandleMouseClick extends MouseAdapter {
    	
    	@Override
		public void mouseClicked(MouseEvent e) {
    		if (e.getClickCount() >= 2) {
    			final TreeMapNode t = getDisplayedRoot().getChild(e.getX(), e.getY());
    			if ( t != null && !t.isLeaf()) {
//    				treeView.setSelectionPath(new TreePath(t.getPath()));
    				zoom(t);
    			} else {
//    				treeView.setSelectionPath(new TreePath(((TreeMapNode)getDisplayedRoot().getParent()).getPath()));
    				zoom((TreeMapNode)getDisplayedRoot().getParent());
    			}
				repaint();
    		}
		}
    }
    
    /**
     * Class who zoom and unzoom the JTreeMap.
     * 
     * @author Laurent Dutheil
     */
    private class Zoom implements Serializable {
        /**
         * 
         */
        private static final long serialVersionUID = 6708828099608367996L;

        private boolean enable;

        private boolean keepProportion = false;

        /**
         * Constructor
         */
        public Zoom() {
            enable = true;
        }

        /**
         * Execute the zoom.
         * 
         * @param dest
         *            TreeMapNode where you want to zoom
         */
        public void execute(final TreeMapNode dest) {
            if (this.enable) {
                JTreeMap.this.setActiveLeaf(null);

                setNewDimension(dest);

                JTreeMap.this.setDisplayedRoot(dest);

                enable = false;
            }
        }

        /**
         * @return Returns the keepProportion.
         */
        public boolean isKeepProportion() {
            return keepProportion;
        }

        /**
         * @param keepProportion
         *            The keepProportion to set.
         */
        public void setKeepProportion(final boolean keepProportion) {
            this.keepProportion = keepProportion;
        }

        /**
         * set the new dimensions of the dest root
         * 
         * @param dest
         *            the root to dimension
         */
        protected void setNewDimension(final TreeMapNode dest) {
            dest.setX(JTreeMap.this.getRoot().getX());
            dest.setY(JTreeMap.this.getRoot().getY());

            final int rootWidth = JTreeMap.this.getRoot().getWidth();
            final int rootHeight = JTreeMap.this.getRoot().getHeight();

            if (isKeepProportion()) {
                final int destHeight = dest.getHeight();
                final int destWidth = dest.getWidth();
                final float divWidth = (float) destWidth / (float) rootWidth;
                final float divHeight = (float) destHeight / (float) rootHeight;

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
                enable = true;
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
