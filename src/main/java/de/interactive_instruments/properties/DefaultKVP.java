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

import java.util.Map;

import de.interactive_instruments.SUtils;

/**
 * Simple generic key value pair
 *
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 *
 * @param <V> ValueType
 */
public class DefaultKVP<V> implements KVP<V>, Map.Entry<String, V> {

	private String key;

	protected V value;

	DefaultKVP() {}

	public DefaultKVP(String key, V value) {
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public V getValue() {
		return value;
	}

	public V setValue(V value) {
		final V oldValue = this.value;
		this.value = value;
		return oldValue;
	}

	public static DefaultKVP<String> createOrNull(String str, String regex) {
		final String[] splitted = SUtils.split2OrNull(str, regex);
		if (splitted != null) {
			return new DefaultKVP<String>(splitted[0], splitted[1]);
		}
		return null;
	}
}
