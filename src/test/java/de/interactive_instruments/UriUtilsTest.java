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

import static de.interactive_instruments.UriUtils.hashFromContent;
import static de.interactive_instruments.UriUtils.hashFromTimestampOrContent;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.util.*;

import org.junit.Test;

public class UriUtilsTest {

	@Test
	public void testGetParent() {

		{
			File testFile = new File("/FOO/BAR/file.txt");
			URI testUri = testFile.toURI();
			assertTrue((UriUtils.getParent(testUri).getPath()).contains("FOO"));
			assertTrue((UriUtils.getParent(testUri).getPath()).contains("BAR"));
			assertFalse(UriUtils.getParent(testUri).getPath().contains("file.txt"));
		}

		{
			// no "/" at the end
			File testFile2 = new File("/FOO/BAR");
			URI testUri2 = testFile2.toURI();
			assertTrue((UriUtils.getParent(testUri2).getPath()).contains("FOO"));
			assertFalse(UriUtils.getParent(testUri2).getPath().contains("BAR"));
		}

		{
			// "/" at the end
			File testFile3 = new File("/FOO/BAR/");
			URI testUri3 = testFile3.toURI();
			assertTrue((UriUtils.getParent(testUri3).getPath()).contains("FOO"));
			assertFalse(UriUtils.getParent(testUri3).getPath().contains("BAR"));
		}

		{
			assertEquals("/FOO/", UriUtils.getParent("/FOO/BAR"));
		}
	}

	@Test
	public void testWithoutQueryParameters() {
		assertEquals("http://foo", UriUtils.withoutQueryParameters("http://foo?bar=barr&t=a"));
	}

