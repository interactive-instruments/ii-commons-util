/**
 * Copyright 2017 European Union, interactive instruments GmbH
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
package de.interactive_instruments;

import java.util.Map;

/**
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
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
