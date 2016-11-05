/**
 * Copyright 2010-2016 interactive instruments GmbH
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

import java.util.Iterator;

/**
 * String Utility Functions
 *
 * @author J. Herrmann ( herrmann <aT) interactive-instruments (doT> de )
 */
final public class SUtils {

	private SUtils() {}

	public static final String ENDL = System.lineSeparator();

	/**
	 * Splits a String with an regex into two Strings or returns null if
	 * regex not found.
	 *
	 * @param str
	 * @param regex
	 * @return String Array with the size 2 or null if regex not found
	 */
	public static String[] split2OrNull(final String str, final String regex) {
		String[] splitted = str.split(regex, 2);
		if (splitted.length == 2 && !splitted[0].isEmpty() && !splitted[1].isEmpty()) {
			return splitted;
		}
		return null;
	}

	/**
	 * Returns a substring with the "right side" of the indexStr.
	 *i
	 * String a = "foo.bar"
	 * rigthOfSubStrOrNull(a, ".") == "bar"
	 *
	 * @param str
	 * @param indexStr
	 * @return
	 */
	public static String rigthOfSubStrOrNull(final String str, final String indexStr) {
		final int pos = str.indexOf(indexStr);
		if (pos == -1) {
			return null;
		}
		return str.substring(pos + indexStr.length());
	}

	/**
	 * Returns a substring with the "left side" of the indexStr.
	 *
	 * String a = "foo.bar"
	 * leftOfSubStrOrNull(a, ".") == "foo"
	 *
	 * @param str
	 * @param indexStr
	 * @return
	 */
	public static String leftOfSubStrOrNull(final String str, final String indexStr) {
		final int pos = str.indexOf(indexStr);
		if (pos == -1) {
			return null;
		}
		return str.substring(0, pos);
	}

	/**
	 * Return true if the String is null or the trimmed String is empty
	 *
	 * @param str
	 * @return
	 */
	public static boolean isNullOrEmpty(final String str) {
		return str == null || str.trim().isEmpty();
	}

	public static String requireNonNullOrEmpty(final String str, final String message) {
		if (str == null)
			throw new NullPointerException(message);
		if (str.trim().isEmpty())
			throw new IllegalArgumentException(message);
		return str;
	}

	/**
	 * Null safe comparison of two String objects.
	 *
	 * Where <code>SUtils.compareNullSafeIgnoreCase(obj, toObj);</code> is like
	 * <code>obj.compareToIgnoreCase(toObj);</code>
	 *
	 * @see de.interactive_instruments.CmpUtils See CmpUtils for a
	 * comparison version which does not ignore case
	 */
	public static int compareNullSafeIgnoreCase(String obj, String toObj) {
		if (obj == null ^ toObj == null) {
			return (obj == null) ? -1 : 1;
		} else if (obj == null && toObj == null) {
			return 0;
		}
		return obj.compareToIgnoreCase(toObj);
	}

	public static String nonNullEmptyOrDefault(final String str, final String def) {
		return isNullOrEmpty(str) ? def : str;
	}


	public static String calcHash(final String str) {
		return new String(MdUtils.getMessageDigest().digest(str.getBytes()));
	}

	/**
	 * Returns the list items as whitespace separated list
	 *
	 * @param it
	 * @return
	 */
	public final static String toBlankSepStr(Iterable<String> it) {
		String list = "";
		Iterator<String> t = it.iterator();
		while (t.hasNext()) {
			list += t.next();
			if (t.hasNext()) {
				list += " ";
			}
		}
		return list;
	}

}
