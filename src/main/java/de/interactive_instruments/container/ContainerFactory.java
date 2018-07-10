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

import java.net.URI;

import de.interactive_instruments.exceptions.ContainerFactoryException;

/**
 * The container factory is used to persist or reference any desired content.
 *
 * The implementing factory decides how to store or reference the content.
 *
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
public interface ContainerFactory {

	/**
	 * Creates a container with a mime type
	 *
	 * @param name
	 * @param mimeType
	 * @param str
	 * @return
	 */
	LazyLoadContainer create(String name, String mimeType, String str) throws ContainerFactoryException;

	/**
	 * Creates a container and sets container content type to a default or guesses it
	 *
	 * @param name
	 * @param str
	 * @return
	 */
	LazyLoadContainer create(String name, String str) throws ContainerFactoryException;

	/**
	 * Base URI that is used for storing container content
	 *
	 * @return
	 */
	URI getUri();

	URI getNewReferencedContainerStoreUri() throws ContainerFactoryException;

	LazyLoadContainer createReferencedContainer(String name, URI uri) throws ContainerFactoryException;

	LazyLoadContainer createReferencedContainer(String name, String mimeType, URI uri) throws ContainerFactoryException;
}
