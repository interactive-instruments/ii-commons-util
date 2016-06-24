/**
 * Copyright 2010-2016 interactive instruments GmbH
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

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
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
