/*
 * Created on 4 nov. 2005
 */
package org.jense.swing.jtreemap.example;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.Enumeration;
import java.util.TreeMap;

import javax.swing.JPanel;

import org.jense.swing.jtreemap.ColorProvider;
import org.jense.swing.jtreemap.JTreeMap;
import org.jense.swing.jtreemap.TreeMapNode;
import org.jense.swing.jtreemap.Value;

/**
 * <p>
 * ColorProvider who choose the color between 13 predefined colors.
 * </p>
 * <p>
 * Each value is associated to a color. If all the colors are already associated
 * the new value is associated to the first color (and so on...)
 * </p>
 * @author Laurent DUTHEIL
 */
public class RandomColorProvider extends ColorProvider {
  private final static Color[] colors = new Color[] { new Color(255, 0, 0),
      new Color(0, 255, 0), new Color(0, 0, 255), new Color(255, 255, 0),
      new Color(255, 0, 255), new Color(0, 255, 255), new Color(102, 102, 51),
      new Color(255, 51, 153), new Color(255, 153, 51),
      new Color(204, 204, 51), new Color(205, 102, 204), 
      new Color(51, 153, 255), new Color(153, 102, 0) };
  private int cursor = 0;
  protected TreeMap<Value, Color> mapping = new TreeMap<Value, Color>();
  private JPanel legend;
  protected JTreeMap jTreeMap;
  
  /**
   * Constructor
   * @param jTreeMap jTreeMap to color
   */
  public RandomColorProvider(JTreeMap jTreeMap) {
    this.jTreeMap = jTreeMap;
  }
  /*
   * (non-Javadoc)
   * 
   * @see org.jense.swing.jtreemap.ColorProvider#getColor(double)
   */
  @Override
  public Color getColor(Value value) {
    if (!this.mapping.containsKey(value)) {
      this.mapping.put(value, colors[this.cursor]);
      this.cursor++;
      if (this.cursor == colors.length) {
        this.cursor = 0;
      }
    }
    return this.mapping.get(value);
  }

  void setValues(TreeMapNode root)
  {
      if (root.isLeaf()) {
          Value value = root.getValue();
          getColor(value);
      } else {
          for (Enumeration e = root.children(); e.hasMoreElements();) {
              TreeMapNode node = (TreeMapNode) e.nextElement();
              setValues(node);
          }
      }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jense.swing.jtreemap.ColorProvider#getLegendPanel()
   */
  @Override
  public JPanel getLegendPanel() {
    if (this.legend == null) {
      this.legend = new Legend();
    }
    return this.legend;
  }

  /**
   * Panel with the legend
   * 
   * @author Laurent Dutheil
   */
  protected class Legend extends JPanel {
    private static final long serialVersionUID = 4652239358357480113L;
    private int x = 20;
    private final int y = 25;
    private final int width = 10;
    private final int height = 20;

    @Override
    public void paintComponent(Graphics g) {
      super.paintComponent(g);
      if (RandomColorProvider.this.mapping.isEmpty()) {
        RandomColorProvider.this.setValues(jTreeMap.getRoot());
      }
      FontMetrics fm = g.getFontMetrics();
      int yString = this.y + (this.height + fm.getAscent() - fm.getDescent())
          / 2;

      this.x = 20;
      for (Value value : RandomColorProvider.this.mapping.keySet()) {
        Color color = RandomColorProvider.this.mapping.get(value);
        g.setColor(color);
        g.fillRect(this.x, this.y, this.width, this.height);
        g.setColor(Color.black);
        this.x = this.x + this.width + 3;
        g.drawString(value.getLabel(), this.x, yString);
        this.x = this.x + fm.stringWidth(value.getLabel()) + 15;
      }

      this.setPreferredSize(new Dimension(this.x, 2 * this.y + this.height));
      this.setSize(this.getPreferredSize());

    }
  }

}
