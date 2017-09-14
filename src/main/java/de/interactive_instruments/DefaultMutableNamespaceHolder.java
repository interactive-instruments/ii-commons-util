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

import java.util.*;

/**
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
public final class DefaultMutableNamespaceHolder implements MutableNamespaceHolder {

	/**
	 * namespace uri -> namespace prefix
	 */
	private Map<String, String> namespaces;
	/**
	 * are later merged with namespaces
	 * unkown namespaces are mapped to "ns1"..."nsx"
	 */
	private Set<String> unknownNamespaces = new TreeSet<>();

	public DefaultMutableNamespaceHolder() {
		this.namespaces = new TreeMap<>();
	}

	/**
	 * Copies the namespaces from an existing namespace holder
	 * @param holder
	 */
	public DefaultMutableNamespaceHolder(NamespaceHolder holder) {
		this.namespaces = new TreeMap<>(holder.getNamespacesAsMap());
	}

	@Override
	public MutableNamespaceHolder addNamespaceUriForLaterPrefixLookup(String namespaceUri) {
		this.unknownNamespaces.add(namespaceUri);
		return this;
	}

	@Override
	public MutableNamespaceHolder addNamespaceUriAndPrefix(String namespaceUri, String prefix) {
		this.namespaces.put(namespaceUri, prefix);
		return this;
	}

	@Override
	public NamespaceHolder normalizeNamespaces() {
		this.unknownNamespaces.forEach(ns -> {
			addUnknownNamespaceUriAndDeterminePrefix(ns);
		});
		this.unknownNamespaces.clear();
		return this;
	}

	@Override
	public Map<String, String> getNamespacesAsMap() {
		return Collections.unmodifiableMap(this.namespaces);
	}
}
