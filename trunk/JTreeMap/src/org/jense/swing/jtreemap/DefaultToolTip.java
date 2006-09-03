/*
 * Created on 28 sept. 2005
 */
package org.jense.swing.jtreemap;

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
  private static final long serialVersionUID = -2492627777999093973L;
  private JTreeMap jTreeMap;
  private Font labelFont;
  private Font valueFont;

  /**
   * Constructor.
   * 
   * @param jTreeMap the jTreeMap who display the tooltip
   */
  public DefaultToolTip(JTreeMap jTreeMap) {
    this.jTreeMap = jTreeMap;
    this.labelFont = new Font("Default", Font.BOLD, 14);
    this.valueFont = new Font("Default", Font.PLAIN, 10);

    int width = 160;
    int height = getFontMetrics(this.labelFont).getHeight()
        + getFontMetrics(this.valueFont).getHeight();

    Dimension size = new Dimension(width, height);
    this.setSize(size);
    this.setPreferredSize(size);
  }

  @Override
  public void paint(Graphics g) {
    if (this.jTreeMap.getActiveLeaf() != null) {
      g.setColor(Color.lightGray);
      g.fill3DRect(0, 0, this.getWidth(), this.getHeight(), true);
      g.setColor(Color.black);
      g.setFont(this.labelFont);
      g.drawString(this.jTreeMap.getActiveLeaf().getLabel(), 5, g
          .getFontMetrics(this.labelFont).getAscent());
      g.setFont(this.valueFont);
      g.drawString(this.jTreeMap.getActiveLeaf().getLabelValue(), 5, this
          .getHeight() - 5);
    }
  }

}
