package net.sf.jtreemap.swing.example;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.sf.jtreemap.swing.TreeMapNode;
import net.sf.jtreemap.swing.TreeMapNodeBuilder;
import net.sf.jtreemap.swing.Value;
import net.sf.jtreemap.swing.ValuePercent;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Parse a XML file to build the tree. <BR>
 * 
 * @author Laurent Dutheil
 */

public class BuilderXML {
    private static final String BRANCH = "branch";

    private static final String LEAF = "leaf";

    private static final String LABEL = "label";

    private static final String WEIGHT = "weight";

    private static final String VALUE = "value";

    private Document document;

    private TreeMapNodeBuilder builder;

    /**
     * Constructor
     * 
     * @param stFileName
     *            XML file name
     * @throws ParseException
     *             if the file don't correspond to the TreeMap.dtd
     */
    public BuilderXML(final String stFileName) throws ParseException {
        this.builder = new TreeMapNodeBuilder();
        parse(stFileName);
    }

    /**
     * get the build root
     * 
     * @return the build root
     */
    public TreeMapNode getRoot() {
        return this.builder.getRoot();
    }

    private void build(final Element elmt, final TreeMapNode parent) throws ParseException {
        TreeMapNode tmn = null;
        if (elmt.getElementsByTagName(LABEL).getLength() == 0) {
            throw new ParseException("The file don't correspond to the TreeMap.dtd", 0);
        }
        String label = ((Element) elmt.getElementsByTagName(LABEL).item(0)).getChildNodes().item(0).getNodeValue();

        tmn = this.builder.buildBranch(label, parent);

        final NodeList children = elmt.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            final Node node = children.item(i);
            if (node instanceof Element) {
                final Element child = (Element) node;

                final String childName = child.getTagName();
                if (BRANCH.equals(childName)) {
                    build(child, tmn);
                } else if (LEAF.equals(childName)) {
                    final NodeList labels = child.getElementsByTagName(LABEL);
                    label = ((Element) labels.item(0)).getChildNodes().item(0).getNodeValue();
                    final NodeList values = child.getElementsByTagName(VALUE);
                    final String valueString = ((Element) values.item(0)).getChildNodes().item(0).getNodeValue();
                    final NodeList weights = child.getElementsByTagName(WEIGHT);
                    final String weightString = ((Element) weights.item(0)).getChildNodes().item(0).getNodeValue();
                    final Value value = new ValuePercent(Double.valueOf(valueString).doubleValue());
                    final double weight = Double.valueOf(weightString).doubleValue();

                    this.builder.buildLeaf(label, weight, value, tmn);
                }

            }
        }
    }

    private void parse(final String stFileName) throws ParseException {
        try {
            final DocumentBuilderFactory fabrique = DocumentBuilderFactory.newInstance();

            final DocumentBuilder constructeur = fabrique.newDocumentBuilder();
            this.document = constructeur.parse(new File(stFileName));

            final Element root = this.document.getDocumentElement();

            build(root, null);
        } catch (final ParserConfigurationException e) {
            throw new ParseException("The file don't correspond to the TreeMap.dtd (" + e.getMessage() + ")", 0);
        } catch (final SAXException e) {
            throw new ParseException("The file don't correspond to the TreeMap.dtd (" + e.getMessage() + ")", 0);
        } catch (final IOException e) {
            throw new ParseException("The file don't correspond to the TreeMap.dtd (" + e.getMessage() + ")", 0);
        }
    }
}
