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
