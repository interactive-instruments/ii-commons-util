/**
 * Copyright 2010-2017 interactive instruments GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.interactive_instruments;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
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
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Very simple XML Utilities
 *
 * @author J. Herrmann ( herrmann <aT) interactive-instruments (doT> de )
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
		NamedNodeMap attributes = node.getAttributes();
		if (attributes != null) {
			final Node val = attributes.getNamedItemNS(namespace, name);
			if (val != null) {
				return val.getNodeValue();
			}

		}
		return null;
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
		Node childNode = node.getFirstChild();
		while (childNode != null && childNode.getNodeType() != nodeType
				&& (name == null || name.equals(childNode.getNodeName()))) {
			childNode = childNode.getNextSibling();
		}
		return childNode;
	}

	public static Node getNextSiblingOfType(final Node current, final short nodeType, final String name) {
		Node sibling = current.getNextSibling();
		while (sibling != null && sibling.getNodeType() != nodeType && (name == null || name.equals(sibling.getNodeName()))) {
			sibling = sibling.getNextSibling();
		}
		return sibling;
	}

	/**
	 * Append a text element as child to an element
	 * @param element Element object where a child element will be appended
	 * @param childElementName name of the child element
	 * @param text string that will be added the child element
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
	 * @param element Element object where a child element will be appended
	 * or overwritten
	 * @param childElementName name of the child object that will be searched
	 * or created
	 * @param text string that will be added or overwritten
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

	/**
	 * Parse a XML file and return the content as DOM Document.
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
			final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			final DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
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

	/**
	 * Write a DOM Document to the file
	 * @param doc The DOM Document
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
