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
