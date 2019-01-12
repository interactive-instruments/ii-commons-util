/**
 * Copyright 2017-2019 European Union, interactive instruments GmbH
 * Licensed under the EUPL, Version 1.2 or - as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 *
 * This work was supported by the EU Interoperability Solutions for
 * European Public Administrations Programme (http://ec.europa.eu/isa)
 * through Action 1.17: A Reusable INSPIRE Reference Platform (ARE3NA).
 */
package de.interactive_instruments;

import java.io.*;

import javax.xml.parsers.*;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.*;
import org.xml.sax.*;

/**
 * Very simple XML Utilities
 *
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 * @version 1.1
 * @since 1.0
 */
public final class XmlUtils {

    private XmlUtils() {}

    public static String[] nodeValues(final NodeList nodeList) {
        if (nodeList == null) {
            return null;
        }
        final String[] strArr = new String[nodeList.getLength()];
        for (int i = 0; i < nodeList.getLength(); i++) {
            strArr[i] = nodeValue(nodeList.item(i));
        }
        return strArr;
    }

    public static String nodeValue(final Node node) {
        if (node == null) {
            return null;
        }
        if (node.getNodeType() == Node.ELEMENT_NODE || node.getNodeType() == Node.DOCUMENT_FRAGMENT_NODE) {
            final Node firstChildNode = node.getFirstChild();
            if (firstChildNode != null && firstChildNode.getNodeType() == Node.TEXT_NODE) {
                return firstChildNode.getNodeValue();
            }
            return null;
        }
        return node.getNodeValue();
    }

    public static String getAttribute(final Node node, final String name) {
        return getAttribute(node, null, name);
    }

    public static String getAttribute(final Node node, final String namespace, final String name) {
        final NamedNodeMap attributes = node.getAttributes();
        if (attributes != null) {
            final Node val = attributes.getNamedItemNS(namespace, name);
            if (val != null) {
                return val.getNodeValue();
            }

        }
        return null;
    }

    public static String getAttributeOrDefault(final Node node, final String name, final String defaultVal) {
        return getAttributeOrDefault(node, name, null, defaultVal);
    }

    public static String getAttributeOrDefault(final Node node, final String name, final String namespace,
            final String defaultVal) {
        final String val = getAttribute(node, namespace, name);
        if (val == null) {
            return defaultVal;
        }
        return val;
    }

    public static XmlHandle newXmlHandle(final InputSource source) throws FileNotFoundException {
        return new XmlHandle(null, source);
    }

    public static XmlHandle newXmlHandle(final XPath xpath, final InputSource source) {
        return new XmlHandle(xpath, source);
    }

    public static XmlHandle newXmlHandle(final File source) throws FileNotFoundException {
        return new XmlHandle(null, new InputSource(new FileInputStream(source)));
    }

    public static XmlHandle newXmlHandle(final XPath xpath, final File source) throws FileNotFoundException {
        return new XmlHandle(xpath, new InputSource(new FileInputStream(source)));
    }

    public static class XmlHandle {
        private final XPath xpath;
        // FIXME can only be used once! Use buffer!
        private final InputSource source;

        XmlHandle(final XPath xpath, final InputSource source) {
            this.xpath = xpath != null ? xpath : XPathFactory.newInstance().newXPath();
            this.source = source;
        }

        public String evaluateValue(final String xpathExpression) throws XPathExpressionException {
            return (String) xpath.evaluate(xpathExpression, source, XPathConstants.STRING);
        }

        public String[] evaluateValues(final String xpathExpression) throws XPathExpressionException {
            return nodeValues((NodeList) xpath.evaluate(xpathExpression, source, XPathConstants.NODESET));
        }
    }

    public static String getLastXpathSegment(final String xpath, final boolean excludeNamespace) {
        final int lastSlash = xpath.lastIndexOf('/');
        final int lastFragmentBeginPos;
        final int maxLength;
        if (lastSlash == xpath.length() - 1) {
            lastFragmentBeginPos = xpath.lastIndexOf('/', xpath.length() - 2);
            maxLength = xpath.length() - 1;
        } else {
            lastFragmentBeginPos = lastSlash;
            maxLength = xpath.length();
        }
        if (lastFragmentBeginPos != -1) {
            final int filterSelectPos = SUtils.minIndexOf(xpath, lastFragmentBeginPos, "[", "=");
            final int lastFragmentEndPos;
            if (filterSelectPos != -1) {
                lastFragmentEndPos = filterSelectPos;
            } else {
                lastFragmentEndPos = maxLength;
            }

            final int fragmentBeginPos;
            if (excludeNamespace) {
                final int afterNamespacePos = xpath.indexOf(':', lastFragmentBeginPos);
                if (afterNamespacePos != -1 && afterNamespacePos < lastFragmentEndPos) {
                    fragmentBeginPos = afterNamespacePos + 1;
                } else {
                    fragmentBeginPos = lastFragmentBeginPos + 1;
                }
            } else {
                fragmentBeginPos = lastFragmentBeginPos + 1;
            }
            return xpath.substring(fragmentBeginPos, lastFragmentEndPos);
        } else {
            return "/";
        }
    }

