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

/**
 * Mutable XML Namespace holder
 *
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
public interface MutableNamespaceHolder extends NamespaceHolder {

	/**
	 * Try to find a "nice" prefix from the namespace uri
	 * @param namespaceUri
	 * @return the determined prefix
	 */
	default String addUnknownNamespaceUriAndDeterminePrefix(final String namespaceUri) {
		final String foundPrefix = getPrefixForNamespaceUri(namespaceUri);
		if (foundPrefix != null) {
			return foundPrefix;
		}

		final int lastSlashPos = namespaceUri.lastIndexOf("/");
		if (lastSlashPos != -1) {
			// Is the last segment of the namespace URI:
			// 3 characters long and not a version
			final String lastSegment = namespaceUri.substring(
					lastSlashPos + 1, namespaceUri.length());

			if (!lastSegment.contains(".") && lastSegment.length() >= 3) {
				addNamespaceUriAndPrefix(namespaceUri, lastSegment);
				return lastSegment;
			} else if (namespaceUri.indexOf("/") != lastSlashPos) {
				// Check the next to last segment
				final int nextToLastSlash = namespaceUri.substring(0, lastSlashPos).lastIndexOf("/");
				final String nextToLastSegment = namespaceUri.substring(
						nextToLastSlash + 1, lastSlashPos);
				if (!nextToLastSegment.contains(".") && nextToLastSegment.length() >= 3) {
					// i.e. "gml_3.2"
					final String prefix = nextToLastSegment + "_" + lastSegment;
					addNamespaceUriAndPrefix(namespaceUri, prefix);
					return prefix;
				}
			}
		}

		// Not usable, just name it nsx
		final String prefix = "ns" + String.valueOf(namespaceMappingSize() + 1);
		addNamespaceUriAndPrefix(namespaceUri, prefix);
		return prefix;
	}

	default String getPrefixForNamespaceUriOrDetermine(final String namespaceUri) {
		final String prefix = getPrefixForNamespaceUri(namespaceUri);
		if (prefix == null) {
			return addUnknownNamespaceUriAndDeterminePrefix(namespaceUri);
		}
		return prefix;
	}

	/**
	 * Add unknown namespace that will be determined when the normalize() method is called
	 *
	 * @param namespaceUri
	 * @return MutableNamespaceHolder
	 */
	MutableNamespaceHolder addNamespaceUriForLaterPrefixLookup(final String namespaceUri);

	/**
	 * Add namespace URI and its namespace prefix
	 *
	 * @param namespaceUri
	 * @param prefix
	 * @return MutableNamespaceHolder
	 */
	MutableNamespaceHolder addNamespaceUriAndPrefix(String namespaceUri, String prefix);

	/**
	 * Normalizes all namespaces and returns a NamespaceHolder
	 * @return NamespaceHolder
	 */
	NamespaceHolder normalizeNamespaces();
}
