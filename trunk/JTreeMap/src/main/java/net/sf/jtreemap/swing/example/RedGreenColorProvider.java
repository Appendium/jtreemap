package net.sf.jtreemap.swing.example;

import javax.swing.JPanel;

import net.sf.jtreemap.swing.ColorProvider;
import net.sf.jtreemap.swing.JTreeMap;
import net.sf.jtreemap.swing.TreeMapNode;
import net.sf.jtreemap.swing.Value;

import java.awt.Color;
import java.awt.Graphics;

/**
 * ColorProvider who, with a max absolute value M, choose the color between
 * values -M and M.
 *
 * @author Laurent Dutheil
 */

public class RedGreenColorProvider extends ColorProvider {
  private JTreeMap jTreeMap;
  private JPanel legend;
  protected Value maxAbsValue;
  protected Value minVal;
  protected final int tabColor[] = { 0, 60, 102, 153, 204, 255 };
  private final int tabLimitValue[] = { 25, 76, 123, 179, 230, 255 };

  /**
   * Constructor
   *
   * @param jTreeMap the JTreeMap to color
   */
  public RedGreenColorProvider(JTreeMap jTreeMap) {
    this.jTreeMap = jTreeMap;
  }

  @Override
  public Color getColor(Value value) {
    //update the max absolute value
    if (this.maxAbsValue == null) {
      setMaxValue(this.jTreeMap.getRoot());
    }

    double dValeur = value.getValue();

    int colorIndex = (int) (255 * Math.abs(dValeur) / this.maxAbsValue
        .getValue());

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
      //update the max absolute value
      if (this.maxAbsValue == null) {
        setMaxValue(this.jTreeMap.getRoot());
      }
      this.legend = new Legend();
    }
    return this.legend;
  }

  /**
   * Set the max and the min value
   * @param root root of the JTreeMap
   */
  private void setMaxValue(TreeMapNode root) {
    if (root.isLeaf()) {
      Value value = root.getValue();
      if (this.maxAbsValue == null || Math.abs(value.getValue()) > this.maxAbsValue.getValue()) {
        try {
          Class c = value.getClass();
          if (this.maxAbsValue == null || this.minVal == null) {
            this.maxAbsValue = (Value) (c.newInstance());
            this.minVal = (Value) (c.newInstance());
          }
          this.minVal.setValue(-Math.abs(value.getValue()));
          this.maxAbsValue.setValue(Math.abs(value.getValue()));
        } catch (IllegalAccessException iae) {
          //ignore
        } catch (InstantiationException ie) {
          //ignore
        }
      }
    } else {
      for (TreeMapNode node : root.getChildren()) {
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
    private final int height = 20;
    private final int width = 10;
    private final int x = 20;
    private final int y = 25;

    /**
     * Constructor of Legend
     */
    public Legend() {
      this.setPreferredSize(new java.awt.Dimension(2 * this.x
          + RedGreenColorProvider.this.tabColor.length * this.width, 2 * this.y
          + this.height));
    }

    @Override
    public void paintComponent(Graphics g) {
      super.paintComponent(g);
      if (RedGreenColorProvider.this.minVal == null
          || RedGreenColorProvider.this.maxAbsValue == null) {
        return;
      }

      int xCursor = 0;

      for (int i = RedGreenColorProvider.this.tabColor.length - 1; i > 0; i--) {
        g.setColor(new Color(RedGreenColorProvider.this.tabColor[i], 0, 0));
        g.fillRect(this.x + xCursor * this.width, this.y, this.width,
            this.height);
        xCursor++;
      }

      g.setColor(Color.black);
      g.drawString(RedGreenColorProvider.this.minVal.getLabel(), this.x - 15,
          this.y - 7);
      g.drawString("0", this.x + xCursor * this.width, this.y - 7);

      for (int i = 0; i < RedGreenColorProvider.this.tabColor.length; i++) {
        g.setColor(new Color(0, RedGreenColorProvider.this.tabColor[i], 0));
        g.fillRect(this.x + xCursor * this.width, this.y, this.width,
            this.height);
        xCursor++;
      }

      g.setColor(Color.black);
      g.drawString(RedGreenColorProvider.this.maxAbsValue.getLabel(), this.x
          + (xCursor - 1) * this.width, this.y - 7);
    }
  }

}
