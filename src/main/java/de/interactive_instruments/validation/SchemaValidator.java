/**
 * Copyright 2017-2019 European Union, interactive instruments GmbH
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.ValidatorHandler;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

import de.interactive_instruments.IoUtils;
import de.interactive_instruments.Releasable;
import de.interactive_instruments.exceptions.ExcUtils;
import de.interactive_instruments.io.MultiFileFilter;
import de.interactive_instruments.io.RemoveBomReader;

/**
 * Schema validator.
 *
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
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
    SchemaValidator(final Schema schema, final ValidatorErrorCollector collHandler)
            throws SAXException, ParserConfigurationException {
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
     * New reader from a file without BOM
     *
     * @param file
     *            file to read
     *
     * @return cleaned reader
     *
     * @throws IOException
     *             Reader error
     */
    private BufferedReader getRemovedBomReader(final File file) throws IOException {
        return RemoveBomReader.getRemovedBomReader(new FileInputStream(file));
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
                IoUtils.closeQuietly(bufferedReader);
            }
        }
        return false;
    }

    @Override
    public void release() {
        collHandler.release();
    }

    public Set<File> getSkippedFiles() {
        return collHandler.getSkippedFiles();
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
