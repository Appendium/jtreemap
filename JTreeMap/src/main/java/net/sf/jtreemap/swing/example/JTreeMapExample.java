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
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.LinkedHashMap;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.DefaultTreeModel;

import net.sf.jtreemap.swing.ColorProvider;
import net.sf.jtreemap.swing.JTreeMap;
import net.sf.jtreemap.swing.SplitByNumber;
import net.sf.jtreemap.swing.SplitBySlice;
import net.sf.jtreemap.swing.SplitBySortedWeight;
import net.sf.jtreemap.swing.SplitByWeight;
import net.sf.jtreemap.swing.SplitSquarified;
import net.sf.jtreemap.swing.SplitStrategy;
import net.sf.jtreemap.swing.TreeMapNode;
import net.sf.jtreemap.swing.provider.HSBTreeMapColorProvider;
import net.sf.jtreemap.swing.provider.RandomColorProvider;
import net.sf.jtreemap.swing.provider.RedGreenColorProvider;
import net.sf.jtreemap.swing.provider.ZoomPopupMenu;

/**
 * Test of JTreeMap
 * 
 * @author Laurent Dutheil
 */
public class JTreeMapExample extends JFrame implements ActionListener {
    private static final double CONSTRAINT_WEIGHTX = 0.5;

    private static final int SCROLLPANE_WIDTH = 140;

    private static final int APPLICATION_HEIGHT = 400;

    private static final int APPLICATION_WIDTH = 600;

    private static final int DEFAULT_FONT_SIZE = 16;

    private static final long serialVersionUID = 2813934810390001709L;

    private static final String EXIT = "Exit";

    private static final String OPEN_TM3_FILE = "Open TM3 File";

    private static final String OPEN_XML_FILE = "Open Xml File";

    private JTreeMap jTreeMap;

    private JTree treeView = new JTree();

    private BuilderTM3 builderTM3;

    private CardLayout cardLayout;

    private JComboBox cmbColorProvider;

    private JComboBox cmbStrategy;

    private JComboBox cmbValue;

    private JComboBox cmbWeight;

    private final LinkedHashMap<String, ColorProvider> colorProviders = new LinkedHashMap<String, ColorProvider>();

    private JPanel panelLegend;

    private JPanel panelTM3;

    private TreeMapNode root;

    private final LinkedHashMap<String, SplitStrategy> strategies = new LinkedHashMap<String, SplitStrategy>();

    private DefaultTreeModel treeModel;

