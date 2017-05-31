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

import java.util.*;
import java.util.regex.Pattern;

/**
 * String Utility Functions
 *
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
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
	public final static String toBlankSepStr(final Iterable it) {
		final StringBuilder list = new StringBuilder();
		if (it != null) {
			final Iterator t = it.iterator();
			while (t.hasNext()) {
				list.append(t.next().toString());
				if (t.hasNext()) {
					list.append(" ");
				}
			}
		}
		return list.toString();
	}

	public final static String concatStr(final String separator, final String... strings) {
		if (strings != null) {
			final StringBuilder builder = new StringBuilder(strings.length * 6);
			for (int i = 0;;) {
				builder.append(strings[i++]);
				if (i < strings.length) {
					builder.append(separator);
				} else {
					break;
				}
			}
			return builder.toString();
		}
		return null;
	}

	public final static String concatStr(final String separator, final Iterable<?> strings) {
		if (strings != null) {
			final StringBuilder builder = new StringBuilder(32);
			for (Iterator<?> it = strings.iterator();;) {
				builder.append(it.next().toString());
				if (it.hasNext()) {
					builder.append(separator);
				} else {
					break;
				}
			}
			return builder.toString();
		}
		return null;
	}

	public final static String concatStrWithPrefixAndSuffix(final String separator, final String prefix, final String suffix,
			final Iterable<?> strings) {
		if (strings != null) {
			final StringBuilder builder = new StringBuilder(32);
			for (Iterator<?> it = strings.iterator();;) {
				builder.append(prefix);
				builder.append(it.next().toString());
				builder.append(suffix);
				if (it.hasNext()) {
					builder.append(separator);
				} else {
					break;
				}
			}
			return builder.toString();
		}
		return null;
	}

	/**
	 * Find the min position of multiple search strings
	 *
	 * @param str string to use
	 * @param from start position
	 * @param search search strings
	 * @return min position
	 */
	public static int lastMinIndexOf(final String str, final int from, final String... search) {
		int min = -1;
		for (int i = 0; i < search.length; i++) {
			int pos = str.lastIndexOf(search[i], from);
			if (pos != -1 && (min == -1 || pos < min)) {
				min = pos;
			}
		}
		return min;
	}

	public static int minIndexOf(final String str, final int from, final String... search) {
		int min = -1;
		for (int i = 0; i < search.length; i++) {
			int pos = str.indexOf(search[i], from);
			if (pos != -1 && (min == -1 || pos < min)) {
				min = pos;
			}
		}
		return min;
	}

	public static int lastIndexOfNot(final String str, final int from, final char c) {
		for (int i = from; i > 0; i--) {
			if (str.charAt(i - 1) != c) {
				return i;
			}
		}
		return -1;
	}

	public static String toKvpStr(final String... strings) {
		if (strings != null) {
			if (strings.length % 2 != 0) {
				throw new IllegalArgumentException("Number of arguments is odd!");
			}
			final StringBuilder builder = new StringBuilder(strings.length * 6);
			for (int i = 0;;) {
				builder.append(strings[i]);
				builder.append("=");
				builder.append(strings[i + 1]);
				i = +2;
				if (i < strings.length) {
					builder.append(", ");
				} else {
					break;
				}
			}
			return builder.toString();
		}
		return null;
	}

	public static Map<String, String> toStrMap(final String... strings) {
		if (strings != null) {
			if (strings.length % 2 != 0) {
				throw new IllegalArgumentException("Number of arguments is odd!");
			}
			final Map<String, String> map = new TreeMap<>();
			for (int i = 0; i < strings.length; i += 2) {
				map.put(strings[i], strings[i + 1]);
			}
			return map;
		}
		return null;
	}

	public static Map<String, String> toStrMap(final Collection<String> strings) {
		if (strings != null) {
			if (strings.size() % 2 != 0) {
				throw new IllegalArgumentException("Number of arguments is odd!");
			}
			final Map<String, String> map = new TreeMap<>();
			for (Iterator<String> it = strings.iterator(); it.hasNext();) {
				map.put(it.next(), it.next());
			}
			return map;
		}
		return null;
	}

	public static boolean strContainsAny(final String str, final String... items) {
		return Arrays.stream(items).parallel().anyMatch(str::contains);
	}

	/**
	 * A wrapper that supports symmetric String comparisons.
	 */
	public static class StrEqContainer implements Comparable {
		private final String s;

		public StrEqContainer(final Object key) {
			s = (String) key;
		}

		public static Set<StrEqContainer> createSet(final Collection<?> c) {
			final Set set = new HashSet();
			c.forEach(e -> set.add(new StrEqContainer(e)));
			return set;
		}

		@Override
		public String toString() {
			return s;
		}

		@Override
		public int hashCode() {
			return s.hashCode();
		}

		@Override
		public boolean equals(final Object obj) {
			return obj.equals(s);
		}

		@Override
		public int compareTo(final Object o) {
			return s.compareTo(o.toString());
		}
	}
}
