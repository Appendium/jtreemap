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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.draw2d.AbstractLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Locator;
import org.eclipse.draw2d.geometry.Dimension;

/**
 * Lays out the child figures into a treemap.  Every child must have
 * a constraint set and that constraint must be a Double which contains
 * the weight of that figure.
 */
public class TreeMapLayout extends AbstractLayout 
{
	private SplitStrategy strategy;
	private Map<IFigure, Double> constraints = new HashMap<IFigure, Double>();

	public TreeMapLayout(SplitStrategy strategy) {
		this.strategy = strategy;
	}

	/**
	 * Calculates the preferred size of the given Figure.
	 * For the DelegatingLayout, this is the largest width and height
	 * values of the passed Figure's children.
	 * 
	 * @param parent the figure whose preferred size is being calculated
	 * @param wHint the width hint
	 * @param hHint the height hint
	 * @return the preferred size
	 * @since 2.0
	 */
	protected Dimension calculatePreferredSize(IFigure parent, int wHint, int hHint) {
		/*
		 * There is no real concept of a preferred size of a tree map.  We could think
		 * up a complicated definition where the size would tend to be bigger for more
		 * complicated maps.  However we just return a fixed size.
		 */
		return new Dimension(500, 500);
	}

	/**
	 * @see org.eclipse.draw2d.LayoutManager#getConstraint(org.eclipse.draw2d.IFigure)
	 */
	public Object getConstraint(IFigure child) {
		return constraints.get(child);
	}

	/**
	 * Lays out the given figure's children based on their {@link Locator} constraint.
	 * @param parent the figure whose children should be laid out
	 */
	public void layout(IFigure parent) {
		List<TreeMapNode2> childNodes = new ArrayList<TreeMapNode2>();
		for (Object childObject : parent.getChildren()) {
			IFigure childFigure = (IFigure)childObject;
			TreeMapNode2 node = new TreeMapNode2(childFigure, constraints.get(childFigure));
			childNodes.add(node);
		}

		if (!childNodes.isEmpty()) {
			strategy.calculatePositionsRec(parent.getClientArea(), strategy.sumWeight(childNodes),
					childNodes);
		}
	}

	/**
	 * Removes the locator for the given figure.
	 * @param child the child being removed
	 */
	public void remove(IFigure child) {
		constraints.remove(child);
	}

	/**
	 * Sets the constraint for the given figure.
	 * @param figure the figure whose constraint is being set
	 * @param constraint the new constraint
	 */
	@Override
	public void setConstraint(IFigure figure, Object constraint) {
		super.setConstraint(figure, constraint);
		if (constraint != null) {
			constraints.put(figure, (Double)constraint);
		} else {
			constraints.remove(figure);
		}
	}
}
