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
