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

import java.util.*;

/**
 * Builder for namespace-holding-objects
 *
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
class RootNamespaceBuilder extends AbstractNamespaceHolder implements NamespaceBuilder {

	protected String defaultNamespaceUri;

	protected final Set<String> unknownNamespaces = new LinkedHashSet<>();

	@Override
	public String getDefaultNamespaceUri() {
		return this.defaultNamespaceUri;
	}

	@Override
	public RootNamespaceBuilder setDefaultNamespaceUri(final String defaultNamespaceUri) {
		if (this.defaultNamespaceUri == null) {
			this.defaultNamespaceUri = defaultNamespaceUri;
		} else if (!defaultNamespaceUri.equals(defaultNamespaceUri)) {
			throw new IllegalArgumentException("The default namespace is already set to '" + defaultNamespaceUri + "'");
		}
		return this;
	}

	@Override
	public RootNamespaceBuilder setDefaultNamespaceUriContextAware(final String defaultNamespaceUri) {
		if (this.defaultNamespaceUri == null) {
			this.defaultNamespaceUri = defaultNamespaceUri;
		} else if (!defaultNamespaceUri.equals(defaultNamespaceUri)) {
			throw new IllegalArgumentException("The default namespace is already set to '" + defaultNamespaceUri + "'");
		}
		return this;
	}

	public NamespaceBuilder addNamespaceUri(final String namespaceUri) {
		this.unknownNamespaces.add(namespaceUri);
		return this;
	}

	@Override
	public synchronized RootNamespaceBuilder addNamespaceUriAndPrefixContextAware(final String namespaceUri,
			final String prefix) {
		// if the prefix mapping already exists, a child namespace build must be created for the current context;
		final String nsUri = this.prefixMappings.get(prefix);
		if (nsUri == null) {
			this.prefixMappings.put(prefix, namespaceUri);
			final Collection<String> prefixes = this.namespacesUriMappings.get(namespaceUri);
			if (prefixes != null) {
				prefixes.add(prefix);
			} else {
				this.namespacesUriMappings.put(namespaceUri, new ArrayList<String>() {
					{
						add(prefix);
					}
				});
			}
			return this;
		} else if (nsUri.equals(namespaceUri)) {
			// redefinition attempt can be ignored
			return this;
		} else {
			final ChildNamespaceBuilder childNamespaceBuilder = new ChildNamespaceBuilder(this);
			childNamespaceBuilder.addNamespaceUriAndPrefix(namespaceUri, prefix);
			return childNamespaceBuilder;
		}
	}

	@Override
	public synchronized RootNamespaceBuilder addNamespaceUriAndPrefix(final String namespaceUri, final String prefix) {
		final String nsUri = this.prefixMappings.get(prefix);
		if (nsUri == null) {
			this.prefixMappings.put(prefix, namespaceUri);
			final Collection<String> prefixes = this.namespacesUriMappings.get(namespaceUri);
			if (prefixes != null) {
				prefixes.add(prefix);
			} else {
				this.namespacesUriMappings.put(namespaceUri, new ArrayList<String>() {
					{
						add(prefix);
					}
				});
			}
			return this;
		} else if (nsUri.equals(namespaceUri)) {
			// redefinition attempt can be ignored
			return this;
		} else {
			throw new IllegalArgumentException("The prefix '" + prefix + "' is already mapped to namespace '" + nsUri + "'");
		}
	}

	@Override
	public synchronized RootNamespaceBuilder addNamespaceUrisAndPrefixes(final Map<String, Collection<String>> map) {
		if (map != null) {
			for (final Map.Entry<String, Collection<String>> nsUriPrefixEntry : map.entrySet()) {
				final Collection<String> prefixes = this.namespacesUriMappings.get(nsUriPrefixEntry.getKey());
				if (prefixes != null) {
					prefixes.addAll(nsUriPrefixEntry.getValue());
				} else {
					this.namespacesUriMappings.put(nsUriPrefixEntry.getKey(), nsUriPrefixEntry.getValue());
				}
				for (final String prefix : nsUriPrefixEntry.getValue()) {
					this.prefixMappings.putIfAbsent(prefix, nsUriPrefixEntry.getKey());
				}
			}
		}
		return this;
	}

	@Override
	public synchronized RootNamespaceBuilder addPrefixesAndNamespaceUris(final Map<String, String> map) {
		if (map != null) {
			for (final Map.Entry<String, String> prefixNsUriEntry : map.entrySet()) {
				if (this.prefixMappings.putIfAbsent(prefixNsUriEntry.getKey(), prefixNsUriEntry.getValue()) == null) {
					final Collection<String> prefixes = this.namespacesUriMappings.get(prefixNsUriEntry.getValue());
					if (prefixes != null) {
						prefixes.add(prefixNsUriEntry.getKey());
					} else {
						this.namespacesUriMappings.put(prefixNsUriEntry.getValue(), new ArrayList<String>() {
							{
								add(prefixNsUriEntry.getKey());
							}
						});
					}
				}
			}
		}
		return this;
	}

	protected LinkedHashMap<String, Collection<String>> getNsUrimappingsCopy() {
		return new LinkedHashMap<>(this.namespacesUriMappings);
	}

	protected LinkedHashMap<String, String> getPrefixMappingsCopy() {
		return new LinkedHashMap<>(this.prefixMappings);
	}

	@Override
	public NamespaceHolder build() {
		this.unknownNamespaces.removeIf(this.prefixMappings::containsKey);
		final Map<String, Collection<String>> namespacesUriMappingsCopy = getNsUrimappingsCopy();
		final Map<String, String> prefixMappingsCopy = getPrefixMappingsCopy();
		if (!this.unknownNamespaces.isEmpty()) {
			int i = 0;
			for (final String unknownNamespaceUri : this.unknownNamespaces) {
				final String generatedPrefix = generateNamespaceUri(unknownNamespaceUri);
				final String prefix;
				++i;
				if (prefixMappingsCopy.containsKey(generatedPrefix)) {
					prefix = generatedPrefix + Integer.toString(i);
				} else {
					prefix = generatedPrefix;
				}
				prefixMappingsCopy.put(prefix, unknownNamespaceUri);
				namespacesUriMappingsCopy.put(unknownNamespaceUri, new ArrayList<String>() {
					{
						add(prefix);
					}
				});
			}
		}
		return new DefaultNamespaceHolder(this.defaultNamespaceUri, namespacesUriMappingsCopy, prefixMappingsCopy);
	}
}
