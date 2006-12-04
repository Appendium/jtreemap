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
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;

import javax.swing.JApplet;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import net.sf.jtreemap.swing.ColorProvider;
import net.sf.jtreemap.swing.JTreeMap;
import net.sf.jtreemap.swing.SplitBySortedWeight;
import net.sf.jtreemap.swing.TreeMapNode;
import net.sf.jtreemap.swing.provider.HSBTreeMapColorProvider;
import net.sf.jtreemap.swing.provider.RandomColorProvider;
import net.sf.jtreemap.swing.provider.RedGreenColorProvider;
import net.sf.jtreemap.swing.provider.ZoomPopupMenu;

/**
 * Test of JTreeMap in a JApplet.
 * Accepts 3 parameters;
 * "dataFile" which is the path to data file relative to code base.
 * "dataFileType" is either dt3 or xml.
 * "showTM3CTonf", true if the tm3 configuration panel should be shown for tm3 files. 
 * 
 * @author Laurent Dutheil
 */
public class JTreeMapAppletExample extends JApplet {

    private static final double CONSTRAINT_WEIGHTX = 0.5;

    private static final String XML = "xml";

    private static final String TM3 = "tm3";

    private static final int DEFAULT_FONT_SIZE = 12;

    private static final long serialVersionUID = -8376357344981512167L;

    private JTreeMap jTreeMap;

    private javax.swing.JPanel jContentPane = null;

    private JComboBox cmbValue;

    private JComboBox cmbWeight;

    private JPanel panelTM3;

    private BuilderTM3 builderTM3;

    private boolean showTM3CTonf;

    /**
     * This is the default constructor
     */
    public JTreeMapAppletExample() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.applet.Applet#start()
     */
    @Override
    public void start() {
        super.start();
        final String dataFile = getParameter("dataFile");
        final String dataFileType = getParameter("dataFileType");
        TreeMapNode root = null;
        if(TM3.equalsIgnoreCase(dataFileType)) {
            try {
                builderTM3 = new BuilderTM3(createReader(dataFile));
                root = builderTM3.getRoot();
                if (showTM3CTonf) {
                    setTM3Fields();
                    panelTM3.setVisible(true);
                }
            } catch (final IOException e) {
                root = handleException(e);
            }
        } else if(XML.equalsIgnoreCase(dataFileType)) {
            try {
                final URL url = new URL(getCodeBase() + dataFile);
                final URLConnection connection = url.openConnection();
                final BuilderXML bXml = new BuilderXML(connection.getInputStream());
                root = bXml.getRoot();
            } catch (final ParseException e) {
                root = handleException(e);
            } catch (final MalformedURLException e) {
                root = handleException(e);
            } catch (final IOException e) {
                root = handleException(e);
            }
        } else {
            root = DemoUtil.buildDemoRoot();
        }

        this.jTreeMap = new JTreeMap(root, new SplitBySortedWeight());
        this.jTreeMap.setFont(new Font(null, Font.BOLD, DEFAULT_FONT_SIZE));
        
        final String colourProvider = getParameter("colorProvider");
        
        ColorProvider colourProviderInstance = null;
        if ("Random".equalsIgnoreCase(colourProvider)) {
            colourProviderInstance = new RandomColorProvider(this.jTreeMap);
        } else if ("HSBLinear".equalsIgnoreCase(colourProvider)) {
            colourProviderInstance = new HSBTreeMapColorProvider(jTreeMap,
                  HSBTreeMapColorProvider.ColorDistributionTypes.Linear, Color.GREEN, Color.RED);
        } else if ("HSBLog".equalsIgnoreCase(colourProvider)) {
            colourProviderInstance = new HSBTreeMapColorProvider(jTreeMap, HSBTreeMapColorProvider.ColorDistributionTypes.Log,
                  Color.GREEN, Color.RED);
        } else if ("HSBSquareRoot".equalsIgnoreCase(colourProvider)) {
            colourProviderInstance = new RandomColorProvider(this.jTreeMap);
        } else if ("HSBCubicRoot".equalsIgnoreCase(colourProvider)) {
            colourProviderInstance = new HSBTreeMapColorProvider(jTreeMap,
                  HSBTreeMapColorProvider.ColorDistributionTypes.CubicRoot, Color.GREEN, Color.RED);
        } else if ("HSBExp".equalsIgnoreCase(colourProvider)) {
            colourProviderInstance = new HSBTreeMapColorProvider(jTreeMap,
                  HSBTreeMapColorProvider.ColorDistributionTypes.Exp, Color.GREEN, Color.RED);
        }
        
        if (colourProviderInstance == null) {
            colourProviderInstance = new RedGreenColorProvider(this.jTreeMap);
        }
        
        this.jTreeMap.setColorProvider(colourProviderInstance);

        // Add a popupMenu to zoom
        new ZoomPopupMenu(this.jTreeMap, true);

        getJContentPane().add(this.jTreeMap, BorderLayout.CENTER);
    }

