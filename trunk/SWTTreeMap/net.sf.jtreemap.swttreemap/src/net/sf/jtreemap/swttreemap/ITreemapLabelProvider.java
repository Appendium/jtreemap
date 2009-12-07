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
 * Copyright 2009 Nigel Westbury and contributors
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
package net.sf.jtreemap.swttreemap;

import org.eclipse.draw2d.Border;
import org.eclipse.draw2d.IFigure;
import org.eclipse.jface.viewers.IBaseLabelProvider;

/**
 * Extends <code>IBaseLabelProvider</code> with the methods to provide the
 * <code>IFigure</code> or <code>Border</code> to be used to render the element
 * and size weighting for the element. Used by Treemap viewers.
 */
public interface ITreemapLabelProvider extends IBaseLabelProvider {
    /**
	 * Returns a figure to use to display this item.
	 * <P>
	 * This method will always be called when a leaf element is rendered.
	 * Normally <code>getBorder</code> will be called for elements that have
	 * children and the child elements will be shown in a nested treemap inside
	 * the border. However this method will be called for elements with children
	 * if there is insufficient space to sensibly show the children. Therefore
	 * this method should return a figure for all possible elements including
	 * the root element (the input to the viewer).
	 * <P>
	 * The figure can be be any draw2d IFigure implementation but it would
	 * typically have a border so it can be distinguished from its neighbors.
	 * 
	 * @param element
	 *            the element for which to provide the label image
	 * @return the figure to be used to render the element, which cannot be
	 *         <code>null</code>
	 */
    public IFigure getFigure(Object element);

    /**
	 * Returns a border to use when displaying this item.
	 * <P>
	 * This method will be called for an element only if that element has child
	 * elements. The child elements will be shown in a treemap inside the
	 * border.
	 * <P>
	 * Note that a single instance of a <code>Border</code> object can be used
	 * to render multiple figures. Therefore label providers will typically
	 * cache a border to be returned for multiple elements.
	 * 
	 * @param element
	 *            the element for which to provide the border
	 * @return the border to be used when rendering the element, which cannot be
	 *         <code>null</code>
	 */
    public Border getBorder(Object element);

    /**
     * Returns the 'size' of the given element.  This is a weighting,
     * so only the ratio of sizes of elements within the same parent
     * matter.  All elements with the same parent will have areas that
     * are in proportion to their 'sizes'.
     *
     * @param element the element for which to provide the weighting
     * @return the weighting for this element, which must be a positive
     * 		number
     */
    public double getWeighting(Object element);
}
