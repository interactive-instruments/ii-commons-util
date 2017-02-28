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

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.Map;

import de.interactive_instruments.IFile;

public interface MutablePropertyHolder extends PropertyHolder {

	MutablePropertyHolder setProperty(final String key, final String value);

	void removeProperty(final String key);

	default void setPropertiesFrom(final PropertyHolder properties, boolean overwrite) {
		if (properties == null) {
			throw new IllegalArgumentException("PropertyHolder is null");
		}
		properties.namePropertyPairs().forEach(p -> {
			if (overwrite || !hasProperty(p.getKey())) {
				setProperty(p.getKey(), p.getValue());
			}
		});
	}

	default void setPropertiesFrom(final java.util.Properties properties, boolean overwrite) {
		if (properties == null) {
			throw new IllegalArgumentException("Properties are null");
		}
		properties.entrySet().forEach(p -> {
			if (overwrite || !hasProperty(p.getKey().toString())) {
				setProperty(p.getKey().toString(), p.getValue().toString());
			}
		});
	}

	default void setPropertiesFrom(final IFile propertyFile, boolean overwrite) throws IOException {
		if (propertyFile == null) {
			throw new IllegalArgumentException("Property file is null");
		}
		propertyFile.expectFileIsReadable();
		final java.util.Properties properties = new java.util.Properties();
		BufferedInputStream stream = null;
		try {
			stream = new BufferedInputStream(new FileInputStream(propertyFile));
			properties.load(stream);
		} finally {
			IFile.closeQuietly(stream);
		}
		setPropertiesFrom(properties, overwrite);
	}

}
