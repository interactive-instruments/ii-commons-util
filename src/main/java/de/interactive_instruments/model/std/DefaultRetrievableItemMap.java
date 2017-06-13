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
 * An object that simplifies the mapping of RetrievableItem objects to values.
 *
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
public final class DefaultRetrievableItemMap<T extends RetrievableItem> implements RetrievableItemMap<T> {

	final Map<Id, T> internalMap = new LinkedHashMap<>();

	public DefaultRetrievableItemMap() {}

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
	public T internalGet(Object key) {
		return internalMap.get(key);
	}

	@Override
	public T put(T m) {
		return internalMap.put(m.getId(), m);
	}

	@Override
	public T put(Id key, T value) {
		return internalMap.put(key, value);
	}

	@Override
	public T internalRemove(Object key) {
		return internalMap.remove(key);
	}

	@Override
	public void putAll(Map<? extends Id, ? extends T> m) {
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
	public Collection<T> values() {
		return internalMap.values();
	}

	@Override
	public Set<Entry<Id, T>> entrySet() {
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
}
