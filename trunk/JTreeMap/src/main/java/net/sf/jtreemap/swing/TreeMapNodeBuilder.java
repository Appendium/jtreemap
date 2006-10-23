package net.sf.jtreemap.swing;

/**
 * Tree builder for a JTreeMap.
 *
 * @author Laurent Dutheil
 */

public class TreeMapNodeBuilder {
  private TreeMapNode root;

  /**
   * Add a branch to the tree. <BR>
   * If the parent is null, the build node become the root if and only if the
   * tree have no root yet. If the parent is null and if the root is already
   * build, the node will NOT be added to the tree.
   *
   * @param label label of the node
   * @param parent father of the node
   * @return the created node
   */
  public TreeMapNode buildBranch(String label, TreeMapNode parent) {
    TreeMapNode node = new TreeMapNode(label);
    if (parent != null) {
      parent.add(node);
    } else if (this.root == null) {
      this.root = node;
    }
    return node;
  }

  /**
   * add a leaf to the tree. <BR>
   * If the parent is null, the build node become the root if and only if the
   * tree have no root yet. If the parent is null and if the root is already
   * build, the node will NOT be added to the tree.
   *
   * @param label label of the leaf
   * @param weight weight of the leaf
   * @param value Value of the leaf
   * @param parent father of the leaf
   * @return the created node
   */
  public TreeMapNode buildLeaf(String label, double weight, Value value,
      TreeMapNode parent) {
    TreeMapNode node = new TreeMapNode(label, weight, value);
    if (parent != null) {
      parent.add(node);
    } else if (this.root == null) {
      this.root = node;
    }
    return node;
  }

  /**
   * get the build tree.
   *
   * @return the root of the tree
   */
  public TreeMapNode getRoot() {
    return this.root;
  }
}
