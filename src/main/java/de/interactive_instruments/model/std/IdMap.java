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
import java.util.Map;

/**
 * An interface that simplifies the mapping of Id objects to values.
 *
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
public interface IdMap<V> extends Map<Id, V> {
	default Collection<V> asCollection() {
		return values();
	}

	class StrEqContainer implements Comparable {
		private final String s;

		StrEqContainer(final Object key) {
			s = (String) key;
		}

		@Override
		public String toString() {
			return s;
		}

		@Override
		public int hashCode() {
			return s.hashCode();
		}

		@Override
		public boolean equals(final Object obj) {
			return obj.equals(s);
		}

		@Override
		public int compareTo(final Object o) {
			return s.compareTo(o.toString());
		}
	}

	/**
	 * Default interface wrapper for searching the Id map directly with strings.
	 *
	 *
	 * Returns the value to which the specified key is mapped,
	 * or {@code null} if this map contains no mapping for the key.
	 *
	 * <p>More formally, if this map contains a mapping from a key
	 * {@code k} to a value {@code v} such that {@code (key==null ? k==null :
	 * key.equals(k))}, then this method returns {@code v}; otherwise
	 * it returns {@code null}.  (There can be at most one such mapping.)
	 *
	 * <p>If this map permits null values, then a return value of
	 * {@code null} does not <i>necessarily</i> indicate that the map
	 * contains no mapping for the key; it's also possible that the map
	 * explicitly maps the key to {@code null}.  The {@link #containsKey
	 * containsKey} operation may be used to distinguish these two cases.
	 *
	 * @param key the key whose associated value is to be returned
	 * @return the value to which the specified key is mapped, or
	 *         {@code null} if this map contains no mapping for the key
	 * @throws ClassCastException if the key is of an inappropriate type for
	 *         this map
	 * (<a href="{@docRoot}/java/util/Collection.html#optional-restrictions">optional</a>)
	 * @throws NullPointerException if the specified key is null and this map
	 *         does not permit null keys
	 * (<a href="{@docRoot}/java/util/Collection.html#optional-restrictions">optional</a>)
	 */
	default V get(final Object key) {
		if (key instanceof String) {
			return _internalGet(new StrEqContainer(key));
		}
		return _internalGet(key);
	}

	V _internalGet(Object key);

	default V remove(Object key) {
		if (key instanceof String) {
			return _internalRemove(new StrEqContainer(key));
		}
		return _internalRemove(key);
	}

	V _internalRemove(Object key);

	default boolean containsKey(Object key) {
		if (key instanceof String) {
			return _internalContainsKey(new StrEqContainer(key));
		}
		return _internalContainsKey(key);
	}

	boolean _internalContainsKey(Object key);
}
