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
 * $Id: HSBTreeMapColorProvider.java 75 2006-10-24 23:00:51Z benoitx $
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
package net.sf.jtreemap.swtdemo;

import net.sf.jtreemap.swttreemap.TreemapViewer;

import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

/**
 * An HSB color space color provider for KTreeMap. Uses a specified function to
 * map the values onto the HSB color space. The default is a linear function,
 * but in my experience one of the logarithmic ones works best for this color
 * space.
 *
 * @author Andy Adamczak
 */
public class HSBTreeMapColorProvider {
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
  public HSBTreeMapColorProvider(Color color) {
    this(ColorDistributionTypes.Linear, color, color);
  }

  /**
   * @param treeMap
   * @param positiveColor
   * @param negativeColor
   */
  public HSBTreeMapColorProvider(Color positiveColor,
      Color negativeColor) {
    this(ColorDistributionTypes.Linear, positiveColor, negativeColor);
  }

  /**
   * @param treeMap
   * @param colorDistribution
   * @param color
   */
  public HSBTreeMapColorProvider(TreemapViewer treeMap,
      ColorDistributionTypes colorDistribution, Color color) {
    this(colorDistribution, color, color);
  }

  /**
   * @param treeMap
   * @param colorDistribution
   * @param positiveColor
   * @param negativeColor
   */
  public HSBTreeMapColorProvider(
      ColorDistributionTypes colorDistribution, Color positiveColor,
      Color negativeColor) {
    super();
    m_colorDistribution = colorDistribution;
    adjustColor(positiveColor, negativeColor);
  }

  /**
   * @param treeMap
   * @param colorDistribution
   * @param hue
   * @param saturation
   */
  public HSBTreeMapColorProvider(TreemapViewer treeMap,
      ColorDistributionTypes colorDistribution, float hue, float saturation) {
    this(colorDistribution, hue, saturation, hue, saturation);
  }

  /**
   * @param treeMap
   * @param colorDistribution
   * @param positiveHue
   * @param positiveSaturation
   * @param negativeHue
   * @param negativeSaturation
   */
  public HSBTreeMapColorProvider(
      ColorDistributionTypes colorDistribution, float positiveHue,
      float positiveSaturation, float negativeHue, float negativeSaturation) {
    super();
    m_colorDistribution = colorDistribution;
    adjustColor(positiveHue, positiveSaturation, negativeHue,
        negativeSaturation);
  }

  /**
   * @param treeMap
   * @param hue
   * @param saturation
   */
  public HSBTreeMapColorProvider(float hue, float saturation) {
    this(ColorDistributionTypes.Linear, hue, saturation, hue,
        saturation);
  }

  /**
   * @param treeMap
   * @param positiveHue
   * @param positiveSaturation
   * @param negativeHue
   * @param negativeSaturation
   */
  public HSBTreeMapColorProvider(float positiveHue,
      float positiveSaturation, float negativeHue, float negativeSaturation) {
    this(ColorDistributionTypes.Linear, positiveHue,
        positiveSaturation, negativeHue, negativeSaturation);
  }

  public void setRange(double minValue, double maxValue) {
	  this.m_minValue = minValue;
	  this.m_maxValue = maxValue;
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

  public Color getForeground(Object value) {
    return Display.getDefault().getSystemColor(SWT.COLOR_WHITE);
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

  public Color getBackground(double val) {
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

//public Color getBackground(Object element) {
//	if (element instanceof XMLBean) {
//		XMLBean bean = (XMLBean)element;
//		return getBackground(bean.getValue()); 
//	}
//	if (element instanceof TM3Bean) {
//		TM3Bean bean = (TM3Bean)element;
//		return getBackground(bean.getValue("value")); 
//	}
//	return null;
//}

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
