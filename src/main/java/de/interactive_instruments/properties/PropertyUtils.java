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
package de.interactive_instruments.properties;

/**
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
public class PropertyUtils {

	private PropertyUtils() {}

	public static String getenv(final String key, final String def) {
		final String val = System.getenv(key);
		return (val == null) ? def : val;
	}

	private static String getenvOrProperty(final String key) {
		final String val = System.getenv(key);
		return val == null ? System.getProperty(key.toLowerCase().replace("_", "."), null) : val;
	}

	/**
	 * Returns a upper case system environment variable (e.g. SYS_SUBSYSTEM_VAR),
	 * the corresponding lower case runtime environment property
	 * (e.g. sys.subystem.var) or if both not set a default value.
	 *
	 * @param key upper case variable name
	 * @param def default value
	 * @return variable
	 */
	public static String getenvOrProperty(final String key, final String def) {
		final String val = getenvOrProperty(key);
		return val != null ? val : def;
	}

	/**
	 * Returns a upper case system environment variable (e.g. SYS_SUBSYSTEM_VAR),
	 * the corresponding lower case runtime environment property
	 * (e.g. sys.subystem.var) or if both not set a default value.
	 *
	 * @param key upper case variable name
	 * @param def default value
	 * @return variable
	 */
	public static int getenvOrProperty(final String key, final int def) {
		final String val = getenvOrProperty(key);
		return val != null ? Integer.parseInt(val) : def;
	}

	/**
	 * Returns a upper case system environment variable (e.g. SYS_SUBSYSTEM_VAR),
	 * the corresponding lower case runtime environment property
	 * (e.g. sys.subystem.var) or if both not set a default value.
	 *
	 * @param key upper case variable name
	 * @param def default value
	 * @return variable
	 */
	public static long getenvOrProperty(final String key, final long def) {
		final String val = getenvOrProperty(key);
		return val != null ? Long.parseLong(val) : def;
	}
}
