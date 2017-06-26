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

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

/**
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
public class SampleTest {

	@Test
	public void testNormalDistributed1() {
		final List<String> list = new ArrayList<>();
		list.add("1");
		list.add("2");
		list.add("3");
		list.add("4");
		list.add("5");
		list.add("6");
		list.add("7");
		list.add("8");

		for (int i = 0; i < 64; i++) {
			final List<String> sampleList = Sample.normalDistributed(list, 2);
			assertNotNull(sampleList);
			assertEquals(2, sampleList.size());
			// 1/8 * 1/8 = 1/64
			assertNotEquals(sampleList.get(0), sampleList.get(1));
			System.out.println(sampleList.get(0));
			System.out.println(sampleList.get(1));
		}
	}

	@Test
	public void testNormalDistributed2() {
		final List<String> list = new ArrayList<>();
		list.add("1");
		list.add("2");
		list.add("3");
		list.add("4");
		list.add("5");
		list.add("6");
		list.add("7");
		list.add("8");
		list.add("9");
		list.add("10");

		for (int i = 0; i < 100; i++) {
			final List<String> sampleList = Sample.normalDistributed(list, 3);
			assertNotNull(sampleList);
			assertEquals(3, sampleList.size());
			assertNotEquals(sampleList.get(0), sampleList.get(1));
			assertNotEquals(sampleList.get(0), sampleList.get(2));
			assertNotEquals(sampleList.get(1), sampleList.get(2));
		}
	}

	@Test
	public void testNormalDistributed3() {
		final List<String> list = new ArrayList<>();
		list.add("1");
		list.add("2");
		list.add("3");
		list.add("4");

		assertArrayEquals(list.toArray(), Sample.normalDistributed(list, 4).toArray());
		assertArrayEquals(list.toArray(), Sample.normalDistributed(list, 5).toArray());
	}
}
