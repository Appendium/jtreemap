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

import net.sf.jtreemap.swttreemap.TreemapViewer;

import org.eclipse.draw2d.Border;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.SimpleRaisedBorder;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public class DemoFigure extends Figure {
	private TreeMapNode node;
	private TreemapViewer viewer;
	
	public DemoFigure(final TreeMapNode node, final TreemapViewer viewer) {
		this.node = node;
		this.viewer = viewer;
		
		Border border = new SimpleRaisedBorder();
		setBorder(border);

		Figure toolTipFigure = new Figure() {
			public void paintFigure(Graphics g) {
				g.drawRectangle(bounds.x, bounds.y, bounds.width - 1, bounds.height - 1);
				setBackgroundColor(Display.getCurrent().getSystemColor(SWT.COLOR_YELLOW));
				g.fillRectangle(bounds);
				
				g.drawText(node.getLabel(), bounds.x + bounds.width / 4, bounds.y + bounds.height / 4);
			}
		};
		toolTipFigure.setSize(200, 60);
		this.setToolTip(toolTipFigure);
		
		addMouseListener(new MouseListener() {
			public void mouseDoubleClicked(org.eclipse.draw2d.MouseEvent me) {
				// Do nothing
			}

			public void mousePressed(org.eclipse.draw2d.MouseEvent me) {
				// Do nothing
			}
			
			public void mouseReleased(org.eclipse.draw2d.MouseEvent me) {
				if (me.button == 3) {
					MenuManager manager = new MenuManager();
					
					Action editPropertiesAction = new Action() {
						public void run() {
							// Zoom in to level
							MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Info", "Action performed");
						}
					};
					editPropertiesAction.setText("Perform Action X on " + node.getLabel());
					manager.add(editPropertiesAction);
					
				    // Separator
				    String id = "separator";
				    manager.add(new Separator(id));

				    // Parents
				    boolean zoomingOut = false;
				    TreeMapNode thisNode = DemoFigure.this.node;
				    while (thisNode != null) {
				    	if (thisNode == viewer.getInput()) {
				    		zoomingOut = true;
				    	} else {
				    		// Zoom in and out icons are provided by the TreeMap plug-in.
				    		ImageDescriptor imageDescriptor = AbstractUIPlugin.imageDescriptorFromPlugin(
				    				"net.sf.jtreemap.swttreemap",
									zoomingOut ? "icons/unzoom.gif" : "icons/zoom.gif");
							ZoomAction action = new ZoomAction(imageDescriptor, thisNode, viewer);
				    		manager.insertBefore(id, action);
				    	}
				    	
				    	thisNode = thisNode.getParent();
				    }

				    Menu menu = manager.createContextMenu(viewer.getControl());						
//					menu.setLocation(me.x, me.y);
					menu.setVisible(true);
				}
			}
		});
		
	}

	public void paintFigure(Graphics g) {
		IColorProvider colorProvider = viewer.getColorProvider();
		setForegroundColor(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
		g.drawRectangle(bounds.x, bounds.y, bounds.width - 1, bounds.height - 1);
		setBackgroundColor(colorProvider.getBackground(node));
		g.fillRectangle(bounds);
		setForegroundColor(colorProvider.getForeground(node));
		g.drawText(node.getLabel(), bounds.x + bounds.width / 4, bounds.y + bounds.height / 4);
	}

	private class ZoomAction extends Action {
		private TreeMapNode node;
		private Viewer viewer;
		
		/**
		 * Constructor
		 *
		 * @param text text of the action
		 * @param image image
		 * @param node destination TreeMapNode of the zoom
		 * @param figure 
		 */
		public ZoomAction(ImageDescriptor image, TreeMapNode node, Viewer viewer) {
			super(node.getLabel(), image);
			this.node = node;
			this.viewer = viewer;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see org.eclipse.jface.action.Action#run()
		 */
		@Override
		public void run() {
			viewer.setInput(node);
		}
	}
}
