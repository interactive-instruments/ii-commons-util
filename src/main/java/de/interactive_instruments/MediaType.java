/**
 * Copyright 2017-2018 European Union, interactive instruments GmbH
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

/**
 * Interface which exposes methods from Apache Tika MediaType
 *
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
public interface MediaType extends Comparable<MediaType> {

	/**
	 * Returns the base form of the MediaType, excluding
	 *  any parameters, such as "text/plain" for
	 *  "text/plain; charset=utf-8"
	 */
	MediaType getBaseType();

	/**
	 * Return the Type of the MediaType, such as
	 *  "text" for "text/plain"
	 */
	String getType();

	/**
	 * Return the Sub-Type of the MediaType,
	 *  such as "plain" for "text/plain"
	 */
	String getSubtype();

	/**
	 * Returns an immutable sorted map of the parameters of this media type.
	 * The parameter names are guaranteed to be trimmed and in lower case.
	 *
	 * @return sorted map of parameters
	 */
	Map<String, String> getParameters();

	@Override
	default int compareTo(MediaType o) {
		return this.toString().compareTo(o.toString());
	}
}
