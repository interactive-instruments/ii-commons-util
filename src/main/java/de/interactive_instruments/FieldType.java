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
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
public class FieldType {
	final Object t;

	public FieldType(final Field field) {
		this.t = field.getGenericType();
	}

	public FieldType(final Object t) {
		this.t = t;
	}

	public boolean isParametrizedType() {
		return t instanceof ParameterizedType;
	}

	public boolean isCollection() {
		return t instanceof ParameterizedType && Collection.class.isAssignableFrom(getClassOrRawClass());
	}

	public boolean isMap() {
		return t instanceof ParameterizedType && Map.class.isAssignableFrom(getClassOrRawClass());
	}

	public Class<?> getClassOrRawClass() {
		if (isParametrizedType()) {
			final Type type = ((ParameterizedType) t).getRawType();
			if (type instanceof ParameterizedType) {
				return null;
			}
			return (Class<?>) type;
		}
		return (Class<?>) t;
	}

	public List<Class<?>> getArguments() {
		if (!isParametrizedType()) {
			return null;
		}
		final Type[] args = ((ParameterizedType) t).getActualTypeArguments();
		final List<Class<?>> argsList = new ArrayList<>(args.length);
		for (final Type arg : args) {
			if (arg instanceof ParameterizedType) {
				argsList.add((Class<?>) ((ParameterizedType) t).getRawType());
			} else {
				argsList.add((Class<?>) arg);
			}
		}
		return argsList;
	}

	public List<FieldType> getArgumentsAsFieldTypes() {
		if (!isParametrizedType()) {
			return null;
		}
		final Type[] args = ((ParameterizedType) t).getActualTypeArguments();
		final List<FieldType> argsList = new ArrayList<>(args.length);
		for (final Type arg : args) {
			argsList.add(new FieldType(arg));
		}
		return argsList;
	}

	public int argumentSize() {
		return isParametrizedType() ? ((ParameterizedType) t).getActualTypeArguments().length : 0;
	}

	public FieldType getFirstArgument() {
		return new FieldType(((ParameterizedType) t).getActualTypeArguments()[0]);

	}

	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer("FieldType{");
		sb.append("class='").append(getClassOrRawClass().getSimpleName()).append("', ");
		sb.append("p='").append(isParametrizedType()).append("', ");
		sb.append("a=<");
		if (isParametrizedType()) {
			for (final FieldType fieldType : getArgumentsAsFieldTypes()) {
				sb.append(fieldType.getClassOrRawClass().getSimpleName());
				if (fieldType.isParametrizedType()) {
					sb.append("(");
					fieldType.getArguments().forEach(a -> sb.append(a.getSimpleName() + " "));
					sb.append(")");
				}
				sb.append(" ");
			}
		}
		sb.append(">");
		sb.append('}');
		return sb.toString();
	}
}
