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
package de.interactive_instruments.container;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Every Container object implements the LazyLoadContainer Interface which allows
 * access of the underlying object.
 **
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
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
