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

import java.io.*;
import java.net.*;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import org.apache.commons.io.IOUtils;

import de.interactive_instruments.exceptions.ExcUtils;
import de.interactive_instruments.exceptions.MimeTypeUtilsException;
import de.interactive_instruments.io.FileHashVisitor;

/**
 * URI Utilities
 *
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 *
 */
public final class UriUtils {

	// Initial TCP handshake connection timeout: 60 seconds
	final private static int TIMEOUT = 60000;
	// Timeout on waiting to read data: 120 seconds
	private final static int READ_TIMEOUT = 120000;

	private static IFile tmpDir;

	private static IFile getTempDir() throws IOException {
		if (tmpDir == null) {
			tmpDir = IFile.createTempDir("ii_" + UUID.randomUUID().toString());
		}
		return tmpDir;
	}

	private UriUtils() {}

	public static class UriNotAbsoluteException extends IOException {
		final URI uri;

		public UriNotAbsoluteException(final String message, final URI uri) {
			super(message);
			this.uri = uri;
		}

		public URI getUri() {
			return uri;
		}
	}

	public static class ConnectionException extends IOException {

		private final int code;
		private final String responseMessage;
		private final String errorMessage;
		private final URL url;

		public ConnectionException(final IOException e, final URLConnection connection) {
			super(e);
			int codeTemp = -1;
			String responseMessageTemp = null;
			String errorMessageTemp = null;

			if (connection instanceof HttpURLConnection) {
				final HttpURLConnection c = ((HttpURLConnection) connection);
				try {
					codeTemp = c.getResponseCode();
				} catch (IOException ign) {
					ExcUtils.suppress(ign);
				}
				try {
					responseMessageTemp = c.getResponseMessage();
				} catch (IOException ign) {
					ExcUtils.suppress(ign);
				}
				try {
					if (c.getErrorStream() != null && c.getErrorStream().available() > 0) {
						errorMessageTemp = IOUtils.toString(c.getErrorStream(), "UTF-8");
					}
				} catch (IOException ign) {
					ExcUtils.suppress(ign);
				}
				url = connection.getURL();
			} else {
				url = null;
			}
			code = codeTemp;
			responseMessage = responseMessageTemp;
			errorMessage = errorMessageTemp;
		}

		public int getResponseCode() {
			return code;
		}

		public String getResponseMessage() {
			return String.valueOf(code) +
					(!SUtils.isNullOrEmpty(responseMessage) ? (" (" + responseMessage + ")") : "");
		}

		public String getErrorMessage() {
			return errorMessage;
		}

		public URL getUrl() {
			return url;
		}

		@Override
		public String getMessage() {
			if (code == -1) {
				final String message = super.getMessage();
				if (message.startsWith("java.net.UnknownHostException:")) {
					return "Unknown host: " + message.substring(31);
				} else if (message.contains("Connection refused")) {
					return "Connection refused. Please check the port you are trying to connect to "
							+ "is open or whether the port is blocked by a firewall.";
				}
				return super.getMessage();
			}
			final StringBuilder sb = new StringBuilder("Returned HTTP status code was '");
			sb.append(code);
			sb.append("'");
			if (!SUtils.isNullOrEmpty(responseMessage)) {
				sb.append(" (");
				sb.append(responseMessage);
				sb.append(" )");
			}
			sb.append(".");
			return sb.toString();
		}
	}

	/**
	 * Return the parent location or the current URI if applicable,
	 * including a trailing slash
	 *
	 * @param uri URI
	 * @return parent URI as string including a trailing slash
	 */
	public static String getParent(final String uri) {
		return getParent(URI.create(uri)).toString();
	}

	/**
	 * Return the parent location or the current URI if applicable
	 * @param uri URI
	 * @return parent URI
	 */
	public static URI getParent(final URI uri) {
		return uri.getPath().endsWith("/") ? uri.resolve("..") : uri.resolve(".");
	}

	/**
	 * Return the parent location or the current URI if applicable
	 * @param uri URI
	 * @return parent URI
	 */
	public static URI getParent(final URI uri, final int level) {
		if (level <= 0) {
			return uri;
		}
		return uri.getPath().endsWith("/") ? getParent(uri.resolve(".."), level - 1) : getParent(uri.resolve("."), level - 1);
	}

