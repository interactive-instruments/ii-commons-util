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
package de.interactive_instruments.model.std;

import java.util.UUID;

/**
 * An universal interface for identifying domain model items.
 *
 * The interface enables the usage of unique (UUID) and non-unique identifiers (String). The
 * implementor decides on which type is generated, the clients of this interface obtain a uniform
 * access.
 *
 * @author J. Herrmann ( herrmann <aT) interactive-instruments (doT> de )
 */
public interface Id extends Comparable {

	/**
	 * Returns the string representation of this Id object
	 *
	 * @return identifier as String
	 */
	String getId();

	/**
	 * Returns the UUID representation of this Id object
	 *
	 * @return identifier as UUID
	 */
	UUID toUuid();
}