    /**
     * Constructor
     */
    public JTreeMapExample() {
        root = DemoUtil.buildDemoRoot();

        jTreeMap = new JTreeMap(this.root, treeView);
        jTreeMap.setFont(new Font(null, Font.BOLD, DEFAULT_FONT_SIZE));
        jTreeMap.setPreferredSize(new Dimension(APPLICATION_WIDTH, APPLICATION_HEIGHT));
        jTreeMap.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

        /* uncomment if you want to keep proportions on zooming */
        // jTreeMap.setZoomKeepProportion(true);
        /*
         * uncomment if you want to change the max border between two nodes of
         * the same level
         */
        // TreeMapNode.setBorder(5);
        // add a popup menu to zoom the JTreeMap
        new ZoomPopupMenu(this.jTreeMap);

        // init GUI
        try {
            initGUI();
        } catch (final Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * main
     * 
     * @param args
     *            command line
     */
    public static void main(final String[] args) {
        final JTreeMapExample example = new JTreeMapExample();
        example.setVisible(true);
        example.pack();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(final ActionEvent e) {
        // Action performed for the File Menu @see addMenu()

        final String command = e.getActionCommand();

        if (OPEN_XML_FILE.equals(command)) {
            final JFileChooser chooser = new JFileChooser(System.getProperty("user.home"));
            final FileFilter filter = new XMLFileFilter();
            chooser.setFileFilter(filter);
            final int returnVal = chooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                setXmlFile(chooser.getSelectedFile().getPath());
            }
            // create new colorProviders for the new TreeMap
            createColorProviders();
            // then update the lengend panel
            updateLegendPanel();
        } else if (OPEN_TM3_FILE.equals(command)) {
            final JFileChooser chooser = new JFileChooser(System.getProperty("user.home"));
            final FileFilter filter = new TM3FileFilter();
            chooser.setFileFilter(filter);
            final int returnVal = chooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                setTm3File(chooser.getSelectedFile().getPath());
            }
            // create new colorProviders for the new TreeMap
            createColorProviders();
            // then update the lengend panel
            updateLegendPanel();

        } else if (EXIT.equals(command)) {
            windowClosingEvent(null);
        }
    }

    /**
     * Set the tm3 file
     * 
     * @param path
     *            the path of the tm3 file
     */
    public void setTm3File(final String path) {
        try {
            builderTM3 = new BuilderTM3(new File(path));
            root = builderTM3.getRoot();

            jTreeMap.setRoot(this.root);
            treeModel.setRoot(this.root);

            setTM3Fields();
            panelTM3.setVisible(true);
        } catch (final IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, e.getMessage(), "File error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Set the xml file corresponding to the TreeMap.dtd
     * 
     * @param xmlFileName
     *            xml file name
     */
    public void setXmlFile(final String xmlFileName) {
        try {
            final BuilderXML bXml = new BuilderXML(xmlFileName);
            root = bXml.getRoot();

            jTreeMap.setRoot(this.root);
            treeModel.setRoot(this.root);

            panelTM3.setVisible(false);
        } catch (final ParseException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, e.getMessage(), "File error", JOptionPane.ERROR_MESSAGE);
        }

    }

    /**
     * Code to execute before closing the window
     * 
     * @param e
     *            WindowEvent
     */
    protected void windowClosingEvent(final WindowEvent e) {
        System.exit(0);
    }

    private void addMenu() {
        final JMenuBar menuBar = new JMenuBar();
        final JMenu menu = new JMenu("File");

        JMenuItem item = new JMenuItem(OPEN_XML_FILE);
        item.addActionListener(this);
        item.setAccelerator(KeyStroke.getKeyStroke('O', java.awt.event.InputEvent.ALT_MASK));
        menu.add(item);

        item = new JMenuItem(OPEN_TM3_FILE);
        item.addActionListener(this);
        item.setAccelerator(KeyStroke.getKeyStroke('T', java.awt.event.InputEvent.ALT_MASK));
        menu.add(item);

        item = new JMenuItem(EXIT);
        item.setAccelerator(KeyStroke.getKeyStroke('X', java.awt.event.InputEvent.ALT_MASK));
        item.addActionListener(this);
        menu.add(item);

        menuBar.add(menu);
        setJMenuBar(menuBar);
    }

    /**
     * Add a splitPane with a treeview on the left and the JTreeMap on the right
     */
    private void addPanelCenter(final Container parent) {
        final JSplitPane splitPaneCenter = new JSplitPane();
        splitPaneCenter.setBorder(BorderFactory.createEmptyBorder());
        parent.add(splitPaneCenter, BorderLayout.CENTER);

        final JScrollPane jScrollPane1 = new JScrollPane();
        splitPaneCenter.setTopComponent(jScrollPane1);
        splitPaneCenter.setBottomComponent(this.jTreeMap);

        treeModel = new DefaultTreeModel(this.root);
        treeView = new JTree(this.treeModel);
        jScrollPane1.getViewport().add(this.treeView);
        jScrollPane1.setPreferredSize(new Dimension(SCROLLPANE_WIDTH, jTreeMap.getRoot().getHeight()));
        treeView.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(final TreeSelectionEvent e) {
                // for each selected elements ont the treeView, we zoom the
                // JTreeMap
                TreeMapNode dest = (TreeMapNode) JTreeMapExample.this.treeView.getLastSelectedPathComponent();

                // if the element is a leaf, we select the parent
                if (dest != null && dest.isLeaf()) {
                    dest = (TreeMapNode) dest.getParent();
                }
                if (dest == null) {
                    return;
                }

                JTreeMapExample.this.jTreeMap.zoom(dest);
                JTreeMapExample.this.jTreeMap.repaint();
            }
        });
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
                JTreeMapExample.this.builderTM3.setWeights(field);
                JTreeMapExample.this.repaint();
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
                JTreeMapExample.this.builderTM3.setValues(field);
                createColorProviders();
                updateLegendPanel();
                JTreeMapExample.this.repaint();
            }
        });

        panelTM3.setVisible(false);
    }

    /**
     * add a combobox with all strategies
     */
    private void addPanelNorth(final Container parent) {
        final JPanel panelNorth = new JPanel();
        panelNorth.setBorder(BorderFactory.createEmptyBorder());
        final JLabel lblStrategy = new JLabel();
        lblStrategy.setText("Strategy :");
        parent.add(panelNorth, BorderLayout.NORTH);
        panelNorth.add(lblStrategy);
        cmbStrategy = new JComboBox();
        panelNorth.add(this.cmbStrategy);

        createStrategies();

        cmbStrategy.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                updateStrategy();
            }
        });
    }

    /**
     * add a combobox with all color providers and the legend panel
     */
    private void addPanelSouth(final Container parent) {
        final JPanel southPanel = new JPanel();
        southPanel.setLayout(new BorderLayout());
        final JPanel jPanelLegendNorth = new JPanel();
        final JLabel lblColorProvider = new JLabel();
        lblColorProvider.setText("Color Provider :");
        jPanelLegendNorth.add(lblColorProvider);
        cmbColorProvider = new JComboBox();
        jPanelLegendNorth.add(this.cmbColorProvider);
        southPanel.add(jPanelLegendNorth, BorderLayout.NORTH);
        panelLegend = new JPanel();
        southPanel.add(this.panelLegend, BorderLayout.CENTER);
        parent.add(southPanel, BorderLayout.SOUTH);
        cardLayout = new CardLayout();
        panelLegend.setLayout(this.cardLayout);

        createColorProviders();

        for (final String key : colorProviders.keySet()) {
            cmbColorProvider.addItem(key);
        }

        cmbColorProvider.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                if (JTreeMapExample.this.cmbColorProvider.getSelectedIndex() > -1) {
                    updateLegendPanel();
                }
            }
        });

        cmbColorProvider.setSelectedIndex(0);
    }

    protected void createColorProviders() {
        colorProviders.put("Red Green", new RedGreenColorProvider(this.jTreeMap));
        colorProviders.put("Random", new RandomColorProvider(jTreeMap));
        colorProviders.put("HSB linear", new HSBTreeMapColorProvider(jTreeMap,
                HSBTreeMapColorProvider.ColorDistributionTypes.Linear, Color.GREEN, Color.RED));
        colorProviders.put("HSB log", new HSBTreeMapColorProvider(jTreeMap, HSBTreeMapColorProvider.ColorDistributionTypes.Log,
                Color.GREEN, Color.RED));
        colorProviders.put("HSB SquareRoot", new HSBTreeMapColorProvider(jTreeMap,
                HSBTreeMapColorProvider.ColorDistributionTypes.SquareRoot, Color.GREEN, Color.RED));
        colorProviders.put("HSB CubicRoot", new HSBTreeMapColorProvider(jTreeMap,
                HSBTreeMapColorProvider.ColorDistributionTypes.CubicRoot, Color.GREEN, Color.RED));
        colorProviders.put("HSB exp", new HSBTreeMapColorProvider(jTreeMap, HSBTreeMapColorProvider.ColorDistributionTypes.Exp,
                Color.GREEN, Color.RED));
        for (final String key : colorProviders.keySet()) {
            final ColorProvider cp = colorProviders.get(key);
            panelLegend.add(cp.getLegendPanel(), key);
        }
    }

    private void createStrategies() {
        strategies.put("Squarified", new SplitSquarified());
        strategies.put("Sorted Weight", new SplitBySortedWeight());
        strategies.put("Weight", new SplitByWeight());
        strategies.put("Slice", new SplitBySlice());
        strategies.put("Equal Weight", new SplitByNumber());
        cmbStrategy.removeAllItems();
        for (final String key : strategies.keySet()) {
            cmbStrategy.addItem(key);
        }
    }

    /**
     * init the window
     * 
     * @throws Exception
     */
    private void initGUI() throws Exception {
        setTitle("JTreeMap Example");

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent e) {
                windowClosingEvent(e);
            }
        });

        // a File menu to import other files
        addMenu();

        // panel to choose the strategy
        addPanelNorth(this.getContentPane());
        // splitPane with treeView on the left and JTreeMap on the right
        addPanelCenter(this.getContentPane());
        // panel to choose the color provider
        addPanelSouth(this.getContentPane());
        // panel to choose the fields for a TM3 file
        addPanelEast(this.getContentPane());

        // update the chosen strategy
        updateStrategy();
        // update the chosen color provider
        updateLegendPanel();
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

    protected void updateLegendPanel() {
        final String key = (String) cmbColorProvider.getSelectedItem();
        final ColorProvider cp = colorProviders.get(key);
        if (cp != null) {
            jTreeMap.setColorProvider(cp);
            cardLayout.show(this.panelLegend, key);
        }
        JTreeMapExample.this.repaint();
    }

    void updateStrategy() {
        final String key = (String) cmbStrategy.getSelectedItem();
        final SplitStrategy strat = strategies.get(key);
        jTreeMap.setStrategy(strat);
        jTreeMap.repaint();
    }

    static class TM3FileFilter extends FileFilter {
        // return true if should accept a given file
        @Override
        public boolean accept(final File f) {
            if (f.isDirectory()) {
                return true;
            }
            final String path = f.getPath().toLowerCase(Locale.getDefault());
            if (path.endsWith(".tm3")) {
                return true;
            }
            return false;
        }

        // return a description of files
        @Override
        public String getDescription() {
            return "TM3 file (*.tm3)";
        }
    }

    static class XMLFileFilter extends FileFilter {
        // return true if should accept a given file
        @Override
        public boolean accept(final File f) {
            if (f.isDirectory()) {
                return true;
            }
            final String path = f.getPath().toLowerCase(Locale.getDefault());
            if (path.endsWith(".xml")) {
                return true;
            }
            return false;
        }

        // return a description of files
        @Override
        public String getDescription() {
            return "XML file (*.xml)";
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
