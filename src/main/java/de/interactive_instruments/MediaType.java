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

import java.util.Map;

/**
 * Interface which exposes methods from Apache Tika MediaType
 *
 * @author J. Herrmann ( herrmann <aT) interactive-instruments (doT> de )
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
