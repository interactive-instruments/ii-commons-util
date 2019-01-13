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

import static de.interactive_instruments.CLUtils.getResourceAsStream;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import de.interactive_instruments.exceptions.ExcUtils;
import de.interactive_instruments.exceptions.IOsizeLimitExceededException;

/**
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
public final class IoUtils {

    private IoUtils() {}

    public static void copySecure(final InputStream inputStream, final OutputStream outputStream,
            final int bufferSize, final long maxSize) throws IOException {
        final byte[] buffer = new byte[bufferSize];
        long bytesWritten = 0;
        int length;
        while (-1 != (length = inputStream.read(buffer))) {
            outputStream.write(buffer, 0, length);
            bytesWritten += length;
            if (bytesWritten > maxSize) {
                throw new IOsizeLimitExceededException(maxSize);
            }
        }
    }

    public static void copy(final InputStream inputStream, final OutputStream outputStream,
            final int bufferSize) throws IOException {
        final byte[] buffer = new byte[bufferSize];
        int length;
        while (-1 != (length = inputStream.read(buffer))) {
            outputStream.write(buffer, 0, length);
        }
    }

    public static void copy(final InputStream inputStream, final OutputStream outputStream,
            final byte[] buffer) throws IOException {
        int length;
        while (-1 != (length = inputStream.read(buffer))) {
            outputStream.write(buffer, 0, length);
        }
    }

    /**
     * Copies a resource to a resource path. Note: First use the class to find the resource, if nothing is found the associated classloader will be used as fallback.
     *
     * @param ctxObj
     * @param resourcePath
     * @param destFile
     * @throws IOException
     */
    public static void copyResourceToFile(final Object ctxObj, final String resourcePath, final IFile destFile)
            throws IOException {
        destFile.getParentFile().mkdirs();
        destFile.expectFileIsWritable();
        final InputStream stream = getResourceAsStream(ctxObj, resourcePath);
        destFile.write(IoUtils.requireNonNullIO(stream, "Resource " + resourcePath + " not found"));
    }

    public static <T> T requireNonNullIO(final T obj, final String message) throws IOException {
        if (obj == null)
            throw new IOException(message);
        return obj;
    }

    /**
     * Unconditionally close a closeable object and ignore errors. ! Use with care !
     */
    public static void closeQuietly(final Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (final Exception e) {
            ExcUtils.suppress(e);
        }
    }
}
