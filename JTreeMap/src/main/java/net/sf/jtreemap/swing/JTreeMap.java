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
import javax.swing.tree.TreePath;

import net.sf.jtreemap.swing.provider.ColorProvider;
import net.sf.jtreemap.swing.provider.UniqueColorProvider;

/**
 * A JComponent that represents each element of a tree in a rectangle of a size
 * proportional to its importance in the tree.
 * <p>
 * A tree structure may include elements of varying importance. For example, in
 * a file system tree, some files may be very large. It can be useful to

 * visualize which directories consume the most disk space.
 * </p>
 * <p>
 * Additionally, a color code can be used to introduce new information into the
 * representation of the tree structure.
 * </p>
 * <p>
 * Thus, in a JTreeMap, you can see the size and value of an element in a tree.
 * </p>
 *
 * @see net.sf.jtreemap.swing.TreeMapNode
 * @author Laurent Dutheil
 */
public class JTreeMap extends JComponent {
    private static final long serialVersionUID = 7255952672238300249L;
    private static final int BORDER_FOR_FONT = 5;
    private static final int MAX_NUM_CHAR = 3;
    private static final int INSET = 4;
    private static final int DISMISS_DELAY_MS = 100000;
    private static final Color TRANSPARENCY_COLOR = new Color(204, 204, 204, 128);

    /**
     * The optional tree representation of the hierarchical data.
     */
    private JTree treeView;

    private TreeMapNode activeLeaf;

    private ColorProvider colorProvider;

    private TreeMapNode displayedRoot;

    private TreeMapNode root;

    private SplitStrategy strategy;

    private IToolTipBuilder toolTipBuilder;

    // zoom
    private final Zoom zoom = new Zoom();

    /**
     * Constructs a JTreeMap with the specified root node.
     * <p>
     * The default strategy is {@link SplitSquarified}, and the default color
     * provider is {@link UniqueColorProvider}.
     * </p>
     *
     * @param root the root of the tree to display
     * @see SplitSquarified
     * @see UniqueColorProvider
     */
    public JTreeMap(final TreeMapNode root) {
        this(root, new SplitSquarified(), null, null, false);
    }

    /**
     * Constructs a JTreeMap with the specified root node and a JTree view.
     * <p>
     * The default strategy is {@link SplitSquarified}, and the default color
     * provider is {@link UniqueColorProvider}.
     * </p>
     *
     * @param root     the root of the tree to display
     * @param treeView the JTree representation of the hierarchical data
     * @see SplitSquarified
     * @see UniqueColorProvider
     */
    public JTreeMap(final TreeMapNode root, final JTree treeView) {
        this(root, new SplitSquarified(), null, null, false);
        this.treeView = treeView;
    }

    /**
     * Constructs a JTreeMap with the specified root node, split strategy, and JTree
     * view.
     * <p>
     * The default color provider is {@link UniqueColorProvider}.
     * </p>
     *
     * @param root         the root of the tree to display
     * @param strategy     the split strategy
     * @param treeView     the JTree representation of the hierarchical data
     * @param weightPrefix the prefix for the weight in the tooltip
     * @param valuePrefix  the prefix for the value in the tooltip
     * @param showWeight   if true, the weight is shown in the tooltip
     * @see UniqueColorProvider
     */
    public JTreeMap(final TreeMapNode root, final SplitStrategy strategy, final JTree treeView,
            final String weightPrefix, final String valuePrefix, final boolean showWeight) {
        this(root, strategy, weightPrefix, valuePrefix, showWeight);
        this.treeView = treeView;
    }

    /**
     * Constructs a JTreeMap with the specified root node and split strategy.
     * <p>
     * The default color provider is {@link UniqueColorProvider}.
     * </p>
     *
     * @param root         the root of the tree to display
     * @param strategy     the split strategy
     * @param weightPrefix the prefix for the weight in the tooltip
     * @param valuePrefix  the prefix for the value in the tooltip
     * @param showWeight   if true, the weight is shown in the tooltip
     * @see UniqueColorProvider
     */
    public JTreeMap(final TreeMapNode root, final SplitStrategy strategy, final String weightPrefix,
            final String valuePrefix, final boolean showWeight) {
        final ToolTipManager ttm = ToolTipManager.sharedInstance();
        ttm.setInitialDelay(0);
        ttm.setReshowDelay(0);
        ttm.setDismissDelay(DISMISS_DELAY_MS);
        ttm.setEnabled(true);
        ttm.setLightWeightPopupEnabled(true);
        setToolTipText("");

        this.toolTipBuilder = new DefaultToolTipBuilder(this, weightPrefix, valuePrefix, showWeight);

        setRoot(root);
        setStrategy(strategy);
        setColorProvider(new UniqueColorProvider());

        addMouseMotionListener(new HandleMouseMotion());
        addMouseListener(new HandleMouseClick());
    }

