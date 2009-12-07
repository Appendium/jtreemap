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
 * $Id: DemoTreemapLabelProvider.java 75 2006-10-24 23:00:51Z benoitx $
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

import net.sf.jtreemap.swttreemap.ITreemapLabelProvider;
import net.sf.jtreemap.swttreemap.TreemapViewer;

import org.eclipse.draw2d.Border;
import org.eclipse.draw2d.GroupBoxBorder;
import org.eclipse.draw2d.IFigure;
import org.eclipse.jface.viewers.BaseLabelProvider;


/**
 * ITreemapLabelProvider for a node tree that has been built in memory
 */
public class DemoTreemapLabelProvider extends BaseLabelProvider implements ITreemapLabelProvider {

	private TreemapViewer viewer;
	
	DemoTreemapLabelProvider(TreemapViewer viewer) {
		this.viewer = viewer;
	}
	
	public Border getBorder(Object element) {
		TreeMapNode node = (TreeMapNode)element;
		return new GroupBoxBorder(node.getLabel());
	}

	public IFigure getFigure(Object element) {
		TreeMapNode node = (TreeMapNode)element;
		return new DemoFigure(node, viewer);
	}

	public double getWeighting(Object element) {
		TreeMapNode node = (TreeMapNode)element;
		return node.getWeight();
	}
}
