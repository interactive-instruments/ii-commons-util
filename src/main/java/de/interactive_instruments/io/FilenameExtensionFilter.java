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

import de.interactive_instruments.exceptions.MimeTypeUtilsException;

/**
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
public class FilenameExtensionFilter implements MultiFileFilter {

	private final Set<String> fileExtensions;

	public FilenameExtensionFilter(final Set<String> fileExtensions) {
		this.fileExtensions = new HashSet<>();
		for (final String fileExtension : fileExtensions) {
			this.fileExtensions.add(fileExtension.replace(".", ""));
		}
	}

	public FilenameExtensionFilter(final String... fileExtensions) {
		this.fileExtensions = new HashSet<>();
		for (int i = 0; i < fileExtensions.length; i++) {
			this.fileExtensions.add(fileExtensions[i].replace(".", ""));
		}
	}

	@Override
	public boolean accept(final File path) {
		final String pathname = path.getName();
		final int index = pathname.lastIndexOf(".");
		if (index == -1) {
			return false;
		}
		return fileExtensions.contains(pathname.substring(index + 1).toLowerCase());
	}
}
