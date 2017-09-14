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
package de.interactive_instruments;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * XML Namespace holder
 *
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
public interface NamespaceHolder {

	/**
	 * Returns a Map with a namespace uri -> namespace prefix mapping
	 * @return namespace uri -> namespace prefix map
	 */
	Map<String, String> getNamespacesAsMap();

	default boolean hasPrefixForNamespace(final String namespaceUri) {
		return getNamespacesAsMap().containsKey(namespaceUri);
	}

	/**
	 * Returns a Entryset with a namespace uri -> namespace prefix mapping
	 * @return namespace uri -> namespace prefix entry set
	 */
	default Set<Map.Entry<String, String>> getEntrySet() {
		return getNamespacesAsMap().entrySet();
	}

	/**
	 * Returns the prefix for a known namespace URI or null if unknown
	 * @param namespaceUri
	 * @return namespace URI or null
	 */
	default String getPrefixForNamespaceUri(String namespaceUri) {
		return getNamespacesAsMap().get(namespaceUri);
	}

	/**
	 * Returns the number of known namespace URI mappings
	 * @return size
	 */
	default int namespaceMappingSize() {
		return getNamespacesAsMap().size();
	}

	/**
	 * Namespaces are returned in the format:
	 * xmlns:<namespacePrefix>='<namespaceURL>'
	 */
	default String getXmlnsDeclarations() {
		return getEntrySet().stream().map(e -> "xmlns:" + e.getValue() + "='" + e.getKey() + "'")
				.collect(Collectors.joining(" "));
	}

	/**
	 * Namespaces are returned in the format:
	 * declare namespace <namespacePrefix>='<namespaceURL>';
	 */
	default String getDeclarationsForXPathExpressions() {
		return getEntrySet().stream()
				.map(e -> "declare namespace " + e.getValue() + "='" + e.getKey() + "';" + System.getProperty("line.separator"))
				.collect(Collectors.joining(" "));
	}
}
