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
package de.interactive_instruments;

import de.interactive_instruments.exceptions.InitializationException;
import de.interactive_instruments.exceptions.InvalidStateTransitionException;
import de.interactive_instruments.exceptions.config.ConfigurationException;

/**
 * Interface for initializing objects
 *
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
public interface Initializable {

	/**
	 * Initialized the Object
	 *
	 * @throws ConfigurationException if the configuration options are invalid
	 * @throws InitializationException if the initialization failed
	 * @throws InvalidStateTransitionException if Object is already initialized
	 */
	void init() throws ConfigurationException, InitializationException, InvalidStateTransitionException;

	/**
	 * Checks if the Object is initialized
	 *
	 * @return true if object is initialized, false otherwise
	 */
	boolean isInitialized();
}
