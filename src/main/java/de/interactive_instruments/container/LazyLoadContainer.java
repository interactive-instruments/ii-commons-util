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
package de.interactive_instruments.container;

import java.io.IOException;
import java.io.OutputStream;


/**
 * Every Container object implements the LazyLoadContainer Interface which allows
 * access of the underlying object.
 **
 * @author J. Herrmann ( herrmann <aT) interactive-instruments (doT> de )
 *
 */
public interface LazyLoadContainer {

	/**
	 * Name of the stored object
	 * @return stored object name
	 */
	String getName();

	/**
	 * Returns true if the container has to load the
	 * referenced object first
	 */
	boolean isReference();

	/**
	 * Returns the content type
	 * @return
	 */
	String getContentType();

	/**
	 * Returns the object or a reference to it
	 * @return
	 * @throws IOException
	 */
	String getAsString() throws IOException;

	/**
	 * Load and returns the loaded object
	 * @return the loaded object
	 * @throws IOException
	 */
	String forceLoad() throws IOException;

	/**
	 * Returns the loaded object as stream
	 * @return OutputStream
	 * @throws IOException
	 */
	void forceLoadAsStream(OutputStream outStream) throws IOException;
}
