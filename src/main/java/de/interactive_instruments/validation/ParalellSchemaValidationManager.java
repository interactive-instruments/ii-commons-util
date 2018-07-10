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
package de.interactive_instruments.validation;

import static javax.xml.validation.SchemaFactory.newInstance;

import java.io.File;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.Schema;

import org.xml.sax.SAXException;

import de.interactive_instruments.Factory;
import de.interactive_instruments.io.MultiFileFilter;

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
