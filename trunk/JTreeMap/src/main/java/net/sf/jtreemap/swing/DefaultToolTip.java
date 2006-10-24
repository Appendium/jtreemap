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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JToolTip;

/**
 * Default ToolTip for the jTreeMap.
 * 
 * @author Laurent DUTHEIL
 */
public class DefaultToolTip extends JToolTip {
    private static final int TOOLTIP_OFFSET = 5;

    private static final int DEFAULT_VALUE_SIZE = 10;

    private static final int DEFAULT_LABEL_SIZE = 14;

    private static final long serialVersionUID = -2492627777999093973L;

    private JTreeMap jTreeMap;

    private Font labelFont;

    private Font valueFont;

    /**
     * Constructor.
     * 
     * @param jTreeMap
     *            the jTreeMap who display the tooltip
     */
    public DefaultToolTip(final JTreeMap jTreeMap) {
        this.jTreeMap = jTreeMap;
        this.labelFont = new Font("Default", Font.BOLD, DEFAULT_LABEL_SIZE);
        this.valueFont = new Font("Default", Font.PLAIN, DEFAULT_VALUE_SIZE);

        final int width = 160;
        final int height = getFontMetrics(this.labelFont).getHeight() + getFontMetrics(this.valueFont).getHeight();

        final Dimension size = new Dimension(width, height);
        this.setSize(size);
        this.setPreferredSize(size);
    }

    @Override
    public void paint(final Graphics g) {
        if (this.jTreeMap.getActiveLeaf() != null) {
            g.setColor(Color.lightGray);
            g.fill3DRect(0, 0, this.getWidth(), this.getHeight(), true);
            g.setColor(Color.black);
            g.setFont(this.labelFont);
            g.drawString(this.jTreeMap.getActiveLeaf().getLabel(), TOOLTIP_OFFSET, g.getFontMetrics(this.labelFont).getAscent());
            g.setFont(this.valueFont);
            g.drawString(this.jTreeMap.getActiveLeaf().getLabelValue(), TOOLTIP_OFFSET, this.getHeight() - TOOLTIP_OFFSET);
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
