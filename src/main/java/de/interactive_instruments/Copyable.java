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
package de.interactive_instruments;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * An interface for objects that can make copies of themselves.
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
	 * If the passed collection implements a copy Constructor Collection(Collection collection),
	 * the returned type will match the input type. Otherwise an ArrayList is returned.
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
			Collection newCollection;
			try {
				newCollection = collection.getClass().getConstructor().newInstance();
			} catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
				newCollection = new ArrayList();
			}
			for (final T t : collection) {
				newCollection.add(((Copyable) t).createCopy());
			}
			return newCollection;
		} else {
			return new ArrayList<>(collection);
		}
	}

	/**
	 * Create a copy of a set of copyable items.
	 *
	 * If the passed set implements a copy Constructor Set(Set set), the returned type will
	 * match the input type. Otherwise a {@link LinkedHashSet} is returned, if the first item implements
	 * the {@link Object#equals(Object)} method and {@link Comparable}, otherwise a {@link TreeSet}.
	 *
	 * @param set set to copy
	 * @param <T> type
	 * @return a copy of the collection of {@link Copyable} items
	 */
	static <T> Set<T> createCopy(final Set<T> set) {
		final T firstItem = Objects.requireNonNull(set, "Collection to copy is null").iterator().next();
		if (firstItem == null) {
			return new LinkedHashSet<>();
		} else if (firstItem instanceof Copyable) {
			Set newSet;
			try {
				newSet = set.getClass().getConstructor().newInstance();
			} catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
				// check if the object implements Comparable and the hash code function so
				// we can use a linked hash set
				if (firstItem instanceof Comparable &&
						ReflectionUtils.isHashable(firstItem.getClass())) {
					newSet = new TreeSet<>();
				} else {
					newSet = new LinkedHashSet<>();
				}
			}
			for (final T t : set) {
				newSet.add(((Copyable) t).createCopy());
			}
			return newSet;
		} else {
			try {
				return set.getClass().getConstructor(Map.class).newInstance(set);
			} catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
				// check if the object implements Comparable and the hash code function so
				// we can use a linked hash set
				if (firstItem instanceof Comparable &&
						ReflectionUtils.isHashable(firstItem.getClass())) {
					return new TreeSet<>(set);
				} else {
					return new LinkedHashSet<>(set);
				}
			}
		}
	}

	/**
	 * Create a copy of a map.
	 *
	 * The first item is checked, if the key and/or the value is copyable or if key and value reference
	 * the same object.
	 *
	 * If the passed map implements a copy Constructor Map(Map map), the returned type will
	 * match the input type. Otherwise a {@link LinkedHashMap} is returned, if the first item implements
	 * the {@link Object#equals(Object)} method and {@link Comparable}, otherwise a {@link TreeMap}.
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
			try {
				return map.getClass().getConstructor(Map.class).newInstance(map);
			} catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
				// check if the object implements Comparable and the hash code function so
				// we can use a linked hash map
				if (entry.getKey() instanceof Comparable &&
						ReflectionUtils.isHashable(entry.getKey().getClass())) {
					return new TreeMap<>(map);
				} else {
					return new LinkedHashMap<>(map);
				}
			}
		}
		Map newMap;
		try {
			newMap = map.getClass().getConstructor().newInstance();
		} catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
			// check if the object implements Comparable and the hash code function so
			// we can use a linked hash map
			if (entry.getKey() instanceof Comparable &&
					ReflectionUtils.isHashable(entry.getKey().getClass())) {
				newMap = new TreeMap<>();
			} else {
				newMap = new LinkedHashMap<>();
			}
		}
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
		default:
			// k and v are non copyable objects
			return new LinkedHashMap<>(map);
		}
	}

}
