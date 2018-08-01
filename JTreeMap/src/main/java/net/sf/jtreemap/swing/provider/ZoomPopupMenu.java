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
 * $Id: ColorProvider.java 69 2006-10-24 16:20:20Z benoitx $
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
package net.sf.jtreemap.swing.provider;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JPopupMenu;

import net.sf.jtreemap.swing.JTreeMap;
import net.sf.jtreemap.swing.TreeMapNode;

/**
 * PopupMenu which permits to zoom the JTreeMap<BR>
 * The menuItems are the ancestors and the children of the displayed TreeMapNode
 * of the JTreeMap
 *
 * @author Laurent Dutheil
 */
public class ZoomPopupMenu extends JPopupMenu {
    private static final long serialVersionUID = 8468224816342601183L;

    /**
     * Unzoom icon
     */
    public static final Icon UNZOOM_ICON = new ImageIcon(ZoomPopupMenu.class.getResource("icons/unzoom.png"));

    /**
     * Zoom icon
     */
    public static final Icon ZOOM_ICON = new ImageIcon(ZoomPopupMenu.class.getResource("icons/zoom.png"));

    private final JTreeMap jTreeMap;

    private transient final MouseListener mouseListener;

    private final boolean showAbout;

    /**
     * Constructor
     *
     * @param jTreeMap
     *            jTreeMap which you want to add a zoom popup menu
     */
    public ZoomPopupMenu(final JTreeMap jTreeMap) {
        this(jTreeMap, false);
    }

    public ZoomPopupMenu(final JTreeMap jTreeMap, final boolean showAbout) {
        super();
        this.jTreeMap = jTreeMap;
        this.mouseListener = new HandleClickMouse();
        this.jTreeMap.addMouseListener(this.mouseListener);
        this.showAbout = showAbout;
    }

    protected class HandleClickMouse extends MouseAdapter {
        @Override
        public void mouseClicked(final MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON3
                    || e.getButton() == MouseEvent.BUTTON1 && (e.isAltDown() || e.isControlDown() || e.isMetaDown() || e.isAltGraphDown())) {

                for (int i = ZoomPopupMenu.this.getComponentCount(); i > 0; i--) {
                    ZoomPopupMenu.this.remove(i - 1);
                }

                final TreeMapNode orig = ZoomPopupMenu.this.jTreeMap.getDisplayedRoot();

                TreeMapNode cursor = orig;
                // Parents
                while (cursor.getParent() != null) {
                    final TreeMapNode parent = (TreeMapNode) cursor.getParent();
                    final ZoomAction action = new ZoomAction(jTreeMap, parent, UNZOOM_ICON);
                    ZoomPopupMenu.this.insert(action, 0);
                    cursor = parent;
                }
                if (orig.getParent() != null) {
                    // Separator
                    ZoomPopupMenu.this.addSeparator();
                }
                // children
                cursor = orig;
                while (cursor.getChild(e.getX(), e.getY()) != null) {
                    final TreeMapNode child = cursor.getChild(e.getX(), e.getY());
                    if (!child.isLeaf()) {
                        final ZoomAction action = new ZoomAction(jTreeMap, child, ZOOM_ICON);
                        ZoomPopupMenu.this.add(action);
                    }
                    cursor = child;
                }

                if (showAbout) {
                    // Separator
                    ZoomPopupMenu.this.addSeparator();
                    final AboutAction action = new AboutAction();
                    ZoomPopupMenu.this.add(action);
                }

                ZoomPopupMenu.this.show(e.getComponent(), e.getX(), e.getY());
            }
        }
    }
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
