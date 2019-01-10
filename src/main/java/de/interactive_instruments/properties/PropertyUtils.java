/**
 * Copyright 2017-2019 European Union, interactive instruments GmbH
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
package de.interactive_instruments.properties;

/**
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
public class PropertyUtils {

	private PropertyUtils() {}

	/**
	 * Get environment variable or default if environment is null or empty
	 *
	 * @param key environment variable key
	 * @param def default fallback value
	 * @return key or default value
	 */
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
