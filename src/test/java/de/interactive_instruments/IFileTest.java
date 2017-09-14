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
package de.interactive_instruments;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.apache.commons.lang3.SystemUtils;
import org.junit.Test;

public class IFileTest {

	@Test
	public void testExpandPathDown() throws IOException {
		IFile reportDir = new IFile("/bla/bar");

		IFile malFile1 = reportDir.secureExpandPathDown("..\\bli");
		IFile malFile2 = reportDir.secureExpandPathDown("..//bli");
		IFile malFile3 = reportDir.secureExpandPathDown("..\\\\bli");
		IFile malFile4 = reportDir.secureExpandPathDown("..////bli");
		IFile malFile5 = reportDir.secureExpandPathDown("/bla/bar/bli");
		IFile malFile6 = reportDir.secureExpandPathDown("/bla/../bar/bli");
		IFile malFile7 = reportDir.secureExpandPathDown("/bla/bar/..//bli");
		IFile malFile8 = reportDir.secureExpandPathDown("/foo/bli");
		IFile malFile9 = reportDir.secureExpandPathDown("/foo/../bli");

		assertFalse(malFile1.toPath().toAbsolutePath().toString().contains(".."));
		assertFalse(malFile2.toPath().toAbsolutePath().toString().contains(".."));
		assertFalse(malFile3.toPath().toAbsolutePath().toString().contains(".."));
		assertFalse(malFile4.toPath().toAbsolutePath().toString().contains(".."));
		assertFalse(malFile5.toPath().toAbsolutePath().toString().contains(".."));
		assertFalse(malFile6.toPath().toAbsolutePath().toString().contains(".."));
		assertFalse(malFile7.toPath().toAbsolutePath().toString().contains(".."));
		assertFalse(malFile8.toPath().toAbsolutePath().toString().contains(".."));
		assertFalse(malFile9.toPath().toAbsolutePath().toString().contains(".."));

		if (SystemUtils.IS_OS_UNIX) {
			assertEquals(malFile1.getAbsolutePath(), "/bla/bar/bli");
			assertEquals(malFile2.getAbsolutePath(), "/bla/bar/bli");
			assertEquals(malFile3.getAbsolutePath(), "/bla/bar/bli");
			assertEquals(malFile4.getAbsolutePath(), "/bla/bar/bli");
			assertEquals(malFile5.getAbsolutePath(), "/bla/bar/bli");
			assertEquals(malFile6.getAbsolutePath(), "/bla/bar/bli");
			assertEquals(malFile7.getAbsolutePath(), "/bla/bar/bli");
			assertEquals(malFile8.getAbsolutePath(), "/bla/bar/foo/bli");
			assertEquals(malFile9.getAbsolutePath(), "/bla/bar/foo/bli");
		}
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
		// reject non SEMVER-versioned libraries
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
		if (SystemUtils.IS_OS_UNIX) {
			// Lorem nonsense
			assertEquals("Uebergeordnete: delivrance aehnlicher Spiessigkeit\\ manque d air",
					IFile.sanitize("Übergeordnete: délivrance ähnlicher, Spießigkeit\\ manque d'air"));
		} else {
			assertEquals("Uebergeordnete delivrance aehnlicher Spiessigkeit manque d air",
					IFile.sanitize("Übergeordnete: délivrance ähnlicher, Spießigkeit\\ manque d'air"));
		}
	}

	@Test
	public void createBackup() throws IOException {
		final IFile tmpFile = IFile.createTempFile("etf", "junit");
		tmpFile.write(new ByteArrayInputStream("test".getBytes("UTF-8")));
		final IFile backupFile = IFile.createBackup(tmpFile);
		assertEquals("test", backupFile.readContent("UTF-8").toString());
	}

