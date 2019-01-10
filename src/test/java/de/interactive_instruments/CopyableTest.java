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
package de.interactive_instruments;

import static org.junit.Assert.*;

import java.util.*;

import org.junit.Test;

/**
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
public class CopyableTest {

	private static class CopyableObj implements Copyable<CopyableObj>, Comparable<CopyableObj> {
		protected String property;

		public CopyableObj(final String property) {
			this.property = property;
		}

		public String getProperty() {
			return property;
		}

		public void setProperty(final String property) {
			this.property = property;
		}

		@Override
		public CopyableObj createCopy() {
			return new CopyableObj(property);
		}

		@Override
		public int compareTo(final CopyableObj o) {
			return property.compareTo(o.property);
		}
	}

	private static class CopyableObjWithHashCode extends CopyableObj {
		public CopyableObjWithHashCode(final String property) {
			super(property);
		}

		@Override
		public CopyableObj createCopy() {
			return new CopyableObjWithHashCode(property);
		}

		@Override
		public int hashCode() {
			return property.hashCode();
		}

		@Override
		public boolean equals(final Object obj) {
			if (obj instanceof CopyableObjWithHashCode) {
				return property.equals(((CopyableObjWithHashCode) obj).getProperty());
			}
			return false;
		}
	}

	@Test
	public void testCopyableMaps() {
		{
			final Map<String, String> map = new HashMap<>();
			map.put("1", "10");
			map.put("2", "20");

			final Map<String, String> copiedMap = Copyable.createCopy(map);
			assertEquals(map.getClass().getName(), copiedMap.getClass().getName());
			assertEquals(2, copiedMap.size());
			map.clear();
			assertEquals("10", copiedMap.get("1"));
			assertEquals("20", copiedMap.get("2"));
		}
		{
			final Map<String, CopyableObj> map = new HashMap<>();
			map.put("1", new CopyableObj("prop1"));
			map.put("2", new CopyableObj("prop2"));

			final Map<String, CopyableObj> copiedMap = Copyable.createCopy(map);
			assertEquals(map.getClass().getName(), copiedMap.getClass().getName());
			assertEquals(2, copiedMap.size());
			assertEquals("prop1", copiedMap.get("1").getProperty());
			assertEquals("prop2", copiedMap.get("2").getProperty());

			map.get("1").setProperty("newProperty1");
			assertEquals("newProperty1", map.get("1").getProperty());
			assertEquals("prop1", copiedMap.get("1").getProperty());
		}
		{
			final Map<CopyableObj, CopyableObj> map = new TreeMap<>();
			final CopyableObj property1 = new CopyableObj("prop1");
			final CopyableObj property2 = new CopyableObj("prop2");
			map.put(property1, new CopyableObj("prop10"));
			map.put(property2, new CopyableObj("prop20"));

			final Map<CopyableObj, CopyableObj> copiedMap = Copyable.createCopy(map);
			assertEquals(map.getClass().getName(), copiedMap.getClass().getName());
			assertEquals(2, copiedMap.size());
			assertEquals("prop10", copiedMap.get(property1).getProperty());
			assertEquals("prop20", copiedMap.get(property2).getProperty());

			map.get(property1).setProperty("newProperty1");
			assertEquals("newProperty1", map.get(property1).getProperty());
			assertEquals("prop10", copiedMap.get(property1).getProperty());
		}

		{
			final Map<CopyableObj, String> map = new TreeMap<>();
			final CopyableObj property1 = new CopyableObj("prop1");
			final CopyableObj property2 = new CopyableObj("prop2");
			map.put(property1, "prop10");
			map.put(property2, "prop20");

			final Map<CopyableObj, String> copiedMap = Copyable.createCopy(map);
			assertEquals(map.getClass().getName(), copiedMap.getClass().getName());
			assertEquals(2, copiedMap.size());
			assertEquals("prop10", copiedMap.get(property1));
			assertEquals("prop20", copiedMap.get(property2));

			property1.setProperty("newProperty1");
			assertTrue(map.keySet().contains(property1));
			assertFalse(map.keySet().contains(new CopyableObj("prop1")));
			assertTrue(copiedMap.keySet().contains(new CopyableObj("prop1")));
		}

		{
			// Check unmodifiable
			final Map<CopyableObj, String> map = new TreeMap<>();
			final CopyableObj property1 = new CopyableObj("prop1");
			final CopyableObj property2 = new CopyableObj("prop2");
			map.put(property1, "prop10");
			map.put(property2, "prop20");

			final Map<CopyableObj, String> copiedMap = Copyable.createCopy(Collections.unmodifiableMap(map));
			assertEquals(TreeMap.class.getName(), copiedMap.getClass().getName());
			assertEquals(2, copiedMap.size());
			assertEquals("prop10", copiedMap.get(property1));
			assertEquals("prop20", copiedMap.get(property2));
		}
		{
			// Check unmodifiable with hashcode
			final Map<CopyableObj, String> map = new TreeMap<>();
			final CopyableObj property1 = new CopyableObjWithHashCode("prop1");
			final CopyableObj property2 = new CopyableObjWithHashCode("prop2");
			map.put(property1, "prop10");
			map.put(property2, "prop20");

			final Map<CopyableObj, String> copiedMap = Copyable.createCopy(Collections.unmodifiableMap(map));
			assertEquals(LinkedHashMap.class.getName(), copiedMap.getClass().getName());
			assertEquals(2, copiedMap.size());
			assertEquals("prop10", copiedMap.get(property1));
			assertEquals("prop20", copiedMap.get(property2));
		}
	}

	@Test
	public void testCopyableCollections() {
		{
			final ArrayList<CopyableObj> list = new ArrayList<>();
			list.add(new CopyableObj("prop1"));
			list.add(new CopyableObj("prop2"));

			final Collection<CopyableObj> copiedList = Copyable.createCopy(list);
			assertEquals(list.getClass().getName(), copiedList.getClass().getName());
			assertEquals(2, copiedList.size());
			final Iterator<CopyableObj> it = copiedList.iterator();
			assertEquals("prop1", it.next().getProperty());
			assertEquals("prop2", it.next().getProperty());

			list.iterator().next().setProperty("newProperty1");
			assertEquals("newProperty1", list.iterator().next().getProperty());
			assertEquals("prop1", copiedList.iterator().next().getProperty());
		}
		{
			// Check unmodifiable
			final ArrayList<CopyableObj> list = new ArrayList<>();
			list.add(new CopyableObj("prop1"));
			list.add(new CopyableObj("prop2"));

			final Collection<CopyableObj> copiedList = Copyable.createCopy(Collections.unmodifiableCollection(list));
			assertEquals(ArrayList.class.getName(), copiedList.getClass().getName());
			assertEquals(2, copiedList.size());
			final Iterator<CopyableObj> it = copiedList.iterator();
			assertEquals("prop1", it.next().getProperty());
			assertEquals("prop2", it.next().getProperty());
		}
	}

	@Test
	public void testCopyableSet() {
		{
			final Set<CopyableObj> set = new LinkedHashSet<>();
			set.add(new CopyableObj("prop1"));
			set.add(new CopyableObj("prop2"));

			final Set<CopyableObj> copiedSet = Copyable.createCopy(set);
			assertEquals(set.getClass().getName(), copiedSet.getClass().getName());
			assertEquals(2, copiedSet.size());
			final Iterator<CopyableObj> it = copiedSet.iterator();
			assertEquals("prop1", it.next().getProperty());
			assertEquals("prop2", it.next().getProperty());

			set.iterator().next().setProperty("newProperty1");
			assertEquals("newProperty1", set.iterator().next().getProperty());
			assertEquals("prop1", copiedSet.iterator().next().getProperty());
		}
		{
			// Check unmodifiable
			final Set<CopyableObj> set = new TreeSet<>();
			set.add(new CopyableObj("prop1"));
			set.add(new CopyableObj("prop2"));

			final Set<CopyableObj> copiedSet = Copyable.createCopy(Collections.unmodifiableSet(set));
			assertEquals(TreeSet.class.getName(), copiedSet.getClass().getName());
			assertEquals(2, copiedSet.size());
			final Iterator<CopyableObj> it = copiedSet.iterator();
			assertEquals("prop1", it.next().getProperty());
			assertEquals("prop2", it.next().getProperty());
		}
		{
			// Check unmodifiable with hashcode
			final Set<CopyableObj> set = new LinkedHashSet<>();
			set.add(new CopyableObjWithHashCode("prop1"));
			set.add(new CopyableObjWithHashCode("prop2"));

			final Set<CopyableObj> copiedSet = Copyable.createCopy(Collections.unmodifiableSet(set));
			assertEquals(LinkedHashSet.class.getName(), copiedSet.getClass().getName());
			assertEquals(2, copiedSet.size());
			final Iterator<CopyableObj> it = copiedSet.iterator();
			assertEquals("prop1", it.next().getProperty());
			assertEquals("prop2", it.next().getProperty());
		}
	}
}
