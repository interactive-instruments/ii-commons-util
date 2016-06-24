/**
 * Copyright 2010-2016 interactive instruments GmbH
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
package de.interactive_instruments.properties;

import java.text.ParseException;
import java.util.*;
import java.util.Map.Entry;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import de.interactive_instruments.II_Constants;
import de.interactive_instruments.SUtils;
import de.interactive_instruments.jaxb.adapters.MapToListAdapter;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Properties", namespace = II_Constants.II_COMMON_UTILS_NS)
public class Properties implements MutablePropertyHolder, ClassifyingPropertyHolder {

	@XmlElement(name = "Items")
	@XmlJavaTypeAdapter(MapToListAdapter.class)
	final Map<String, String> properties;

	public Properties() {
		properties = new LinkedHashMap<>();
	}

	public Properties(final Map<String, String> map) {
		if (map == null) {
			throw new IllegalArgumentException("Map is null");
		}
		properties = map;
	}

	public Properties(final PropertyHolder propertyHolder) {
		/*
		if(propertyHolder==null) {
			throw new IllegalArgumentException("Map is null");
		}
		*/
		properties = new LinkedHashMap<>();
		if (propertyHolder != null && propertyHolder.namePropertyPairs() != null) {
			for (Entry<String, String> entry : propertyHolder.namePropertyPairs()) {
				properties.put(entry.getKey(), entry.getValue());
			}
		}
	}

	public Map<String, String> getAsMap() {
		return this.properties;
	}

	@Override
	public Map<String, String> getAsUnmodifiableMap() {
		return Collections.unmodifiableMap(this.properties);
	}

	@Override
	public String getProperty(String key) {
		return properties.get(key);
	}

	@Override
	public Set<String> getPropertyNames() {
		return properties.keySet();
	}

	@Override
	public Set<Entry<String, String>> namePropertyPairs() {
		return properties.entrySet();
	}

	@Override
	public Properties setProperty(String key, String value) {
		properties.put(key, value);
		return this;
	}

	@Override
	public void removeProperty(String key) {
		properties.remove(key);
	}

	@Override
	public int size() {
		return properties != null ? properties.size() : 0;
	}

	@Override
	public boolean isEmpty() {
		return properties == null || properties.isEmpty();
	}

	@Override
	public boolean hasProperty(final String key) {
		return properties.containsKey(key);
	}

	@Override
	public Iterator<Entry<String, String>> iterator() {
		return properties.entrySet().iterator();
	}

	@Override
	public ClassifyingPropertyHolder getPropertiesByClassification(final String classification) {
		if (SUtils.isNullOrEmpty(classification)) {
			throw new IllegalArgumentException("Classification is null");
		}
		final String clEnd = classification.endsWith(".") ? "" : ".";
		final Map<String, String> classificationMap = new LinkedHashMap<>();
		properties.forEach((k, v) -> {
			if (k.startsWith(classification + clEnd)) {
				classificationMap.put(k, v);
			}
		});
		return new Properties(classificationMap);
	}

	@Override
	public Set<String> getFirstLevelClassifications() {
		final Set<String> classifications = new TreeSet<>();
		properties.keySet().forEach(k -> {
			final String l = SUtils.leftOfSubStrOrNull(k, ".");
			if (l != null) {
				classifications.add(l);
			}
		});
		return classifications;
	}
}