	@Test
	public void testGetParentLevel() {

		{
			File testFile = new File("/FOO/BAR/file.txt");
			URI testUri = testFile.toURI();
			assertTrue(UriUtils.getParent(testUri, 2).getPath().contains("FOO"));
			assertFalse(UriUtils.getParent(testUri, 2).getPath().contains("BAR"));
			assertFalse(UriUtils.getParent(testUri, 2).getPath().contains("file.txt"));
		}

		{
			File testFile2 = new File("/FOO/BAR/BAR2/BAR3");
			URI testUri2 = testFile2.toURI();
			assertTrue((UriUtils.getParent(testUri2, 3).getPath()).contains("FOO"));
			assertFalse(UriUtils.getParent(testUri2, 3).getPath().contains("BAR"));
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
	public void testGetQueryParameters() throws URISyntaxException, IOException {
		final URI url1 = new URI("http://server/service?OUTPUTFORMAT=application%2Fgml%2Bxml%3B+version%3D3.2&param2=bla");
		final Map<String, List<String>> params = UriUtils.getQueryParameters(url1, false);
		final List<String> outputformats = params.get("OUTPUTFORMAT");
		assertEquals("application/gml+xml; version=3.2", outputformats.get(0));

		final List<String> param2 = params.get("param2");
		assertEquals("bla", param2.get(0));
	}

	@Test
	public void testGetQueryParametersWithTemplateUrls() throws URISyntaxException, IOException {
		final String url1 = "http://service/service"
				+ "?spatial_dataset_identifier_code={inspire_dls:spatial_dataset_identifier_code?}"
				+ "&spatial_dataset_identifier_namespace={inspire_dls:spatial_dataset_identifier_namespace?}"
				+ "&p=p"
				+ "&q=";
		final Map<String, List<String>> params = UriUtils.getQueryParameters(url1, false);

		final List<String> spatial_dataset_identifier_code = params.get("spatial_dataset_identifier_code");
		assertEquals("{inspire_dls:spatial_dataset_identifier_code?}", spatial_dataset_identifier_code.get(0));

		final List<String> spatial_dataset_identifier_namespace = params.get("spatial_dataset_identifier_namespace");
		assertEquals("{inspire_dls:spatial_dataset_identifier_namespace?}", spatial_dataset_identifier_namespace.get(0));

		final List<String> p = params.get("p");
		assertEquals("p", p.get(0));

		final List<String> q = params.get("q");
		assertEquals("", q.get(0));
	}

	@Test
	public void testSortQueryParameters() throws URISyntaxException, IOException {
		final URI url1 = new URI("http://server/service?OUTPUTFORMAT=application%2Fgml%2Bxml%3B+version%3D3.2"
				+ "&param3=bla3&param1=bla1&param2=bla2");
		// as URI
		assertEquals("http://server/service?OUTPUTFORMAT=application%2Fgml%2Bxml%3B+version%3D3.2"
				+ "&param1=bla1&param2=bla2&param3=bla3",
				UriUtils.sortQueryParameters(url1, false).toString());
		// as String
		assertEquals("http://server/service?OUTPUTFORMAT=application%2Fgml%2Bxml%3B+version%3D3.2"
				+ "&param1=bla1&param2=bla2&param3=bla3",
				UriUtils.sortQueryParameters(url1.toString(), false));
	}

	@Test
	public void testSetQueryParameters() throws URISyntaxException, IOException {
		final URI url1 = new URI("http://server/service?OUTPUTFORMAT=application%2Fgml%2Bxml%3B+version%3D3.2&param3=bla");
		final Map<String, String> params = new HashMap<>();
		params.put("outputformat", "application/gml+xml; version=3.1");
		params.put("param2", "bli");

		// with uppercase
		assertEquals("http://server/service?OUTPUTFORMAT=application%2Fgml%2Bxml%3B+version%3D3.1&PARAM2=bli&PARAM3=bla",
				UriUtils.setQueryParameters(url1.toString(), params, true));

		assertEquals("http://server/service?OUTPUTFORMAT=application%2Fgml%2Bxml%3B+version%3D3.1&param2=bli&param3=bla",
				UriUtils.setQueryParameters(url1.toString(), params, false));
	}

	@Test
	public void testWithQueryParameters() throws URISyntaxException, IOException {
		final URI url1 = new URI("http://server/service?OUTPUTFORMAT=application%2Fgml%2Bxml%3B+version%3D3.2&param3=bla");
		final Map<String, String> params = new HashMap<>();
		params.put("outputformat", "application/gml+xml; version=3.1");
		params.put("aparam2", "bli");
		assertEquals("http://server/service?aparam2=bli&outputformat=application%2Fgml%2Bxml%3B+version%3D3.1",
				UriUtils.withQueryParameters(url1.toString(), params, false));
	}

	@Test
	public void testLastSegment() {
		assertEquals("file.txt", UriUtils.lastSegment("https://server/file.txt"));

		final String encoded = "http%3A%2F%2Fserver%2Fservice%3FOUTPUTFORMAT%3Dapplication%2Fgml%2Bxml%3B%20version%3D3.2&param2=bla";
		assertEquals("service", UriUtils.lastSegment(encoded));

		assertEquals("bar", UriUtils.lastSegment("/foo/bar/"));
	}

	@Test
	public void testDownloadFile() throws URISyntaxException, IOException {
		final URI url = new URI("http://herrmann.cx/ps-ro-50.zip");
		final IFile downloadedFile = UriUtils.download(url);
		assertEquals(2414481L, UriUtils.getContentLength(downloadedFile.toURI()));
	}

	@Test
	public void testException() throws URISyntaxException, IOException {
		final URI url = new URI("http://www.interactive-instruments.de/doesnotexist");
		boolean exceptionThrown = false;
		try (InputStream inputStream = UriUtils.openStream(url)) {

		} catch (UriUtils.ConnectionException e) {
			exceptionThrown = true;
			assertEquals(404, e.getResponseCode());
			assertNotNull(e.getErrorMessage());
			assertEquals("Returned HTTP status code was '404' (Not Found )", e.getMessage());
			assertEquals("http://www.interactive-instruments.de/doesnotexist", e.getUrl().toString());
		}
		assertTrue(exceptionThrown);
	}

	@Test
	public void testDownloadFile2() throws URISyntaxException, IOException {
		final URI url1 = new URI("http://herrmann.cx/ps-ro-50.zip");
		final IFile downloadedFile1 = UriUtils.download(url1);
		assertEquals(2414481L, UriUtils.getContentLength(downloadedFile1.toURI()));

		final URI url2 = new URI("https://www.dropbox.com/s/uewjg48vq4owwlb/ps-ro-50.zip?dl=1");
		assertEquals("ps-ro-50.zip", UriUtils.proposeFilename(url2, true));

		final IFile downloadedFile2 = UriUtils.download(url2);
		assertEquals(2414481L, UriUtils.getContentLength(downloadedFile2.toURI()));

		assertEquals(hashFromContent(downloadedFile1.toURI()),
				hashFromContent(downloadedFile2.toURI()));

		downloadedFile2.unzipTo(new IFile("/tmp/bla"));
	}

	@Test
	public void proposeFilenameFromConnection() throws URISyntaxException, IOException {
		final URI url1 = new URI("http://herrmann.cx");
		assertEquals("herrmann.cx", UriUtils.proposeFilenameFromConnection(
				(HttpURLConnection) url1.toURL().openConnection(), true));

		final URI url2 = new URI("http://herrmann.cx/index.php");
		assertEquals("index.php", UriUtils.proposeFilenameFromConnection(
				(HttpURLConnection) url2.toURL().openConnection(), true));
	}

	@Test
	public void isPrivateNet() throws UnknownHostException, URISyntaxException, MalformedURLException {
		assertTrue(UriUtils.isPrivateNet("127.0.0.1"));
		assertTrue(UriUtils.isPrivateNet("192.168.0.1"));
		assertTrue(UriUtils.isPrivateNet("192.168.131.1"));
		assertTrue(UriUtils.isPrivateNet("192.168.131.1"));
		assertFalse(UriUtils.isPrivateNet("8.8.8.8"));

		assertTrue(UriUtils.isPrivateNet("::1"));
		assertTrue(UriUtils.isPrivateNet("0000:0000:0000:0000:0000:0000:0000:0001"));
		assertTrue(UriUtils.isPrivateNet("0000:0000:0:0:0000:0:0000:0001"));
		assertTrue(UriUtils.isPrivateNet("::ffff:172.16.0.8"));

		assertFalse(UriUtils.isPrivateNet("0000:1:0:0:0000:0:0000:0001"));
		assertFalse(UriUtils.isPrivateNet("::ffff:8.8.8.8"));

		assertTrue(UriUtils.isPrivateNet(new URI("http://127.0.0.1")));
		assertFalse(UriUtils.isPrivateNet(new URI("file://here")));
	}

	@Test
	public void testHashFromContent() throws URISyntaxException, IOException {
		final URI url = new URI("https://www.dropbox.com/s/uewjg48vq4owwlb/ps-ro-50.zip?dl=1");
		assertTrue(UriUtils.exists(url));
		assertEquals("CAD2E06CB685872D", hashFromContent(url));
	}

	@Test
	public void testHashFromMultipleContent() throws URISyntaxException, IOException {
		final String expectedHash = "A502B1FBCA10DCD6";

		final Collection<URI> uris1 = new ArrayList<>();
		uris1.add(new URI("https://www.dropbox.com/s/uewjg48vq4owwlb/ps-ro-50.zip?dl=1"));
		uris1.add(new URI("https://www.dropbox.com/s/m4z4s25jerfcajx/hy-test-0.zip?dl=1"));
		assertEquals(expectedHash, hashFromContent(uris1));

		final Collection<URI> uris2 = new ArrayList<>();
		uris2.add(new URI("https://www.dropbox.com/s/m4z4s25jerfcajx/hy-test-0.zip?dl=1"));
		uris2.add(new URI("https://www.dropbox.com/s/uewjg48vq4owwlb/ps-ro-50.zip?dl=1"));
		assertEquals(expectedHash, hashFromContent(uris2));
	}

	@Test
	public void testHashFromMultipleTimeOrContent() throws URISyntaxException, IOException {
		final String expectedHash = "A502B1FBCA10DCD6";

		final Collection<URI> uris1 = new ArrayList<>();
		uris1.add(new URI("https://www.dropbox.com/s/uewjg48vq4owwlb/ps-ro-50.zip?dl=1"));
		uris1.add(new URI("https://www.dropbox.com/s/m4z4s25jerfcajx/hy-test-0.zip?dl=1"));
		assertEquals(expectedHash, hashFromTimestampOrContent(uris1));

		final Collection<URI> uris2 = new ArrayList<>();
		uris2.add(new URI("https://www.dropbox.com/s/m4z4s25jerfcajx/hy-test-0.zip?dl=1"));
		uris2.add(new URI("https://www.dropbox.com/s/uewjg48vq4owwlb/ps-ro-50.zip?dl=1"));
		assertEquals(expectedHash, hashFromTimestampOrContent(uris2));
	}

	@Test
	public void testContentLength() throws URISyntaxException, IOException {
		final URI url = new URI("https://www.dropbox.com/s/uewjg48vq4owwlb/ps-ro-50.zip?dl=1");
		assertEquals(2414481, UriUtils.getContentLength(url));
	}

	@Test
	public void testHttpExists() throws URISyntaxException, IOException {
		assertTrue(UriUtils.httpExists(new URI(
				"https://www.dropbox.com/s/m4z4s25jerfcajx/hy-test-0.zip?dl=1"), null));
	}

	@Test(expected = UriUtils.UriNotAnHttpAddressException.class)
	public void testHttpExistsException1() throws URISyntaxException, IOException {
		UriUtils.httpExists(new URI("/filepath"), null);
	}

	@Test(expected = UriUtils.UriNotAnHttpAddressException.class)
	public void testHttpExistsException2() throws URISyntaxException, IOException {
		UriUtils.httpExists(new URI("ftp://path"), null);
	}

	@Test
	public void testExists() throws URISyntaxException, IOException {
		assertTrue(UriUtils.exists(new URI(
				"https://www.dropbox.com/s/m4z4s25jerfcajx/hy-test-0.zip?dl=1"), null));
	}
}
