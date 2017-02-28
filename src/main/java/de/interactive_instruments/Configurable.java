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

import de.interactive_instruments.properties.ConfigPropertyHolder;

/**
 * An interface for objects that possess properties that can be used for configuration.
 *
 * NOTE: configure the properties before calling the init() method.
 *
 * @author J. Herrmann ( herrmann <aT) interactive-instruments (doT> de )
 */
public interface Configurable extends Initializable {

	/**
	 * Gets the configurable properties
	 *
	 * NOTE: The implementing class can clear the properties after init() has been called.
	 *
	 * @return ConfigPropertyHolder
	 */
	ConfigPropertyHolder getConfigurationProperties();
}
