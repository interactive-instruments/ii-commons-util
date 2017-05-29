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
package de.interactive_instruments.io;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import de.interactive_instruments.IFile;
import de.interactive_instruments.MimeTypeUtils;
import de.interactive_instruments.exceptions.MimeTypeUtilsException;

/**
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
public class ContentTypeFilter implements MultiFileFilter {

	private final Set<String> allowedMimeTypes;

	public ContentTypeFilter(final Set<String> allowedMimeTypes) {
		this.allowedMimeTypes = Objects.requireNonNull(allowedMimeTypes);
	}

	public ContentTypeFilter(final String... allowedMimeTypes) {
		this.allowedMimeTypes = new HashSet<>(Arrays.asList(allowedMimeTypes));
	}

	public Set<String> getAllowedMimeTypes() {
		return allowedMimeTypes;
	}

	public boolean accept(final String mimeType) {
		return allowedMimeTypes.contains(mimeType);
	}

	@Override
	public boolean accept(final File path) {
		try {
			return allowedMimeTypes.contains(MimeTypeUtils.detectMimeType(path));
		} catch (MimeTypeUtilsException e) {
			return false;
		}
	}
}
