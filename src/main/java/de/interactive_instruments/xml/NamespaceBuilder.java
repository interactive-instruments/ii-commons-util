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

import java.util.Collection;
import java.util.Map;

/**
 * Namespace Builder for building Namespace Holders
 *
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
public interface NamespaceBuilder extends NamespaceHolder {

	/**
	 * Set default namespace. An Exception will be thrown if it is already set.
	 *
	 * @param defaultNamespaceUri the default namespace URI
	 * @return Builder object
	 */
	NamespaceBuilder setDefaultNamespaceUri(final String defaultNamespaceUri);

	/**
	 * Set default namespace. If the default namespace is already set a new Builder object will be returned.
	 *
	 * @param defaultNamespaceUri the default namespace URI
	 * @return Builder object
	 */
	NamespaceBuilder setDefaultNamespaceUriContextAware(final String defaultNamespaceUri);

	/**
	 * Add a namespace URI and a prefix. Prefix redefinition attempts will throw an Exception.
	 *
	 * @param namespaceUri namespace URI
	 * @param prefix prefix for the namespace URI
	 * @return Builder object
	 */
	NamespaceBuilder addNamespaceUriAndPrefix(final String namespaceUri, final String prefix);

	/**
	 * Add a namespace URI and a prefix. Prefix redefinition attempts will return a new Builder
	 * object for the current context.
	 *
	 * @param namespaceUri namespace URI
	 * @param prefix prefix for the namespace URI
	 * @return Builder object
	 */
	NamespaceBuilder addNamespaceUriAndPrefixContextAware(final String namespaceUri, final String prefix);

	/**
	 * Add namespace URIs and prefixes from a map.
	 * Existing namespace URIs or prefixes are not overridden.
	 *
	 * @param map namespace URIs mapped to one or multiple prefixes
	 * @return Builder object
	 */
	NamespaceBuilder addNamespaceUrisAndPrefixes(final Map<String, Collection<String>> map);

	/**
	 * Add prefixes and namespace URIs from a map.
	 * Existing namespace URIs or prefixes are not overridden.
	 *
	 * @param map prefixes mapped to namespace URIs
	 * @return Builder object
	 */
	NamespaceBuilder addPrefixesAndNamespaceUris(final Map<String, String> map);

	/**
	 * Add a namespace URI. The prefix can either be defined later or will be generated
	 * automatically when the {@link #build()} method is invoked.
	 *
	 * @param namespaceUri
	 * @return
	 */
	NamespaceBuilder addNamespaceUri(final String namespaceUri);

	/**
	 * Creates an immutable NamespaceHolder
	 *
	 * @return immutable NamespaceHolder
	 */
	NamespaceHolder build();

	/**
	 * Create a new Namespace Builder object
	 * @return Namespace Builder object
	 */
	static NamespaceBuilder newInstance() {
		return new RootNamespaceBuilder();
	}
}
