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

/**
 * Utility functions for comparing Objects
 *
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 *
 */
public final class CmpUtils {

	private CmpUtils() {}

	/**
	 * Null safe comparison of two objects.
	 * Where <code>SUtils.compareNullSafe(obj, toObj);</code> is equal to
	 * <code>obj.compareTo(toObj);</code>
	 */
	public static int cmpNullSafe(Comparable obj, Comparable toObj) {
		if (obj == null ^ toObj == null) {
			return (obj == null) ? -1 : 1;
		}
		if (obj == null && toObj == null) {
			return 0;
		}
		return obj.compareTo(toObj);
	}

	/**
	 * Compare multiple object types
	 *
	 * @param objs List of object tuples (objType1, objType1, objType2, objType2, ...)
	 * @return
	 */
	public static int cmpMultipleNullSafe(Comparable... objs) {
		if (objs.length % 2 != 0) {
			throw new IllegalArgumentException("Incorrect number of arguments");
		}
		for (int i = 0; i < objs.length; i += 2) {
			final int cmp = cmpNullSafe(objs[i], objs[i + 1]);
			if (cmp != 0) {
				return cmp;
			}
		}
		return 0;
	}
}
