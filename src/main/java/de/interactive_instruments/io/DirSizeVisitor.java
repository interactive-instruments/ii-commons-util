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

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * Calculate recursively the size of all (filtered) files in a directory.
 *
 * @author J. Herrmann ( herrmann <aT) interactive-instruments (doT> de )
 */
public class DirSizeVisitor implements FileVisitor<Path> {

	private long size = 0;
	private final PathFilter filter;

	/**
	 * Calculate size of filtered files in the directory
	 * @param filter A PathFilter or null
	 * @param filter PathFilter
	 */
	public DirSizeVisitor(PathFilter filter) {
		if (filter == null) {
			this.filter = (p) -> true;
		} else {
			this.filter = filter;
		}
	}

	/**
	 * Get calculated size
	 * @return
	 */
	public long getSize() {
		return size;
	}

	@Override
	public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
		if (filter.accept(file)) {
			size += attrs.size();
		}
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
		return FileVisitResult.CONTINUE;
	}

}
