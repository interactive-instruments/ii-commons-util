/**
 * Copyright 2010-2016 interactive instruments GmbH
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

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.ValidatorHandler;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

import de.interactive_instruments.IFile;
import de.interactive_instruments.Releasable;
import de.interactive_instruments.exceptions.ExcUtils;
import de.interactive_instruments.io.MultiFileFilter;

/**
 * Schema validator.
 *
 * @author J. Herrmann ( herrmann <aT) interactive-instruments (doT> de )
 */
public class SchemaValidator implements Releasable, MultiFileFilter {

	private final ValidatorErrorCollector collHandler;
	private final ValidatorHandler vh;
	private final XMLReader reader;

	/**
	 * Constructor for Parallel Task Builder
	 *
	 * @throws SAXException
	 */
	SchemaValidator(final Schema schema, final ValidatorErrorCollector collHandler) throws SAXException, ParserConfigurationException {
		this.collHandler = collHandler;
		final SAXParserFactory spf = SAXParserFactory.newInstance();
		if (schema == null) {
			spf.setValidating(false);
			spf.setNamespaceAware(true);
		}
		spf.setNamespaceAware(true);
		reader = spf.newSAXParser().getXMLReader();
		if (schema != null) {
			vh = schema.newValidatorHandler();
			reader.setContentHandler(vh);
		} else {
			vh = null;
		}
	}

	/**
	 * Default Constructor
	 *
	 * @throws SAXException
	 */
	public SchemaValidator(final Schema schema) throws SAXException, ParserConfigurationException {
		this(schema, new ValidatorErrorCollector(1000));
	}

	/**
	 * Removes the problematic UTF-8 byte order mark
	 *
	 * @param reader Reader
	 * @param bom    bom sequence
	 *
	 * @return true if bom has been removed
	 *
	 * @throws IOException reader error
	 */
	private static boolean removeBOM(Reader reader, char[] bom) throws IOException {
		int bomLength = bom.length;
		reader.mark(bomLength);
		char[] possibleBOM = new char[bomLength];
		reader.read(possibleBOM);
		for (int x = 0; x < bomLength; x++) {
			if ((int) bom[x] != (int) possibleBOM[x]) {
				reader.reset();
				return false;
			}
		}
		return true;
	}

	private static final char[] UTF32BE = {0x0000, 0xFEFF};
	private static final char[] UTF32LE = {0xFFFE, 0x0000};
	private static final char[] UTF16BE = {0xFEFF};
	private static final char[] UTF16LE = {0xFFFE};
	private static final char[] UTF8 = {0xEFBB, 0xBF};

	/**
	 * Removes the problematic UTF-8 byte order mark
	 *
	 * @param reader Reader
	 *
	 * @return true if bom has been removed
	 *
	 * @throws IOException reader error
	 */
	private static void removeBOM(Reader reader) throws IOException {
		if (removeBOM(reader, UTF32BE)) {
			return;
		}
		if (removeBOM(reader, UTF32LE)) {
			return;
		}
		if (removeBOM(reader, UTF16BE)) {
			return;
		}
		if (removeBOM(reader, UTF16LE)) {
			return;
		}
		if (removeBOM(reader, UTF8)) {
			return;
		}
	}

	/**
	 * New reader from a file without BOM
	 *
	 * @param file file to read
	 *
	 * @return cleaned reader
	 *
	 * @throws IOException Reader error
	 */
	private BufferedReader getRemovedBomReader(final File file) throws IOException {
		final InputStream inputStream = new FileInputStream(file);
		final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
		removeBOM(bufferedReader);
		return bufferedReader;
	}

	/**
	 * Validate file with the SAX parser.
	 * <p>
	 * The results are collected in the ValidatorErrorCollector of the parent class.
	 *
	 * @return true if the file is well-formed
	 *
	 * @param inputFile
	 */
	public boolean validate(final File inputFile) {
		BufferedReader bufferedReader = null;
		final ValidatorErrorCollector.ValidatorErrorHandler eh = collHandler.newErrorHandler(inputFile);
		try {
			if (vh != null) {
				vh.setErrorHandler(eh);
			} else {
				reader.setErrorHandler(eh);
			}
			bufferedReader = getRemovedBomReader(inputFile);
			reader.parse(new InputSource(bufferedReader));
			eh.release();
			return !eh.hasErrors();
		} catch (SAXParseException ign) {
			// Already logged by error handler
			ExcUtils.suppress(ign);
		} catch (IOException | SAXException e) {
			eh.logUnknownError();
		} finally {
			eh.release();
			if (bufferedReader != null) {
				IFile.closeQuietly(bufferedReader);
			}
		}
		return false;
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

	@Override
	public boolean accept(final File pathname) {
		return validate(pathname);
	}
}
