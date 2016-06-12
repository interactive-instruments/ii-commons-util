/**
 * Copyright 2016 interactive instruments GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.interactive_instruments;

import de.interactive_instruments.exceptions.ExcUtils;
import de.interactive_instruments.exceptions.MimeTypeUtilsException;
import de.interactive_instruments.io.FileHashVisitor;
import org.apache.tika.io.IOUtils;

import java.io.*;
import java.net.*;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * URI Utilities
 * 
 * @author J. Herrmann ( herrmann <aT) interactive-instruments (doT> de )
 *
 */
public final class UriUtils {

    // Initial TCP handshake connection timeout: 11 seconds
    final private static int TIMEOUT=11000;
    // Timeout on waiting to read data: 91 seconds
    private final static int READ_TIMEOUT=91000;

	private UriUtils() { }

	/**
	 * Return the parent location or the current URI if applicable
	 * @param uri
	 * @return 
	 */
	public static URI getParent(final URI uri) {
		return uri.getPath().endsWith("/") ? uri.resolve("..") : uri.resolve(".");
	}
	
	/**
	 * Return the parent location or the current URI if applicable
	 * @param uri
	 * @return 
	 */
	public static URI getParent(final URI uri, final int level) {
		if(level<=0) {
			return uri;
		}
		return uri.getPath().endsWith("/") ? 
				getParent(uri.resolve(".."), level - 1) :
					getParent(uri.resolve("."), level - 1);
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

  public static Map<String, List<String>> getQueryParameters(final URI uri)
      throws UnsupportedEncodingException {
    return getQueryParameters(uri.getPath());
  }

  public static Map<String, List<String>> getQueryParameters(final String url)
      throws UnsupportedEncodingException {
      final String[] urlParts = url.split("\\?");
      if (urlParts.length > 1) {
        final String query = urlParts[1];
        final String[] split = query.split("&");
        final Map<String, List<String>> params = new HashMap<>();
        for (int i = 0, splitLength = split.length; i < splitLength; i++) {
          final String param = split[i];
          final String[] pair = param.split("=");
          final String key = URLDecoder.decode(pair[0], "UTF-8").toUpperCase();
          final String value;
          if (pair.length > 1) {
            value = URLDecoder.decode(pair[1], "UTF-8").toUpperCase();
          }else{
            value="";
          }
          final List<String> values = params.get(key);
          if (values== null) {
            params.put(key, new ArrayList<String>() {{ add(value); }});
          }else {
            values.add(value);
          }

        }
        return params;
      }else{
        return Collections.emptyMap();
      }
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
    BufferedReader reader = null;
        if(!encodeBase64) {
            try {
                final InputStreamReader urlStreamReader = new InputStreamReader(
                        urlStream);
                reader = new BufferedReader(urlStreamReader);
                final char[] buf = new char[1024];
                int numRead;
                while ((numRead = reader.read(buf)) != -1) {
                    urlData.append(buf, 0, numRead);
                }
            } finally {
                IFile.closeQuietly(urlStream);
                IFile.closeQuietly(reader);
            }
            return urlData.toString();
        }else{
            byte[] bytes = IOUtils.toByteArray(urlStream);
            return Base64.getEncoder().encodeToString(bytes);
        }
	}

	public static String loadAsString(final URI uri) throws IOException {
		if(isUrl(uri)) {
			return loadFromConnection(openConnection(uri, null),false);
		}else if(isFile(uri)) {
			return new IFile(uri).readContent().toString();
		}
		return null;
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
        if(isUrl(uri)) {
            final URLConnection connection = openConnection(uri,credentials);
            return new ContentAndType(connection, encodeBase64);
        }else if(isFile(uri)) {
            final IFile file = new IFile(uri);
            try {
                return new ContentAndType(MimeTypeUtils.detectMimeType(file) ,file.readContent().toString());
            } catch (MimeTypeUtilsException e) {
                throw new IOException(e);
            }
        }
        throw new IOException("Unable to handle URI: "+uri);
    }

    private static Pattern privateNets = Pattern.compile(
            "(^127\\.)|(^192\\.168\\.)|(^10\\.)|(^172\\.1[6-9]\\.)|(^172\\.2[0-9]\\.)|" +
                    "(^172\\.3[0-1]\\.)|(^::1$)|(^[fF][cCdD])"
    );

    public static boolean isPrivateNet(URI uri) throws
            MalformedURLException, UnknownHostException
    {
        if(isFile(uri)) {
            return false;
        }

        final InetAddress address = InetAddress.getByName(uri.toURL().getHost());
        final String ip = address.getHostAddress();
        Matcher m = privateNets.matcher(ip);
        return m.find();
    }

    private static void expectAbsolute(final URI uri) throws IOException {
        if(!uri.isAbsolute()) {
            throw new IOException("URI '"+uri.toString()+"' is not absolute");
        }
    }

    private static URLConnection openConnection(final URI uri, final Credentials credentials) throws IOException {
        expectAbsolute(uri);
        final URLConnection c = uri.toURL().openConnection();
        c.setConnectTimeout(TIMEOUT);
        c.setReadTimeout(READ_TIMEOUT);
        if(credentials==null || credentials.isEmpty()) {
            return c;
        }
        c.setRequestProperty("Authorization", credentials.toBasicAuth());
        return c;
    }

    /**
     * Opens an URL connection optionally with user credentials. Times out after
     * 11 seconds.
     * @param uri URI
     * @param cred Credentials or null
     * @return
     * @throws IOException
     */
    private static InputStream openStream(final URI uri, final Credentials cred) throws IOException {
        final URLConnection c = openConnection(uri, cred);
        InputStream s = null;
        try {
            s = c.getInputStream();
        } catch (IOException e) {
            IFile.closeQuietly(s);
            throw e;
        }
        return s;
    }


    private static InputStream openStream(URI uri) throws IOException {
        return openStream(uri, null);
    }

    public static String hashFromContent(final URI uri) throws IOException {
        return hashFromContent(uri,null);
    }

	public static String hashFromContent(final URI uri, final Credentials cred) throws IOException {
		final MessageDigest md = MdUtils.getMessageDigest();
		final byte[] buffer = new byte[4096];
		InputStream stream = null;
    BufferedInputStream streamReader = null;

        try{
            if (isFile(uri)) {
                stream = new FileInputStream(new File(uri));
            }else{
                stream = openStream(uri, cred);
            }
            streamReader = new BufferedInputStream(stream);
            while(streamReader.read(buffer) != -1){
                md.update(buffer);
            }
        }finally {
            IFile.closeQuietly(stream);
            IFile.closeQuietly(streamReader);
        }
		return new String(md.digest());
	}

    public static String hashFromContent(final Collection<URI> uris) throws IOException {
        return hashFromContent(uris,null);
    }

    public static String hashFromContent(final Collection<URI> uris, Credentials cred) throws IOException {

		final MessageDigest md = MdUtils.getMessageDigest();

		final byte[] buffer = new byte[4096];
        final List<URI> sortedUris = new ArrayList<>();
        Collections.sort(sortedUris);
        InputStream urlStream = null;
        BufferedInputStream urlStreamReader = null;
        try {
            for (final URI uri : sortedUris) {
                    urlStream = openStream(uri, cred);
                    urlStreamReader = new BufferedInputStream(urlStream);
                    while (urlStreamReader.read(buffer) != -1) {
                        md.update(buffer);
                    }
                    urlStream.close();
                    urlStream=null;
                }
            }
        finally{
            IFile.closeQuietly(urlStream);
            IFile.closeQuietly(urlStreamReader);
        }
		return new String(md.digest());
	}

    private static void hashFromTimestampOrContent(final File file, final MessageDigest md) throws IOException {
        Files.walkFileTree(file.toPath(),
                EnumSet.of(FileVisitOption.FOLLOW_LINKS),5,new FileHashVisitor(null, md));
    }

    public static String hashFromTimestampOrContent(final URI uri) throws IOException {
        final MessageDigest md = MdUtils.getMessageDigest();

        if (isFile(uri)) {
            hashFromTimestampOrContent(new File(uri), md);
        }else{
            final byte[] buffer = new byte[4096];
            InputStream stream=null;
            BufferedInputStream streamReader = null;
            try {
                stream = openStream(uri);
                streamReader = new BufferedInputStream(stream);
                while (streamReader.read(buffer) != -1) {
                    md.update(buffer);
                }
            }finally{
                IFile.closeQuietly(stream);
                IFile.closeQuietly(streamReader);
            }
        }
        return new String(md.digest());
    }

    public static synchronized String hashFromTimestampOrContent(final Collection<URI> uris) throws IOException {
        return hashFromTimestampOrContent(uris, null);
    }

    public static synchronized String hashFromTimestampOrContent(final Collection<URI> uris, Credentials cred) throws IOException {

        final MessageDigest md = MdUtils.getMessageDigest();

        final byte[] buffer = new byte[4096];
        final List<URI> sortedUris = new ArrayList<>();
        sortedUris.addAll(uris);
        Collections.sort(sortedUris);
        InputStream stream=null;
        try {
            for (final URI uri : sortedUris) {
                if (isFile(uri)) {
                    hashFromTimestampOrContent(new File(uri), md);
                } else {
                    stream = openStream(uri, cred);
                    while (stream.read(buffer) != -1) {
                        md.update(buffer);
                    }
                    stream.close();
                    stream=null;
                }
            }
        }finally{
            IFile.closeQuietly(stream);
        }
        return new String(md.digest());
    }

    public static boolean exists(final URI uri) {
        if(isFile(uri)) {
            return new IFile(uri).exists();
        }else{
            try {
                final HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
                connection.setConnectTimeout(TIMEOUT);
                connection.setReadTimeout(READ_TIMEOUT);
                connection.setRequestMethod("HEAD");
                final int responseCode = connection.getResponseCode();
                return (200 >= responseCode && responseCode < 400);
            } catch (IOException | IllegalArgumentException exception) {
                // isFile() will return false if the URI scheme is null and
                // opening a connection will also fail: check if the scheme is null
                // here and return an exception in this case
                if(uri.getScheme()==null) {
                    ExcUtils.suppress(exception);
                    throw new IllegalArgumentException("URI scheme is null");
                }
                return false;
            }
        }
    }

    public static long getContentLength(final URI uri, final Credentials credentials) throws IOException {
      if(isFile(uri)) {
        return new RandomAccessFile(uri.getPath(), "r").length();
      }
      final URLConnection connection = openConnection(uri, credentials);
      return connection.getContentLength();
    }

}
