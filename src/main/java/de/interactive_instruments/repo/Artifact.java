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
package de.interactive_instruments.repo;

import java.io.File;
import java.io.IOException;

import de.interactive_instruments.IFile;
import de.interactive_instruments.Version;
import de.interactive_instruments.exceptions.config.ConfigurationException;

/**
 *
 *
 * @author J. Herrmann ( herrmann <aT) interactive-instruments (doT> de )
 */
public interface Artifact {

	/**
	 * Returns unique artifact ID
	 */
	String getId();

	/**
	 * Get artifact name
	 *
	 * @return
	 */
	String getName();

	/**
	 * Get artifact group name
	 *
	 * @return
	 */
	String getGroupname();

	/**
	 * Get artifact version
	 *
	 * @return
	 */
	Version getVersion();

	/**
	 * Download to destination
	 *
	 * @param destination
	 */
	void copyTo(final File destination) throws IOException;
}