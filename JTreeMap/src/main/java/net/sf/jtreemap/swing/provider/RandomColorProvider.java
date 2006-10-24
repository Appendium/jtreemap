/*
 * Created on 4 nov. 2005
 */
package net.sf.jtreemap.swing.provider;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.Enumeration;
import java.util.TreeMap;

import javax.swing.JPanel;

import net.sf.jtreemap.swing.ColorProvider;
import net.sf.jtreemap.swing.JTreeMap;
import net.sf.jtreemap.swing.TreeMapNode;
import net.sf.jtreemap.swing.Value;

/**
 * <p>
 * ColorProvider who choose the color between 13 predefined COLOURS.
 * </p>
 * <p>
 * Each value is associated to a color. If all the COLOURS are already
 * associated the new value is associated to the first color (and so on...)
 * </p>
 * 
 * @author Laurent DUTHEIL
 */
public class RandomColorProvider extends ColorProvider {
    /**
     * 
     */
    private static final long serialVersionUID = -8184356270950978553L;

    private static final Color[] COLOURS = new Color[] { new Color(255, 0, 0), new Color(0, 255, 0), new Color(0, 0, 255),
            new Color(255, 255, 0), new Color(255, 0, 255), new Color(0, 255, 255), new Color(102, 102, 51),
            new Color(255, 51, 153), new Color(255, 153, 51), new Color(204, 204, 51), new Color(205, 102, 204),
            new Color(51, 153, 255), new Color(153, 102, 0) };

    private int cursor = 0;

    private final TreeMap<Value, Color> mapping = new TreeMap<Value, Color>();

    private JPanel legend;

    private JTreeMap jTreeMap;

    /**
     * Constructor
     * 
     * @param jTreeMap
     *            jTreeMap to color
     */
    public RandomColorProvider(final JTreeMap jTreeMap) {
        this.jTreeMap = jTreeMap;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.jtreemap.swing.ColorProvider#getColor(double)
     */
    @Override
    public Color getColor(final Value value) {
        if (!this.mapping.containsKey(value)) {
            mapping.put(value, COLOURS[this.cursor]);
            cursor++;
            if (this.cursor == COLOURS.length) {
                cursor = 0;
            }
        }
        return mapping.get(value);
    }

    void setValues(final TreeMapNode root) {
        if (root.isLeaf()) {
            final Value value = root.getValue();
            getColor(value);
        } else {
            for (final Enumeration e = root.children(); e.hasMoreElements();) {
                final TreeMapNode node = (TreeMapNode) e.nextElement();
                setValues(node);
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.jtreemap.swing.ColorProvider#getLegendPanel()
     */
    @Override
    public JPanel getLegendPanel() {
        if (this.legend == null) {
            legend = new Legend();
        }
        return legend;
    }

    /**
     * Panel with the legend
     * 
     * @author Laurent Dutheil
     */
    protected class Legend extends JPanel {
        private static final int OFFSET = 3;

        private static final int X_OFFSET = 15;

        private static final int INITIAL_X_POS = 20;

        private static final long serialVersionUID = 4652239358357480113L;

        private int x = INITIAL_X_POS;

        private static final int Y = 25;

        private static final int WIDTH = 10;

        private static final int HEIGHT = 20;

        @Override
        public void paintComponent(final Graphics g) {
            super.paintComponent(g);
            if (RandomColorProvider.this.mapping.isEmpty()) {
                RandomColorProvider.this.setValues(jTreeMap.getRoot());
            }
            final FontMetrics fm = g.getFontMetrics();
            final int yString = Legend.Y + (Legend.HEIGHT + fm.getAscent() - fm.getDescent()) / 2;

            x = INITIAL_X_POS;
            for (final Value value : RandomColorProvider.this.mapping.keySet()) {
                final Color color = RandomColorProvider.this.mapping.get(value);
                g.setColor(color);
                g.fillRect(this.x, Legend.Y, Legend.WIDTH, Legend.HEIGHT);
                g.setColor(Color.black);
                x = x + Legend.WIDTH + OFFSET;
                g.drawString(value.getLabel(), x, yString);
                x = x + fm.stringWidth(value.getLabel()) + X_OFFSET;
            }

            setPreferredSize(new Dimension(this.x, 2 * Legend.Y + Legend.HEIGHT));
            setSize(this.getPreferredSize());
        }
    }
}