    /**
     * @param dataFile
     * @return
     * @throws IOException
     */
    private BufferedReader createReader(final String dataFile) throws IOException {
        final URL url = new URL(getCodeBase() + dataFile);
        final URLConnection connection = url.openConnection();
        final BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream()));
        return reader;
    }

    private TreeMapNode handleException(final Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, e.getMessage(), "File error", JOptionPane.ERROR_MESSAGE);
        return DemoUtil.buildDemoRoot();
    }

    /**
     * Add a pane to choose the weight and the value for TM3 file
     */
    private void addPanelEast(final Container parent) {
        GridBagConstraints gridBagConstraints;
        panelTM3 = new JPanel();
        parent.add(this.panelTM3, BorderLayout.EAST);

        final JPanel choicePanel = new JPanel();
        choicePanel.setLayout(new java.awt.GridBagLayout());
        choicePanel.setBorder(new TitledBorder("Choose the TM3 fields"));
        panelTM3.add(choicePanel);

        final JLabel lblWeight = new JLabel(" weight : ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        choicePanel.add(lblWeight, gridBagConstraints);

        cmbWeight = new JComboBox();
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = CONSTRAINT_WEIGHTX;
        choicePanel.add(this.cmbWeight, gridBagConstraints);
        cmbWeight.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                final JComboBox cmb = (JComboBox) e.getSource();
                final String field = (String) cmb.getSelectedItem();
                JTreeMapAppletExample.this.builderTM3.setWeights(field);
                JTreeMapAppletExample.this.repaint();
            }
        });

        final JLabel lblValue = new JLabel(" value : ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weighty = 1.0;
        choicePanel.add(lblValue, gridBagConstraints);

        cmbValue = new JComboBox();
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = CONSTRAINT_WEIGHTX;
        gridBagConstraints.weighty = 1.0;
        choicePanel.add(this.cmbValue, gridBagConstraints);
        cmbValue.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                final JComboBox cmb = (JComboBox) e.getSource();
                final String field = (String) cmb.getSelectedItem();
                JTreeMapAppletExample.this.builderTM3.setValues(field);
//              createColorProviders();
//              updateLegendPanel();
                JTreeMapAppletExample.this.repaint();
            }
        });

        panelTM3.setVisible(false);
    }

    private void setTM3Fields() {
        final String[] numberFields = builderTM3.getNumberFields();
        final String[] cmbValues = new String[numberFields.length + 1];
        cmbValues[0] = "";
        for (int i = 1; i < cmbValues.length; i++) {
            cmbValues[i] = numberFields[i - 1];
        }
        cmbWeight.removeAllItems();
        cmbValue.removeAllItems();
        for (final String item : cmbValues) {
            cmbWeight.addItem(item);
            cmbValue.addItem(item);
        }
    }

    /**
     * This method initializes this
     */
    @Override
    public void init() {
        // Width and height params are mandatory for an applet/object element in html
        // and should be defined via html.
        // this.setSize(APPLET_WIDTH, APPLET_HEIGHT);
        this.setContentPane(getJContentPane());
        showTM3CTonf = "true".equalsIgnoreCase(getParameter("showTM3Conf"));
        if (showTM3CTonf) {
            addPanelEast(getJContentPane());
        }
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
