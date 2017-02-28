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
package de.interactive_instruments.jaxb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.*;

import org.junit.Test;

import de.interactive_instruments.FieldType;

/**
 * Created by herrmann@interactive-instruments.de.
 */
public class JaxbUtilsTest {

	final Map<String, Integer> concreteMap_1 = new HashMap<>();

	final Map<String, Map<String, Integer>> concreteMap_2 = new HashMap<>();

	final List<String> concreteList_3 = new ArrayList<>();

	final String str_4 = "";

	final Map<String, List<String>> concreteMap_5 = new HashMap<>();

	private static class GenericTestType<V1, V2> {

		final Map<String, V2> genericMap_1 = new HashMap<>();

		final Map<String, Map<V1, V2>> genericMap_2 = new HashMap<>();

		final List<V1> genericList_3 = new ArrayList<>();

		final String str_4 = "";

		final Map<String, V1> genericMap_5 = new HashMap<>();

	}

	@Test
	public void testResolveGenericSimpleType() throws NoSuchFieldException {

		final Map<String, FieldType> result_3 = JaxbUtils.resolveGenericTypes(
				this.getClass().getDeclaredField("concreteList_3").getGenericType(),
				GenericTestType.class.getDeclaredField("genericList_3").getGenericType());
		assertEquals(String.class, result_3.get("V1").getClassOrRawClass());
	}

	@Test
	public void testResolveGenericEmbeddedType() throws NoSuchFieldException {

		final Map<String, FieldType> result_1 = JaxbUtils.resolveGenericTypes(
				this.getClass().getDeclaredField("concreteMap_1").getGenericType(),
				GenericTestType.class.getDeclaredField("genericMap_1").getGenericType());
		assertEquals(Integer.class, result_1.get("V2").getClassOrRawClass());
	}

	@Test
	public void testResolveGenericDeepEmbeddedType() throws NoSuchFieldException {
		final Map<String, FieldType> result_2 = JaxbUtils.resolveGenericTypes(
				this.getClass().getDeclaredField("concreteMap_2").getGenericType(),
				GenericTestType.class.getDeclaredField("genericMap_2").getGenericType());
		assertEquals(String.class, result_2.get("V1").getClassOrRawClass());
		assertEquals(Integer.class, result_2.get("V2").getClassOrRawClass());
	}

	@Test
	public void testResolveGenericDeepEmbeddedType2() throws NoSuchFieldException {
		final Map<String, FieldType> result_2 = JaxbUtils.resolveGenericTypes(
				this.getClass().getDeclaredField("concreteMap_5").getGenericType(),
				GenericTestType.class.getDeclaredField("genericMap_5").getGenericType());
		assertTrue(new FieldType(this.getClass().getDeclaredField("concreteMap_5")).isMap());
		assertEquals(Map.class, new FieldType(this.getClass().getDeclaredField("concreteMap_5")).getClassOrRawClass());

		System.out.println(new FieldType(this.getClass().getDeclaredField("concreteMap_5")).toString());

		assertEquals(List.class, result_2.get("V1").getClassOrRawClass());
		assertTrue(result_2.get("V1").isCollection());
		assertFalse(result_2.get("V1").isMap());
		assertEquals(String.class, result_2.get("V1").getArguments().get(0));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testResolveGenericTypeExpectException1() throws NoSuchFieldException {
		final Map<String, FieldType> result_4 = JaxbUtils.resolveGenericTypes(
				this.getClass().getDeclaredField("str_4").getGenericType(),
				GenericTestType.class.getDeclaredField("str_4").getGenericType());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testResolveGenericTypeExpectException2() throws NoSuchFieldException {
		final Map<String, FieldType> result_5 = JaxbUtils.resolveGenericTypes(
				this.getClass().getDeclaredField("concreteMap_1").getGenericType(),
				GenericTestType.class.getDeclaredField("genericMap_2").getGenericType());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testResolveGenericTypeExpectException3() throws NoSuchFieldException {
		final Map<String, FieldType> result_6 = JaxbUtils.resolveGenericTypes(
				this.getClass().getDeclaredField("concreteMap_2").getGenericType(),
				GenericTestType.class.getDeclaredField("genericMap_1").getGenericType());
	}

}
