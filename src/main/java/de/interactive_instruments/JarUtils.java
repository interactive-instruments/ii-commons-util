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
package de.interactive_instruments;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author J. Herrmann ( herrmann <aT) interactive-instruments (doT> de )
 */
public class JarUtils {
	private JarUtils() {}

	/**
	 * Returns a list of all class names in a jar
	 */
	public static List<String> scanForClassNames(File jar) throws IOException {
		// The trick is here to not load the Jar with the class loader but to extract it as ZIP
		final List<String> classNames = new ArrayList<String>();
		ZipInputStream zip = null;
		try {
			zip = new ZipInputStream(new FileInputStream(jar));
			for (ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()) {
				if (entry.getName().endsWith(".class") && !entry.isDirectory()) {
					StringBuilder className = new StringBuilder();

					// Extract full qualified class name from the path entry by
					// replacing / with .
					for (String part : entry.getName().split("/")) {
						if (className.length() != 0) {
							className.append(".");
						}
						className.append(part);
						if (part.endsWith(".class")) {
							className.setLength(className.length() - ".class".length());
						}
					}
					classNames.add(className.toString());
				}
			}
		} finally {
			IFile.closeQuietly(zip);
		}
		return classNames;
	}
}
