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
