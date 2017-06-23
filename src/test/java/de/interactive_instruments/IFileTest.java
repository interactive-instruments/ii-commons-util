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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.junit.Test;

public class IFileTest {

	@Test
	public void testExpandPathDown() throws IOException {
		IFile reportDir = new IFile("/bla");

		IFile malFile1 = reportDir.secureExpandPathDown("..\\bli");
		IFile malFile2 = reportDir.secureExpandPathDown("..//bli");
		IFile malFile3 = reportDir.secureExpandPathDown("..\\\\bli");
		IFile malFile4 = reportDir.secureExpandPathDown("..////bli");

		assertFalse(malFile1.toPath().toAbsolutePath().toString().contains(".."));
		assertFalse(malFile2.toPath().toAbsolutePath().toString().contains(".."));
		assertFalse(malFile3.toPath().toAbsolutePath().toString().contains(".."));
		assertFalse(malFile4.toPath().toAbsolutePath().toString().contains(".."));

	}

	@Test
	public void testFilenameWithoutExtAndVersion() throws IOException {
		assertEquals("libraryX", IFile.getFilenameWithoutExtAndVersion("libraryX-1.1.0-SNAPSHOT.jar"));
		assertEquals("libraryX", IFile.getFilenameWithoutExtAndVersion("/dir/libraryX-1.1.0-SNAPSHOT.jar"));
		assertEquals("libraryX", IFile.getFilenameWithoutExtAndVersion("C:\\dir\\libraryX-1.1.0-SNAPSHOT.jar"));
		assertEquals("libraryX-ALPHA1", IFile.getFilenameWithoutExtAndVersion("C:\\dir\\libraryX-ALPHA1.jar"));
		assertEquals("libraryX-sub2", IFile.getFilenameWithoutExtAndVersion("libraryX-sub2-1-ALPHA1.jar"));

		assertEquals("libraryX-1", IFile.getFilenameWithoutExtAndVersion("libraryX-1"));
		assertEquals("libraryX-1", IFile.getFilenameWithoutExtAndVersion("libraryX-1.jar"));
		assertEquals("libraryX", IFile.getFilenameWithoutExtAndVersion("libraryX-1.2.jar"));
		assertEquals("libraryX", IFile.getFilenameWithoutExtAndVersion("libraryX-1-SNAPSHOT.jar"));
		assertEquals("libraryX", IFile.getFilenameWithoutExtAndVersion("libraryX-1-BETA-2.jar"));
	}

	@Test
	public void testExceptions1() throws IOException {
		IFile testFile = null;
		try {
			testFile = IFile.createTempDir("etf_junit");
		} catch (IOException e) {}
		testFile.expectDirIsWritable();
		testFile.expectDirExists();
		testFile.expectIsReadable();
		testFile.expectIsReadAndWritable();
		testFile.expectIsWritable();
	}

	@Test(expected = IOException.class)
	public void testExceptions2() throws IOException {
		new IFile("/foooo").expectDirIsReadable();
	}

	@Test
	public void testSanitize() {
		// Lorem nonsense
		assertEquals("Uebergeordnete delivrance aehnlicher Spiessigkeit manque d air",
				IFile.sanitize("Übergeordnete délivrance ähnlicher, Spießigkeit\\ manque d'air"));
	}

	@Test
	public void createBackup() throws IOException {
		final IFile tmpFile = IFile.createTempFile("etf", "junit");
		tmpFile.write(new ByteArrayInputStream("test".getBytes("UTF-8")));
		final IFile backupFile = IFile.createBackup(tmpFile);
		assertEquals("test", backupFile.readContent("UTF-8").toString());
	}

}
