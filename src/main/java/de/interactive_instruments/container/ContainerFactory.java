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
package de.interactive_instruments.container;

import java.io.InputStream;
import java.net.URI;
import java.util.UUID;

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
