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
package de.interactive_instruments.io;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

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
