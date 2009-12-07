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
package net.sf.jtreemap.swtdemo;

import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.swt.graphics.Color;

public class DemoTreemapColorProvider implements IColorProvider {

	private HSBTreeMapColorProvider hsbColorProvider;
	
	public DemoTreemapColorProvider(HSBTreeMapColorProvider hsbColorProvider) {
		this.hsbColorProvider = hsbColorProvider;
	}
	
	public Color getBackground(Object element) {
		return hsbColorProvider.getBackground(calculateValue(element));
	}

	public Color getForeground(Object element) {
		return hsbColorProvider.getForeground(calculateValue(element));
	}

	private double calculateValue(Object element) {
		TreeMapNode node = (TreeMapNode)element;
		
		/*
		 * Only leaves have values.  However when a branch is so small that its contents
		 * cannot be displayed we need to provide a background color.  We do this by taking
		 * a weighted average of all its children's values.
		 */
		Totals totals = new Totals();
		accumulateValues(node, totals);
		double value = totals.accumulatedWeightedValue / totals.accumulatedWeight;
		return value;
	}

	class Totals {
		double accumulatedWeight = 0;
		double accumulatedWeightedValue = 0;
	}
	
	private void accumulateValues(TreeMapNode node, Totals totals) {
		if (node.isLeaf()) {
			double value;
			if (node.getValue() instanceof XMLBean) {
				value = ((XMLBean)node.getValue()).getValue();
			} else {
				value = 0; // No value for this input?
			}
			totals.accumulatedWeight += node.getWeight();
			totals.accumulatedWeightedValue += value * node.getWeight();
		} else {
			for (TreeMapNode child : node.getChildren()) {
				accumulateValues(child, totals);
			}
		}
	}
}
