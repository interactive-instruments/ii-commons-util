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

import java.util.Map;
import java.util.Objects;

/**
 * @author J. Herrmann ( herrmann <aT) interactive-instruments (doT> de )
 */
public class DefaultMediaType implements MediaType {

	private final MediaType baseType;
	private final String type;
	private final String subtype;
	private final Map<String, String> parameters;
	private final String typeStr;

	DefaultMediaType(final MediaType baseType, final String type, final String subtype, final Map<String, String> parameters) {
		this.baseType = baseType;
		this.type = type;
		this.subtype = subtype;
		this.parameters = parameters;
		this.typeStr = type;
	}

	@Override
	public MediaType getBaseType() {
		return baseType;
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public String getSubtype() {
		return subtype;
	}

	@Override
	public Map<String, String> getParameters() {
		return parameters;
	}

	@Override
	public String toString() {
		return typeStr;
	}
}
