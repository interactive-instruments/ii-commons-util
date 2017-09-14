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
package de.interactive_instruments.container;

import java.io.OutputStream;
import java.io.PrintWriter;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

/**
 * Stores an object as simple String.
 *
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 *
 */
@XmlRootElement(name = "StrContainer")
public class StringDataContainer implements LazyLoadContainer {

	@XmlAttribute
	private final String name;

	@XmlValue
	private final String str;

	StringDataContainer() {
		str = "";
		name = "";
	}

	public StringDataContainer(String name, String str) {
		this.name = name;
		this.str = str;
	}

	/**
	 * Always returns false
	 */
	@Override
	public boolean isReference() {
		return false;
	}

	@Override
	public String getContentType() {
		return "plain/text";
	}

	@Override
	public String getAsString() {
		return str;
	}

	@Override
	public String forceLoad() {
		return getAsString();
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void forceLoadAsStream(OutputStream outStream) {
		new PrintWriter(outStream).write(this.str);
	}
}
