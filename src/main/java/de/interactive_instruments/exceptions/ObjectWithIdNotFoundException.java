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
package de.interactive_instruments.exceptions;

import de.interactive_instruments.SUtils;

/**
 * Thrown if an object is not found by the managing object.
 *
 * @author J. Herrmann ( herrmann <aT) interactive-instruments (doT> de )
 *
 */
public class ObjectWithIdNotFoundException extends Exception {

	private static final long serialVersionUID = 7040707309361467467L;

	public ObjectWithIdNotFoundException(final Object mngObj, final int hashCode) {
		super((mngObj!=null ? mngObj.getClass().getSimpleName() + ":" : "")  + " An object with the id \"" + hashCode + "\" could not be found!");
	}

	public ObjectWithIdNotFoundException(final int id) {
		super("An object with the hashCode \"" + id + "\" could not be found!");
	}

	public ObjectWithIdNotFoundException(final Object mngObj, final String id) {
		super((mngObj!=null ? mngObj.getClass().getSimpleName() + ":" : "") +" An object with the id \"" + id + "\" could not be found!");
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
