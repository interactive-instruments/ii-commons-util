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
package de.interactive_instruments;

import static org.junit.Assert.*;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import de.interactive_instruments.properties.ClassifyingPropertyHolder;
import de.interactive_instruments.properties.Properties;

/**
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
public class PropertiesTest {

	private Properties properties = new Properties();

	@Before
	public void setUp() {
		properties = new Properties();
		properties.setProperty("foo.bar", "value1");
		properties.setProperty("foo", "value2");
		properties.setProperty("bar.foo", "value3");
		properties.setProperty("key.4", "value4");
		properties.setProperty("foo.bar.1", "value5");
		properties.setProperty("foo.bar.2", "value6");
		properties.setProperty("fool", "value7");
	}

	@Test
	public void testGetPropertiesByClassification() {
		final ClassifyingPropertyHolder classified = properties.getPropertiesByClassification("foo");

		assertEquals("value1", classified.getProperty("foo.bar"));
		assertEquals("value5", classified.getProperty("foo.bar.1"));
		assertEquals("value6", classified.getProperty("foo.bar.2"));

		assertNull(classified.getProperty("bar.foo"));
		assertNull(classified.getProperty("key.4"));
		assertNull(classified.getProperty("fool"));

		assertEquals(3, classified.size());
	}

	@Test
	public void testGetPropertiesByClassificationNotExisting() {
		final ClassifyingPropertyHolder classified = properties.getPropertiesByClassification("bla");

		assertNull(classified.getProperty("foo"));
		assertNull(classified.getProperty("bar.foo"));
		assertNull(classified.getProperty("key.4"));
		assertNull(classified.getProperty("fool"));

		assertTrue(classified.isEmpty());
	}

	@Test
	public void testGetPropertiesByClassificationEmpty() {
		final ClassifyingPropertyHolder classified = new Properties().getPropertiesByClassification("bla");
		assertNull(classified.getProperty("foo"));
		assertTrue(classified.isEmpty());
	}

	@Test
	public void testGetFlattenedPropertiesByClassification() {
		final ClassifyingPropertyHolder classified = properties.getFlattenedPropertiesByClassification("foo");

		assertEquals("value1", classified.getProperty("bar"));
		assertEquals("value5", classified.getProperty("bar.1"));
		assertEquals("value6", classified.getProperty("bar.2"));

		assertNull(classified.getProperty("foo"));
		assertNull(classified.getProperty("bar.foo"));
		assertNull(classified.getProperty("key.4"));
		assertNull(classified.getProperty("fool"));

		assertEquals(3, classified.size());
	}

	@Test
	public void testGetFlattenedPropertiesByClassificationNotExisting() {
		final ClassifyingPropertyHolder classified = properties.getFlattenedPropertiesByClassification("bla");

		assertNull(classified.getProperty("foo"));
		assertNull(classified.getProperty("bar.foo"));
		assertNull(classified.getProperty("key.4"));
		assertNull(classified.getProperty("fool"));

		assertTrue(classified.isEmpty());
	}

	@Test
	public void testGetFlattenedPropertiesByClassificationEmpty() {
		final ClassifyingPropertyHolder classified = new Properties().getFlattenedPropertiesByClassification("bla");
		assertNull(classified.getProperty("foo"));
		assertTrue(classified.isEmpty());
	}

	@Test
	public void testGetFirstLevelClassifications() {
		final Set<String> firstLevels = properties.getFirstLevelClassifications();

		assertFalse(firstLevels.isEmpty());

		assertTrue(firstLevels.contains("foo"));
		assertTrue(firstLevels.contains("bar"));
		assertTrue(firstLevels.contains("key"));

		assertEquals(3, firstLevels.size());
	}

}
