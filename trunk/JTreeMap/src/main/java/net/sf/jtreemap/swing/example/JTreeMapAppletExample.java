/*
 * Created on 28 oct. 2005
 */
package net.sf.jtreemap.swing.example;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.JApplet;

import net.sf.jtreemap.swing.JTreeMap;
import net.sf.jtreemap.swing.SplitBySortedWeight;
import net.sf.jtreemap.swing.TreeMapNode;
import net.sf.jtreemap.swing.TreeMapNodeBuilder;
import net.sf.jtreemap.swing.Value;

/**
 * Test of JTreeMap in a JApplet
 *
 * @author Laurent Dutheil
 */
public class JTreeMapAppletExample extends JApplet {
  private static final long serialVersionUID = -8376357344981512167L;
  protected JTreeMap jTreeMap;
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
    TreeMapNode root = getRoot();

    this.jTreeMap = new JTreeMap(root, new SplitBySortedWeight());
    this.jTreeMap.setFont(new Font(null, Font.BOLD, 16));
    this.jTreeMap.setColorProvider(new RedGreenColorProvider(this.jTreeMap));

    //Add a popupMenu to zoom
    new ZoomPopupMenu(this.jTreeMap);

    getJContentPane().add(this.jTreeMap, BorderLayout.CENTER);

  }

  /**
   * This method initializes this
   */
  @Override
  public void init() {
    this.setSize(600, 400);
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

  /**
   * This method build a TreeMap root.<BR>
   * This methode may be replaced by one who get the root from a servlet or a
   * jdbc client or something else...
   * @return the root of the TreeMap.
   */
  private TreeMapNode getRoot() {
    TreeMapNodeBuilder builder = new TreeMapNodeBuilder();

    TreeMapNode root = builder.buildBranch("Root", null);
    TreeMapNode tmn1 = builder.buildBranch("branch1", root);
    TreeMapNode tmn11 = builder.buildBranch("branch11", tmn1);
    Value value = new ValuePercent(0.0);
    builder.buildLeaf("leaf111",1.0, value, tmn11);
    value = new ValuePercent(-5.0);
    builder.buildLeaf("leaf112",2.0, value, tmn11);
    value = new ValuePercent(2.0);
    builder.buildLeaf("leaf113",0.5, value, tmn11);
    value = new ValuePercent(2.0);
    builder.buildLeaf("leaf114",3.0, value, tmn11);
    value = new ValuePercent(-5.0);
    builder.buildLeaf("leaf115",0.25, value, tmn11);
    TreeMapNode tmn12 = builder.buildBranch("branch12", tmn1);
    value = new ValuePercent(1.0);
    builder.buildLeaf("leaf121",1.0, value, tmn12);
    value = new ValuePercent(5.0);
    builder.buildLeaf("leaf122",2.0, value, tmn12);
    value = new ValuePercent(2.0);
    builder.buildLeaf("leaf123",0.5, value, tmn12);
    value = new ValuePercent(-2.0);
    builder.buildLeaf("leaf124",3.0, value, tmn12);
    value = new ValuePercent(5.0);
    builder.buildLeaf("leaf125",0.25, value, tmn12);
    TreeMapNode tmn13 = builder.buildBranch("branch13", tmn1);
    value = new ValuePercent(1.0);
    builder.buildLeaf("leaf131",1.0, value, tmn13);
    value = new ValuePercent(5.0);
    builder.buildLeaf("leaf132",2.0, value, tmn13);
    value = new ValuePercent(2.0);
    builder.buildLeaf("leaf133",0.5, value, tmn13);
    value = new ValuePercent(2.0);
    builder.buildLeaf("leaf134",3.0, value, tmn13);
    value = new ValuePercent(2.0);
    builder.buildLeaf("leaf14",3.0, value, tmn1);
    value = new ValuePercent(-5.0);
    builder.buildLeaf("leaf15",2.0, value, tmn1);
    TreeMapNode tmn2 = builder.buildBranch("branch2", root);
    TreeMapNode tmn21 = builder.buildBranch("branch21", tmn2);
    value = new ValuePercent(-1.0);
    builder.buildLeaf("leaf211",1.0, value, tmn21);
    value = new ValuePercent(-5.0);
    builder.buildLeaf("leaf212",2.0, value, tmn21);
    value = new ValuePercent(2.0);
    builder.buildLeaf("leaf213",0.5, value, tmn21);
    value = new ValuePercent(2.0);
    builder.buildLeaf("leaf214",3.0, value, tmn21);
    value = new ValuePercent(5.0);
    builder.buildLeaf("leaf215",0.25, value, tmn21);
    TreeMapNode tmn22 = builder.buildBranch("branch22", tmn2);
    value = new ValuePercent(1.0);
    builder.buildLeaf("leaf221",1.0, value, tmn22);
    value = new ValuePercent(5.0);
    builder.buildLeaf("leaf222",2.0, value, tmn22);
    value = new ValuePercent(2.0);
    builder.buildLeaf("leaf223",0.5, value, tmn22);
    value = new ValuePercent(2.0);
    builder.buildLeaf("leaf224",3.0, value, tmn22);
    TreeMapNode tmn3 = builder.buildBranch("branch3", root);
    TreeMapNode tmn31 = builder.buildBranch("branch31", tmn3);
    value = new ValuePercent(-1.0);
    builder.buildLeaf("leaf311",1.0, value, tmn31);
    value = new ValuePercent(-5.0);
    builder.buildLeaf("leaf312",2.0, value, tmn31);
    value = new ValuePercent(-2.0);
    builder.buildLeaf("leaf313",0.5, value, tmn31);
    value = new ValuePercent(-2.0);
    builder.buildLeaf("leaf314",3.0, value, tmn31);
    value = new ValuePercent(-5.0);
    builder.buildLeaf("leaf315",0.25, value, tmn31);
    TreeMapNode tmn32 = builder.buildBranch("branch32", tmn3);
    value = new ValuePercent(-1.0);
    builder.buildLeaf("leaf321",1.0, value, tmn32);
    value = new ValuePercent(-5.0);
    builder.buildLeaf("leaf322",2.0, value, tmn32);
    value = new ValuePercent(2.0);
    builder.buildLeaf("leaf323",0.5, value, tmn32);
    value = new ValuePercent(2.0);
    builder.buildLeaf("leaf324",3.0, value, tmn32);
    value = new ValuePercent(-5.0);
    builder.buildLeaf("leaf325",0.25, value, tmn32);
    TreeMapNode tmn33 = builder.buildBranch("branch33", tmn3);
    value = new ValuePercent(-1.0);
    builder.buildLeaf("leaf331",1.0, value, tmn33);
    value = new ValuePercent(5.0);
    builder.buildLeaf("leaf332",2.0, value, tmn33);
    value = new ValuePercent(2.0);
    builder.buildLeaf("leaf333",0.5, value, tmn33);
    value = new ValuePercent(-2.0);
    builder.buildLeaf("leaf334",3.0, value, tmn33);
    TreeMapNode tmn34 = builder.buildBranch("branch34", tmn3);
    value = new ValuePercent(-1.0);
    builder.buildLeaf("leaf341",1.0, value, tmn34);
    value = new ValuePercent(5.0);
    builder.buildLeaf("leaf342",2.0, value, tmn34);
    value = new ValuePercent(-2.0);
    builder.buildLeaf("leaf343",0.5, value, tmn34);
    TreeMapNode tmn4 = builder.buildBranch("branch4", root);
    TreeMapNode tmn41 = builder.buildBranch("branch41", tmn4);
    value = new ValuePercent(1.0);
    builder.buildLeaf("leaf411",1.0, value, tmn41);
    value = new ValuePercent(5.0);
    builder.buildLeaf("leaf412",2.0, value, tmn41);
    value = new ValuePercent(2.0);
    builder.buildLeaf("leaf413",0.5, value, tmn41);
    value = new ValuePercent(2.0);
    builder.buildLeaf("leaf414",3.0, value, tmn41);
    value = new ValuePercent(-5.0);
    builder.buildLeaf("leaf415",0.25, value, tmn41);
    TreeMapNode tmn42 = builder.buildBranch("branch42", tmn4);
    value = new ValuePercent(1.0);
    builder.buildLeaf("leaf421",1.0, value, tmn42);
    value = new ValuePercent(5.0);
    builder.buildLeaf("leaf422",2.0, value, tmn42);
    value = new ValuePercent(2.0);
    builder.buildLeaf("leaf423",0.5, value, tmn42);
    value = new ValuePercent(2.0);
    builder.buildLeaf("leaf424",3.0, value, tmn42);
    value = new ValuePercent(-5.0);
    builder.buildLeaf("leaf425",0.25, value, tmn42);
    TreeMapNode tmn43 = builder.buildBranch("branch43", tmn4);
    value = new ValuePercent(1.0);
    builder.buildLeaf("leaf431",1.0, value, tmn43);
    value = new ValuePercent(-5.0);
    builder.buildLeaf("leaf432",2.0, value, tmn43);
    value = new ValuePercent(2.0);
    builder.buildLeaf("leaf433",0.5, value, tmn43);
    value = new ValuePercent(0.0);
    builder.buildLeaf("leaf434",3.0, value, tmn43);

    return builder.getRoot();
  }
}
