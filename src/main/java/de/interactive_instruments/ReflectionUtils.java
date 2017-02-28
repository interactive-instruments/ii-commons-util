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

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author J. Herrmann ( herrmann <aT) interactive-instruments (doT> de )
 */
public class ReflectionUtils {

	private ReflectionUtils() {

	}

	public static List<Field> getFieldsAndSuperFields(Class<?> clasz) {
		final List<Field> fields = new LinkedList<>();
		fields.addAll(Arrays.asList(clasz.getDeclaredFields()));
		final Class<?> superClass = clasz.getSuperclass();
		if (superClass != null) {
			getAllFields(fields, superClass);
		}
		return fields;
	}

	private static List<Field> getAllFields(List<Field> fields, Class<?> clasz) {
		fields.addAll(Arrays.asList(clasz.getDeclaredFields()));
		final Class<?> superClass = clasz.getSuperclass();
		if (superClass != null) {
			return getAllFields(fields, superClass);
		}
		return fields;
	}
}
