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
package de.interactive_instruments.xml;

import java.util.*;

/**
 * Abstract XML Namespace holder
 *
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
abstract class AbstractNamespaceHolder implements NamespaceHolder {

	// namespace uri -> namespace prefix
	protected final Map<String, Collection<String>> namespacesUriMappings;
	protected final Map<String, String> prefixMappings;

	AbstractNamespaceHolder() {
		namespacesUriMappings = new LinkedHashMap<>();
		prefixMappings = new LinkedHashMap<>();
	}

	AbstractNamespaceHolder(final Map<String, Collection<String>> namespacesUriMappings,
			final Map<String, String> prefixMappings) {
		this.namespacesUriMappings = namespacesUriMappings;
		this.prefixMappings = prefixMappings;
	}

	AbstractNamespaceHolder(final Map<String, Collection<String>> namespaceUriToPrefixMappings) {
		this.namespacesUriMappings = namespaceUriToPrefixMappings;
		this.prefixMappings = new LinkedHashMap<>();
		for (final Map.Entry<String, Collection<String>> namespaceUriToPrefixMapping : this.namespacesUriMappings.entrySet()) {
			for (final String prefix : namespaceUriToPrefixMapping.getValue()) {
				this.prefixMappings.put(prefix, namespaceUriToPrefixMapping.getKey());
			}
		}
	}

	@Override
	public Map<String, Iterable<String>> getNamespacesAsMap() {
		return Collections.unmodifiableMap(namespacesUriMappings);
	}

	@Override
	public String getNamespaceURI(final String prefix) {
		return prefixMappings.get(prefix);
	}

	@Override
	public String getPrefix(final String namespaceUri) {
		if (namespaceUri != null) {
			final Iterable<String> ps = namespacesUriMappings.get(namespaceUri);
			if (ps != null) {
				return ps.iterator().next();
			}
		}
		return null;
	}

	@Override
	public Iterator<String> getPrefixes(final String namespaceUri) {
		return namespacesUriMappings.get(namespaceUri).iterator();
	}

	@Override
	public int prefixesSize() {
		return prefixMappings.size();
	}
}
