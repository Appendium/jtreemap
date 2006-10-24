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
import net.sf.jtreemap.swing.TreeMapNodeBuilder;
import net.sf.jtreemap.swing.Value;

/**
 * Test of JTreeMap
 * 
 * @author Laurent Dutheil
 */
public class JTreeMapExample extends JFrame implements ActionListener {
    private static final long serialVersionUID = 2813934810390001709L;

    private static final String EXIT = "Exit";

    private static final String OPEN_TM3_FILE = "Open TM3 File";

    private static final String OPEN_XML_FILE = "Open Xml File";

    protected JTreeMap jTreeMap;

    protected JTree treeView = new JTree();

    protected ZoomPopupMenu zoomPopup;

    protected BuilderTM3 builderTM3;

    private CardLayout cardLayout;

    protected JComboBox cmbColorProvider;

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
        this.root = getDefaultRoot();

        this.jTreeMap = new JTreeMap(this.root);
        this.jTreeMap.setFont(new Font(null, Font.BOLD, 16));
        this.jTreeMap.setPreferredSize(new Dimension(600, 400));
        this.jTreeMap.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

        /* uncomment if you want to keep proportions on zooming */
        // this.jTreeMap.setZoomKeepProportion(true);
        /*
         * uncomment if you want to change the max border between two nodes of
         * the same level
         */
        // TreeMapNode.setBorder(5);
        // add a popup menu to zoom the JTreeMap
        this.zoomPopup = new ZoomPopupMenu(this.jTreeMap);

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
            this_windowClosing(null);
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
            this.builderTM3 = new BuilderTM3(new File(path));
            this.root = this.builderTM3.getRoot();

            this.jTreeMap.setRoot(this.root);
            this.treeModel.setRoot(this.root);

            setTM3Fields();
            this.panelTM3.setVisible(true);
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
            this.root = bXml.getRoot();

            this.jTreeMap.setRoot(this.root);
            this.treeModel.setRoot(this.root);