	/**
	 * Encodes an input string and makes it usable as URI
	 * @param s string to encode
	 * @return encoded String
	 */
	public static String encode(final String s) {
		try {
			return URLEncoder.encode(s, "UTF-8")
					.replaceAll("\\+", "%20")
					.replaceAll("\\%21", "!")
					.replaceAll("\\%27", "'")
					.replaceAll("\\%28", "(")
					.replaceAll("\\%29", ")")
					.replaceAll("\\%7E", "~");
		} catch (UnsupportedEncodingException e) {
			return s;
		}
	}

	/**
	 * Keys can be transformed to upper-case
	 *
	 * @param uri
	 * @return
	 */
	public static Map<String, List<String>> getQueryParameters(final URI uri, final boolean keysUpperCase) {
		return getQueryParameters(uri.toString(), keysUpperCase);
	}

	/**
	 * Keys are transformed to upper-case
	 *
	 * @param uri
	 * @return
	 */
	public static Map<String, List<String>> getQueryParameters(final URI uri) {
		return getQueryParameters(uri.toString(), true);
	}

	/**
	 * Keys can be transformed to upper-case
	 *
	 * @param url
	 * @return
	 */
	public static Map<String, List<String>> getQueryParameters(final String url, final boolean keysUpperCase) {
		final String[] urlParts = ensureUrlDecoded(url).split("\\?", 2);
		if (urlParts.length > 1) {
			final String query = urlParts[1];
			final String[] split = query.split("&amp;|&");
			final Map<String, List<String>> params = new HashMap<>();
			for (int i = 0, splitLength = split.length; i < splitLength; i++) {
				final String param = split[i];
				final String[] pair = param.split("=", 2);
				final String key;
				if (keysUpperCase) {
					key = pair[0].toUpperCase(Locale.ENGLISH);
				} else {
					key = pair[0];
				}
				final String value;
				if (pair.length > 1) {
					value = pair[1];
				} else {
					value = "";
				}
				final List<String> values = params.get(key);
				if (values == null) {
					params.put(key, new ArrayList<String>() {
						{
							add(value);
						}
					});
				} else {
					values.add(value);
				}

			}
			return params;
		} else {
			return Collections.emptyMap();
		}
	}

	/**
	 * Keys are transformed to upper-case
	 *
	 * @param url
	 * @return
	 */
	public static Map<String, List<String>> getQueryParameters(final String url) {
		return getQueryParameters(url, true);
	}

	public static String withoutQueryParameters(final String url) {
		final int paramIndex = Objects.requireNonNull(url, "URL is null").indexOf("?");
		if (paramIndex != -1) {
			return url.substring(0, paramIndex);
		}
		return url;
	}

	public static URI withoutQueryParameters(final URI uri) throws URISyntaxException {
		return new URI(uri.getScheme(),
				uri.getAuthority(),
				uri.getPath(),
				null,
				uri.getFragment());
	}

	public static String withQueryParameters(final String url, final Map<String, String> parameters) {
		return withQueryParameters(url, parameters, false);
	}

	public static String withQueryParameters(final String url, final Map<String, String> parameters,
			final boolean keysUpperCase) {
		final String urlWithoutParams = withoutQueryParameters(url);
		if (parameters != null && !parameters.isEmpty()) {
			final StringBuilder urlWithParams = new StringBuilder(urlWithoutParams + "?");
			final Iterator<Map.Entry<String, String>> it = parameters.entrySet().iterator();
			for (Map.Entry<String, String> param = it.next();;) {
				if (keysUpperCase) {
					urlWithParams.append(param.getKey().toUpperCase(Locale.ENGLISH));
				} else {
					urlWithParams.append(param.getKey());
				}
				urlWithParams.append("=").append(param.getValue());
				if (it.hasNext()) {
					urlWithParams.append("&");
					param = it.next();
				} else {
					break;
				}
			}
			return urlWithParams.toString();
		}
		return urlWithoutParams;
	}

	public static boolean isFile(final URI uri) {
		return "file".equalsIgnoreCase(uri.getScheme());
	}

	public static boolean isUrl(final URI uri) {
		return !isFile(uri) && uri.isAbsolute();
	}

