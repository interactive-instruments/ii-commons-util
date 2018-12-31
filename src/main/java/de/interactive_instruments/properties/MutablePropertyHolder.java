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
package de.interactive_instruments.properties;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

import de.interactive_instruments.IFile;
import de.interactive_instruments.IoUtils;

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
			IoUtils.closeQuietly(stream);
		}
		setPropertiesFrom(properties, overwrite);
	}

}
