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

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A XML Namespace Builder that overrides the default namespace or prefixes for a specific context.
 *
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
final class ChildNamespaceBuilder extends RootNamespaceBuilder {

	private final RootNamespaceBuilder parent;

	ChildNamespaceBuilder(final RootNamespaceBuilder parent) {
		this.parent = parent;
	}

	@Override
	public synchronized String getDefaultNamespaceUri() {
		if (this.defaultNamespaceUri == null) {
			return parent.getDefaultNamespaceUri();
		}
		return this.defaultNamespaceUri;
	}

	@Override
	public int prefixesSize() {
		// TODO should be optimized
		return getPrefixMappingsCopy().size();
	}

	@Override
	public synchronized Map<String, Iterable<String>> getNamespacesAsMap() {
		final Map<String, Iterable<String>> merge = new LinkedHashMap<>(this.namespacesUriMappings);
		merge.putAll(parent.getNamespacesAsMap());
		return merge;
	}

	@Override
	public String getNamespaceURI(final String prefix) {
		final String nsUri = super.getNamespaceURI(prefix);
		if (nsUri == null) {
			return parent.getNamespaceURI(prefix);
		}
		return nsUri;
	}

	@Override
	public String getPrefix(final String namespaceUri) {
		final String prefix = super.getPrefix(namespaceUri);
		if (prefix == null) {
			return parent.getPrefix(prefix);
		}
		return prefix;
	}

	@Override
	public NamespaceHolder getParentNamespaceHolder() {
		return this.parent;
	}

	@Override
	protected LinkedHashMap<String, Collection<String>> getNsUrimappingsCopy() {
		final LinkedHashMap<String, Collection<String>> copy = parent.getNsUrimappingsCopy();
		copy.putAll(this.namespacesUriMappings);
		return copy;
	}

	@Override
	protected LinkedHashMap<String, String> getPrefixMappingsCopy() {
		final LinkedHashMap<String, String> copy = parent.getPrefixMappingsCopy();
		copy.putAll(this.prefixMappings);
		return copy;
	}

	@Override
	public NamespaceHolder build() {
		this.unknownNamespaces.addAll(parent.unknownNamespaces);
		return super.build();
	}
}
