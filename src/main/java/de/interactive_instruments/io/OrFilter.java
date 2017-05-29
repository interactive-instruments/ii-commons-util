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
import java.io.FileFilter;
import java.util.Objects;

/**
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
class OrFilter implements MultiFileFilter {

	private final FileFilter[] fileFilters;

	OrFilter(final FileFilter... fileFilters) {
		this.fileFilters = Objects.requireNonNull(fileFilters, "Filters are null");
	}

	public OrFilter(final MultiFileFilter multiFileFilter, final FileFilter[] fileFilters) {
		this.fileFilters = new FileFilter[fileFilters.length + 1];
		this.fileFilters[0] = multiFileFilter;
		for (int i = 1; i < this.fileFilters.length; i++) {
			this.fileFilters[i] = fileFilters[i];
		}
	}

	@Override
	public boolean accept(final File pathname) {
		for (int i = 0, fileFiltersLength = fileFilters.length; i < fileFiltersLength; i++) {
			if (fileFilters[i].accept(pathname)) {
				return true;
			}
		}
		return false;
	}
}
