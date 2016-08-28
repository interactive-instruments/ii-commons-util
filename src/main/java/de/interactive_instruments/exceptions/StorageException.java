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
package de.interactive_instruments.exceptions;

/**
 * This exception indicates that the an internal error occurred persisting or loading an object.
 *
 * @author J. Herrmann ( herrmann <aT) interactive-instruments (doT> de )
 *
 */
public class StorageException extends Exception {

	private static final long serialVersionUID = 8918479002295277311L;

	public StorageException(final String mesg) {
		super(mesg);
	}

	public StorageException(final Throwable e) {
		super(e);
	}

	public StorageException(final String mesg, final Throwable e) {
		super(mesg, e);
	}
}
