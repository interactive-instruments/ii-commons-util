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

import static org.junit.Assert.assertEquals;
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