    /**
     * Calculates the positions for the displayed root.
     * <p>
     * The positions of the root must be calculated first.
     * </p>
     */
    public void calculatePositions() {
        if (this.getStrategy() != null && this.displayedRoot != null) {
            getStrategy().calculatePositions(this.displayedRoot);
        }
    }

    @Override
    public JToolTip createToolTip() {
        return toolTipBuilder.getToolTip();
    }

    /**
     * Draws the item.
     *
     * @param g    the Graphics context where the item has to be drawn
     * @param item the item to draw
     */
    protected void draw(final Graphics g, final TreeMapNode item) {
        if (item.isLeaf() && item.getValue() != null) {
            g.setColor(this.colorProvider.getColor(item.getValue()));
            g.fillRect(item.getX(), item.getY(), item.getWidth(), item.getHeight());
        } else {
            for (final Enumeration<?> e = item.children(); e.hasMoreElements();) {
                draw(g, (TreeMapNode) e.nextElement());
            }
        }
    }

    /**
     * Writes the label in the middle of the item.
     * <p>
     * You must first define the font of the Graphics.
     * </p>
     * <p>
     * You may override this method to change the position or the color of the
     * label.
     * </p>
     *
     * @param g    the Graphics context where the label has to be drawn
     * @param item the TreeMapNode to draw
     */
    protected void drawLabel(final Graphics g, final TreeMapNode item) {
        final FontMetrics fm = g.getFontMetrics(g.getFont());
        if (fm.getHeight() < item.getHeight() - 2) {
            String label = item.getLabel();
            final int y = (item.getHeight() + fm.getAscent() - fm.getDescent()) / 2;
            final int stringWidth = fm.stringWidth(label);
            if (item.getWidth() - BORDER_FOR_FONT <= stringWidth) {
                final int nbChar = label.length() * item.getWidth() / stringWidth;
                if (nbChar > MAX_NUM_CHAR) {
                    label = label.substring(0, nbChar - MAX_NUM_CHAR) + "...";
                } else {
                    label = "";
                }
            }
            final int x = (item.getWidth() - fm.stringWidth(label)) / 2;
            g.setColor(Color.black);
            g.drawString(label, item.getX() + x + 1, item.getY() + y + 1);
            g.drawString(label, item.getX() + x - 1, item.getY() + y + 1);
            g.drawString(label, item.getX() + x + 1, item.getY() + y - 1);
            g.drawString(label, item.getX() + x - 1, item.getY() + y - 1);
            g.drawString(label, item.getX() + x + 1, item.getY() + y);
            g.drawString(label, item.getX() + x - 1, item.getY() + y);
            g.drawString(label, item.getX() + x, item.getY() + y + 1);
            g.drawString(label, item.getX() + x, item.getY() + y - 1);
            g.setColor(Color.white);
            g.drawString(label, item.getX() + x, item.getY() + y);
        }
    }

