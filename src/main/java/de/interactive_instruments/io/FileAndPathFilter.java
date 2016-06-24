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
import java.nio.file.Path;

/**
 *
 * @author J. Herrmann ( herrmann <aT) interactive-instruments (doT> de )
 */
public abstract class FileAndPathFilter implements FileFilter, PathFilter {

	private final FileFilter ff;

	protected FileAndPathFilter(final FileFilter ff) {
		this.ff = ff;
	}

	protected FileAndPathFilter() {
		ff = null;
	}

	public abstract boolean doAccept(final File pathname);

	@Override
	public boolean accept(final File pathname) {
		return doAccept(pathname) &&
				(ff == null || ff.accept(pathname));
	}

	@Override
	public boolean accept(final Path path) {
		return accept(path.toFile());
	}
}
