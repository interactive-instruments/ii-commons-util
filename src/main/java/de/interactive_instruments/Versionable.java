/**
 * Copyright 2010-2016 interactive instruments GmbH
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

/**
 * An interface for versionable Objects
 *
 * @author J. Herrmann ( herrmann <aT) interactive-instruments (doT> de )
 **
 * @see de.interactive_instruments.Version
 */
public interface Versionable extends Comparable {

	/**
	 * Returns the version of the object.
	 *
	 * @return version as Version object
	 */
	ImmutableVersion getVersion();

	/**
	 * Compares this object with a Versionable or a Version object for order.
	 * Returns a negative integer, zero, or a positive integer as this object is less
	 * than, equal to, or greater than the specified object.
	 *
	 * @param o the Versionable or Version object to be compared.
	 * @return  a negative integer, zero, or a positive integer as this object
	 *          is less than, equal to, or greater than the specified object.
	*/
	@Override
	default int compareTo(final Object o) {
		if (o instanceof ImmutableVersion) {
			return getVersion().compareTo(((ImmutableVersion) o));
		}
		return getVersion().compareTo(((Versionable) o).getVersion());
	}
}
