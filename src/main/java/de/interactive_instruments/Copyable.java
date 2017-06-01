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
package de.interactive_instruments;

import java.util.*;

/**
 * An interface for objects that can make copies of themselves.
 *
 * NOTE: configure the properties before calling the init() method.
 *
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
public interface Copyable<T extends Copyable> {

	/**
	 * Creates a new copy of the this object.
	 *
	 * @return a new copy of the object
	 */
	T createCopy();

	/**
	 * Create a copy of a collection of copyable items.
	 *
	 * @param collection collection to copy
	 * @param <T> type
	 * @return a copy of the collection of {@link Copyable} items
	 */
	static <T> Collection<T> createCopy(final Collection<T> collection) {
		final T firstItem = Objects.requireNonNull(collection, "Collection to copy is null").iterator().next();
		if (firstItem == null) {
			return new ArrayList<>();
		} else if (firstItem instanceof Copyable) {
			final List list = new ArrayList<>();
			for (final T t : collection) {
				list.add(((Copyable) t).createCopy());
			}
			return list;
		} else {
			return new ArrayList<>(collection);
		}
	}

	/**
	 * Create a copy of a set of copyable items.
	 *
	 * @param set set to copy
	 * @param <T> type
	 * @return a copy of the collection of {@link Copyable} items
	 */
	static <T> Set<T> createCopy(final Set<T> set) {
		final T firstItem = Objects.requireNonNull(set, "Collection to copy is null").iterator().next();
		if (firstItem == null) {
			return new HashSet<>();
		} else if (firstItem instanceof Copyable) {
			final Set newSet = new LinkedHashSet<>();
			for (final T t : set) {
				newSet.add(((Copyable) t).createCopy());
			}
			return newSet;
		} else {
			return new LinkedHashSet<>(set);
		}
	}

	/**
	 * Create a copy of a map.
	 *
	 * The first item is checked, if the key and/or the value is copyable or if key and value reference
	 * the same object.
	 *
	 * @param map
	 * @param <K> value
	 * @param <V> key
	 * @return a copy of the map of {@link Copyable} keys and or {@link Copyable} values
	 */
	static <K, V> Map<K, V> createCopy(final Map<K, V> map) {
		// get first item
		final Iterator<Map.Entry<K, V>> it = Objects.requireNonNull(map, "Map to copy is null").entrySet().iterator();
		if (!it.hasNext()) {
			return new LinkedHashMap<>();
		}
		Map.Entry entry = it.next();
		// there are 5 possible cases:
		// 0: k not and v not,
		// 1: k copyable and v not,
		// 2: k not and v is copyable,
		// 3: k copyable and v copyable,
		// 4: k and v are equal objects,
		int copyStrategy = 0;
		if (entry.getKey() instanceof Copyable) {
			// key copyable
			copyStrategy = 1;
		}
		if (entry.getValue() instanceof Copyable) {
			// value copyable
			copyStrategy += 2;
		}
		// check if objects are equal if they are both copyable
		if (copyStrategy > 2 && entry.getKey() == entry.getValue()) {
			// special map: key and values are equal
			copyStrategy = 4;
		}
		if (copyStrategy == 0) {
			return new LinkedHashMap<>(map);
		}
		final LinkedHashMap newMap = new LinkedHashMap<>();
		switch (copyStrategy) {
		case 1:
			// 1: k copyable and v not
			newMap.put(((Copyable) entry.getKey()).createCopy(), entry.getValue());
			while (it.hasNext()) {
				entry = it.next();
				newMap.put(((Copyable) entry.getKey()).createCopy(), entry.getValue());
			}
			return newMap;
		case 2:
			// 2: k not and v is copyable
			newMap.put(entry.getKey(), ((Copyable) entry.getValue()).createCopy());
			while (it.hasNext()) {
				entry = it.next();
				newMap.put(entry.getKey(), ((Copyable) entry.getValue()).createCopy());
			}
			return newMap;
		case 3:
			// 3: k copyable and v copyable
			newMap.put(((Copyable) entry.getKey()).createCopy(), ((Copyable) entry.getValue()).createCopy());
			while (it.hasNext()) {
				entry = it.next();
				newMap.put(((Copyable) entry.getKey()).createCopy(), ((Copyable) entry.getValue()).createCopy());
			}
			return newMap;
		case 4:
			// 4: k and v are equal objects
			Copyable o = ((Copyable) entry).createCopy();
			newMap.put(o, o);
			while (it.hasNext()) {
				entry = it.next();
				o = ((Copyable) entry).createCopy();
				newMap.put(o, o);
			}
			return newMap;
		}
		return null;
	}

}
