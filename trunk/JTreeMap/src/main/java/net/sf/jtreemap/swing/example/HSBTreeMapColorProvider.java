/*
 * HSBTreeMapColorProvider.java Created on Feb 17, 2006
 */
package net.sf.jtreemap.swing.example;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Enumeration;

import javax.swing.JPanel;

import net.sf.jtreemap.swing.ColorProvider;
import net.sf.jtreemap.swing.DefaultValue;
import net.sf.jtreemap.swing.JTreeMap;
import net.sf.jtreemap.swing.TreeMapNode;
import net.sf.jtreemap.swing.Value;

/**
 * An HSB color space color provider for JTreeMap. Uses a specified function to
 * map the values onto the HSB color space. The default is a linear function,
 * but in my experience one of the logarithmic ones works best for this color
 * space.
 *
 * @author Andy Adamczak
 */
public class HSBTreeMapColorProvider extends ColorProvider {
    /**
     * @author Andy Adamczak
     */
    public enum ColorDistributionTypes {
        /**
         *
         */
        Linear,
        /**
         *
         */
        Log,
        /**
         *
         */
        Exp,
        /**
         *
         */
        SquareRoot,
        /**
         *
         */
        CubicRoot
    }

    /**
     * @param treeMap
     * @param color
     */
    public HSBTreeMapColorProvider(JTreeMap treeMap, Color color)
    {
        this(treeMap, ColorDistributionTypes.Linear, color, color);
    }

    /**
     * @param treeMap
     * @param colorDistribution
     * @param color
     */
    public HSBTreeMapColorProvider(JTreeMap treeMap, ColorDistributionTypes colorDistribution, Color color)
    {
        this(treeMap, colorDistribution, color, color);
    }

    /**
     * @param treeMap
     * @param positiveColor
     * @param negativeColor
     */
    public HSBTreeMapColorProvider(JTreeMap treeMap, Color positiveColor, Color negativeColor)
    {
        this(treeMap, ColorDistributionTypes.Linear, positiveColor, negativeColor);
    }

    /**
     * @param treeMap
     * @param colorDistribution
     * @param positiveColor
     * @param negativeColor
     */
    public HSBTreeMapColorProvider(JTreeMap treeMap, ColorDistributionTypes colorDistribution, Color positiveColor, Color negativeColor)
    {
        super();
        m_jTreeMap = treeMap;
        m_colorDistribution = colorDistribution;
        adjustColor(positiveColor, negativeColor);
    }

    /**
     * @param treeMap
     * @param hue
     * @param saturation
     */
    public HSBTreeMapColorProvider(JTreeMap treeMap, float hue, float saturation)
    {
        this(treeMap, ColorDistributionTypes.Linear, hue, saturation, hue, saturation);
    }

    /**
     * @param treeMap
     * @param colorDistribution
     * @param hue
     * @param saturation
     */
    public HSBTreeMapColorProvider(JTreeMap treeMap, ColorDistributionTypes colorDistribution, float hue, float saturation)
    {
        this(treeMap, colorDistribution, hue, saturation, hue, saturation);
    }

    /**
     * @param treeMap
     * @param positiveHue
     * @param positiveSaturation
     * @param negativeHue
     * @param negativeSaturation
     */
    public HSBTreeMapColorProvider(JTreeMap treeMap, float positiveHue, float positiveSaturation, float negativeHue, float negativeSaturation)
    {
        this(treeMap, ColorDistributionTypes.Linear, positiveHue, positiveSaturation, negativeHue, negativeSaturation);
    }

    /**
     * @param treeMap
     * @param colorDistribution
     * @param positiveHue
     * @param positiveSaturation
     * @param negativeHue
     * @param negativeSaturation
     */
    public HSBTreeMapColorProvider(JTreeMap treeMap, ColorDistributionTypes colorDistribution, float positiveHue, float positiveSaturation,
        float negativeHue, float negativeSaturation)
    {
        super();
        m_jTreeMap = treeMap;
        m_colorDistribution = colorDistribution;
        adjustColor(positiveHue, positiveSaturation, negativeHue, negativeSaturation);
    }

    /*
     * (non-Javadoc)
     *
     * @see net.sf.jtreemap.swing.ColorProvider#getLegendPanel()
     */
    @Override
    public JPanel getLegendPanel()
    {
        if (m_legend == null) {
          m_legend = new Legend();
        }

        return m_legend;
    }

    /**
     * @param color
     */
    public void adjustColor(Color color)
    {
        adjustColor(color, color);
    }

    /**
     * @param positiveColor
     * @param negativeColor
     */
    public void adjustColor(Color positiveColor, Color negativeColor)
    {
        // Figure out the hue of the passed in colors. Note, greys will map to reds in this color space, so use the
        // hue/saturation
        // constructions for grey scales.
        float[] hsbvals = new float[3];

        hsbvals = Color.RGBtoHSB(positiveColor.getRed(), positiveColor.getGreen(), positiveColor.getBlue(), hsbvals);
        m_positiveHue = hsbvals[0];
        m_positiveSaturation = 1f;

        hsbvals = Color.RGBtoHSB(negativeColor.getRed(), negativeColor.getGreen(), negativeColor.getBlue(), hsbvals);
        m_negativeHue = hsbvals[0];
        m_negativeSaturation = 1f;
    }

    /**
     * @param hue
     * @param saturation
     */
    public void adjustColor(float hue, float saturation)
    {
        adjustColor(hue, saturation, hue, saturation);
    }

