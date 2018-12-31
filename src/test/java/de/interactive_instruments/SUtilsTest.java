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

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class SUtilsTest {

	@Before
	public void setUp() throws Exception {}

	@Test
	public void testSplit2OrNull() {
		assertNotNull(SUtils.split2OrNull("Test.Str", "\\.."));
		assertEquals("Test", SUtils.split2OrNull("Test.Str", "\\..")[0]);
		assertEquals("tr", SUtils.split2OrNull("Test.Str", "\\..")[1]);
	}

	@Test
	public void testRigthOfSubStrOrNull() {
		assertNotNull(SUtils.rigthOfSubStrOrNull("Test.Str", "."));
		assertEquals("Str", SUtils.rigthOfSubStrOrNull("Test.Str", "."));
	}

	@Test
	public void testLeftOfSubStrOrNull() {
		assertNotNull(SUtils.leftOfSubStrOrNull("Test.Str", "."));
		assertEquals("Test", SUtils.leftOfSubStrOrNull("Test.Str", "."));
	}

	@Test
	public void testIsNullOrEmpty() {
		assertFalse(SUtils.isNullOrEmpty("1"));
		assertTrue(SUtils.isNullOrEmpty(""));
		assertTrue(SUtils.isNullOrEmpty(" "));
		assertTrue(SUtils.isNullOrEmpty("  "));
		assertTrue(SUtils.isNullOrEmpty(null));
	}

	@Test
	public void testCompareNullSafeIgnoreCase() {
		assertEquals(0, SUtils.compareNullSafeIgnoreCase("1", "1"));
		assertEquals(-1, SUtils.compareNullSafeIgnoreCase("1", "2"));
		assertEquals(1, SUtils.compareNullSafeIgnoreCase("2", "1"));
		assertEquals(-1, SUtils.compareNullSafeIgnoreCase(null, "1"));
		assertEquals(1, SUtils.compareNullSafeIgnoreCase("1", null));
		assertEquals(0, SUtils.compareNullSafeIgnoreCase(null, null));
	}

	@Test
	public void testNonNullEmptyOrDefault() {
		assertEquals("1", SUtils.nonNullEmptyOrDefault("1", "bla"));
		assertEquals("bla", SUtils.nonNullEmptyOrDefault(" ", "bla"));
		assertEquals("bla", SUtils.nonNullEmptyOrDefault(null, "bla"));
	}

	@Test
	public void testCalcHash() {
		assertEquals("AE922B1FEB1997339D6994B79CB4611FC5CC9EBD7161214A65473AAD4232C7CF",
				SUtils.secureCalcHashAsHexStr("akjsdhflhalsg"));
		assertEquals("18AE1FE7C3C7E6B05A282315CCB2AFD01C02525FDD06305275D240D6567769D2",
				SUtils.secureCalcHashAsHexStr("asdkgha"));

		assertEquals("33FE6A4AB9C1796B", SUtils.fastCalcHashAsHexStr("akjsdhflhalsg"));
		assertEquals("F230496A7B19B162", SUtils.fastCalcHashAsHexStr("asdkgha"));
	}

	@Test
	public void testToBlankSepStr() {
		final List l = new ArrayList();
		l.add("1");
		l.add("2");
		l.add("3");
		assertEquals("1 2 3", SUtils.toBlankSepStr(l));
		assertEquals("", SUtils.toBlankSepStr(null));
	}

	@Test
	public void testConcatStr() {
		final List l = new ArrayList();
		l.add("1");
		l.add("2");
		l.add("3");
		assertEquals("1-2-3", SUtils.concatStr("-", l));
	}

	@Test
	public void testLastMinIndexOf() {
		assertEquals(6, SUtils.lastMinIndexOf("A A A B C B C F", 8, "B", "C"));

		assertEquals(2, SUtils.lastMinIndexOf("A A A B C B C F", 3, "B", "C", "A"));
	}

	@Test
	public void testMinIndexOf() {
		assertEquals(8, SUtils.minIndexOf("A A A B C B C F", 7, "B", "C"));

		assertEquals(4, SUtils.minIndexOf("A A A B C B C F", 3, "B", "C", "A"));
	}

	@Test
	public void testToKvpStr() {
		assertEquals("A=B", SUtils.toKvpStr("A", "B"));
		assertEquals("A=B, C=D", SUtils.toKvpStr("A", "B", "C", "D"));

		boolean excThrown = false;
		try {
			SUtils.toKvpStr("A", "B", "C");
		} catch (IllegalArgumentException e) {
			excThrown = true;
		}
		assertTrue(excThrown);
	}
}
