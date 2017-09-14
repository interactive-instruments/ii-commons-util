/**
 * Copyright 2017 European Union, interactive instruments GmbH
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
