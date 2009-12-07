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
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.jface.viewers.ITreeContentProvider;

/**
 * This figure creates children lazily.  They are created when this
 * figure is painted.
 * 
 * @author Nigel Westbury
 *
 */
public class TreemapFigure extends Figure {
	private TreemapViewer viewer;
	private Object element;

	public TreemapFigure(TreemapViewer viewer, Object element) {
		this.viewer = viewer;
		this.element = element;
		TreeMapLayout layout = new TreeMapLayout(viewer.getSplitStrategy());
		setLayoutManager(layout);
		setOpaque(true);
	}

	@Override
	public void layout() {
		/*
		 * If children have not yet been added, add them now.
		 */
		if (getChildren().isEmpty()) {
			ITreeContentProvider contentProvider = (ITreeContentProvider)viewer.getContentProvider();
			ITreemapLabelProvider labelProvider = (ITreemapLabelProvider)viewer.getLabelProvider();

			for (Object childElement : contentProvider.getChildren(element)) {
				if (contentProvider.hasChildren(childElement)) {
					Border border = labelProvider.getBorder(childElement);
					IFigure expandedFigure = new TreemapFigure(viewer, childElement);
					expandedFigure.setBorder(border);

					IFigure collapsedFigure = labelProvider.getFigure(childElement);

					add(new ExpandableFigure(collapsedFigure, expandedFigure), labelProvider.getWeighting(childElement));
				} else {
					IFigure childFigure = labelProvider.getFigure(childElement);
					add(childFigure, labelProvider.getWeighting(childElement));
				}
			}
		}

		super.layout();
	}
}

