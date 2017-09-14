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

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URI;

/**
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
public class UriModificationCheck {
	private final URI uri;
	private final Credentials credentials;
	private final boolean useHeadMethod;
	// 1 Last-Modified, 2 ETag, 3 content hash
	private final int type;
	private String expected;
	// Initial TCP handshake connection timeout: 60 seconds
	final private static int TIMEOUT = 60000;

	public UriModificationCheck(final URI uri, final Credentials credentials) throws IOException {
		this.uri = uri;
		this.credentials = credentials;

		HttpURLConnection connectionWithHead = null;
		HttpURLConnection connectionWithGet = null;
		try {
			connectionWithHead = (HttpURLConnection) UriUtils.openConnection(uri, this.credentials, TIMEOUT);
			connectionWithHead.setReadTimeout(TIMEOUT);
			connectionWithHead.setRequestMethod("HEAD");
			connectionWithHead.setInstanceFollowRedirects(true);
			final int responseCodeHead = connectionWithHead.getResponseCode();
			if (200 >= responseCodeHead && responseCodeHead < 400) {
				final String lastModified = connectionWithHead.getHeaderField("Last-Modified");
				if (!SUtils.isNullOrEmpty(lastModified)) {
					useHeadMethod = true;
					type = 1;
					expected = lastModified;
					return;
				} else {
					final String eTag = connectionWithHead.getHeaderField("ETag");
					if (!SUtils.isNullOrEmpty(eTag)) {
						useHeadMethod = true;
						type = 2;
						expected = eTag;
						return;
					}
				}
			}
			UriUtils.disconnectQuietly(connectionWithHead);
			useHeadMethod = false;
			connectionWithGet = (HttpURLConnection) UriUtils.openConnection(uri, this.credentials, TIMEOUT);
			connectionWithGet.setReadTimeout(TIMEOUT);
			connectionWithGet.setRequestMethod("GET");
			connectionWithGet.setInstanceFollowRedirects(true);
			final int responseCodeGet = connectionWithHead.getResponseCode();
			if (200 >= responseCodeGet && responseCodeGet < 400) {
				final String lastModified = connectionWithGet.getHeaderField("Last-Modified");
				if (!SUtils.isNullOrEmpty(lastModified)) {
					type = 1;
					expected = lastModified;
					return;
				} else {
					final String eTag = connectionWithGet.getHeaderField("ETag");
					if (!SUtils.isNullOrEmpty(eTag)) {
						type = 2;
						expected = eTag;
						return;
					}
				}
			}
			type = 3;
			final byte[] bytes = UriUtils.toByteArray(uri, credentials);
			expected = MdUtils.checksumAsHexStr(bytes);
		} catch (ProtocolException e) {
			UriUtils.disconnectQuietly(connectionWithHead);
			UriUtils.disconnectQuietly(connectionWithGet);
			throw new IOException(e);
		}
	}

	/**
	 * Returns the read bytes if the resource was modified or null otherwise
	 *
	 * @return ready bytes or null
	 *
	 * @throws IOException if the resource could not be accessed
	 */
	public synchronized byte[] getIfModified() throws IOException {
		if (type == 3) {
			final byte[] bytes = UriUtils.toByteArray(uri, credentials);
			final String actual = MdUtils.checksumAsHexStr(bytes);
			if (actual.equals(expected)) {
				return null;
			}
			return bytes;
		} else {
			final HttpURLConnection connection = (HttpURLConnection) UriUtils.openConnection(
					uri, this.credentials, TIMEOUT);
			connection.setReadTimeout(TIMEOUT);
			if (useHeadMethod) {
				connection.setRequestMethod("HEAD");
			} else {
				connection.setRequestMethod("GET");
			}
			if (type == 1) {
				connection.setRequestProperty("If-Unmodified-Since", expected);
			} else {
				// type 2
				connection.setRequestProperty("If-None-Match", expected);
			}
			final int responseCode = connection.getResponseCode();
			if (responseCode == 304) {
				// not modified
				return null;
			} else if (responseCode == 200) {
				final String actual;
				if (type == 1) {
					actual = connection.getHeaderField("Last-Modified");
				} else {
					// type 2
					actual = connection.getHeaderField("ETag");
				}
				if (!SUtils.isNullOrEmpty(actual) && actual.equals(expected)) {
					return null;
				}
				final byte[] bytes = UriUtils.toByteArray(connection);
				expected = actual;
				return bytes;
			} else {
				throw new IOException("Server returned HTTP response code '" + responseCode + "'");
			}
		}
	}
}
