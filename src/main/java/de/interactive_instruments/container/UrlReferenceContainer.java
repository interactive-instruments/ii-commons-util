/**
 * Copyright 2017-2019 European Union, interactive instruments GmbH
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
package de.interactive_instruments.container;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import de.interactive_instruments.UriUtils;
import de.interactive_instruments.exceptions.ExcUtils;

/**
 * The container references an object with an URL.
 *
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 *
 */
@XmlRootElement(name = "UrlReferenceContainer")
public class UrlReferenceContainer implements LazyLoadContainer {

	@XmlAttribute
	String name;

	@XmlAttribute
	String contentType;

	@XmlAttribute
	URL referenceURL;

	@XmlAttribute
	boolean loadDataOnDemand;

	@XmlTransient
	long contentSize;

	UrlReferenceContainer() {}

	UrlReferenceContainer(final String name, final long size, final URL referenceURL, final String contentType,
			final boolean loadDataOnDemand) {
		this.name = name;
		this.loadDataOnDemand = loadDataOnDemand;
		this.referenceURL = referenceURL;
		this.contentSize = size;
	}

	public UrlReferenceContainer(final String name, final URL referenceURL, final String contentType,
			final boolean loadDataOnDemand) {
		this.name = name;
		this.loadDataOnDemand = loadDataOnDemand;
		this.referenceURL = referenceURL;
	}

	@Override
	public boolean isReference() {
		return true;
	}

	@Override
	public String getContentType() {
		return contentType;
	}

	@Override
	public String getAsString() throws IOException {
		if (loadDataOnDemand) {
			return referenceURL.toString();
		}
		return loadData();
	}

	@Override
	public String forceLoad() throws IOException {
		return loadData();
	}

	/**
	 * Returns the size of the referenced URL
	 * -2 if an error occurred
	 * -1 if the size is unknown
	 * @return file size, -2 if an error occurred, -1 if the size is unknown
	 */
	@XmlAttribute(name = "size")
	public long getSizeOrErrorCode() {
		if (this.contentSize > 0) {
			return this.contentSize;
		}
		try {
			return UriUtils.getContentLength(referenceURL.toURI(), null);
		} catch (IOException | URISyntaxException e) {
			ExcUtils.suppress(e);
			return -2;
		}
	}

	private String loadData() throws IOException {
		try {
			return UriUtils.loadAsString(referenceURL.toURI());
		} catch (URISyntaxException e) {
			ExcUtils.suppress(e);
			return "InvalidUrlReferenceContainer";
		}
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void forceLoadAsStream(OutputStream outStream) throws IOException {
		final InputStream urlStream = referenceURL.openStream();
		final byte[] buffer = new byte[1024];
		int bytesRead;
		while ((bytesRead = urlStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, bytesRead);
		}
		urlStream.close();
	}

	@Override
	public String toString() {
		return "UrlReferenceContainer{" + "name='" + name + '\'' +
				", contentType='" + contentType + '\'' +
				", referenceURL=" + referenceURL +
				", loadDataOnDemand=" + loadDataOnDemand +
				'}';
	}
}
