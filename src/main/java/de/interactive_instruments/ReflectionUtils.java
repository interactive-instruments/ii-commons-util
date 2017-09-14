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

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
public class ReflectionUtils {

	private ReflectionUtils() {

	}

	public static List<Field> getFieldsAndSuperFields(final Class<?> clasz) {
		final List<Field> fields = new LinkedList<>();
		fields.addAll(Arrays.asList(clasz.getDeclaredFields()));
		final Class<?> superClass = clasz.getSuperclass();
		if (superClass != null) {
			getAllFields(fields, superClass);
		}
		return fields;
	}

	private static List<Field> getAllFields(final List<Field> fields, Class<?> clasz) {
		fields.addAll(Arrays.asList(clasz.getDeclaredFields()));
		final Class<?> superClass = clasz.getSuperclass();
		if (superClass != null) {
			return getAllFields(fields, superClass);
		}
		return fields;
	}

	/**
	 * Returns true if the class overrides the {@link Object#equals(Object)} and the
	 * {@link Object#hashCode()} function
	 *
	 * @param clasz the Class to check
	 *
	 * @return true if equals and hashCode are overridden
	 */
	public static boolean isHashable(final Class clasz) {
		try {
			return Object.class == clasz.getMethod("hashCode").getDeclaringClass() &&
					Object.class == clasz.getMethod("equals", Object.class).getDeclaringClass();
		} catch (final NoSuchMethodException e) {
			return false;
		}
	}

	public static boolean setObjectField(final Object object, final String fieldName, final Object fieldValue) {
		Class<?> clazz = object.getClass();
		while (clazz != null) {
			try {
				final Field field = clazz.getDeclaredField(fieldName);
				field.setAccessible(true);
				field.set(object, fieldValue);
				return true;
			} catch (NoSuchFieldException e) {
				clazz = clazz.getSuperclass();
			} catch (Exception e) {
				throw new IllegalStateException(e);
			}
		}
		return false;
	}
}
