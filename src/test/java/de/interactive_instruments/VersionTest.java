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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
public class VersionTest {

	@Test
	public void parseVersions() {

		boolean excIllegalVersionThrown = false;
		try {
			new Version("1");
		} catch (IllegalArgumentException e) {
			excIllegalVersionThrown = true;
		}
		assertTrue(excIllegalVersionThrown);

		assertEquals("1.2.0", new Version("1.2").toString());
		assertEquals("1.2.0", new Version("1.2").getAsString());
		assertEquals("1.2.0", new Version("1.2").getAsStringWithExtension());

		assertEquals("1.2.3", new Version("1.2.3").toString());
		assertEquals("1.2.3", new Version("1.2.3").getAsString());
		assertEquals("1.2.3", new Version("1.2.3").getAsStringWithExtension());

		assertEquals("1.2.0", new Version("1.2-SNAPSHOT").toString());
		assertEquals("1.2.0", new Version("1.2-SNAPSHOT").getAsString());
		assertEquals("1.2.0-SNAPSHOT", new Version("1.2-SNAPSHOT").getAsStringWithExtension());

		assertEquals("1.2.3", new Version("1.2.3-SNAPSHOT").toString());
		assertEquals("1.2.3", new Version("1.2.3-SNAPSHOT").getAsString());
		assertEquals("1.2.3-SNAPSHOT", new Version("1.2.3-SNAPSHOT").getAsStringWithExtension());

	}

	@Test
	public void compareVersions() {
		assertEquals(-1, new Version("1.2").compareTo(new Version("1.3")));
		assertEquals(-1, new Version("1.2.3").compareTo(new Version("1.3")));
		assertEquals(0, new Version("1.2").compareTo(new Version("1.2.0")));
		assertEquals(0, new Version("1.2.3").compareTo(new Version("1.2.3")));
		assertEquals(1, new Version("1.2").compareTo(new Version("1.1")));
		assertEquals(1, new Version("1.2.0").compareTo(new Version("1.1.3")));

		assertEquals(-1, new Version("1.2.3").compareTo(new Version("1.3.3-SNAPSHOT")));
		assertEquals(-1, new Version("1.2.3").compareTo(new Version("1.3.0-SNAPSHOT")));
		assertEquals(-1, new Version("1.2-SNAPSHOT").compareTo(new Version("1.2")));
		assertEquals(0, new Version("1.2.3-SNAPSHOT").compareTo(new Version("1.2.3-SNAPSHOT")));
		assertEquals(0, new Version("1.2-SNAPSHOT").compareTo(new Version("1.2-SNAPSHOT")));
		assertEquals(1, new Version("1.2").compareTo(new Version("1.1")));
		assertEquals(1, new Version("1.2-SNAPSHOT").compareTo(new Version("1.1")));
		assertEquals(1, new Version("1.2").compareTo(new Version("1.2-SNAPSHOT")));
	}
}
