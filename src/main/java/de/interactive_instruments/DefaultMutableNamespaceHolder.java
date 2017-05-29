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