            this.panelTM3.setVisible(false);
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
    protected void this_windowClosing(final WindowEvent e) {
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
        this.setJMenuBar(menuBar);
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

        this.treeModel = new DefaultTreeModel(this.root);
        this.treeView = new JTree(this.treeModel);
        jScrollPane1.getViewport().add(this.treeView);
        jScrollPane1.setPreferredSize(new Dimension(140, this.jTreeMap.getRoot().getHeight()));
        this.treeView.addTreeSelectionListener(new TreeSelectionListener() {
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
        this.panelTM3 = new JPanel();
        parent.add(this.panelTM3, BorderLayout.EAST);

        final JPanel choicePanel = new JPanel();
        choicePanel.setLayout(new java.awt.GridBagLayout());
        choicePanel.setBorder(new TitledBorder("Choose the TM3 fields"));
        this.panelTM3.add(choicePanel);

        final JLabel lblWeight = new JLabel(" weight : ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        choicePanel.add(lblWeight, gridBagConstraints);

        this.cmbWeight = new JComboBox();
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        choicePanel.add(this.cmbWeight, gridBagConstraints);
        this.cmbWeight.addActionListener(new ActionListener() {
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

        this.cmbValue = new JComboBox();
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 1.0;
        choicePanel.add(this.cmbValue, gridBagConstraints);
        this.cmbValue.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                final JComboBox cmb = (JComboBox) e.getSource();
                final String field = (String) cmb.getSelectedItem();
                JTreeMapExample.this.builderTM3.setValues(field);
                createColorProviders();
                updateLegendPanel();
                JTreeMapExample.this.repaint();
            }
        });

        this.panelTM3.setVisible(false);
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
        this.cmbStrategy = new JComboBox();
        panelNorth.add(this.cmbStrategy);

        createStrategies();

        this.cmbStrategy.addActionListener(new ActionListener() {
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
        this.cmbColorProvider = new JComboBox();
        jPanelLegendNorth.add(this.cmbColorProvider);
        southPanel.add(jPanelLegendNorth, BorderLayout.NORTH);
        this.panelLegend = new JPanel();
        southPanel.add(this.panelLegend, BorderLayout.CENTER);
        parent.add(southPanel, BorderLayout.SOUTH);
        this.cardLayout = new CardLayout();
        this.panelLegend.setLayout(this.cardLayout);

        createColorProviders();

        for (final String key : this.colorProviders.keySet()) {
            this.cmbColorProvider.addItem(key);
        }

        this.cmbColorProvider.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                if (JTreeMapExample.this.cmbColorProvider.getSelectedIndex() > -1) {
                    updateLegendPanel();
                }
            }
        });

        this.cmbColorProvider.setSelectedIndex(0);
    }

    protected void createColorProviders() {
        this.colorProviders.put("Red Green", new RedGreenColorProvider(this.jTreeMap));
        this.colorProviders.put("Random", new RandomColorProvider(jTreeMap));
        this.colorProviders.put("HSB linear", new HSBTreeMapColorProvider(jTreeMap,
                HSBTreeMapColorProvider.ColorDistributionTypes.Linear, Color.GREEN, Color.RED));
        this.colorProviders.put("HSB log", new HSBTreeMapColorProvider(jTreeMap,
                HSBTreeMapColorProvider.ColorDistributionTypes.Log, Color.GREEN, Color.RED));
        this.colorProviders.put("HSB SquareRoot", new HSBTreeMapColorProvider(jTreeMap,
                HSBTreeMapColorProvider.ColorDistributionTypes.SquareRoot, Color.GREEN, Color.RED));
        this.colorProviders.put("HSB CubicRoot", new HSBTreeMapColorProvider(jTreeMap,
                HSBTreeMapColorProvider.ColorDistributionTypes.CubicRoot, Color.GREEN, Color.RED));
        this.colorProviders.put("HSB exp", new HSBTreeMapColorProvider(jTreeMap,
                HSBTreeMapColorProvider.ColorDistributionTypes.Exp, Color.GREEN, Color.RED));
        for (final String key : this.colorProviders.keySet()) {
            final ColorProvider cp = this.colorProviders.get(key);
            this.panelLegend.add(cp.getLegendPanel(), key);
        }
    }

    private void createStrategies() {
        this.strategies.put("Squarified", new SplitSquarified());
        this.strategies.put("Sorted Weight", new SplitBySortedWeight());
        this.strategies.put("Weight", new SplitByWeight());
        this.strategies.put("Slice", new SplitBySlice());
        this.strategies.put("Equal Weight", new SplitByNumber());
        this.cmbStrategy.removeAllItems();
        for (final String key : this.strategies.keySet()) {
            this.cmbStrategy.addItem(key);
        }
    }

    /**
     * This method build a default TreeMap root. <BR>
     * This methode may be replaced by one who get the root from a servlet or a
     * jdbc client or something else...
     * 
     * @return the root of the TreeMap.
     */
    private TreeMapNode getDefaultRoot() {
        final TreeMapNodeBuilder builder = new TreeMapNodeBuilder();

        final TreeMapNode root = builder.buildBranch("Root", null);
        final TreeMapNode tmn1 = builder.buildBranch("branch1", root);
        final TreeMapNode tmn11 = builder.buildBranch("branch11", tmn1);
        Value value = new ValuePercent(0.45);
        builder.buildLeaf("leaf111", 1.0, value, tmn11);
        value = new ValuePercent(-5.0);
        builder.buildLeaf("leaf112", 2.0, value, tmn11);
        value = new ValuePercent(2.0);
        builder.buildLeaf("leaf113", 0.5, value, tmn11);
        value = new ValuePercent(2.0);
        builder.buildLeaf("leaf114", 3.0, value, tmn11);
        value = new ValuePercent(-5.0);
        builder.buildLeaf("leaf115", 0.25, value, tmn11);
        final TreeMapNode tmn12 = builder.buildBranch("branch12", tmn1);
        value = new ValuePercent(1.0);
        builder.buildLeaf("leaf121", 1.0, value, tmn12);
        value = new ValuePercent(5.0);
        builder.buildLeaf("leaf122", 2.0, value, tmn12);
        value = new ValuePercent(2.0);
        builder.buildLeaf("leaf123", 0.5, value, tmn12);
        value = new ValuePercent(-2.0);
        builder.buildLeaf("leaf124", 3.0, value, tmn12);
        value = new ValuePercent(0.0);
        builder.buildLeaf("leaf125", 0.25, value, tmn12);
        final TreeMapNode tmn13 = builder.buildBranch("branch13", tmn1);
        value = new ValuePercent(1.0);
        builder.buildLeaf("leaf131", 1.0, value, tmn13);
        value = new ValuePercent(5.0);
        builder.buildLeaf("leaf132", 2.0, value, tmn13);
        value = new ValuePercent(2.0);
        builder.buildLeaf("leaf133", 0.5, value, tmn13);
        value = new ValuePercent(2.0);
        builder.buildLeaf("leaf134", 3.0, value, tmn13);
        value = new ValuePercent(2.0);
        builder.buildLeaf("leaf14", 3.0, value, tmn1);
        value = new ValuePercent(-5.0);
        builder.buildLeaf("leaf15", 2.0, value, tmn1);
        final TreeMapNode tmn2 = builder.buildBranch("branch2", root);
        final TreeMapNode tmn21 = builder.buildBranch("branch21", tmn2);
        value = new ValuePercent(-1.0);
        builder.buildLeaf("leaf211", 1.0, value, tmn21);
        value = new ValuePercent(-5.0);
        builder.buildLeaf("leaf212", 2.0, value, tmn21);
        value = new ValuePercent(2.0);
        builder.buildLeaf("leaf213", 0.5, value, tmn21);
        value = new ValuePercent(2.0);
        builder.buildLeaf("leaf214", 3.0, value, tmn21);
        value = new ValuePercent(5.0);
        builder.buildLeaf("leaf215", 0.25, value, tmn21);
        final TreeMapNode tmn22 = builder.buildBranch("branch22", tmn2);
        value = new ValuePercent(1.0);
        builder.buildLeaf("leaf221", 1.0, value, tmn22);
        value = new ValuePercent(5.0);
        builder.buildLeaf("leaf222", 2.0, value, tmn22);
        value = new ValuePercent(2.0);
        builder.buildLeaf("leaf223", 0.5, value, tmn22);
        value = new ValuePercent(2.0);
        builder.buildLeaf("leaf224", 3.0, value, tmn22);
        final TreeMapNode tmn3 = builder.buildBranch("branch3", root);
        final TreeMapNode tmn31 = builder.buildBranch("branch31", tmn3);
        value = new ValuePercent(-1.0);
        builder.buildLeaf("leaf311", 1.0, value, tmn31);
        value = new ValuePercent(-5.0);
        builder.buildLeaf("leaf312", 2.0, value, tmn31);
        value = new ValuePercent(-2.0);
        builder.buildLeaf("leaf313", 0.5, value, tmn31);
        value = new ValuePercent(-2.0);
        builder.buildLeaf("leaf314", 3.0, value, tmn31);
        value = new ValuePercent(-5.0);
        builder.buildLeaf("leaf315", 0.25, value, tmn31);
        final TreeMapNode tmn32 = builder.buildBranch("branch32", tmn3);
        value = new ValuePercent(-1.0);
        builder.buildLeaf("leaf321", 1.0, value, tmn32);
        value = new ValuePercent(-5.0);
        builder.buildLeaf("leaf322", 2.0, value, tmn32);
        value = new ValuePercent(0.0);
        builder.buildLeaf("leaf323", 0.5, value, tmn32);
        value = new ValuePercent(2.0);
        builder.buildLeaf("leaf324", 3.0, value, tmn32);
        value = new ValuePercent(-5.0);
        builder.buildLeaf("leaf325", 0.25, value, tmn32);
        final TreeMapNode tmn33 = builder.buildBranch("branch33", tmn3);
        value = new ValuePercent(-1.0);
        builder.buildLeaf("leaf331", 1.0, value, tmn33);
        value = new ValuePercent(5.0);
        builder.buildLeaf("leaf332", 2.0, value, tmn33);
        value = new ValuePercent(2.0);
        builder.buildLeaf("leaf333", 0.5, value, tmn33);
        value = new ValuePercent(-2.0);
        builder.buildLeaf("leaf334", 3.0, value, tmn33);
        final TreeMapNode tmn34 = builder.buildBranch("branch34", tmn3);
        value = new ValuePercent(-1.0);
        builder.buildLeaf("leaf341", 1.0, value, tmn34);
        value = new ValuePercent(5.0);
        builder.buildLeaf("leaf342", 2.0, value, tmn34);
        value = new ValuePercent(-2.0);
        builder.buildLeaf("leaf343", 0.5, value, tmn34);
        final TreeMapNode tmn4 = builder.buildBranch("branch4", root);
        final TreeMapNode tmn41 = builder.buildBranch("branch41", tmn4);
        value = new ValuePercent(1.0);
        builder.buildLeaf("leaf411", 1.0, value, tmn41);
        value = new ValuePercent(5.0);
        builder.buildLeaf("leaf412", 2.0, value, tmn41);
        value = new ValuePercent(2.0);
        builder.buildLeaf("leaf413", 0.5, value, tmn41);
        value = new ValuePercent(2.0);
        builder.buildLeaf("leaf414", 3.0, value, tmn41);
        value = new ValuePercent(-5.0);
        builder.buildLeaf("leaf415", 0.25, value, tmn41);
        final TreeMapNode tmn42 = builder.buildBranch("branch42", tmn4);
        value = new ValuePercent(1.0);
        builder.buildLeaf("leaf421", 1.0, value, tmn42);
        value = new ValuePercent(5.0);
        builder.buildLeaf("leaf422", 2.0, value, tmn42);
        value = new ValuePercent(2.0);
        builder.buildLeaf("leaf423", 0.5, value, tmn42);
        value = new ValuePercent(2.0);
        builder.buildLeaf("leaf424", 3.0, value, tmn42);
        value = new ValuePercent(-5.0);
        builder.buildLeaf("leaf425", 0.25, value, tmn42);
        final TreeMapNode tmn43 = builder.buildBranch("branch43", tmn4);
        value = new ValuePercent(1.0);
        builder.buildLeaf("leaf431", 1.0, value, tmn43);
        value = new ValuePercent(-5.0);
        builder.buildLeaf("leaf432", 2.0, value, tmn43);
        value = new ValuePercent(2.0);
        builder.buildLeaf("leaf433", 0.5, value, tmn43);
        value = new ValuePercent(0.0);
        builder.buildLeaf("leaf434", 3.0, value, tmn43);
        value = new ValuePercent(0.0);
        builder.buildLeaf("leaf5", 5.0, value, root);

        return builder.getRoot();
    }

    /**
     * init the window
     * 
     * @throws Exception
     */
    private void initGUI() throws Exception {
        this.setTitle("JTreeMap Example");

        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent e) {
                this_windowClosing(e);
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
        final String[] numberFields = this.builderTM3.getNumberFields();
        final String[] cmbValues = new String[numberFields.length + 1];
        cmbValues[0] = "";
        for (int i = 1; i < cmbValues.length; i++) {
            cmbValues[i] = numberFields[i - 1];
        }
        this.cmbWeight.removeAllItems();
        this.cmbValue.removeAllItems();
        for (final String item : cmbValues) {
            this.cmbWeight.addItem(item);
            this.cmbValue.addItem(item);
        }

    }

    protected void updateLegendPanel() {
        final String key = (String) this.cmbColorProvider.getSelectedItem();
        final ColorProvider cp = this.colorProviders.get(key);
        if (cp != null) {
            this.jTreeMap.setColorProvider(cp);
            this.cardLayout.show(this.panelLegend, key);
        }
        JTreeMapExample.this.repaint();
    }

    void updateStrategy() {
        final String key = (String) this.cmbStrategy.getSelectedItem();
        final SplitStrategy strat = this.strategies.get(key);
        this.jTreeMap.setStrategy(strat);
        this.jTreeMap.repaint();
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
