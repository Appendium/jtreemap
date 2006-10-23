/*
 * HSBTreeMapColorProvider.java Created on Feb 17, 2006
 */
package net.sf.jtreemap.ktreemap.example;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import net.sf.jtreemap.ktreemap.ITreeMapColorProvider;
import net.sf.jtreemap.ktreemap.ITreeMapProvider;
import net.sf.jtreemap.ktreemap.KTreeMap;
import net.sf.jtreemap.ktreemap.TreeMapNode;

/**
 * An HSB color space color provider for KTreeMap. Uses a specified function to
 * map the values onto the HSB color space. The default is a linear function,
 * but in my experience one of the logarithmic ones works best for this color
 * space.
 *
 * @author Andy Adamczak
 */
public class HSBTreeMapColorProvider implements ITreeMapColorProvider {
  protected KTreeMap m_jTreeMap;
  protected double m_maxValue = Double.MIN_VALUE;
  protected double m_minValue = Double.MAX_VALUE;
  private float m_positiveHue;
  private float m_negativeHue;
  private float m_positiveSaturation = 1f;
  private float m_negativeSaturation = 1f;

  private ColorDistributionTypes m_colorDistribution = ColorDistributionTypes.Linear;

  /**
   * @param treeMap
   * @param color
   */
  public HSBTreeMapColorProvider(KTreeMap treeMap, Color color) {
    this(treeMap, ColorDistributionTypes.Linear, color, color);
  }

  /**
   * @param treeMap
   * @param positiveColor
   * @param negativeColor
   */
  public HSBTreeMapColorProvider(KTreeMap treeMap, Color positiveColor,
      Color negativeColor) {
    this(treeMap, ColorDistributionTypes.Linear, positiveColor, negativeColor);
  }

  /**
   * @param treeMap
   * @param colorDistribution
   * @param color
   */
  public HSBTreeMapColorProvider(KTreeMap treeMap,
      ColorDistributionTypes colorDistribution, Color color) {
    this(treeMap, colorDistribution, color, color);
  }

  /**
   * @param treeMap
   * @param colorDistribution
   * @param positiveColor
   * @param negativeColor
   */
  public HSBTreeMapColorProvider(KTreeMap treeMap,
      ColorDistributionTypes colorDistribution, Color positiveColor,
      Color negativeColor) {
    super();
    m_jTreeMap = treeMap;
    m_colorDistribution = colorDistribution;
    adjustColor(positiveColor, negativeColor);
  }

  /**
   * @param treeMap
   * @param colorDistribution
   * @param hue
   * @param saturation
   */
  public HSBTreeMapColorProvider(KTreeMap treeMap,
      ColorDistributionTypes colorDistribution, float hue, float saturation) {
    this(treeMap, colorDistribution, hue, saturation, hue, saturation);
  }

  /**
   * @param treeMap
   * @param colorDistribution
   * @param positiveHue
   * @param positiveSaturation
   * @param negativeHue
   * @param negativeSaturation
   */
  public HSBTreeMapColorProvider(KTreeMap treeMap,
      ColorDistributionTypes colorDistribution, float positiveHue,
      float positiveSaturation, float negativeHue, float negativeSaturation) {
    super();
    m_jTreeMap = treeMap;
    m_colorDistribution = colorDistribution;
    adjustColor(positiveHue, positiveSaturation, negativeHue,
        negativeSaturation);
  }

  /**
   * @param treeMap
   * @param hue
   * @param saturation
   */
  public HSBTreeMapColorProvider(KTreeMap treeMap, float hue, float saturation) {
    this(treeMap, ColorDistributionTypes.Linear, hue, saturation, hue,
        saturation);
  }

  /**
   * @param treeMap
   * @param positiveHue
   * @param positiveSaturation
   * @param negativeHue
   * @param negativeSaturation
   */
  public HSBTreeMapColorProvider(KTreeMap treeMap, float positiveHue,
      float positiveSaturation, float negativeHue, float negativeSaturation) {
    this(treeMap, ColorDistributionTypes.Linear, positiveHue,
        positiveSaturation, negativeHue, negativeSaturation);
  }

  /**
   * @param color
   */
  public void adjustColor(Color color) {
    adjustColor(color, color);
  }

  /**
   * @param positiveColor
   * @param negativeColor
   */
  public void adjustColor(Color positiveColor, Color negativeColor) {
    // Figure out the hue of the passed in colors. Note, greys will map to reds
    // in this color space, so use the
    // hue/saturation
    // constructions for grey scales.
    float[] hsbvals = new float[3];

    hsbvals = java.awt.Color.RGBtoHSB(positiveColor.getRed(), positiveColor
        .getGreen(), positiveColor.getBlue(), hsbvals);
    m_positiveHue = hsbvals[0];
    m_positiveSaturation = 1f;

    hsbvals = java.awt.Color.RGBtoHSB(negativeColor.getRed(), negativeColor
        .getGreen(), negativeColor.getBlue(), hsbvals);
    m_negativeHue = hsbvals[0];
    m_negativeSaturation = 1f;
  }

