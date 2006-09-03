package org.jense.ktreemap.example;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jense.ktreemap.*;
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

public class BuilderXML extends TreeMapNodeBuilder {
  private static final String BRANCH = "branch";
  private static final String LEAF = "leaf";
  private static final String LABEL = "label";
  private static final String WEIGHT = "weight";
  private static final String VALUE = "value";
  private Document document;

  /**
   * Constructor
   * 
   * @param file XML file name
   * @throws ParseException if the file don't correspond to the TreeMap.dtd
   */
  public BuilderXML(File file) throws ParseException {
    parse(file);
  }

  private void build(Element elmt, TreeMapNode parent) throws ParseException {
    TreeMapNode tmn = null;
    if (elmt.getElementsByTagName(LABEL).getLength() == 0) {
      throw new ParseException("The file don't correspond to the TreeMap.dtd",
          0);
    }
    String label = ((Element) elmt.getElementsByTagName(LABEL).item(0))
        .getChildNodes().item(0).getNodeValue();
    
    XMLBean bean = new XMLBean();
    bean.setLabel(label);

    tmn = buildBranch(bean, parent);

    NodeList children = elmt.getChildNodes();
    for (int i = 0; i < children.getLength(); i++) {
      Node node = children.item(i);
      if (node instanceof Element) {
        Element child = (Element) node;

        String childName = child.getTagName();
        if (BRANCH.equals(childName)) {
          build(child, tmn);
        } else if (LEAF.equals(childName)) {
          NodeList labels = child.getElementsByTagName(LABEL);
          label = ((Element) labels.item(0)).getChildNodes().item(0)
              .getNodeValue();
          NodeList values = child.getElementsByTagName(VALUE);
          String valueString = ((Element) values.item(0)).getChildNodes().item(
              0).getNodeValue();
          NodeList weights = child.getElementsByTagName(WEIGHT);
          String weightString = ((Element) weights.item(0)).getChildNodes()
              .item(0).getNodeValue();
          
          XMLBean beanChild = new XMLBean();
          beanChild.setLabel(label);
          beanChild.setValue(Double.valueOf(valueString)
              .doubleValue());
          beanChild.setWeight(Double.valueOf(weightString).doubleValue());
          
          buildLeaf(beanChild, tmn);

        }

      }
    }

  }

  private void parse(File file) throws ParseException {
    try {
      DocumentBuilderFactory fabrique = DocumentBuilderFactory.newInstance();

      DocumentBuilder constructeur = fabrique.newDocumentBuilder();
      this.document = constructeur.parse(file);

      Element root = this.document.getDocumentElement();

      build(root, null);
    } catch (ParserConfigurationException e) {
      throw new ParseException("The file don't correspond to the TreeMap.dtd ("
          + e.getMessage() + ")", 0);
    } catch (SAXException e) {
      throw new ParseException("The file don't correspond to the TreeMap.dtd ("
          + e.getMessage() + ")", 0);
    } catch (IOException e) {
      throw new ParseException("The file don't correspond to the TreeMap.dtd ("
          + e.getMessage() + ")", 0);
    }

  }

  @Override
  public double getWeight(Object value) {
    if (value instanceof XMLBean) {
      XMLBean bean = (XMLBean)value;
      return bean.getWeight();
    }
    return 0;
  }

}
