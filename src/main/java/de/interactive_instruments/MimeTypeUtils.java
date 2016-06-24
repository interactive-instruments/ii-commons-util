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
package de.interactive_instruments;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.tika.Tika;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;

import de.interactive_instruments.exceptions.MimeTypeUtilsException;

/**
 * Utility functions for detecting mime types and file extensions on basis of the Tika library.
 *
 * @author J. Herrmann ( herrmann <aT) interactive-instruments (doT> de )
 */
public class MimeTypeUtils {

	private static ConcurrentMap<String, String> mimeTypeToFileExt = new ConcurrentHashMap<String, String>();
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

	public static String detectMimeType(final String str) throws MimeTypeUtilsException {
		InputStream stream = null;
		try {
			stream = new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8));
			return tika.getDetector().detect(stream, new Metadata()).toString();
		} catch (IOException e) {
			throw new MimeTypeUtilsException(e);
		} finally {
			IFile.closeQuietly(stream);
		}
	}

	public static String detectMimeType(final IFile file) throws MimeTypeUtilsException {
		try {
			return tika.detect(file);
		} catch (IOException e) {
			throw new MimeTypeUtilsException(e);
		}
	}

	public static String detectFileExtension(final String str) throws MimeTypeUtilsException {
		return getFileExtensionForMimeType(detectMimeType(str));
	}

}