  /**
   * @param hue
   * @param saturation
   */
  public void adjustColor(float hue, float saturation) {
    adjustColor(hue, saturation, hue, saturation);
  }

  /**
   * @param positiveHue
   * @param positiveSaturation
   * @param negativeHue
   * @param negativeSaturation
   */
  public void adjustColor(float positiveHue, float positiveSaturation,
      float negativeHue, float negativeSaturation) {
    m_positiveHue = positiveHue;
    m_positiveSaturation = positiveSaturation;
    m_negativeHue = negativeHue;
    m_negativeSaturation = negativeSaturation;
  }

  public Color getBackground(Object value) {
    // Figure out the current range of colors, map that range into a scale from
    // 0 to 1,
    // using the specified distribution type
    if (m_maxValue == Double.MIN_VALUE || m_minValue == Double.MAX_VALUE) {
      setValues(m_jTreeMap.getRoot());
    }
    ITreeMapProvider provider = m_jTreeMap.getTreeMapProvider();
    double val = provider.getDoubleValue(value);

    return getBackground(val);
  }

  public Color getForeground(Object value) {
    return Display.getDefault().getSystemColor(SWT.COLOR_WHITE);
  }

  public Composite getLegend(Composite parent, int style) {
    return new Legend(parent, style);
  }

  /**
   * Set the max and the min values in the tree map
   *
   * @param root root of the JTreeMap
   */
  void setValues(TreeMapNode root) {
    if (root.isLeaf()) {
      Object value = root.getValue();
      ITreeMapProvider provider = m_jTreeMap.getTreeMapProvider();
      double rootValue = provider.getDoubleValue(value);
      if (rootValue >= m_maxValue) {
        m_maxValue = rootValue;
      }

      if (rootValue <= m_minValue) {
        m_minValue = rootValue;
      }
    } else {
      for (TreeMapNode node : root.getChildren()) {
        setValues(node);
      }
    }
  }

  /**
   * Given a value, maps that value to a new value using the specified math
   * function
   *
   * @param value the value to convert
   * @return the converted value
   */
  private double adjustValue(double value) {
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

  private Color getBackground(double val) {
    double maxValue = m_maxValue;
    double minValue = m_minValue;
    if (val >= 0) {
      // Value is greater than 0, use the positive colors
      double range = maxValue - Math.max(0, minValue);
      val -= Math.max(0, minValue);
      range = adjustValue(range);
      java.awt.Color cc = new java.awt.Color(java.awt.Color.HSBtoRGB(
          m_positiveHue, m_positiveSaturation,
          (float)(adjustValue(val) / range)));
      return ResourceManager.getColor(cc.getRed(), cc.getGreen(), cc.getBlue());
    }

    // Value is less than 0, use the negative colors
    double range = Math.abs(minValue - Math.min(0, maxValue));
    val += Math.min(0, maxValue);
    val = Math.abs(val);
    // Value and range are not positive values, we need them to be for the math
    // functions
    range = adjustValue(range);
    java.awt.Color cc = new java.awt.Color(java.awt.Color.HSBtoRGB(
        m_negativeHue, m_negativeSaturation, (float)(adjustValue(val) / range)));
    return ResourceManager.getColor(cc.getRed(), cc.getGreen(), cc.getBlue());
  }

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

  private class Legend extends Canvas {

    /**
     * Constructor
     * @param parent parent Composite
     * @param style style
     */
    public Legend(Composite parent, int style) {
      super(parent, style);

      addPaintListener(new PaintListener() {
        public void paintControl(PaintEvent e) {
          Legend.this.paintControl(e);
        }
      });
    }

    @Override
    public Point computeSize(int wHint, int hHint, boolean changed) {
      int height = 20;
      if (hHint != SWT.DEFAULT)
        height = hHint;
      return new Point(wHint, height);
    }

    protected void paintControl(PaintEvent e) {
      GC gc = e.gc;
      int width = this.getBounds().width;
      double step = (m_maxValue - m_minValue) / width;
      double value = m_minValue;
      for (int i = 0; i < width; i++) {
        gc.setBackground(HSBTreeMapColorProvider.this.getBackground(value));
        gc.fillRectangle(i, 0, 1, this.getBounds().height);
        value += step;
      }
    }
  }
}
