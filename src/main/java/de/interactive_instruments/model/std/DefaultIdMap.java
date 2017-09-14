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
package de.interactive_instruments.model.std;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * An object that simplifies the mapping of Id objects to values.
 *
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
public final class DefaultIdMap<V> implements IdMap<V> {

	private final Map<Id, V> internalMap;

	/**
	 * Constructs an empty insertion-ordered <tt>DefaultIdMap</tt> instance.
	 */
	public DefaultIdMap() {
		internalMap = new LinkedHashMap<>();
	}

	/**
	 * Constructs an empty insertion-ordered <tt>DefaultIdMap</tt> instance
	 * with the specified initial capacity.
	 *
	 * @param  initialCapacity the initial capacity
	 */
	public DefaultIdMap(int initialCapacity) {
		internalMap = new LinkedHashMap<>(initialCapacity);
	}

	@Override
	public int size() {
		return internalMap.size();
	}

	@Override
	public boolean isEmpty() {
		return internalMap.isEmpty();
	}

	@Override
	public boolean internalContainsKey(Object key) {
		return internalMap.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return internalMap.containsValue(value);
	}

	@Override
	public V put(final Id key, final V value) {
		return internalMap.put(key, value);
	}

	@Override
	public V internalRemove(Object key) {
		return internalMap.remove(key);
	}

	@Override
	public void putAll(Map<? extends Id, ? extends V> m) {
		internalMap.putAll(m);
	}

	@Override
	public void clear() {
		internalMap.clear();
	}

	@Override
	public Set<Id> keySet() {
		return internalMap.keySet();
	}

	@Override
	public Collection<V> values() {
		return internalMap.values();
	}

	@Override
	public Set<Map.Entry<Id, V>> entrySet() {
		return internalMap.entrySet();
	}

	@Override
	public boolean equals(Object o) {
		return internalMap.equals(o);
	}

	@Override
	public int hashCode() {
		return internalMap.hashCode();
	}

	@Override
	public V internalGet(Object key) {
		return internalMap.get(key);
	}
}