    /**
     * Draws all the labels to draw.
     * <p>
     * You may override this method to draw the labels you want. For example, all
     * the leaves, or all the first level children, or all of them.
     * </p>
     *
     * @param g    the Graphics context where the labels have to be drawn
     * @param item the TreeMapNode to draw
     */
    protected void drawLabels(final Graphics g, final TreeMapNode item) {
        g.setFont(this.getFont());
        if (this.displayedRoot.isLeaf()) {
            drawLabel(g, this.displayedRoot);
        } else {
            for (final Enumeration<?> e = this.displayedRoot.children(); e.hasMoreElements();) {
                drawLabel(g, (TreeMapNode) e.nextElement());
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
     * Gets the active leaf (the one under the mouse).
     *
     * @return the active leaf
     */
    public TreeMapNode getActiveLeaf() {
        return this.activeLeaf;
    }

    /**
     * Gets the ColorProvider.
     *
     * @return the ColorProvider
     */
    public ColorProvider getColorProvider() {
        return this.colorProvider;
    }

    /**
     * Gets the displayed root.
     * <p>
     * This may not be the root of the JTreeMap. After a zoom, the displayed root
     * can be the root of a subtree.
     * </p>
     *
     * @return the displayed root
     */
    public TreeMapNode getDisplayedRoot() {
        return this.displayedRoot;
    }

    /**
     * Gets the root of the JTreeMap.
     *
     * @return the root
     */
    public TreeMapNode getRoot() {
        return this.root;
    }

    /**
     * Gets the SplitStrategy.
     *
     * @return the SplitStrategy
     */
    public SplitStrategy getStrategy() {
        return this.strategy;
    }

    @Override
    public Point getToolTipLocation(final MouseEvent event) {
        int posX;
        int posY;
        final JToolTip toolTip = createToolTip();
        final int xMax = this.displayedRoot.getX() + this.displayedRoot.getWidth();
        final int yMin = this.displayedRoot.getY();
        if (this.activeLeaf != null) {
            if (this.activeLeaf.getWidth() >= toolTip.getWidth() + 2 * INSET
                    && activeLeaf.getHeight() >= toolTip.getHeight() + 2 * INSET) {
                posX = this.activeLeaf.getX() + INSET;
                posY = this.activeLeaf.getY() + INSET;
            } else {
                posX = this.activeLeaf.getX() + this.activeLeaf.getWidth() + INSET;
                posY = this.activeLeaf.getY() - toolTip.getHeight() - INSET;
            }

            if (posY < yMin + INSET) {
                posY = yMin + INSET;
            }
            if (posX + toolTip.getWidth() > xMax - INSET && this.activeLeaf.getX() >= toolTip.getWidth() + INSET) {
                posX = this.activeLeaf.getX() - INSET - toolTip.getWidth();
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
        root.setDimension(this.root.getX(), root.getY(), width - border - insets.left - insets.right, height - border - insets.top - insets.bottom);

        if (!this.root.equals(this.displayedRoot)) {
            displayedRoot.setDimension(this.displayedRoot.getX(), displayedRoot.getY(), width - border - insets.left - insets.right,
                    height - border - insets.top - insets.bottom);
        }

        calculatePositions();

        if (this.displayedRoot.children().hasMoreElements()) {
            // the background
            g.setColor(this.getBackground());
            g.fillRect(this.displayedRoot.getX(), displayedRoot.getY(), displayedRoot.getWidth() + border, displayedRoot.getHeight() + border);
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
     * Reveals the item.
     *
     * @param g    the Graphics context where the item has to be drawn
     * @param item the TreeMapNode to reveal
     */
    protected void reveal(final Graphics g, final TreeMapNode item) {
        if (item.isLeaf()) {
            g.setColor(TRANSPARENCY_COLOR);
            g.fillRect(item.getX(), item.getY(), item.getWidth(), item.getHeight());
        }
    }

    /**
     * Sets the active leaf.
     *
     * @param newActiveLeaf the new active leaf
     */
    public void setActiveLeaf(final TreeMapNode newActiveLeaf) {
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
    public void setBorder(final Border border) {
        // Substract the previous border insets
        Insets insets = getInsets();
        displayedRoot.setDimension(this.displayedRoot.getX() - insets.left, displayedRoot.getY() - insets.top,
                displayedRoot.getWidth() + insets.left + insets.right, displayedRoot.getHeight() + insets.top + insets.bottom);

        super.setBorder(border);

        // Add the new border insets
        insets = getInsets();
        displayedRoot.setDimension(this.displayedRoot.getX() + insets.left, displayedRoot.getY() + insets.top,
                displayedRoot.getWidth() - insets.left - insets.right, displayedRoot.getHeight() - insets.top - insets.bottom);
    }

    /**
     * Sets the ColorProvider.
     *
     * @param newColorProvider the new ColorProvider
     */
    public void setColorProvider(final ColorProvider newColorProvider) {
        this.colorProvider = newColorProvider;
    }

    /**
     * Sets the displayed root.
     * <p>
     * This may not be the root of the JTreeMap. After a zoom, the displayed root
     * can be the root of a subtree.
     * </p>
     *
     * @param newDisplayedRoot the new displayed root
     */
    public void setDisplayedRoot(final TreeMapNode newDisplayedRoot) {
        this.displayedRoot = newDisplayedRoot;
    }

    /**
     * Sets the new root.
     *
     * @param newRoot the new root to set
     */
    public void setRoot(final TreeMapNode newRoot) {
        this.root = newRoot;
        final Insets insets = getInsets();
        this.root.setX(insets.left);
        this.root.setY(insets.top);
        setDisplayedRoot(this.root);
    }

    /**
     * Sets the new strategy.
     *
     * @param newStrat the new strategy to set
     */
    public void setStrategy(final SplitStrategy newStrat) {
        this.strategy = newStrat;
    }

    /**
     * Sets the builder of the toolTip.
     *
     * @param toolTipBuilder the toolTipBuilder to set
     */
    public void setToolTipBuilder(final IToolTipBuilder toolTipBuilder) {
        this.toolTipBuilder = toolTipBuilder;
    }

    /**
     * When you zoom the JTreeMap, you have the choice to keep proportions or not.
     *
     * @param keepProportion true if you want to keep proportions, else false
     */
    public void setZoomKeepProportion(final boolean keepProportion) {
        this.zoom.setKeepProportion(keepProportion);
    }

    /**
     * Undoes the zoom to display the root.
     */
    public void unzoom() {
        this.zoom.undo();
    }

    /**
     * Zooms the JTreeMap to the specified destination node.
     *
     * @param dest the node we want to zoom to
     */
    public void zoom(final TreeMapNode dest) {
        unzoom();
        if (dest != null) {
            this.zoom.execute(dest);
        }
    }

    /**
     * A listener that defines the active leaf and sets the tooltip text.
     *
     * @author Laurent Dutheil
     */
    protected class HandleMouseMotion extends MouseMotionAdapter {
        @Override
        public void mouseMoved(final MouseEvent e) {
            if (getDisplayedRoot().children().hasMoreElements()) {
                final TreeMapNode t = getDisplayedRoot().getActiveLeaf(e.getX(), e.getY());
                if (t == null || !t.equals(getActiveLeaf())) {
                    setActiveLeaf(t);
                    repaint();
                }
            }
        }
    }

    /**
     * A listener that listens for double-clicks to navigate one level down.
     *
     * @author Ekin Gulen
     */
    protected class HandleMouseClick extends MouseAdapter {
        @Override
        public void mouseClicked(final MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 1) {
                final TreeMapNode t = getDisplayedRoot().getChild(e.getX(), e.getY());
                if (t != null && !t.isLeaf()) {
                    if (JTreeMap.this.treeView == null) {
                        zoom(t);
                    } else {
                        final TreePath path = new TreePath(t.getPath());
                        JTreeMap.this.treeView.setSelectionPath(path);
                        JTreeMap.this.treeView.scrollPathToVisible(path);
                    }
                } else {
                    if (JTreeMap.this.treeView == null) {
                        zoom((TreeMapNode) getDisplayedRoot().getParent());
                    } else {
                        final TreePath path = new TreePath(
                                ((TreeMapNode) getDisplayedRoot().getParent()).getPath());
                        JTreeMap.this.treeView.setSelectionPath(path);
                        JTreeMap.this.treeView.scrollPathToVisible(path);
                    }
                }
                repaint();
            }
        }

        @Override
        public void mouseExited(final MouseEvent arg0) {
            setActiveLeaf(null);
            repaint();
        }
    }

    /**
     * A class that zooms and un-zooms the JTreeMap.
     *
     * @author Laurent Dutheil
     */
    private class Zoom implements Serializable {
        private static final long serialVersionUID = 6708828099608367996L;
        private boolean enable;
        private boolean keepProportion;

        public Zoom() {
            this.enable = true;
        }

        /**
         * Executes the zoom.
         *
         * @param dest the TreeMapNode where you want to zoom
         */
        public void execute(final TreeMapNode dest) {
            if (this.enable) {
                JTreeMap.this.setActiveLeaf(null);
                setNewDimension(dest);
                JTreeMap.this.setDisplayedRoot(dest);
                this.enable = false;
            }
        }

        /**
         * Returns whether to keep the proportion when zooming.
         *
         * @return true if the proportion should be kept, false otherwise
         */
        public boolean isKeepProportion() {
            return this.keepProportion;
        }

        /**
         * Sets whether to keep the proportion when zooming.
         *
         * @param keepProportion true if the proportion should be kept, false otherwise
         */
        public void setKeepProportion(final boolean keepProportion) {
            this.keepProportion = keepProportion;
        }

        /**
         * Sets the new dimensions of the destination root.
         *
         * @param dest the root to dimension
         */
        protected void setNewDimension(final TreeMapNode dest) {
            if (dest == null) {
                return;
            }
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
         * Undoes the zoom.
         */
        public void undo() {
            if (!this.enable) {
                JTreeMap.this.setDisplayedRoot(JTreeMap.this.getRoot());
                this.enable = true;
            }
        }
    }

    /**
     * Gets the JTree view.
     *
     * @return the JTree view
     */
    public JTree getTreeView() {
        return this.treeView;
    }

    /**
     * Sets the JTree view.
     *
     * @param treeView the JTree view
     */
    public void setTreeView(final JTree treeView) {
        this.treeView = treeView;
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
