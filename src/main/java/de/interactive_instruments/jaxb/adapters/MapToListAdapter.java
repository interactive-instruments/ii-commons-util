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
package de.interactive_instruments.jaxb.adapters;

import java.lang.reflect.Modifier;
import java.util.*;

import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * MapToListAdapter that supports un-/marshal simple maps with <String, object>
 * pairs or <String, Collection> pairs.
 * @param <V> Map value
 *
 */

@XmlSeeAlso({MapToListAdapter.CollectionEntry.class})
public class MapToListAdapter<V> extends XmlAdapter<MapToListAdapter.RefObjectList<V>, Map<String, V>> {

	interface EntryInterface<V> {
		String getKey();

		V getValue();
	}

	@XmlRootElement(name = "Collection")
	static class CollectionEntry<V extends Collection<?>> implements EntryInterface<V> {

		@XmlAttribute(name = "name")
		private String key;

		@XmlElementWrapper(name = "Item")
		private V value;

		CollectionEntry() {}

		public CollectionEntry(String key, V value) {
			this.key = key;
			this.value = value;
		}

		public String getKey() {
			return key;
		}

		public V getValue() {
			return value;
		}
	}

	@XmlRootElement(name = "Item")
	static class SimpleEntry<V> implements EntryInterface<V> {

		@XmlAttribute(name = "name")
		private String key;

		@XmlElement(name = "value")
		private V value;

		SimpleEntry() {}

		public SimpleEntry(String key, V value) {
			this.key = key;
			this.value = value;
		}

		public String getKey() {
			return key;
		}

		public V getValue() {
			return value;
		}
	}

	@XmlSeeAlso({MapToListAdapter.CollectionEntry.class, MapToListAdapter.SimpleEntry.class})
	static class RefObjectList<V> {
		@SuppressWarnings("rawtypes")

		@XmlElements({
				@XmlElement(name = "Collection", type = CollectionEntry.class),
				@XmlElement(name = "Item", type = SimpleEntry.class)})
		private List<EntryInterface<V>> ids;

		RefObjectList() {
			ids = new ArrayList<EntryInterface<V>>();
		}

		public RefObjectList(int size) {
			ids = new ArrayList<EntryInterface<V>>(size);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, V> unmarshal(RefObjectList<V> refObjectList) throws Exception {
		final Map<String, V> map = new LinkedHashMap<>(refObjectList.ids.size());
		for (EntryInterface<?> entry : refObjectList.ids) {
			map.put(entry.getKey(), (V) entry.getValue());
		}
		return map;
	}

	@Override
	public RefObjectList<V> marshal(Map<String, V> map) throws Exception {
		if (map != null && map.values().iterator().hasNext()) {
			final RefObjectList<V> refObjectList = new RefObjectList<V>(map.size());
			// Get one element from the values to check if it is a collection.
			final Object o = map.values().iterator().next();
			final Class clasz = o.getClass();
			if (clasz.isInterface() || Modifier.isAbstract(clasz.getModifiers())) {
				throw new JAXBException("Can not marshal interface or abstract class " +
						o.getClass() + " via MapToListAdapter");
			}

			for (final Map.Entry<String, V> mapEntry : map.entrySet()) {
				// if(typeName.equals("AbstractList")) {
				if (o instanceof Collection) {
					final CollectionEntry<Collection<?>> kvp = new CollectionEntry<Collection<?>>(
							mapEntry.getKey(), (Collection<?>) mapEntry.getValue());
					refObjectList.ids.add((EntryInterface<V>) kvp);
				} else {
					final SimpleEntry<V> kvp = new SimpleEntry<V>(
							mapEntry.getKey(), mapEntry.getValue());
					refObjectList.ids.add(kvp);
				}
			}
			return refObjectList;
		} else {
			return new RefObjectList<V>(0);
		}
	}
}
