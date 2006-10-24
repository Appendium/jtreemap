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
     * 
     */
    private static final long serialVersionUID = 5009655580804320847L;
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
    public HSBTreeMapColorProvider(final JTreeMap treeMap, final Color color)
    {
        this(treeMap, ColorDistributionTypes.Linear, color, color);
    }

    /**
     * @param treeMap
     * @param colorDistribution
     * @param color
     */
    public HSBTreeMapColorProvider(final JTreeMap treeMap, final ColorDistributionTypes colorDistribution, final Color color)
    {
        this(treeMap, colorDistribution, color, color);
    }

    /**
     * @param treeMap
     * @param positiveColor
     * @param negativeColor
     */
    public HSBTreeMapColorProvider(final JTreeMap treeMap, final Color positiveColor, final Color negativeColor)
    {
        this(treeMap, ColorDistributionTypes.Linear, positiveColor, negativeColor);
    }

    /**
     * @param treeMap
     * @param colorDistribution
     * @param positiveColor
     * @param negativeColor
     */
    public HSBTreeMapColorProvider(final JTreeMap treeMap, final ColorDistributionTypes colorDistribution, final Color positiveColor, final Color negativeColor)
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
    public HSBTreeMapColorProvider(final JTreeMap treeMap, final float hue, final float saturation)
    {
        this(treeMap, ColorDistributionTypes.Linear, hue, saturation, hue, saturation);
    }

    /**
     * @param treeMap
     * @param colorDistribution
     * @param hue
     * @param saturation
     */
    public HSBTreeMapColorProvider(final JTreeMap treeMap, final ColorDistributionTypes colorDistribution, final float hue, final float saturation)
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
    public HSBTreeMapColorProvider(final JTreeMap treeMap, final float positiveHue, final float positiveSaturation, final float negativeHue, final float negativeSaturation)
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
    public HSBTreeMapColorProvider(final JTreeMap treeMap, final ColorDistributionTypes colorDistribution, final float positiveHue, final float positiveSaturation,
        final float negativeHue, final float negativeSaturation)
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
    public void adjustColor(final Color color)
    {
        adjustColor(color, color);
    }

    /**
     * @param positiveColor
     * @param negativeColor
     */
    public void adjustColor(final Color positiveColor, final Color negativeColor)
    {
        // Figure out the hue of the passed in colors. Note, greys will map to
        // reds in this color space, so use the
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
    public void adjustColor(final float hue, final float saturation)
    {
        adjustColor(hue, saturation, hue, saturation);
    }

    /**
     * @param positiveHue
     * @param positiveSaturation
     * @param negativeHue
     * @param negativeSaturation
     */
    public void adjustColor(final float positiveHue, final float positiveSaturation, final float negativeHue, final float negativeSaturation)
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
    public Color getColor(final Value value)
    {
        // Figure out the current range of colors, map that range into a scale
        // from 0 to 1,
        // using the specified distribution type
        if (m_maxValue == null || m_minValue == null) {
          setValues(m_jTreeMap.getRoot());
        }
        final double maxValue = m_maxValue.getValue();
        final double minValue = m_minValue.getValue();
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
        // Value and range are not positive values, we need them to be for the
        // math functions
        range = adjustValue(range);
        return Color.getHSBColor(m_negativeHue, m_negativeSaturation, (float) (adjustValue(val) / range));
    }

    /**
     * Given a value, maps that value to a new value using the specified math
     * function
     * 
     * @param value
     *            the value to convert
     * @return the converted value
     */
    private double adjustValue(final double value)
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
    void setValues(final TreeMapNode root)
    {
        if (root.isLeaf()) {
            final Value value = root.getValue();

            if (m_maxValue == null || value.getValue() >= m_maxValue.getValue()) {
                try {
                    final Class c = value.getClass();
                    if (m_maxValue == null) {
                      m_maxValue = (Value) (c.newInstance());
                    }
                    m_maxValue.setValue(value.getValue());
                } catch (final IllegalAccessException iae) {
                    // ignore
                } catch (final InstantiationException ie) {
                    // Ignore
                    System.err.println("Error: " + ie.getMessage());
                    ie.printStackTrace();
                }
            }

            if (m_minValue == null || value.getValue() <= m_minValue.getValue()) {
                try {
                    final Class c = value.getClass();
                    if (m_minValue == null) {
                      m_minValue = (Value) (c.newInstance());
                    }
                    m_minValue.setValue(value.getValue());
                } catch (final IllegalAccessException iae) {
                    // ignore
                } catch (final InstantiationException ie) {
                    // Ignore
                    System.err.println("Error: " + ie.getMessage());
                    ie.printStackTrace();
                }
            }
        } else {
            for (final Enumeration e = root.children(); e.hasMoreElements();) {
                final TreeMapNode node = (TreeMapNode) e.nextElement();
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
    private static final int HEIGHT = 20;
    private static final int WIDTH = 120;
    private static final int X = 20;
    private static final int Y = 25;

    /**
     * Constructor of Legend
     */
    public Legend() {
      this.setSize(new java.awt.Dimension(2 * Legend.X + Legend.WIDTH, 2 * Legend.Y
          + Legend.HEIGHT));
      this.setPreferredSize(new java.awt.Dimension(2 * Legend.X + Legend.WIDTH, 2
          * Legend.Y + Legend.HEIGHT));
    }

    @Override
    public void paintComponent(final Graphics g) {
      super.paintComponent(g);
      if (HSBTreeMapColorProvider.this.m_minValue == null
          || HSBTreeMapColorProvider.this.m_maxValue == null) {
        setValues(HSBTreeMapColorProvider.this.m_jTreeMap.getRoot());
      }
      final Value min = HSBTreeMapColorProvider.this.m_minValue;
      final Value max = HSBTreeMapColorProvider.this.m_maxValue;

      g.setColor(Color.black);
      g.drawString(min.getLabel(), Legend.X - 15, Legend.Y - 7);
      g.drawString(max.getLabel(), Legend.X + Legend.WIDTH - 15, Legend.Y - 7);

      final double step = (max.getValue() - min.getValue()) / Legend.WIDTH;
      final Value value = new DefaultValue(min.getValue());
      for (int i = 0; i < Legend.WIDTH; i++) {
        g.setColor(HSBTreeMapColorProvider.this.getColor(value));
        g.fillRect(Legend.X + i, Legend.Y, 1, Legend.HEIGHT);
        value.setValue(value.getValue() + step);
      }
    }
  }

}
