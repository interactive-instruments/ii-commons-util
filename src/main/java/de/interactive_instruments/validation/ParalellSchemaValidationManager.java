/*
 * Copyright ${year} interactive instruments GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.interactive_instruments.validation;

import de.interactive_instruments.Factory;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.Schema;

import java.io.File;

import static javax.xml.validation.SchemaFactory.newInstance;

/**
 * @author J. Herrmann ( herrmann <aT) interactive-instruments (doT> de )
 */
public class SchemaValidatorFactory implements Factory<DefaultSchemaValidator> {

	private static final int MAX_ERRORS = 1000;
	private final Schema schema;
	private final ValidatorErrorCollector collHandler;

	public SchemaValidatorFactory(final File schemaFile) throws SAXException {
		this.schema = newInstance("http://www.w3.org/2001/XMLSchema").newSchema(schemaFile);
		collHandler = new ValidatorErrorCollector(MAX_ERRORS);
	}

	@Override public DefaultSchemaValidator create() {
		try {
			return new DefaultSchemaValidator(this.schema, collHandler);
		} catch (ParserConfigurationException | SAXException e) {
			e.printStackTrace();
		}
		return null;
	}
}
