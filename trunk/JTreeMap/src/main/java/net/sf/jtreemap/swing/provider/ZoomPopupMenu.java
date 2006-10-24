/*
 * Created on 28 oct. 2005
 */
package net.sf.jtreemap.swing.provider;

import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.AbstractAction;
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

    private JTreeMap jTreeMap;

    private transient MouseListener mouseListener;

    /**
     * Constructor
     * 
     * @param jTreeMap
     *            jTreeMap which you want to add a zoom popup menu
     */
    public ZoomPopupMenu(final JTreeMap jTreeMap) {
        super();
        this.jTreeMap = jTreeMap;
        this.mouseListener = new HandleClickMouse();
        this.jTreeMap.addMouseListener(this.mouseListener);
    }

    protected class HandleClickMouse extends MouseAdapter {

        /*
         * (non-Javadoc)
         * 
         * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
         */
        @Override
        public void mouseClicked(final MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON3
                    || (e.getButton() == MouseEvent.BUTTON1 && (e.isAltDown() || e.isControlDown() 
                            || e.isMetaDown() || e.isAltGraphDown()))) {

                for (int i = ZoomPopupMenu.this.getComponentCount(); i > 0; i--) {
                    ZoomPopupMenu.this.remove(i - 1);
                }

                final TreeMapNode orig = ZoomPopupMenu.this.jTreeMap.getDisplayedRoot();

                TreeMapNode cursor = orig;
                // Parents
                while (cursor.getParent() != null) {
                    final TreeMapNode parent = (TreeMapNode) cursor.getParent();
                    final ZoomAction action = new ZoomAction(parent, UNZOOM_ICON);
                    ZoomPopupMenu.this.insert(action, 0);
                    cursor = parent;
                }
                // Separator
                ZoomPopupMenu.this.addSeparator();

                // children
                cursor = orig;
                while (cursor.getChild(e.getX(), e.getY()) != null) {
                    final TreeMapNode child = cursor.getChild(e.getX(), e.getY());
                    if (!child.isLeaf()) {
                        final ZoomAction action = new ZoomAction(child, ZOOM_ICON);
                        ZoomPopupMenu.this.add(action);
                    }
                    cursor = child;
                }

                ZoomPopupMenu.this.show(e.getComponent(), e.getX(), e.getY());
            }
        }

    }

    private class ZoomAction extends AbstractAction {
        private static final long serialVersionUID = -8559400865920393294L;

        private TreeMapNode node;

        /**
         * Constructor
         * 
         * @param node
         *            where you want to zoom/unzoom
         * @param icon
         *            icon corresponding to the operation (zoom or unzoom)
         */
        public ZoomAction(final TreeMapNode node, final Icon icon) {
            super(node.getLabel(), icon);
            this.node = node;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(final ActionEvent e) {
            ZoomPopupMenu.this.jTreeMap.zoom(this.node);
            ZoomPopupMenu.this.jTreeMap.repaint();

        }

        /*
         * (non-Javadoc)
         * 
         * @see javax.swing.Action#isEnabled()
         */
        @Override
        public boolean isEnabled() {
            return true;
        }

    }
}
