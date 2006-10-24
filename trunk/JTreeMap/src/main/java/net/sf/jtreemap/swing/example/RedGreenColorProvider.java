package net.sf.jtreemap.swing.example;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

import net.sf.jtreemap.swing.ColorProvider;
import net.sf.jtreemap.swing.JTreeMap;
import net.sf.jtreemap.swing.TreeMapNode;
import net.sf.jtreemap.swing.Value;

/**
 * ColorProvider who, with a max absolute value M, choose the color between
 * values -M and M.
 * 
 * @author Laurent Dutheil
 */

public class RedGreenColorProvider extends ColorProvider {
    /**
     * 
     */
    private static final long serialVersionUID = 5030306338780462810L;

    private JTreeMap jTreeMap;

    private JPanel legend;

    protected Value maxAbsValue;

    protected Value minVal;

    protected final int tabColor[] = { 0, 60, 102, 153, 204, 255 };

    private final int tabLimitValue[] = { 25, 76, 123, 179, 230, 255 };

    /**
     * Constructor
     * 
     * @param jTreeMap
     *            the JTreeMap to color
     */
    public RedGreenColorProvider(final JTreeMap jTreeMap) {
        this.jTreeMap = jTreeMap;
    }

    @Override
    public Color getColor(final Value value) {
        // update the max absolute value
        if (this.maxAbsValue == null) {
            setMaxValue(this.jTreeMap.getRoot());
        }

        final double dValeur = value.getValue();

        int colorIndex = (int) (255 * Math.abs(dValeur) / this.maxAbsValue.getValue());

        if (colorIndex > 255) {
            colorIndex = 255;
        }

        for (int i = 0; i < this.tabLimitValue.length; i++) {
            if (colorIndex <= this.tabLimitValue[i]) {
                colorIndex = this.tabColor[i];
                break;
            }
        }

        if (dValeur >= 0) {
            return new Color(0, colorIndex, 0);
        }
        return new Color(colorIndex, 0, 0);
    }

    @Override
    public JPanel getLegendPanel() {
        if (this.legend == null) {
            // update the max absolute value
            if (this.maxAbsValue == null) {
                setMaxValue(this.jTreeMap.getRoot());
            }
            this.legend = new Legend();
        }
        return this.legend;
    }

    /**
     * Set the max and the min value
     * 
     * @param root
     *            root of the JTreeMap
     */
    private void setMaxValue(final TreeMapNode root) {
        if (root.isLeaf()) {
            final Value value = root.getValue();
            if (this.maxAbsValue == null || Math.abs(value.getValue()) > this.maxAbsValue.getValue()) {
                try {
                    final Class c = value.getClass();
                    if (this.maxAbsValue == null || this.minVal == null) {
                        this.maxAbsValue = (Value) (c.newInstance());
                        this.minVal = (Value) (c.newInstance());
                    }
                    this.minVal.setValue(-Math.abs(value.getValue()));
                    this.maxAbsValue.setValue(Math.abs(value.getValue()));
                } catch (final IllegalAccessException iae) {
                    // ignore
                } catch (final InstantiationException ie) {
                    // ignore
                }
            }
        } else {
            for (final TreeMapNode node : root.getChildren()) {
                setMaxValue(node);
            }
        }
    }

    /**
     * Panel with the legend
     * 
     * @author Laurent Dutheil
     */
    private class Legend extends JPanel {
        private static final long serialVersionUID = -536198802533900214L;

        private static final int HEIGHT = 20;

        private static final int WIDTH = 10;

        private static final int X = 20;

        private static final int Y = 25;

        /**
         * Constructor of Legend
         */
        public Legend() {
            this.setPreferredSize(new java.awt.Dimension(
                    2 * Legend.X + RedGreenColorProvider.this.tabColor.length * Legend.WIDTH, 2 * Legend.Y + Legend.HEIGHT));
        }

        @Override
        public void paintComponent(final Graphics g) {
            super.paintComponent(g);
            if (RedGreenColorProvider.this.minVal == null || RedGreenColorProvider.this.maxAbsValue == null) {
                return;
            }

            int xCursor = 0;

            for (int i = RedGreenColorProvider.this.tabColor.length - 1; i > 0; i--) {
                g.setColor(new Color(RedGreenColorProvider.this.tabColor[i], 0, 0));
                g.fillRect(Legend.X + xCursor * Legend.WIDTH, Legend.Y, Legend.WIDTH, Legend.HEIGHT);
                xCursor++;
            }

            g.setColor(Color.black);
            g.drawString(RedGreenColorProvider.this.minVal.getLabel(), Legend.X - 15, Legend.Y - 7);
            g.drawString("0", Legend.X + xCursor * Legend.WIDTH, Legend.Y - 7);

            for (final int element : RedGreenColorProvider.this.tabColor) {
                g.setColor(new Color(0, element, 0));
                g.fillRect(Legend.X + xCursor * Legend.WIDTH, Legend.Y, Legend.WIDTH, Legend.HEIGHT);
                xCursor++;
            }

            g.setColor(Color.black);
            g
                    .drawString(RedGreenColorProvider.this.maxAbsValue.getLabel(), Legend.X + (xCursor - 1) * Legend.WIDTH,
                            Legend.Y - 7);
        }
    }

}
