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
package de.interactive_instruments.container;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import org.apache.commons.lang3.StringEscapeUtils;

import de.interactive_instruments.Configurable;
import de.interactive_instruments.IFile;
import de.interactive_instruments.MimeTypeUtils;
import de.interactive_instruments.SUtils;
import de.interactive_instruments.exceptions.*;
import de.interactive_instruments.exceptions.config.ConfigurationException;
import de.interactive_instruments.properties.ConfigProperties;
import de.interactive_instruments.properties.ConfigPropertyHolder;

/**
 * A factory which decides which Container object to construct based on the string length.
 *
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 *
 */
public final class CLenFileFactory implements ContainerFactory, Configurable {

	public final static String PROPERTY_BASE_URI = "ii.container.factory.clenfile.base.uri";
	public final static String PROPERTY_OUTPUT_DIR = "ii.container.factory.clenfile.output.dir";
	public final static String PROPERTY_FILENAME_PREFIX = "ii.container.factory.clenfile.filename.prefix";
	public final static String PROPERTY_FILENAME_ADD_EXTENSION_TO_URI = "ii.container.factory.clenfile.filename.add.uri";
	public final static String PROPERTY_MAX_INLINE_LENGTH = "ii.container.factory.clenfile.inline.max.length";
	public final static String PROPERTY_ESCAPE_INLINE = "ii.container.factory.clenfile.inline.escape";

	private final ConfigProperties configurationProperties;

	private IFile outputDir = new IFile(System.getProperty("java.io.tmpdir"));
	private URI baseUri = outputDir.toURI();
	private String filenamePrefix = "";
	private int maxInlineStrLen = 4000;
	private boolean escape = false;
	private boolean addExtensionToUri = true;

	/**
	 * Creates a new factory. The output directory is set to the default
	 * temporary directory.
	 */
	public CLenFileFactory() {
		this.configurationProperties = new ConfigProperties();
		this.configurationProperties.setProperty(PROPERTY_BASE_URI, baseUri.toString());
		this.configurationProperties.setProperty(PROPERTY_OUTPUT_DIR, outputDir.getAbsolutePath());
		this.configurationProperties.setProperty(PROPERTY_FILENAME_PREFIX, this.filenamePrefix);
		this.configurationProperties.setProperty(PROPERTY_FILENAME_ADD_EXTENSION_TO_URI,
				String.valueOf(this.addExtensionToUri));
		this.configurationProperties.setProperty(PROPERTY_MAX_INLINE_LENGTH, String.valueOf(maxInlineStrLen));
		this.configurationProperties.setProperty(PROPERTY_ESCAPE_INLINE, String.valueOf(escape));
	}

	private CLenFileFactory(final CLenFileFactory factory) {
		this.configurationProperties = factory.configurationProperties.cloneWithoutLock();
	}

	public final CLenFileFactory copy() {
		return new CLenFileFactory(this);
	}

	@Override
	public LazyLoadContainer create(String name, String str) throws ContainerFactoryException {
		return create(name, null, str);
	}

	@Override
	public LazyLoadContainer create(String name, String mimeType, String str) throws ContainerFactoryException {
		if (!isInitialized()) {
			throw new IllegalStateException(this.getClass().getName() + " not initialized");
		}
		if (str.length() < this.maxInlineStrLen &&
				(mimeType == null || (!mimeType.contains("image") &&
						(mimeType.contains("text") ||
								mimeType.contains("xml") ||
								mimeType.contains("gml"))))) {
			// Save inline
			if (this.escape) {
				return new StringDataContainer(name, StringEscapeUtils.escapeHtml4(str));
			}
			return new StringDataContainer(name, str);
		} else {
			// Dump as file
			try {
				final String fileExtension;
				if (!SUtils.isNullOrEmpty(mimeType)) {
					fileExtension = MimeTypeUtils.getFileExtensionForMimeType(mimeType);
				} else {
					fileExtension = MimeTypeUtils.detectFileExtension(str);
				}
				// Prefix + name + random uuid
				final String filename = this.filenamePrefix + UUID.randomUUID().toString();
				final IFile file = outputDir.expandPath(filename + fileExtension);
				file.writeContent(new StringBuffer(str));
				if (this.addExtensionToUri) {
					return new UrlReferenceContainer(name,
							file.length(), new URI(baseUri + filename + fileExtension).toURL(),
							mimeType, false);
				} else {
					return new UrlReferenceContainer(name,
							file.length(), new URI(baseUri + filename).toURL(),
							mimeType, false);
				}
			} catch (URISyntaxException | MimeTypeUtilsException | IOException e) {
				throw new ContainerFactoryException(e);
			}
		}
	}

