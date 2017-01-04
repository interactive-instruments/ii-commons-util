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
package de.interactive_instruments.io;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.nio.file.Path;

/**
 *
 * @author J. Herrmann ( herrmann <aT) interactive-instruments (doT> de )
 */
public interface MultiFileFilter extends FileFilter, FilenameFilter, PathFilter {

	default boolean accept(final Path pathname) {
		return accept(pathname.toFile());
	}

	default boolean accept(final File dir, final String name) {
		return accept(new File(dir, name));
	}

	default MultiFileFilter and(final FileFilter... fileFilters) {
		return new AndFilter(this, fileFilters);
	}

	default MultiFileFilter and(final FileFilter fileFilter) {
		return new AndFilter(this, fileFilter);
	}

	default MultiFileFilter or(final FileFilter... fileFilters) {
		return new OrFilter(this, fileFilters);
	}

	default MultiFileFilter or(final FileFilter fileFilter) {
		return new OrFilter(this, fileFilter);
	}
}
