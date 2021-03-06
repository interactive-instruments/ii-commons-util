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
package de.interactive_instruments;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.tika.Tika;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;

import de.interactive_instruments.container.Pair;
import de.interactive_instruments.exceptions.MimeTypeUtilsException;

/**
 * Utility functions for detecting mime types and file extensions on basis of the Tika library.
 *
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
public class MimeTypeUtils {

    private static ConcurrentMap<String, String> mimeTypeToFileExt = new ConcurrentHashMap<String, String>() {
        {
            put("application/gml+xml", ".gml");
        }

        {
            put("application/gml", ".gml");
        }

        {
            put("application/gml+xml; version=2.1", ".gml");
        }

        {
            put("application/gml+xml; version=3.0", ".gml");
        }

        {
            put("application/gml+xml; version=3.1", ".gml");
        }

        {
            put("application/gml+xml; version=3.2", ".gml");
        }

        {
            put("text/xml; subtype=gml/2.1.2", ".gml");
        }

        {
            put("text/xml; subtype=gml/3.0.1", ".gml");
        }

        {
            put("text/xml; subtype=gml/3.1.1", ".gml");
        }

        {
            put("text/xml; subtype=gml/3.2.1", ".gml");
        }

        {
            put("text/xml; subtype=\"gml/2.1.2\"", ".gml");
        }

        {
            put("text/xml; subtype=\"gml/3.0.1\"", ".gml");
        }

        {
            put("text/xml; subtype=\"gml/3.1.1\"", ".gml");
        }

        {
            put("text/xml; subtype=\"gml/3.2.1\"", ".gml");
        }
    };
    private static MimeTypes allTypes = MimeTypes.getDefaultMimeTypes();
    private static Tika tika = new Tika();

    /**
     * Including a dot character before the extension
     *
     * @param mimeType
     * @return
     * @throws MimeTypeUtilsException
     */
    public static String getFileExtensionForMimeType(final String mimeType) throws MimeTypeUtilsException {
        String ext = mimeTypeToFileExt.get(mimeType);
        if (ext != null) {
            return ext;
        } else {
            try {
                final MediaType mediaType = MediaType.parse(mimeType);
                ext = allTypes.forName(mediaType.getBaseType().toString()).getExtension();
            } catch (MimeTypeException e) {
                throw new MimeTypeUtilsException(e);
            }
            mimeTypeToFileExt.put(mimeType, ext);
            return ext;
        }
    }

    /**
     * Content based detection
     */
    public static String detectMimeType(final String str) throws MimeTypeUtilsException {
        InputStream stream = null;
        try {
            stream = new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8));
            return tika.getDetector().detect(stream, new Metadata()).toString();
        } catch (IOException e) {
            throw new MimeTypeUtilsException(e);
        } finally {
            IoUtils.closeQuietly(stream);
        }
    }

    public static String detectMimeTypeFromFilename(final String filename) {
        try {
            return tika.detect(filename);
        } catch (IllegalStateException e) {
            return "";
        }
    }

    public static String detectMimeType(final File file) throws MimeTypeUtilsException {
        try {
            return tika.detect(file);
        } catch (IOException e) {
            throw new MimeTypeUtilsException(e);
        }
    }

    public static String detectFileExtension(final String str) throws MimeTypeUtilsException {
        return getFileExtensionForMimeType(detectMimeType(str));
    }

    /**
     * Set the file extension based on a mime type
     *
     * @param file
     *            the file to rename
     * @param knownMimeType
     *            MIME type or null
     * @return the MIME type and the new file with the extension
     * @throws IOException
     *             if the file can not be read, or a file with the extension already exists
     * @throws MimeTypeUtilsException
     *             if an internal error occurs
     */
    public static Pair<String, IFile> setFileExtension(final IFile file, final String knownMimeType)
            throws IOException, MimeTypeUtilsException {
        file.expectFileIsReadable();
        final String name = file.getFilenameWithoutExt();
        final String mimeType = knownMimeType != null ? knownMimeType : detectMimeType(file);
        final String extension = detectFileExtension(mimeType);
        final String newPath = file.getParent() + File.separator + name + extension;
        if (!file.getFileExtension().equals(extension)) {
            file.moveTo(newPath);
        }
        return new Pair<>(mimeType, new IFile(newPath));
    }

}
