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
package de.interactive_instruments.validation;

import de.interactive_instruments.Factory;
import de.interactive_instruments.io.MultiFileFilter;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.Schema;
import java.io.File;
import java.util.Set;

import static javax.xml.validation.SchemaFactory.newInstance;

/**
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
public class ParalellSchemaValidationManager implements Factory<MultiFileFilter> {

	private static final int MAX_ERRORS = 1000;
	private final Schema schema;
	private final ValidatorErrorCollector collHandler;

	public ParalellSchemaValidationManager(final File schemaFile) throws SAXException {
		this.schema = newInstance("http://www.w3.org/2001/XMLSchema").newSchema(schemaFile);
		collHandler = new ValidatorErrorCollector(MAX_ERRORS);
	}

	public ParalellSchemaValidationManager(final File schemaFile, final int errorLimit) throws SAXException {
		this.schema = newInstance("http://www.w3.org/2001/XMLSchema").newSchema(schemaFile);
		collHandler = new ValidatorErrorCollector(errorLimit);
	}

	public ParalellSchemaValidationManager() throws SAXException {
		this.schema = null;
		collHandler = new ValidatorErrorCollector(MAX_ERRORS);
	}

	public ParalellSchemaValidationManager(final int errorLimit) throws SAXException {
		this.schema = null;
		collHandler = new ValidatorErrorCollector(errorLimit);
	}

	@Override
	public SchemaValidator create() {
		try {
			return new SchemaValidator(this.schema, collHandler);
		} catch (ParserConfigurationException | SAXException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void release() {
		collHandler.release();
	}

	/**
	 * Returns all concatenated error messages.
	 *
	 * @return concatenated error messages
	 */
	final public String getErrorMessages() {
		return collHandler.getErrorMessages();
	}

	/**
	 * Returns the number of errors.
	 *
	 * @return number of errors.
	 */
	final public int getErrorCount() {
		return collHandler.getErrorCount();
	}

	public Set<File> getSkippedFiles() {
		return collHandler.getSkippedFiles();
	}
}
