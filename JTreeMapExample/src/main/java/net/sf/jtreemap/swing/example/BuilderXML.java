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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import lombok.extern.slf4j.Slf4j;
import net.sf.jtreemap.swing.TreeMapNode;
import net.sf.jtreemap.swing.TreeMapNodeBuilder;
import net.sf.jtreemap.swing.Value;
import net.sf.jtreemap.swing.ValuePercent;

/**
 * Parse a XML file to build the tree. <BR>
 *
 * @author Laurent Dutheil
 */
@Slf4j
public class BuilderXML {
    private static final String THE_FILE_DON_T_CORRESPOND_TO_THE_TREE_MAP_DTD = "The file don't correspond to the TreeMap.dtd (";
    private static final String BRANCH = "branch";
    private static final String LEAF = "leaf";
    private static final String LABEL = "label";
    private static final String WEIGHT = "weight";
    private static final String VALUE = "value";
    private Document document;
    private final TreeMapNodeBuilder builder;

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
        parse(new File(stFileName));
    }

    /**
     * Constructor
     *
     * @param stFile
     *            XML file object
     * @throws ParseException
     *             if the file don't correspond to the TreeMap.dtd
     */
    public BuilderXML(final File stFile) throws ParseException {
        this.builder = new TreeMapNodeBuilder();
        parse(stFile);
    }

    /**
     * Constructor
     *
     * @param stream
     *            <code>InputStream </code> representing XML file object
     * @throws ParseException
     *             if the file don't correspond to the TreeMap.dtd
     */
    public BuilderXML(final InputStream stream) throws ParseException {
        this.builder = new TreeMapNodeBuilder();
        parse(stream);
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
                    final Value value = new ValuePercent(Double.parseDouble(valueString));
                    final double weight = Double.parseDouble(weightString);

                    this.builder.buildLeaf(label, weight, value, tmn);
                }
            }
        }
    }

    private void parse(final File stFile) throws ParseException {
        try {
            final DocumentBuilderFactory fabrique = DocumentBuilderFactory.newInstance();

            final DocumentBuilder constructeur = fabrique.newDocumentBuilder();
            this.document = constructeur.parse(stFile);

            final Element root = this.document.getDocumentElement();

            build(root, null);
        } catch (final ParserConfigurationException | SAXException | IOException e) {
            throw new ParseException(THE_FILE_DON_T_CORRESPOND_TO_THE_TREE_MAP_DTD + e.getMessage() + ")", 0);
        }
    }

    private void parse(final InputStream stream) throws ParseException {
        try {
            final DocumentBuilderFactory fabrique = DocumentBuilderFactory.newInstance();

            final DocumentBuilder constructeur = fabrique.newDocumentBuilder();
            this.document = constructeur.parse(stream);

            final Element root = this.document.getDocumentElement();

            build(root, null);
        } catch (final ParserConfigurationException | SAXException | IOException e) {
            log.error("IO Issue", e);
            throw new ParseException(THE_FILE_DON_T_CORRESPOND_TO_THE_TREE_MAP_DTD + e.getMessage() + ")", 0);
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
