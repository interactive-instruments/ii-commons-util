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
package de.interactive_instruments;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import de.interactive_instruments.exceptions.MimeTypeUtilsException;

/**
 * @author J. Herrmann ( herrmann <aT) interactive-instruments (doT> de )
 */
public class MimeTypeUtilsTest {

	@Test
	public void testGetParent() throws IOException, MimeTypeUtilsException {

		final ClassLoader classLoader = getClass().getClassLoader();
		final IFile xmlFile = new IFile(classLoader.getResource(
				"MimeTypeUtilsTest/xmlFile.xml").getFile());

		xmlFile.expectFileIsReadable();

		assertEquals("application/xml", MimeTypeUtils.detectMimeType(xmlFile));
	}
}