    /**
     * @param positiveHue
     * @param positiveSaturation
     * @param negativeHue
     * @param negativeSaturation
     */
    public void adjustColor(float positiveHue, float positiveSaturation, float negativeHue, float negativeSaturation)
    {
        m_positiveHue = positiveHue;
        m_positiveSaturation = positiveSaturation;
        m_negativeHue = negativeHue;
        m_negativeSaturation = negativeSaturation;
    }

    /*
     * (non-Javadoc)
     *
     * @see net.sf.jtreemap.swing.ColorProvider#getColor(net.sf.jtreemap.swing.Value)
     */
    @Override
    public Color getColor(Value value)
    {
        // Figure out the current range of colors, map that range into a scale from 0 to 1,
        // using the specified distribution type
        if (m_maxValue == null || m_minValue == null) {
          setValues(m_jTreeMap.getRoot());
        }
        double maxValue = m_maxValue.getValue();
        double minValue = m_minValue.getValue();
        double val = value.getValue();

        if (val >= 0) {
            // Value is greater than 0, use the positive colors
            double range = maxValue - Math.max(0, minValue);
            val -= Math.max(0, minValue);
            range = adjustValue(range);
            return Color.getHSBColor(m_positiveHue, m_positiveSaturation, (float) (adjustValue(val) / range));
        }

        // Value is less than 0, use the negative colors
        double range = Math.abs(minValue - Math.min(0, maxValue));
        val += Math.min(0, maxValue);
        val = Math.abs(val);
        // Value and range are not positive values, we need them to be for the math functions
        range = adjustValue(range);
        return Color.getHSBColor(m_negativeHue, m_negativeSaturation, (float) (adjustValue(val) / range));
    }

    /**
     * Given a value, maps that value to a new value using the specified math function
     *
     * @param value
     *            the value to convert
     * @return the converted value
     */
    private double adjustValue(double value)
    {
        switch (m_colorDistribution) {
            case Log:
                return Math.log1p(value);
            case Exp:
                return Math.exp(value);
            case SquareRoot:
                return Math.sqrt(value);
            case CubicRoot:
                return Math.cbrt(value);
            default:
              // Linear
              return value;
        }
    }

    /**
     * Set the max and the min values in the tree map
     *
     * @param root
     *            root of the JTreeMap
     */
    void setValues(TreeMapNode root)
    {
        if (root.isLeaf()) {
            Value value = root.getValue();

            if (m_maxValue == null || value.getValue() >= m_maxValue.getValue()) {
                try {
                    Class c = value.getClass();
                    if (m_maxValue == null) {
                      m_maxValue = (Value) (c.newInstance());
                    }
                    m_maxValue.setValue(value.getValue());
                } catch (IllegalAccessException iae) {
                    // ignore
                } catch (InstantiationException ie) {
                    // Ignore
                    System.err.println("Error: " + ie.getMessage());
                    ie.printStackTrace();
                }
            }

            if (m_minValue == null || value.getValue() <= m_minValue.getValue()) {
                try {
                    Class c = value.getClass();
                    if (m_minValue == null) {
                      m_minValue = (Value) (c.newInstance());
                    }
                    m_minValue.setValue(value.getValue());
                } catch (IllegalAccessException iae) {
                    // ignore
                } catch (InstantiationException ie) {
                    // Ignore
                    System.err.println("Error: " + ie.getMessage());
                    ie.printStackTrace();
                }
            }
        } else {
            for (Enumeration e = root.children(); e.hasMoreElements();) {
                TreeMapNode node = (TreeMapNode) e.nextElement();
                setValues(node);
            }
        }
    }

    protected JTreeMap m_jTreeMap;

    private JPanel m_legend;

    protected Value m_maxValue;

    protected Value m_minValue;

    private float m_positiveHue;

    private float m_negativeHue;

    private float m_positiveSaturation = 1f;

    private float m_negativeSaturation = 1f;

    private ColorDistributionTypes m_colorDistribution = ColorDistributionTypes.Linear;
  /**
   * Panel with the legend
   *
   * @author Laurent Dutheil
   */
  private class Legend extends JPanel {
    private static final long serialVersionUID = 6371342387871103592L;
    private final int height = 20;
    private final int width = 120;
    private final int x = 20;
    private final int y = 25;

    /**
     * Constructor of Legend
     */
    public Legend() {
      this.setSize(new java.awt.Dimension(2 * this.x + this.width, 2 * this.y
          + this.height));
      this.setPreferredSize(new java.awt.Dimension(2 * this.x + this.width, 2
          * this.y + this.height));
    }

    @Override
    public void paintComponent(Graphics g) {
      super.paintComponent(g);
      if (HSBTreeMapColorProvider.this.m_minValue == null
          || HSBTreeMapColorProvider.this.m_maxValue == null) {
        setValues(HSBTreeMapColorProvider.this.m_jTreeMap.getRoot());
      }
      Value min = HSBTreeMapColorProvider.this.m_minValue;
      Value max = HSBTreeMapColorProvider.this.m_maxValue;

      g.setColor(Color.black);
      g.drawString(min.getLabel(), this.x - 15, this.y - 7);
      g.drawString(max.getLabel(), this.x + this.width - 15, this.y - 7);

      double step = (max.getValue() - min.getValue()) / this.width;
      Value value = new DefaultValue(min.getValue());
      for (int i = 0; i < this.width; i++) {
        g.setColor(HSBTreeMapColorProvider.this.getColor(value));
        g.fillRect(this.x + i, this.y, 1, this.height);
        value.setValue(value.getValue() + step);
      }
    }
  }

}
