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
package de.interactive_instruments.properties;

import java.io.File;
import java.util.*;
import java.util.Map.Entry;

import de.interactive_instruments.IFile;
import de.interactive_instruments.exceptions.ExcUtils;
import de.interactive_instruments.exceptions.config.InvalidPropertyException;
import de.interactive_instruments.exceptions.config.MissingPropertyException;

/**
 * Interface for an object that holds key value pairs
 *
 * @author J. Herrmann ( herrmann <aT) interactive-instruments (doT> de )
 *
 */
public interface PropertyHolder extends Iterable<Entry<String, String>> {

	/**
	 * Get a property value
	 *
	 * @param key mapped key name
	 * @return the associated value
	 */
	String getProperty(String key);

	/**
	 * A list of all mapped keys
	 *
	 * @return
	 */
	Set<String> getPropertyNames();

	/**
	 * A Set of all key value pairs
	 *
	 * @return
	 */
	Set<Entry<String, String>> namePropertyPairs();

	int size();

	default boolean isEmpty() {
		return size() == 0;
	}

	boolean hasProperty(String key);

	default long getPropertyAsLong(final String key) throws InvalidPropertyException {
		try {
			return Long.valueOf(getProperty(key));
		} catch (NumberFormatException e) {
			throw new InvalidPropertyException("Configured property \"" + key + "\" is not a number");
		}
	}

	default int getPropertyAsInt(final String key) throws InvalidPropertyException {
		try {
			return Integer.valueOf(getProperty(key));
		} catch (NumberFormatException e) {
			throw new InvalidPropertyException("Configured property \"" + key + "\" is not a number");
		}
	}

	default double getPropertyAsDouble(final String key) throws InvalidPropertyException {
		try {
			return Double.valueOf(getProperty(key));
		} catch (NumberFormatException e) {
			throw new InvalidPropertyException("Configured property \"" + key + "\" is not a number");
		}
	}

	default IFile getPropertyAsFile(final String key) throws MissingPropertyException {
		final String v = getProperty(key);
		if (v == null) {
			throw new MissingPropertyException(key);
		}
		return new IFile(v, key);
	}

	default String getPropertyOrDefault(final String key, String defaultValue) {
		return hasProperty(key) ? getProperty(key) : defaultValue;
	}

	default long getPropertyOrDefaultAsLong(final String key, long defaultValue) throws InvalidPropertyException {
		return hasProperty(key) ? getPropertyAsLong(key) : defaultValue;
	}

	default int getPropertyOrDefaultAsInt(final String key, int defaultValue) throws InvalidPropertyException {
		return hasProperty(key) ? getPropertyAsInt(key) : defaultValue;
	}

	default double getPropertyOrDefaultAsDouble(final String key, double defaultValue) throws InvalidPropertyException {
		return hasProperty(key) ? getPropertyAsDouble(key) : defaultValue;
	}

	default IFile getPropertyOrDefaultAsFile(final String key, File defaultValue) {
		if (hasProperty(key)) {
			try {
				return getPropertyAsFile(key);
			} catch (MissingPropertyException e) {
				ExcUtils.suppress(e);
			}
		}
		if (defaultValue instanceof IFile) {
			return (IFile) defaultValue;
		} else {
			final IFile f = new IFile(defaultValue);
			f.setIdentifier(key);
			return f;
		}
	}

	default Iterator<Entry<String, String>> iterator() {
		if (isEmpty()) {
			return Collections.emptyIterator();
		}
		return namePropertyPairs().iterator();
	}

}