    public static Node getFirstChildNodeOfType(final Node node, final short nodeType, final String name) {
        final Node childNode = node.getFirstChild();
        if (childNode != null && childNode.getNodeType() != nodeType
                && !(name == null || name.equals(childNode.getNodeName()))) {
            return getNextSiblingOfType(childNode, nodeType, name);
        }
        return childNode;
    }

    public static Node getNextSiblingOfType(final Node current, final short nodeType, final String name) {
        Node sibling = current.getNextSibling();
        while (sibling != null) {
            if (sibling.getNodeType() == nodeType &&
                    (name == null || name.equals(sibling.getNodeName()))) {
                return sibling;
            }
            sibling = sibling.getNextSibling();
        }
        return null;
    }

    /**
     * Append a text element as child to an element
     *
     * @param element
     *            Element object where a child element will be appended
     * @param childElementName
     *            name of the child element
     * @param text
     *            string that will be added the child element
     */
    public static void appendChildTextElement(
            final Element element, final String childElementName, final String text) {
        assert element != null && !childElementName.isEmpty() && !text.isEmpty();

        final Element child = element.getOwnerDocument().createElement(
                childElementName);
        element.appendChild(child);
        final Text textNode = element.getOwnerDocument().createTextNode(text);
        child.appendChild(textNode);
    }

    /**
     * Overwrite or append a text element
     *
     * @param element
     *            Element object where a child element will be appended or overwritten
     * @param childElementName
     *            name of the child object that will be searched or created
     * @param text
     *            string that will be added or overwritten
     */
    public static void setChildTextElement(
            final Element element, final String childElementName, final String text) {
        assert element != null && !childElementName.isEmpty() && !text.isEmpty();

        // Search for the child element
        Node childNode = element.getFirstChild();
        while (childNode != null &&
                childNode.getNodeType() != Node.TEXT_NODE &&
                !childNode.getNodeName().equals(childElementName)) {
            childNode = childNode.getNextSibling();
        }
        if (childNode != null) {
            childNode.setTextContent(text);
            return;
        }

        // Not found so just append a new element
        appendChildTextElement(element, childElementName, text);
    }

    public static boolean isXml(final String str) {
        return str.trim().startsWith("<");
    }

    private static class SimpleErrorHandler implements ErrorHandler {
        public void warning(final SAXParseException e) throws SAXException {
            // ignore
        }

        public void error(SAXParseException e) throws SAXException {
            throw (e);
        }

        public void fatalError(SAXParseException e) throws SAXException {
            throw (e);
        }
    }

    public static boolean isWellFormed(final String str) {
        if (SUtils.isNullOrEmpty(str) || !isXml(str)) {
            return false;
        }
        final SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating(false);
        factory.setNamespaceAware(true);

        final XMLReader reader;
        try {
            final SAXParser parser = factory.newSAXParser();
            reader = parser.getXMLReader();
        } catch (ParserConfigurationException | SAXException e) {
            throw new IllegalStateException(e);
        }
        reader.setErrorHandler(new SimpleErrorHandler());
        try {
            reader.parse(new InputSource(new StringReader(str)));
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        } catch (SAXException e) {
            return false;
        }
        return true;
    }

    /**
     * Parse a XML file and return the content as DOM Document.
     *
     * @return Document
     * @throws SAXException
     * @throws IOException
     * @throws ParserConfigurationException
     */
    public Document readXmlContent(IFile file)
            throws SAXException, IOException, ParserConfigurationException {
        file.expectFileIsReadable();
        Document doc = null;
        try {

            final DocumentBuilder docBuilder = newDocumentBuilderFactoryInstance().newDocumentBuilder();
            doc = docBuilder.parse(file);
        } catch (SAXException e) {
            throw new SAXException("Reading XML-File " +
                    file.identifier + " \"" +
                    file.getCanonicalOrSimplePath() + "\" failed: " +
                    e.getMessage());
        } catch (IOException e) {
            throw new IOException("Reading XML-File " +
                    file.identifier + " \"" +
                    file.getCanonicalOrSimplePath() + "\" failed: " +
                    e.getMessage());
        }
        return doc;
    }

    public static DocumentBuilderFactory newDocumentBuilderFactoryInstance() throws ParserConfigurationException {
        final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        docBuilderFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        docBuilderFactory.setXIncludeAware(false);
        docBuilderFactory.setExpandEntityReferences(false);
        return docBuilderFactory;
    }

    /**
     * Write a DOM Document to the file
     *
     * @param doc
     *            The DOM Document
     * @throws IOException
     * @throws TransformerException
     */
    public void writeXmlContent(IFile file, final Document doc)
            throws IOException, TransformerException {
        try {
            file.expectFileIsWritable();
            final TransformerFactory transformerFactory = TransformerFactory.newInstance();
            final Transformer transformer = transformerFactory.newTransformer();
            final DOMSource source = new DOMSource(doc);
            final StreamResult result = new StreamResult(file);
            transformer.transform(source, result);
        } catch (IOException e) {
            throw new IOException("Writing XML-File " +
                    file.identifier + " \"" +
                    file.getCanonicalOrSimplePath() + "\" failed: " +
                    e.getMessage());
        }
    }
}
