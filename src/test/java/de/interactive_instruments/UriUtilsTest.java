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

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;

public class UriUtilsTest {

	@Before
	public void setUp() throws Exception {}

	@Test
	public void testGetParent() {

		{
			File testFile = new File("/FOO/BAR/file.txt");
			URI testUri = testFile.toURI();
			assertTrue((UriUtils.getParent(testUri).getPath()).contains("FOO"));
			assertTrue((UriUtils.getParent(testUri).getPath()).contains("BAR"));
			assertTrue(!(UriUtils.getParent(testUri).getPath()).contains("file.txt"));
		}

		{
			// no "/" at the end
			File testFile2 = new File("/FOO/BAR");
			URI testUri2 = testFile2.toURI();
			assertTrue((UriUtils.getParent(testUri2).getPath()).contains("FOO"));
			assertTrue(!(UriUtils.getParent(testUri2).getPath()).contains("BAR"));
		}

		{
			// "/" at the end
			File testFile3 = new File("/FOO/BAR/");
			URI testUri3 = testFile3.toURI();
			assertTrue((UriUtils.getParent(testUri3).getPath()).contains("FOO"));
			assertTrue(!(UriUtils.getParent(testUri3).getPath()).contains("BAR"));
		}
	}

	@Test
	public void testGetParentLevel() {

		{
			File testFile = new File("/FOO/BAR/file.txt");
			URI testUri = testFile.toURI();
			assertTrue((UriUtils.getParent(testUri, 2).getPath()).contains("FOO"));
			assertTrue(!(UriUtils.getParent(testUri, 2).getPath()).contains("BAR"));
			assertTrue(!(UriUtils.getParent(testUri, 2).getPath()).contains("file.txt"));
		}

		{
			File testFile2 = new File("/FOO/BAR/BAR2/BAR3");
			URI testUri2 = testFile2.toURI();
			assertTrue((UriUtils.getParent(testUri2, 3).getPath()).contains("FOO"));
			assertTrue(!(UriUtils.getParent(testUri2, 3).getPath()).contains("BAR"));
		}
	}

	@Test
	public void testUrlDecodingEncoding() {
		final String encoded = "https%3A%2F%2Fexample.com%2Fcsw%3Fgetxml%3D%7B999someidentifier999%7D";
		final String decoded = "https://example.com/csw?getxml={999someidentifier999}";
		final String url = "https://example.com/csw?getxml=%7B999someidentifier999%7D";

		assertEquals(decoded, UriUtils.ensureUrlDecoded(decoded));
		assertEquals(decoded, UriUtils.ensureUrlDecoded(encoded));
		assertEquals(decoded, UriUtils.ensureUrlDecoded(url));

		assertEquals(encoded, UriUtils.ensureUrlEncodedOnce(decoded));
		assertEquals(encoded, UriUtils.ensureUrlEncodedOnce(encoded));
		assertEquals(encoded, UriUtils.ensureUrlEncodedOnce(url));
	}

	@Test
	public void testUrlDecodingPlusSign() {
		assertEquals("http://server/service?param=1 2", UriUtils.ensureUrlDecoded("http://server/service?param=1+2"));
		assertEquals("http://server/service?param=1+2/3", UriUtils.ensureUrlDecoded("http://server/service?param=1+2/3"));
		assertEquals("http://server/service?param=1+2",
				UriUtils.ensureUrlDecoded("http%3A%2F%2Fserver%2Fservice%3Fparam%3D1%2B2"));
		assertEquals("http://server/service?OUTPUTFORMAT=application/gml+xml; version=3.2",
				UriUtils.ensureUrlDecoded("http://server/service?OUTPUTFORMAT=application%2Fgml%2Bxml%3B+version%3D3.2"));
		assertEquals("http://server/service?OUTPUTFORMAT=application/gml+xml; version=3.2",
				UriUtils.ensureUrlDecoded("http://server/service?OUTPUTFORMAT=application/gml+xml; version=3.2"));
	}

	@Test
	public void testUrlEncodedParams() {
		final String encoded = "http%3A%2F%2Fserver%2Fservice%3FOUTPUTFORMAT%3Dapplication%2Fgml%2Bxml%3B%20version%3D3.2&param2=bla";
		final String partlyEncoded = "http://server/service?OUTPUTFORMAT=application%2Fgml%2Bxml%3B+version%3D3.2&param2=bla";
		final String notencoded = "http://server/service?OUTPUTFORMAT=application/gml+xml; version=3.2&param2=bla";
		final String expected = "http://server/service?OUTPUTFORMAT=application%2Fgml%2Bxml%3B+version%3D3.2&param2=bla";

		assertEquals(expected, UriUtils.ensureUrlEncodedParams(encoded));
		assertEquals(expected, UriUtils.ensureUrlEncodedParams(partlyEncoded));
		assertEquals(expected, UriUtils.ensureUrlEncodedParams(notencoded));
	}

	@Test
	public void testLastSegment() {
		assertEquals("file.txt", UriUtils.lastSegment("https://server/file.txt"));

		final String encoded = "http%3A%2F%2Fserver%2Fservice%3FOUTPUTFORMAT%3Dapplication%2Fgml%2Bxml%3B%20version%3D3.2&param2=bla";
		assertEquals("service", UriUtils.lastSegment(encoded));
	}

	@Test
	public void testDownloadFile() throws URISyntaxException, IOException {
		final URI url = new URI("http://jherrmann.org/ps-ro-50.zip");
		final IFile downloadedFile = UriUtils.download(url);
		assertEquals(2414481L, UriUtils.getContentLength(downloadedFile.toURI()));
	}

	@Test
	public void testDownloadFile2() throws URISyntaxException, IOException {
		final URI url1 = new URI("http://jherrmann.org/ps-ro-50.zip");
		final IFile downloadedFile1 = UriUtils.download(url1);
		assertEquals(2414481L, UriUtils.getContentLength(downloadedFile1.toURI()));

		final URI url2 = new URI("https://www.dropbox.com/s/uewjg48vq4owwlb/ps-ro-50.zip?dl=1");
		final IFile downloadedFile2 = UriUtils.download(url2);
		assertEquals(2414481L, UriUtils.getContentLength(downloadedFile2.toURI()));

		assertEquals(UriUtils.hashFromContent(downloadedFile1.toURI()),
				UriUtils.hashFromContent(downloadedFile2.toURI()));

		downloadedFile2.unzipTo(new IFile("/tmp/bla"));
	}
}
