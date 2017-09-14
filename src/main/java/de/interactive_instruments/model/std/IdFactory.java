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
package de.interactive_instruments.model.std;

import java.util.UUID;

/**
 * A factory interface for constructing Id objects.
 *
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
public interface IdFactory {

	/**
	 * Creates a random UUID
	 *
	 * @return new Id object
	 */
	Id createRandomUuid();

	/**
	 * Create an Id object from a String, preserves the String as identifier.
	 *
	 * @param str an id string
	 * @return new Id object which holds the string
	 */
	Id createFromStrAndPreserve(String str);

	/**
	 * Create an UUID from the string standard representation.
	 *
	 * @param str a string
	 * @return new Id object which holds an UUID
	 */
	Id createFromStrAsUuid(String str);

	/**
	 * Creates an Id object from an UUID, preserves the UUID
	 *
	 * @param uuid
	 * @return
	 */
	Id createFromUuid(UUID uuid);

	static IdFactory getDefault() {
		return new DefaultIdFactory();
	}
}
