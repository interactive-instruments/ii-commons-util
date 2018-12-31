/**
 * Copyright 2017-2018 European Union, interactive instruments GmbH
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
package de.interactive_instruments.jaxb.adapters;

import java.net.URI;
import java.util.*;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import de.interactive_instruments.II_Constants;

/**
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
@XmlSeeAlso({UriMapAdapter.EntryWrapper.class})
public class UriMapAdapter extends XmlAdapter<UriMapAdapter.EntryWrapper, Map<String, URI>> {

	@XmlRootElement(name = "Uris", namespace = II_Constants.II_COMMON_UTILS_NS)
	static class Property {

		@XmlAttribute(name = "name")
		private String key;

		@XmlElement(name = "uri")
		private URI value;

		Property() {}

		Property(String name, URI value) {
			this.key = name;
			this.value = value;
		}

		String name() {
			return key;
		}

		URI value() {
			return value;
		}

	}

	static class EntryWrapper {
		@XmlElement(name = "Uri")
		private List<Property> entries = new ArrayList<>();

		List<Property> entries() {
			return Collections.unmodifiableList(entries);
		}

		void addEntry(Property entry) {
			entries.add(entry);
		}
	}

	@Override
	public Map<String, URI> unmarshal(EntryWrapper in) throws Exception {
		HashMap<String, URI> hashMap = new HashMap<>();
		for (Property entry : in.entries()) {
			hashMap.put(entry.name(), entry.value());
		}
		return hashMap;
	}

	@Override
	public EntryWrapper marshal(Map<String, URI> map) throws Exception {
		EntryWrapper props = new EntryWrapper();
		for (Map.Entry<String, URI> entry : map.entrySet()) {
			props.addEntry(new Property(entry.getKey(), entry.getValue()));
		}
		return props;
	}

}
