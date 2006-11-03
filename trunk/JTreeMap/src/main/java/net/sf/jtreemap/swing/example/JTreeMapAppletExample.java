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
 * $Id$
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
package net.sf.jtreemap.swing.example;

import java.awt.BorderLayout;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;

import javax.swing.JApplet;
import javax.swing.JOptionPane;

import net.sf.jtreemap.swing.JTreeMap;
import net.sf.jtreemap.swing.SplitBySortedWeight;
import net.sf.jtreemap.swing.TreeMapNode;
import net.sf.jtreemap.swing.provider.RedGreenColorProvider;
import net.sf.jtreemap.swing.provider.ZoomPopupMenu;

/**
 * Test of JTreeMap in a JApplet
 * 
 * @author Laurent Dutheil
 */
public class JTreeMapAppletExample extends JApplet {
	
    private static final String XML = "xml";

	private static final String TM3 = "tm3";

	private static final int APPLET_HEIGHT = 400;

    private static final int APPLET_WIDTH = 600;

    private static final int DEFAULT_FONT_SIZE = 16;

    private static final long serialVersionUID = -8376357344981512167L;

    private JTreeMap jTreeMap;

    private javax.swing.JPanel jContentPane = null;

    /**
     * This is the default constructor
     */
    public JTreeMapAppletExample() {
        super();
        init();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.applet.Applet#start()
     */
    @Override
    public void start() {
        super.start();
        String dataFile = getParameter("dataFile");
        String dataFileType = getParameter("dataFileType");
        TreeMapNode root = null;
        if(TM3.equalsIgnoreCase(dataFileType)) {
            try {
            	BuilderTM3 builderTM3 = new BuilderTM3(new File(getCodeBase() + dataFile));
                root = builderTM3.getRoot();
//                setTM3Fields();
//                panelTM3.setVisible(true);
            } catch (final IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, e.getMessage(), "File error", JOptionPane.ERROR_MESSAGE);
            }
        } else if(XML.equalsIgnoreCase(dataFileType)) {
            try {
                final BuilderXML bXml = new BuilderXML(getCodeBase() + dataFile);
                root = bXml.getRoot();
            } catch (final ParseException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, e.getMessage(), "File error", JOptionPane.ERROR_MESSAGE);
            }

        } else {
        	root = DemoUtil.buildDemoRoot();
        }
        
        this.jTreeMap = new JTreeMap(root, new SplitBySortedWeight());
        this.jTreeMap.setFont(new Font(null, Font.BOLD, DEFAULT_FONT_SIZE));
        this.jTreeMap.setColorProvider(new RedGreenColorProvider(this.jTreeMap));

        // Add a popupMenu to zoom
        new ZoomPopupMenu(this.jTreeMap);

        getJContentPane().add(this.jTreeMap, BorderLayout.CENTER);

    }

    /**
     * This method initializes this
     */
    @Override
    public void init() {
        this.setSize(APPLET_WIDTH, APPLET_HEIGHT);
        this.setContentPane(getJContentPane());
    }

    /**
     * This method initializes jContentPane
     * 
     * @return javax.swing.JPanel
     */
    private javax.swing.JPanel getJContentPane() {
        if (this.jContentPane == null) {
            this.jContentPane = new javax.swing.JPanel();
            this.jContentPane.setLayout(new BorderLayout());
        }

        return this.jContentPane;
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
