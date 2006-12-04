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
 * $Id: ColorProvider.java 69 2006-10-24 16:20:20Z benoitx $
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
package net.sf.jtreemap.swing.provider;

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
    private static final int COLOUR_MAX_VALUE = 255;

    /**
     * 
     */
    private static final long serialVersionUID = 5030306338780462810L;

    private final JTreeMap jTreeMap;

    private JPanel legend;

    private Value maxAbsValue;

    private Value minVal;

    private final int[] tabColor = { 0, 60, 102, 153, 204, COLOUR_MAX_VALUE };

    private static final int[] TAB_LIMIT_VALUE = { 25, 76, 123, 179, 230, COLOUR_MAX_VALUE };

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

        final double dValeur = (value != null ? value.getValue() : 0.00);

        int colorIndex = (int) (COLOUR_MAX_VALUE * Math.abs(dValeur) / this.maxAbsValue.getValue());

        if (colorIndex > COLOUR_MAX_VALUE) {
            colorIndex = COLOUR_MAX_VALUE;
        }

        for (int i = 0; i < TAB_LIMIT_VALUE.length; i++) {
            if (colorIndex <= TAB_LIMIT_VALUE[i]) {
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
            if (value != null && (this.maxAbsValue == null || Math.abs(value.getValue()) > this.maxAbsValue.getValue())) {
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
        private static final int Y_INSET = 7;

        private static final int X_INSET = 15;

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
            g.drawString(RedGreenColorProvider.this.minVal.getLabel(), Legend.X - X_INSET, Legend.Y - Y_INSET);
            g.drawString("0", Legend.X + xCursor * Legend.WIDTH, Legend.Y - Y_INSET);

            for (final int element : RedGreenColorProvider.this.tabColor) {
                g.setColor(new Color(0, element, 0));
                g.fillRect(Legend.X + xCursor * Legend.WIDTH, Legend.Y, Legend.WIDTH, Legend.HEIGHT);
                xCursor++;
            }

            g.setColor(Color.black);
            g.drawString(RedGreenColorProvider.this.maxAbsValue.getLabel(), Legend.X + (xCursor - 1) * Legend.WIDTH, Legend.Y
                    - Y_INSET);
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
