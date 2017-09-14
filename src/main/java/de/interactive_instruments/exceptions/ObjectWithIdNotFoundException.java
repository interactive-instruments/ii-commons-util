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
package de.interactive_instruments.exceptions;

import de.interactive_instruments.SUtils;

/**
 * Thrown if an object is not found by the managing object.
 *
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 *
 */
public class ObjectWithIdNotFoundException extends Exception {

	private static final long serialVersionUID = 7040707309361467467L;

	public ObjectWithIdNotFoundException(final Object mngObj, final int hashCode) {
		super((mngObj != null ? mngObj.getClass().getSimpleName() + ":" : "") + " An object with the id \"" + hashCode
				+ "\" could not be found!");
	}

	public ObjectWithIdNotFoundException(final int id) {
		super("An object with the hashCode \"" + id + "\" could not be found!");
	}

	public ObjectWithIdNotFoundException(final Object mngObj, final String id) {
		super((mngObj != null ? mngObj.getClass().getSimpleName() + ":" : "") + " An object with the id \"" + id
				+ "\" could not be found!");
	}

	public ObjectWithIdNotFoundException(final String id) {
		super("An object with the id \"" + id + "\" could not be found!");
	}

	public ObjectWithIdNotFoundException(final String id, final String objLabel) {
		super("An object with the id \"" + id + "\" (" + objLabel + ") could not be found!");
	}

	public ObjectWithIdNotFoundException(final Iterable iterable) {
		super("Objects with the ids \"" + SUtils.toBlankSepStr(iterable) + "\" could not be found!");
	}

	// User with super(message, 1L);
	protected ObjectWithIdNotFoundException(final String message, final long placeholder) {
		super(message);
	}
}
