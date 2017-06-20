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

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.net.URI;
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
		assertEquals("AED80C7B410CE5E9857D384A8695A8C6071AEA2F", SUtils.calcHashAsHexStr("asdjhgjöhag"));
		assertEquals("3998AAFB1000A0A791A5A22A52D0BA0800AB0E5D", SUtils.calcHashAsHexStr("bklösjrö"));
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

		boolean excThrown = false;
		try {
			SUtils.toKvpStr("A", "B", "C");
		} catch (IllegalArgumentException e) {
			excThrown = true;
		}
		assertTrue(excThrown);
	}
}
