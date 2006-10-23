/*
 * Created on 7 oct. 2005
 *
 */
package net.sf.jtreemap.swing;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

/**
 * Color Provider by default.<BR>
 * All values are associated to a unique color.
 *
 * @author Laurent DUTHEIL
 *
 */
public class UniqueColorProvider extends ColorProvider {
  private final static Color defaultColor = new Color(153,153,51);
  private Color color;
  private JPanel legend;

  /**
   * Constructor.
   */
  public UniqueColorProvider() {
    this.color = defaultColor;
  }

  /**
   * Constructor.
   * @param color unique color
   */
  public UniqueColorProvider(Color color) {
    this.color = color;
  }

  /* (non-Javadoc)
   * @see net.sf.jtreemap.swing.ColorProvider#getColor(double)
   */
  @Override
  public Color getColor(Value value) {
    return this.color;
  }

  /* (non-Javadoc)
   * @see net.sf.jtreemap.swing.ColorProvider#getLegendPanel()
   */
  @Override
  public JPanel getLegendPanel() {
    if (this.legend == null) {
      this.legend = new Legend();
    }
    return this.legend;
  }
  /**
   * Panel with the legend.
   *
   * @author Laurent Dutheil
   */
  private class Legend extends JPanel {
    private static final long serialVersionUID = -8046211081305644785L;
    private String text = "Unique Color Provider";

    /**
     * Constructor.
     */
    public Legend() {
      this.setPreferredSize(new java.awt.Dimension(100, 40));

    }

    @Override
    public void paint(Graphics g) {
      g.setColor(Color.black);
      g.drawString(this.text, 20, 20);
    }
  }
}
