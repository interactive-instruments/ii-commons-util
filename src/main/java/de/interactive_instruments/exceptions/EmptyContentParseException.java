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
package de.interactive_instruments.exceptions;

/**
 * Thrown if an attempt was made to read for instance an empty file
 *
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
public class EmptyContentParseException extends ParseException {

	public EmptyContentParseException(final String errMessage, final String target, final int errorOffset) {
		super(errMessage, target, errorOffset);
	}

	public EmptyContentParseException(final String errMessage, final String target) {
		super(errMessage, target);
	}
}
