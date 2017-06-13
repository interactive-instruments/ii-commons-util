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

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;
import java.io.OutputStream;
import java.io.PrintWriter;

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