	@Test
	public void testGetVersionedFilesInDir() throws IOException {
		// test empty dir
		final IFile tmpEmptyDir = IFile.createTempDir("ii_commons_versioned_files_junit_test");
		final IFile.VersionedFileList emptyList = tmpEmptyDir.getVersionedFilesInDir();
		assertNotNull(emptyList);
		assertNotNull(emptyList.latest());
		assertTrue(emptyList.latest().isEmpty());
		assertTrue(emptyList.isEmpty());
		assertFalse(emptyList.anyVersionExists("bla"));
		assertTrue(emptyList.isNewer("bla"));

		final IFile tmpDir = IFile.createTempDir("ii_commons_versioned_files_junit_test");

		// Latest: lib1-1.0.1-SNAPSHOT.jar
		new IFile(tmpDir, "lib1-1.0.0.jar").createNewFile();
		new IFile(tmpDir, "lib1-1.0.1.jar").createNewFile();
		new IFile(tmpDir, "lib1-1.0.1-SNAPSHOT.jar").createNewFile();

		// Latest: lib2-2.0.1-SNAPSHOT
		new IFile(tmpDir, "lib2-1.0.0").createNewFile();
		new IFile(tmpDir, "lib2-1.0.1").createNewFile();
		new IFile(tmpDir, "lib2-2.0.1-SNAPSHOT").createNewFile();

		// Latest: lib3-3.0.1
		new IFile(tmpDir, "lib3-3.0.0").createNewFile();
		new IFile(tmpDir, "lib3-3.0.0-SNAPSHOT").createNewFile();
		new IFile(tmpDir, "lib3-3.0.1").createNewFile();

		final IFile.VersionedFileList versionedFileList = tmpDir.getVersionedFilesInDir();
		assertNotNull(versionedFileList);

		// Check latest versions
		assertNotNull(versionedFileList.latest());
		assertEquals(3, versionedFileList.latest().size());
		assertEquals("lib1-1.0.1.jar", versionedFileList.latest().get(0).getName());
		assertEquals("lib2-2.0.1-SNAPSHOT", versionedFileList.latest().get(1).getName());
		assertEquals("lib3-3.0.1", versionedFileList.latest().get(2).getName());

		// unknown
		assertTrue(versionedFileList.isNewer("unknown"));
		assertFalse(versionedFileList.anyVersionExists("unknown"));
		assertTrue(versionedFileList.isNewer("unknown.jar"));
		assertFalse(versionedFileList.anyVersionExists("unknown.jar"));
		assertTrue(versionedFileList.isNewer("unknown-1.jar"));
		assertFalse(versionedFileList.anyVersionExists("unknown-1.jar"));
		assertTrue(versionedFileList.isNewer("unknown-1.0.0"));
		assertFalse(versionedFileList.anyVersionExists("unknown-1.0.0"));
		assertTrue(versionedFileList.isNewer("unknown-1.0.0.jar"));
		assertFalse(versionedFileList.anyVersionExists("unknown-1.0.0.jar"));

		// lib 1
		assertFalse(versionedFileList.isNewer("lib1.jar"));
		assertFalse(versionedFileList.isNewer("lib1-1.0.1-SNAPSHOT.jar"));
		assertFalse(versionedFileList.isNewer("lib1-1.0.1.jar"));
		assertTrue(versionedFileList.isNewer("lib1-1.0.2-SNAPSHOT.jar"));
		assertTrue(versionedFileList.isNewer("lib1-1.0.2.jar"));

		// lib 1 exists
		assertFalse(versionedFileList.anyVersionExists("lib1"));
		assertTrue(versionedFileList.anyVersionExists("lib1.jar"));
		assertTrue(versionedFileList.anyVersionExists("lib1-1.0.1-SNAPSHOT.jar"));
		assertTrue(versionedFileList.anyVersionExists("lib1-1.0.1.jar"));
		assertTrue(versionedFileList.anyVersionExists("lib1-1.0.2-SNAPSHOT.jar"));
		assertTrue(versionedFileList.anyVersionExists("lib1-1.0.2.jar"));

		// lib2
		assertFalse(versionedFileList.isNewer("lib2-2.0.1-SNAPSHOT"));
		assertTrue(versionedFileList.isNewer("lib2-2.0.2-SNAPSHOT"));
		assertFalse(versionedFileList.isNewer("lib2"));

		// lib3
		assertFalse(versionedFileList.isNewer("lib3-3.0.1"));
		assertFalse(versionedFileList.isNewer("lib3-3.0.1-SNAPSHOT"));
		assertTrue(versionedFileList.isNewer("lib3-3.0.2"));
	}

}
