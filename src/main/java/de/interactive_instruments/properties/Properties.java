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
package de.interactive_instruments.properties;

import de.interactive_instruments.SUtils;

import java.util.*;
import java.util.Map.Entry;

public class Properties implements MutablePropertyHolder, ClassifyingPropertyHolder {

	private Map<String, Property> properties;

	public static class Property {
		private String name;
		private String value;

		public Property() {}

		public Property(final String name, final String value) {
			this.name = name;
			this.value = value;
		}

		public String getName() {
			return name;
		}

		public String getValue() {
			return value;
		}
	}

	public Properties() {
		properties = new LinkedHashMap<>();
	}

	public Properties(final Map<String, String> map) {
		set(map);
	}

	private void set(final Map<String, String> map) {
		if (map == null) {
			throw new IllegalArgumentException("Map is null");
		}
		map.entrySet().forEach(e -> properties.put(e.getKey(), new Property(e.getKey(), e.getValue())));
	}

	public Properties(final PropertyHolder propertyHolder) {
		properties = new LinkedHashMap<>();
		if (propertyHolder != null && propertyHolder.namePropertyPairs() != null) {
			for (Entry<String, String> entry : propertyHolder.namePropertyPairs()) {
				properties.put(entry.getKey(), new Property(entry.getKey(), entry.getValue()));
			}
		}
	}

	private Map<String, String> getAsMap() {
		final Map<String, String> vals = new HashMap<>();
		properties.entrySet().forEach(e -> vals.put(e.getKey(), e.getValue().getValue()));
		return Collections.unmodifiableMap(vals);
	}

	@Override
	public String getProperty(String key) {
		final Property val = properties.get(key);
		return val != null ? val.getValue() : null;
	}

	@Override
	public Set<String> getPropertyNames() {
		return properties.keySet();
	}

	@Override
	public Set<Entry<String, String>> namePropertyPairs() {
		return getAsMap().entrySet();
	}

	@Override
	public Properties setProperty(String key, String value) {
		properties.put(key, new Property(key, value));
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
		return getAsMap().entrySet().iterator();
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
				classificationMap.put(k, v.getValue());
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
