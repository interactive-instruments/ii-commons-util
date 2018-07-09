/**
 * Copyright 2017 European Union, interactive instruments GmbH
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
package de.interactive_instruments.xml;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;

import de.interactive_instruments.container.Pair;

/**
 * Interface for XML Namespace holding objects
 *
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 *
 */
public interface NamespaceHolder extends NamespaceContext, Iterable<Map.Entry<String, String>> {

	/**
	 * Returns the default namespace URI
	 *
	 * @return default namespace URI or null if not set
	 */
	String getDefaultNamespaceUri();

	/**
	 * Returns a Map with a namespace uri -> namespace prefix mapping
	 * @return namespace uri -> namespace prefix map
	 */
	Map<String, Iterable<String>> getNamespacesAsMap();

	/**
	 * Returns true if there is a prefix mapping for the passed namespace
	 *
	 * @param namespaceUri namespace URI
	 * @return true if mapping exists, false otherwise
	 */
	default boolean hasPrefixForNamespace(final String namespaceUri) {
		return getNamespacesAsMap().containsKey(namespaceUri);
	}

	/**
	 * Returns the prefix for a known namespace URI or null if unknown
	 *
	 * Deprecated, use {@link #getPrefix(String)} instead
	 *
	 * @param namespaceUri
	 * @return namespace URI or null
	 */
	@Deprecated
	default String getPrefixForNamespaceUri(final String namespaceUri) {
		return getPrefix(namespaceUri);
	}

	/**
	 * Returns the prefix for a known namespace URI or null if unknown
	 *
	 * @param namespaceUri
	 * @return
	 */
	@Override
	default String getPrefix(final String namespaceUri) {
		if (namespaceUri != null) {
			final Iterable<String> ps = getNamespacesAsMap().get(namespaceUri);
			if (ps != null) {
				return ps.iterator().next();
			}
		}
		return null;
	}

	@Override
	default Iterator<String> getPrefixes(String namespaceUri) {
		if (namespaceUri != null) {
			final Iterable<String> ps = getNamespacesAsMap().get(namespaceUri);
			if (ps != null) {
				return ps.iterator();
			}
		}
		return null;
	}

	Pattern FRAGMENT_TO_QNAME = Pattern
			.compile("((\\{([\\w:\\/.#]*)\\})(@?[\\w-]*$))|(@?[\\w:\\/.#]*:)([\\w-]*$)|(@?[\\w-]*$)");

	/**
	 * Derive and lookup the qualified name from a string
	 * with the format <code>{NAMESPACE_URI}LOCAL_PART</code>
	 * or <code>{NAMESPACE_URI}@LOCAL_PART</code>
	 * or <code>PREFIX:LOCAL_PART</code> or a XPath
	 * <code>@PREFIX:LOCAL_PART</code>
	 *
	 * @throws IllegalArgumentException When the string format \
	 * cannot be parsed or is null
	 * @throws IllegalStateException When the namespace or \
	 * the prefix cannot be found
	 * @return full qualified name
	 */
	default QName getAsQName(final String fragment) {
		final Matcher matcher = FRAGMENT_TO_QNAME.matcher(fragment);
		if (matcher.matches()) {
			if (matcher.group(7) != null) {
				return new QName(getDefaultNamespaceUri(), matcher.group(7), "");
			} else if (matcher.group(5) != null) {
				final String prefixWithAttribute = matcher.group(5);
				final String prefix;
				final String localPart;
				if (prefixWithAttribute.startsWith("@")) {
					prefix = prefixWithAttribute.substring(1, prefixWithAttribute.length() - 1);
					localPart = "@" + matcher.group(6);
				} else {
					prefix = prefixWithAttribute.substring(0, prefixWithAttribute.length() - 1);
					localPart = matcher.group(6);
				}
				return new QName(getNamespaceURI(prefix), localPart, prefix);
			} else if (matcher.group(0) != null) {
				final String ns = matcher.group(3);
				final String localPart = matcher.group(4);
				final String prefix = getPrefix(ns);
				return new QName(ns, localPart, prefix != null ? prefix : "");
			}
		}
		throw new IllegalArgumentException("Unknown fragment: " + fragment);
	}

	/**
	 * Lookup the prefix for a QName by the namespace.
	 *
	 * Null will be returned, if the prefix is unknown or not set
	 *
	 * @param qName qualified name
	 * @return namespace URI
	 */
	default String getPrefix(final QName qName) {
		if (qName != null && qName.getNamespaceURI() != null) {
			return getPrefix(qName.getNamespaceURI());
		}
		return null;
	}

	/**
	 * Lookup the prefixes for a QName by namespace.
	 *
	 * Null will be returned, if the prefix is unknown or not set
	 *
	 * @param qName qualified name
	 * @return namespace URI
	 */
	default Iterator<String> getPrefixes(final QName qName) {
		if (qName != null && qName.getNamespaceURI() != null) {
			return getPrefixes(qName.getNamespaceURI());
		}
		return null;
	}