	private static String loadFromConnection(final URLConnection connection, boolean encodeBase64) throws IOException {
		final StringBuffer urlData = new StringBuffer(1024);
		final InputStream urlStream = connection.getInputStream();
		if (!encodeBase64) {
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(urlStream, "UTF-8"))) {
				final char[] buf = new char[1024];
				int numRead;
				while ((numRead = reader.read(buf)) != -1) {
					urlData.append(buf, 0, numRead);
				}
			} finally {
				IFile.closeQuietly(urlStream);
			}
			return urlData.toString();
		} else {
			byte[] bytes = IOUtils.toByteArray(urlStream);
			return Base64.getEncoder().encodeToString(bytes);
		}
	}

	/**
	 * Supports gzip and deflate
	 *
	 * @param connection
	 * @return
	 * @throws IOException
	 */
	private static InputStream decodedInputStream(final URLConnection connection) throws IOException {
		final String contentTypeEncoding = connection.getContentEncoding();
		if (contentTypeEncoding != null && contentTypeEncoding.equalsIgnoreCase("gzip")) {
			return new GZIPInputStream(connection.getInputStream());
		} else if (contentTypeEncoding != null && contentTypeEncoding.equalsIgnoreCase("deflate")) {
			return new InflaterInputStream(connection.getInputStream(), new Inflater(true));
		} else {
			return connection.getInputStream();
		}
	}

	private static void streamFromConnection(final URLConnection connection, final boolean encodeBase64,
			final OutputStream outputStream) throws IOException {
		streamFromConnection(connection, encodeBase64, outputStream, false);
	}

	/**
	 * streamFromConnection
	 *
	 * @param connection
	 * @param encodeBase64
	 * @param outputStream
	 * @param decode decode before copying to output stream
	 * @throws IOException
	 */
	private static void streamFromConnection(final URLConnection connection, final boolean encodeBase64,
			final OutputStream outputStream, final boolean decode) throws IOException {
		try (final InputStream urlStream = decode == false ? connection.getInputStream() : decodedInputStream(connection)) {
			if (!encodeBase64) {
				IOUtils.copy(urlStream, outputStream);
			} else {
				final OutputStream encOutputStream = Base64.getEncoder().wrap(outputStream);
				IOUtils.copy(urlStream, encOutputStream);
			}
		}
	}

	public static String loadAsString(final URI uri, final Credentials credentials) throws IOException {
		if (isUrl(uri)) {
			return loadFromConnection(openConnection(uri, credentials), false);
		} else if (isFile(uri)) {
			return new IFile(uri).readContent().toString();
		}
		return null;
	}

	public static String loadAsString(final URI uri) throws IOException {
		return loadAsString(uri, null);
	}

	public static void stream(final URI uri, final OutputStream outputStream) throws IOException {
		stream(uri, outputStream, null);
	}

	public static void stream(final URI uri, final OutputStream outputStream, final Credentials credentials)
			throws IOException {
		if (isUrl(uri)) {
			streamFromConnection(openConnection(uri, credentials), false, outputStream);
		} else if (isFile(uri)) {
			try (final InputStream in = new FileInputStream(new File(uri))) {
				IOUtils.copy(in, outputStream);
			}
		}
	}

	public final static class ContentAndType {
		private final String content;
		private final String type;

		private ContentAndType(final URLConnection connection, boolean encodeBase64) throws IOException {
			this.type = connection.getContentType();
			this.content = loadFromConnection(connection, encodeBase64);
		}

		public ContentAndType(final String type, final String content) {
			this.content = content;
			this.type = type;
		}

		public String getContent() {
			return content;
		}

		public String getType() {
			return type;
		}
	}

	public static ContentAndType load(final URI uri, final Credentials credentials, boolean encodeBase64) throws IOException {
		if (isUrl(uri)) {
			final URLConnection connection = openConnection(uri, credentials);
			return new ContentAndType(connection, encodeBase64);
		} else if (isFile(uri)) {
			final IFile file = new IFile(uri);
			try {
				return new ContentAndType(MimeTypeUtils.detectMimeType(file), file.readContent().toString());
			} catch (MimeTypeUtilsException e) {
				throw new IOException(e);
			}
		}
		throw new IOException("Unable to handle URI: " + uri);
	}

	public static IFile download(final URI uri) throws IOException {
		return download(uri, null);
	}

	public static IFile download(final URI uri, final Credentials credentials) throws IOException {
		if (isFile(uri)) {
			return new IFile(uri);
		}
		HttpURLConnection connection = null;
		try {
			connection = (HttpURLConnection) openConnection(uri, credentials);
			final int responseCode = connection.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				final String fileName = proposeFilenameFromConnection(connection, true);
				final IFile tmpFile = getTempDir().expandPath(fileName);
				if (tmpFile.exists()) {
					if (!tmpFile.delete()) {
						// todo: log this
					}
				}
				downloadTo(connection, tmpFile);
				return tmpFile;
			} else {
				throw new IOException("Could not download file. Server response code: " + responseCode);
			}
		} finally {
			disconnectQuietly(connection);
		}
	}

	public static IFile downloadTo(final URI uri, final IFile destination) throws IOException {
		return downloadTo(uri, destination, null);
	}

	public static IFile downloadTo(final URI uri, final IFile destination, final Credentials credentials) throws IOException {
		if (!destination.isDirectory()) {
			if (destination.exists()) {
				throw new IOException("Cannot download file form " + uri.toString() + " as destination file "
						+ destination.getPath() + " already exists");
			}
			if (isFile(uri)) {
				return new IFile(uri).copyTo(destination.getPath());
			}
		} else if (isFile(uri)) {
			return new IFile(uri).copyTo(destination.secureExpandPathDown(UriUtils.lastSegment(uri.getPath())).getPath());
		}

		HttpURLConnection connection = null;
		try {
			connection = (HttpURLConnection) openConnection(uri, credentials);
			return downloadTo(connection, destination);
		} finally {
			disconnectQuietly(connection);
		}
	}

	private static IFile downloadTo(final HttpURLConnection connection, final IFile destination) throws IOException {
		final int responseCode = connection.getResponseCode();
		if (responseCode == HttpURLConnection.HTTP_OK) {
			final IFile destinationFile;
			if (destination.isDirectory()) {
				destinationFile = destination.secureExpandPathDown(proposeFilenameFromConnection(connection, true));
			} else {
				destinationFile = destination;
			}

			// 4 possible cases:
			// - handle gziped content
			// - handle deflated content
			// - handle content in a special charset
			// - handle content without any encoding information

			final String contentTypeEncoding = connection.getContentEncoding();
			if (contentTypeEncoding != null && contentTypeEncoding.equalsIgnoreCase("gzip")) {
				destinationFile.write(new GZIPInputStream(connection.getInputStream()));

			} else if (contentTypeEncoding != null && contentTypeEncoding.equalsIgnoreCase("deflate")) {
				destinationFile.write(new InflaterInputStream(connection.getInputStream(), new Inflater(true)));
			} else {
				if (contentTypeEncoding != null) {
					try {
						Charset.forName(contentTypeEncoding);
						destinationFile.writeContent(connection.getInputStream(), contentTypeEncoding);
					} catch (UnsupportedCharsetException ign) {
						ExcUtils.suppress(ign);
						destinationFile.write(connection.getInputStream());
					}
				} else {
					destinationFile.write(connection.getInputStream());
				}
			}
			return destinationFile;
		} else {
			throw new IOException("Could not download file. Server response code: " + responseCode);
		}
	}

	// from
	private static final Pattern CONTENT_DISPOSITION_PATTERN = Pattern.compile("attachment;\\s*filename\\s*=\\s*\"([^\"]*)\"");

	/**
	 * Returns the file name from the connections Content-Disposition header or from the last URL segment
	 *
	 * @return
	 */
	public static String proposeFilename(final URI uri, boolean proposeFileExtension) throws IOException {
		return proposeFilenameFromConnection((HttpURLConnection) openConnection(uri, null), proposeFileExtension);
	}

	/**
	 * Returns the file name from the connections Content-Disposition header or from the last URL segment
	 *
	 * @return
	 */
	public static String proposeFilename(final URI uri, final Credentials credentials, boolean proposeFileExtension)
			throws IOException {
		return proposeFilenameFromConnection((HttpURLConnection) openConnection(uri, credentials), proposeFileExtension);
	}

	/**
	 * Returns the file name from the connections Content-Disposition header, from the last URL segment or the hostname
	 *
	 * @return
	 */
	public static String proposeFilenameFromConnection(final HttpURLConnection connection, boolean proposeFileExtension) {
		final String disposition = connection.getHeaderField("Content-Disposition");
		final String url = connection.getURL().toString();
		final String name;
		if (!SUtils.isNullOrEmpty(disposition)) {
			final Matcher m = CONTENT_DISPOSITION_PATTERN.matcher(disposition);
			if (m.find()) {
				name = m.group(1);
			} else {
				name = lastSegment(url);
			}
		} else if (url.indexOf('/', 7) != -1) {
			// take last segment after slash
			name = lastSegment(url);
		} else {
			// take domain name
			final String hostName = connection.getURL().getHost();
			name = hostName.startsWith("www.") ? hostName.substring(4) : hostName;
		}

		// add a file extension if nescessary
		if (proposeFileExtension && name.indexOf(".") == -1) {
			final String contentType = connection.getHeaderField("Content-Type");
			if (!SUtils.isNullOrEmpty(contentType)) {
				try {
					final String ext = MimeTypeUtils.getFileExtensionForMimeType(contentType);
					return IFile.sanitize(name + ext);
				} catch (MimeTypeUtilsException ign) {
					ExcUtils.suppress(ign);
				}
			}
		}
		return IFile.sanitize(name);
	}

	/**
	 * Return file name minus the path and parameters from a full URL
	 *
	 * @return
	 */
	public static String lastSegment(final String url) {
		if (SUtils.isNullOrEmpty(url)) {
			return null;
		}
		final String decUrl = ensureUrlDecoded(url);

		final int qPos = decUrl.indexOf('?');
		final int end = qPos != -1 ? qPos : SUtils.lastIndexOfNot(decUrl, decUrl.length(), '/');
		final int sPos = decUrl.lastIndexOf('/', end - 2);
		final int beg = sPos != -1 ? sPos + 1 : 0;
		return decUrl.substring(beg, end);
	}

	private static Pattern privateNets = Pattern.compile(
			"(127\\.)|"
					+ "(^172\\.1[6-9]\\.)|(^172\\.2[0-9]\\.)|(^172\\.3[0-1]\\.)|"
					+ "(^192\\.168\\.)|"
					+ "(^10\\.)|"
					+ "(^(0{0,4}:){1,7}(0{0,3}1$))");

	/**
	 * Checks if the resource points to a private net. Supports IPv6.
	 *
	 * @param uri
	 * @return
	 * @throws MalformedURLException
	 * @throws UnknownHostException
	 */
	public static boolean isPrivateNet(final URI uri) throws MalformedURLException {
		if (isFile(uri)) {
			return false;
		}
		try {
			return isPrivateNet(uri.toURL().getHost());
		} catch (final UnknownHostException e) {
			// otherwise names can be checked in the lan
			return false;
		}
	}

	public static boolean isPrivateNet(final String host) throws UnknownHostException {
		final String ip = InetAddress.getByName(host).getHostAddress();
		return privateNets.matcher(ip).find();
	}

	private static void expectAbsolute(final URI uri) throws UriNotAbsoluteException {
		if (!uri.isAbsolute()) {
			throw new UriNotAbsoluteException("URI '" + uri.toString() + "' is not absolute", uri);
		}
	}

	private static URLConnection openConnection(final URI uri, final Credentials credentials) throws IOException {
		return openConnection(uri, credentials, READ_TIMEOUT);
	}

	private static URLConnection openConnection(final URI uri, final Credentials credentials, final int readTimeout)
			throws IOException {
		expectAbsolute(uri);
		final URLConnection c = uri.toURL().openConnection();
		c.setConnectTimeout(TIMEOUT);
		c.setReadTimeout(readTimeout);
		if (credentials == null || credentials.isEmpty()) {
			return c;
		}
		c.setRequestProperty("Authorization", credentials.toBasicAuth());
		return c;
	}

	public static InputStream openStream(final URI uri, final Credentials cred) throws IOException {
		return openStream(uri, cred, READ_TIMEOUT);
	}

	public static InputStream openStream(final URI uri, final Credentials cred, final int timeout) throws IOException {
		final URLConnection c = openConnection(uri, cred, timeout);
		InputStream s = null;

		try {
			s = c.getInputStream();
		} catch (SocketTimeoutException e) {
			IFile.closeQuietly(s);
			throw e;
		} catch (IOException e) {
			IFile.closeQuietly(s);
			throw new ConnectionException(e, c);
		}
		return s;
	}

	public static InputStream openStream(URI uri) throws IOException {
		return openStream(uri, null, READ_TIMEOUT);
	}

	public static String hashFromContent(final URI uri) throws IOException {
		return hashFromContent(uri, null);
	}

	public static String hashFromContent(final URI uri, final Credentials cred) throws IOException {
		final MessageDigest md = MdUtils.getMessageDigest();
		final byte[] buffer = new byte[4096];
		InputStream stream = null;
		BufferedInputStream streamReader = null;

		try {
			if (isFile(uri)) {
				stream = new FileInputStream(new File(uri));
			} else {
				stream = openStream(uri, cred, READ_TIMEOUT);
			}
			streamReader = new BufferedInputStream(stream);
			while (streamReader.read(buffer) != -1) {
				md.update(buffer);
			}
		} finally {
			IFile.closeQuietly(stream);
			IFile.closeQuietly(streamReader);
		}
		return new String(md.digest(), "UTF-8");
	}

	public static String hashFromContent(final Collection<URI> uris) throws IOException {
		return hashFromContent(uris, null);
	}

	public static String hashFromContent(final Collection<URI> uris, final Credentials cred) throws IOException {

		final MessageDigest md = MdUtils.getMessageDigest();

		final byte[] buffer = new byte[4096];
		final List<URI> sortedUris = new ArrayList<>();
		Collections.sort(sortedUris);
		InputStream urlStream = null;
		BufferedInputStream urlStreamReader = null;
		try {
			for (final URI uri : sortedUris) {
				urlStream = openStream(uri, cred, READ_TIMEOUT);
				urlStreamReader = new BufferedInputStream(urlStream);
				while (urlStreamReader.read(buffer) != -1) {
					md.update(buffer);
				}
				urlStream.close();
				urlStream = null;
			}
		} finally {
			IFile.closeQuietly(urlStream);
			IFile.closeQuietly(urlStreamReader);
		}
		return new String(md.digest(), "UTF-8");
	}

	private static void hashFromTimestampOrContent(final File file, final MessageDigest md) throws IOException {
		Files.walkFileTree(file.toPath(),
				EnumSet.of(FileVisitOption.FOLLOW_LINKS), 5, new FileHashVisitor(null, md));
	}

	public static String hashFromTimestampOrContent(final URI uri) throws IOException {
		final MessageDigest md = MdUtils.getMessageDigest();

		if (isFile(uri)) {
			hashFromTimestampOrContent(new File(uri), md);
		} else {
			final byte[] buffer = new byte[4096];
			InputStream stream = null;
			BufferedInputStream streamReader = null;
			try {
				stream = openStream(uri);
				streamReader = new BufferedInputStream(stream);
				while (streamReader.read(buffer) != -1) {
					md.update(buffer);
				}
			} finally {
				IFile.closeQuietly(stream);
				IFile.closeQuietly(streamReader);
			}
		}
		return new String(md.digest(), "UTF-8");
	}

	public static synchronized String hashFromTimestampOrContent(final Collection<URI> uris) throws IOException {
		return hashFromTimestampOrContent(uris, null);
	}

	public static synchronized String hashFromTimestampOrContent(final Collection<URI> uris, Credentials cred)
			throws IOException {

		final MessageDigest md = MdUtils.getMessageDigest();

		final byte[] buffer = new byte[4096];
		final List<URI> sortedUris = new ArrayList<>();
		sortedUris.addAll(uris);
		Collections.sort(sortedUris);
		InputStream stream = null;
		try {
			for (final URI uri : sortedUris) {
				if (isFile(uri)) {
					hashFromTimestampOrContent(new File(uri), md);
				} else {
					stream = openStream(uri, cred, READ_TIMEOUT);
					while (stream.read(buffer) != -1) {
						md.update(buffer);
					}
					stream.close();
					stream = null;
				}
			}
		} finally {
			IFile.closeQuietly(stream);
		}
		return new String(md.digest(), "UTF-8");
	}

	public static void disconnectQuietly(final HttpURLConnection connection) {
		if (connection != null) {
			try {
				connection.disconnect();
			} catch (Exception e) {
				ExcUtils.suppress(e);
			}
		}
	}

	public static boolean exists(final URI uri) {
		return exists(uri, null);
	}

	public static boolean exists(final URI uri, final Credentials cred) {
		if (isFile(uri)) {
			return new IFile(uri).exists();
		} else {

			HttpURLConnection connection = null;
			try {
				connection = (HttpURLConnection) openConnection(uri, cred);
				connection.setConnectTimeout(TIMEOUT);
				connection.setReadTimeout(READ_TIMEOUT);
				connection.setRequestMethod("GET");
				final int responseCode = connection.getResponseCode();
				return 200 >= responseCode && responseCode < 400;
			} catch (IOException | IllegalArgumentException exception) {
				disconnectQuietly(connection);
				// isFile() will return false if the URI scheme is null and
				// opening a connection will also fail: check if the scheme is null
				// here and return an exception in this case
				if (uri.getScheme() == null) {
					ExcUtils.suppress(exception);
					throw new IllegalArgumentException("URI scheme is null");
				}
				return false;
			}
		}
	}

	public static long getContentLength(final URI uri) throws IOException {
		return getContentLength(uri, null);
	}

	public static long getContentLength(final URI uri, final Credentials credentials) throws IOException {
		if (isFile(uri)) {
			return new RandomAccessFile(uri.getPath(), "r").length();
		}
		final URLConnection connection = openConnection(uri, credentials);
		return connection.getContentLength();
	}

	public static URI encodedUri(final String uri) {
		return URI.create(ensureUrlEncodedParams(uri));
	}

	/**
	 * Ensure that the URL parameters are encoded once
	 *
	 * @param url
	 * @return
	 */
	public static String ensureUrlEncodedParams(final String url) {
		try {
			final String decodedUrl = ensureUrlDecoded(url);
			final int paramIndex = decodedUrl.indexOf("?");
			if (paramIndex != -1) {
				final StringBuilder newUrl = new StringBuilder(url.length());
				newUrl.append(decodedUrl.substring(0, paramIndex + 1));
				final String[] split = decodedUrl.substring(paramIndex + 1).split("&amp;|&");
				int i = 0;
				while (true) {
					final String param = split[i];
					final int pos = param.indexOf("=");
					if (pos == -1) {
						newUrl.append(param);
					} else if (pos == param.length() - 1) {
						newUrl.append(param);
						newUrl.append("=");
					} else {
						newUrl.append(param.substring(0, pos));
						newUrl.append("=");
						newUrl.append(URLEncoder.encode(param.substring(pos + 1), "UTF-8"));
					}
					if (++i < split.length) {
						newUrl.append("&");
					} else {
						break;
					}
				}
				return newUrl.toString();
			}
			return url;
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("UTF-8 not supported: " + e);
		}
	}

	/**
	 * Ensure that an URL is encoded only once
	 *
	 * @param url
	 * @return
	 */
	public static String ensureUrlEncodedOnce(final String url) {
		try {
			return URLEncoder.encode(ensureUrlDecoded(url), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("UTF-8 not supported: " + e);
		}
	}

	// does not contain +
	private static String unsafeChars = " '!?()*$,/:;@<>#%[]";

	private static boolean isUnsafe(final char ch) {
		return (ch > 128 || ch == 0) || unsafeChars.indexOf(ch) >= 0;
	}

	private static boolean containsUnsafeChars(final String str) {
		for (int i = 0; i < str.length(); i++) {
			if (isUnsafe(str.charAt(i))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Ensure that an URL is decoded. Preserves plus signs.
	 *
	 * @param url
	 * @return encoded URL
	 */
	public static String ensureUrlDecoded(final String url) {
		try {
			final String decoded = URLDecoder.decode(url, "UTF-8");
			if (url.length() == decoded.length() && url.contains("+")) {
				int paramIndex = url.indexOf("?");
				if (paramIndex != -1 && containsUnsafeChars(url.substring(paramIndex + 1))) {
					return url;
				}
			}
			return decoded;
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("UTF-8 not supported: " + e);
		}
	}
}
