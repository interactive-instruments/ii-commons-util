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

import java.io.IOException;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

import org.junit.Test;

import de.interactive_instruments.IFile;

/**
 * @author J. Herrmann ( herrmann <aT) interactive-instruments (doT> de )
 */
public class MultiThreadedFilteredFileVisitorTest {

	@Test
	public void testVisitor() throws IOException {
		final IFile files = new IFile("/Users/herrmann/Testdaten/zshh/LoD1_HH_Von_ZSHH_exportiert");
		final HashMap visitors = new HashMap<String, FileVisitor<Path>>() {
			{
				put("hash", new FileHashVisitor());
			}
		};
		final MultiThreadedFilteredFileVisitor visitor = new MultiThreadedFilteredFileVisitor(new GmlAndXmlFilter().filename(),
				null, visitors, logger);
		Files.walkFileTree(files.toPath(), visitor);
		System.out.println(((FileHashVisitor) visitors.get("hash")).getHash());
	}
}