	/**
	 * Lookup the namespace for a QName by prefix.
	 *
	 * If the prefix is not set in the QName, the default namespace
	 * will be taken. If the default namespace is not available,
	 * null will be returned.
	 *
	 * @param qName qualified name
	 * @return namespace URI
	 */
	default String getNamespaceURI(final QName qName) {
		if (qName != null && qName.getPrefix() != null) {
			final String ns = getNamespaceURI(qName.getPrefix());
			if (ns != null) {
				return ns;
			}
		}
		return getDefaultNamespaceUri();
	}

	/**
	 * Prefixes and namespaces are returned in the format:
	 * declare namespace <namespacePrefix>='<namespaceURL>';
	 *
	 * Each line is separated with the default system line separator.
	 *
	 * @return multipel prefix and namespace declarations
	 */
	default String getDeclarationsForXPathExpressions() {
		final StringBuilder namespaces = new StringBuilder(128);
		getNamespacesAsMap().forEach((ns, ps) -> ps.forEach(p -> {
			namespaces.append("declare namespace ").append(p).append("='").append(ns).append("';")
					.append(System.getProperty("line.separator"));
		}));
		final String defaultNamespace = getDefaultNamespaceUri();
		if (defaultNamespace != null) {
			namespaces.append("declare default element namespace = '").append(defaultNamespace).append("';")
					.append(System.getProperty("line.separator"));
		}
		return namespaces.toString();
	}

	/**
	 * Prefixes and namespaces are returned in the format:
	 * xmlns:<namespacePrefix>='<namespaceURL>'
	 *
	 * Each declaration is delimited with a whitespace
	 *
	 * @return multipel prefix and namespace declarations
	 */
	default String getXmlnsDeclarations() {
		final StringBuilder namespaces = new StringBuilder(128);
		getNamespacesAsMap().forEach((ns, ps) -> ps.forEach(p -> {
			namespaces.append("xmlns:").append(p).append("='").append(ns).append("' ");
		}));
		return namespaces.toString();
	}

	/**
	 * The default namespace and prefixes and namespaces are returned in the format:
	 * xmlns='<defaultNamespace>' xmlns:<namespacePrefix>='<namespaceURL>'
	 *
	 * Each declaration is delimited with a whitespace
	 *
	 * @return multipel prefix and namespace declarations
	 */
	default String getXmlnsDeclarationsWithDefaultNs() {
		final StringBuilder namespaces = new StringBuilder(128);
		final String defaultNamespace = getDefaultNamespaceUri();
		if (defaultNamespace != null) {
			namespaces.append("xmlns='").append(defaultNamespace).append("' ");
		}
		getNamespacesAsMap().forEach((ns, ps) -> ps.forEach(p -> {
			namespaces.append("xmlns:").append(p).append("='").append(ns).append("' ");
		}));
		return namespaces.toString();
	}

	/**
	 * Returns the parent namespace holder or null if this is the root namespace holder
	 *
	 * @return parent Namespace Holder or null
	 */
	default NamespaceHolder getParentNamespaceHolder() {
		return null;
	}

	/**
	 * Get the prefix of a namespace or generate one if the namespace is not known
	 *
	 * @param namespaceUri namespace URI
	 * @return known or generated namespace URI
	 */
	default String getPrefixForNamespaceUriOrGenerate(final String namespaceUri) {
		final String prefix = getPrefix(namespaceUri);
		if (prefix == null) {
			return generateNamespaceUri(namespaceUri);
		}
		return prefix;
	}

	int prefixesSize();

	/**
	 * Generate a prefix for a namespace URI
	 *
	 * @param namespaceUri
	 * @return
	 */
	default String generateNamespaceUri(final String namespaceUri) {
		final int lastSlashPos = namespaceUri.lastIndexOf("/");
		final String candidate;
		if (lastSlashPos != -1) {
			// Is the last segment of the namespace URI:
			// 3 characters long and not a version
			final String lastSegment = namespaceUri.substring(
					lastSlashPos + 1, namespaceUri.length());

			if (!lastSegment.contains(".") && lastSegment.length() >= 3) {
				return lastSegment;
			} else if (namespaceUri.indexOf("/") != lastSlashPos) {
				// Check the next to last segment
				final int nextToLastSlash = namespaceUri.substring(0, lastSlashPos).lastIndexOf("/");
				final String nextToLastSegment = namespaceUri.substring(
						nextToLastSlash + 1, lastSlashPos);
				if (!nextToLastSegment.contains(".") && nextToLastSegment.length() >= 3) {
					// i.e. "gml_3.2"
					return nextToLastSegment + "_" + lastSegment;
				}
			}
		}

		// Not usable, just name it nsx
		return "ns" + String.valueOf(prefixesSize() + 1);
	}

	@Override
	default Iterator<Map.Entry<String, String>> iterator() {
		final List<Map.Entry<String, String>> entries = new ArrayList<>();
		for (final Map.Entry<String, Iterable<String>> namespaceUriPrefixes : getNamespacesAsMap().entrySet()) {
			for (final String prefix : namespaceUriPrefixes.getValue()) {
				entries.add(new Pair<>(namespaceUriPrefixes.getKey(), prefix));
			}
		}
		return entries.iterator();
	}
}
