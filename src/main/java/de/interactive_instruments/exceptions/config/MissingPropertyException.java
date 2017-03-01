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
package de.interactive_instruments.exceptions.config;

import java.util.Collection;

import de.interactive_instruments.SUtils;

/**
 * Thrown if a object is used, that has to be configured first
 * by setting the missing property.
 *
 * @author J. Herrmann ( herrmann <aT) interactive-instruments (doT> de )
 *
 */
public class MissingPropertyException extends ConfigurationException {

	private static final long serialVersionUID = 218983095766850743L;

	public MissingPropertyException(String property) {
		super("Incomplete configuration:the required property \"" +
				property + "\" is not set!");
	}

	public MissingPropertyException(Collection properties) {
		super("Incomplete configuration: the required properties \"" +
				SUtils.toBlankSepStr(properties) + "\" are not set!");
	}

}