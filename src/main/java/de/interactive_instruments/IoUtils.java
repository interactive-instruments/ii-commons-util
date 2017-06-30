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
	 * Unconditionally close a closeable object and ignore errors.
	 * ! Use with care !
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
