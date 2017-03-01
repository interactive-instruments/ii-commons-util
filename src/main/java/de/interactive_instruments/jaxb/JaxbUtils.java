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

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.*;

import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchema;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ClassUtils;

import de.interactive_instruments.FieldType;
import de.interactive_instruments.MutableNamespaceHolder;
import de.interactive_instruments.SUtils;

/**
 * Created by herrmann@interactive-instruments.de.
 */
public class JaxbUtils {

	private JaxbUtils() {}

	public static final String ELEMENT_DEFAULT = "##default";

	public static boolean isEmptyOrDefault(final String str) {
		return SUtils.isNullOrEmpty(str) || ELEMENT_DEFAULT.equals(str);
	}

	/**
	 * Analyze namespace and return default package namespace
	 *
	 * @param clasz
	 * @param nSh
	 * @return
	 */
	public static String analyzeNamespacesFromAnnotations(final Class<?> clasz, MutableNamespaceHolder nSh) {
		final XmlRootElement[] rootElementAnnotation = clasz.getAnnotationsByType(XmlRootElement.class);
		String defaultNamespaceUri = "";
		if (rootElementAnnotation.length == 1) {
			final String namespace = rootElementAnnotation[0].namespace();
			if (!isEmptyOrDefault(namespace)) {
				// defaultNamespaceUri=namespace;
				if (!nSh.hasPrefixForNamespace(namespace)) {
					nSh.addNamespaceUriForLaterPrefixLookup(namespace);
				}
			}
		}

		if (clasz.getPackage() != null) {
			// Get additional namespace from XmlSchema package annotation
			final XmlSchema[] packageNamespaceAnnotation = clasz.getPackage().getAnnotationsByType(XmlSchema.class);
			if (packageNamespaceAnnotation.length == 1) {
				final XmlSchema schemaAnnotation = packageNamespaceAnnotation[0];
				if (!isEmptyOrDefault(schemaAnnotation.namespace())) {
					if (SUtils.isNullOrEmpty(defaultNamespaceUri)) {
						defaultNamespaceUri = schemaAnnotation.namespace();
					}
					if (!nSh.hasPrefixForNamespace(schemaAnnotation.namespace())) {
						nSh.addNamespaceUriForLaterPrefixLookup(schemaAnnotation.namespace());
					}
				}
				if (schemaAnnotation.xmlns().length > 0) {
					for (final XmlNs xmlNs : schemaAnnotation.xmlns()) {
						if (!isEmptyOrDefault(xmlNs.namespaceURI()) &&
								nSh.getPrefixForNamespaceUri(xmlNs.namespaceURI()) == null) {
							if (isEmptyOrDefault(xmlNs.prefix())) {
								nSh.addUnknownNamespaceUriAndDeterminePrefix(xmlNs.prefix());
							} else {
								nSh.addNamespaceUriAndPrefix(xmlNs.namespaceURI(), xmlNs.prefix());
							}
						}
					}
				}
			}

			final Class<?> superClass = clasz.getSuperclass();
			if (superClass != null && !ClassUtils.isPrimitiveOrWrapper(superClass)) {
				analyzeNamespacesFromAnnotations(superClass, nSh);
			}
		}
		return defaultNamespaceUri;
	}

	/**
	 * Resolve unknown generic type parameters of a not instantiated generic class on the basis of a concrete
	 * (parameterized) type. Can be used with XmlJavaTypeAdapter attributes.
	 *
	 * E.g. concreteParameterizedType = Map<String, Map<String,Integer>> ,
	 * genericDefinedType = Map<String, Map<V1,V2>> then the mapping
	 * V1 = String.class , V2=Integer.class is returned
	 *
	 * @param concreteParameterizedType
	 * @param genericDefinedType
	 * @return null if not applicable, else a mapping with the parameter names as strings and the classes as keys
	 */
	public static Map<String, FieldType> resolveGenericTypes(final Type concreteParameterizedType,
			final Type genericDefinedType) {
		if (!(concreteParameterizedType instanceof ParameterizedType) ||
				!(genericDefinedType instanceof ParameterizedType)) {
			throw new IllegalArgumentException("Types are not generics");
		}

		final Map<String, FieldType> mapping = new HashMap<>();
		resolveGenericType((ParameterizedType) concreteParameterizedType, (ParameterizedType) genericDefinedType, mapping);

		if (!mapping.isEmpty()) {
			return mapping;
		}
		return null;
	}

	/**
	 * Resolve unknown generic type parameters of a not instantiated generic class on the basis of a concrete
	 * (parameterized) type. Can be used with XmlJavaTypeAdapter attributes.
	 *
	 * E.g. concreteParameterizedType = Map<String, Map<String,Integer>> ,
	 * genericDefinedType = Map<String, Map<V1,V2>> then the mapping
	 * V1 = String.class , V2=Integer.class is returned
	 *
	 * @param concreteParameterizedType
	 * @param genericDefinedTypeVariables
	 * @return null if not applicable, else a mapping with the parameter names as strings and the classes as keys
	 */
	public static Map<String, FieldType> resolveGenericTypes(final Type concreteParameterizedType,
			final TypeVariable[] genericDefinedTypeVariables) {
		if (!(concreteParameterizedType instanceof ParameterizedType)) {
			throw new IllegalArgumentException(concreteParameterizedType.getTypeName() + " is not generic");
		}

		final Map<String, FieldType> mapping = new HashMap<>();
		resolveGenericType((ParameterizedType) concreteParameterizedType, genericDefinedTypeVariables, mapping);

		if (!mapping.isEmpty()) {
			return mapping;
		}
		return null;
	}

	private static void resolveGenericType(final ParameterizedType concreteParameterizedType,
			final TypeVariable[] genericDefinedTypes,
			final Map<String, FieldType> mapping)

	{
		final Type[] params = concreteParameterizedType.getActualTypeArguments();
		if (params.length != genericDefinedTypes.length) {
			throw new IllegalArgumentException("Incompatible number of parameters ");
		}

		for (int i = 0; i < genericDefinedTypes.length; i++) {
			final Type t = params[i];
			if ((params[i] instanceof ParameterizedType)) {
				if (!Collection.class.isAssignableFrom((Class<?>) ((ParameterizedType) params[i]).getRawType())) {
					throw new IllegalArgumentException("Incompatible types");
				}
			}
			mapping.put(genericDefinedTypes[i].getTypeName(), new FieldType(params[i]));
		}
	}

	private static void resolveGenericType(final ParameterizedType concreteParameterizedType,
			final ParameterizedType genericDefinedType,
			final Map<String, FieldType> mapping) {
		final Type[] params = concreteParameterizedType.getActualTypeArguments();
		final Type[] generics = genericDefinedType.getActualTypeArguments();

		if (params.length != generics.length) {
			throw new IllegalArgumentException("Incompatible number of parameters ");
		}

		for (int i = 0; i < generics.length; i++) {
			final Type t = generics[i];
			if (t instanceof ParameterizedType) {
				// go deeper
				if (!(params[i] instanceof ParameterizedType)) {
					throw new IllegalArgumentException("Incompatible types");
				}
				resolveGenericType((ParameterizedType) params[i], (ParameterizedType) t, mapping);
			} else {
				if ((params[i] instanceof ParameterizedType)) {
					if (!Collection.class.isAssignableFrom((Class<?>) ((ParameterizedType) params[i]).getRawType())) {
						throw new IllegalArgumentException("Incompatible types");
					}
				}
				mapping.put(t.getTypeName(), new FieldType(params[i]));
			}
		}
	}
}