	@Override
	public LazyLoadContainer createReferencedContainer(final String name, final String mimeType, final URI tempStorageUri)
			throws ContainerFactoryException {
		try {
			final IFile file = new IFile(tempStorageUri);
			file.expectFileIsReadable();
			if (file.isGZipped()) {
				final IFile unzipped = new IFile(file + ".unzipped");
				file.gunzipTo(unzipped);
				file.delete();
				unzipped.renameTo(file);
			}

			final String detMimeType;
			if (mimeType == null) {
				detMimeType = MimeTypeUtils.detectMimeType(file);
			} else {
				detMimeType = mimeType;
			}
			final String filename = file.getFilenameWithoutExt() + MimeTypeUtils.getFileExtensionForMimeType(detMimeType);
			final long size = file.length();
			file.renameTo(new File(file.getParent() + File.separator + filename));

			return new UrlReferenceContainer(name, size, new URI(baseUri + filename).toURL(), detMimeType, false);
		} catch (URISyntaxException | IOException | MimeTypeUtilsException e) {
			ExcUtils.suppress(e);
			throw new ContainerFactoryException(e);
		}
	}

	@Override
	public LazyLoadContainer createReferencedContainer(final String name, final URI uri)
			throws ContainerFactoryException {
		return createReferencedContainer(name, null, uri);
	}

	@Override
	public URI getUri() {
		return baseUri;
	}

	@Override
	public URI getNewReferencedContainerStoreUri() throws ContainerFactoryException {
		try {
			return new URI(outputDir.expandPath(this.filenamePrefix + UUID.randomUUID().toString()).getAbsolutePath());
		} catch (URISyntaxException e) {
			ExcUtils.suppress(e);
			throw new ContainerFactoryException(e);
		}
	}

	@Override
	public ConfigPropertyHolder getConfigurationProperties() {
		return this.configurationProperties;
	}

	@Override
	public void init() throws ConfigurationException, InitializationException, InvalidStateTransitionException {
		this.outputDir = new IFile(this.configurationProperties.getProperty(PROPERTY_OUTPUT_DIR));
		try {
			this.outputDir.expectDirIsWritable();
			this.maxInlineStrLen = Math.abs(Integer.parseInt(
					this.configurationProperties.getProperty(PROPERTY_MAX_INLINE_LENGTH)));
			this.escape = Boolean.valueOf(this.configurationProperties.getProperty(PROPERTY_ESCAPE_INLINE));
			String nBaseUri = this.configurationProperties.getProperty(PROPERTY_BASE_URI);
			if ('/' != (nBaseUri.charAt(nBaseUri.length() - 1))) {
				nBaseUri += "/";
			}
			this.baseUri = new URI(nBaseUri);
			this.addExtensionToUri = Boolean.valueOf(this.configurationProperties.getProperty(
					PROPERTY_FILENAME_ADD_EXTENSION_TO_URI));
		} catch (URISyntaxException | IOException | IllegalArgumentException e) {
			throw new InitializationException(e);
		}
		this.filenamePrefix = this.configurationProperties.getProperty(PROPERTY_FILENAME_PREFIX);
		configurationProperties.lock();
	}

	@Override
	public boolean isInitialized() {
		return configurationProperties.isLocked();
	}

}
