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

import java.util.*;
import java.util.stream.Collectors;

import de.interactive_instruments.IFile;
import de.interactive_instruments.exceptions.config.MissingPropertyException;

public final class ConfigProperties implements ConfigPropertyHolder {

	final private Map<String, String> properties;
	final private Set<String> requiredProperties;
	private boolean locked = false;

	public ConfigProperties(Map<String, String> map) {
		this.properties = map;
		this.requiredProperties = null;
	}

	public ConfigProperties(Map<String, String> properties, Set<String> requiredProperties) {
		this.properties = properties;
		this.requiredProperties = requiredProperties;
	}

	public ConfigProperties() {
		this.properties = new LinkedHashMap<String, String>();
		this.requiredProperties = null;
	}

	public ConfigProperties(Map<String, String> properties, String... mandatoryPropertyNames) {
		this.properties = properties;
		this.requiredProperties = new LinkedHashSet<String>();
		for (String mandatoryPropertyName : mandatoryPropertyNames) {
			this.requiredProperties.add(mandatoryPropertyName);
		}
	}

	public ConfigProperties(String... mandatoryPropertyNames) {
		this.properties = new LinkedHashMap<String, String>();
		this.requiredProperties = new LinkedHashSet<String>();
		for (String mandatoryPropertyName : mandatoryPropertyNames) {
			this.requiredProperties.add(mandatoryPropertyName);
		}
	}

	private ConfigProperties(final ConfigProperties configProperties) {
		this.properties = new LinkedHashMap<String, String>(configProperties.properties);
		this.requiredProperties = configProperties.requiredProperties != null ? new LinkedHashSet<String>(configProperties.requiredProperties) : null;
	}

	public ConfigProperties cloneWithoutLock() {
		return new ConfigProperties(this);
	}

	/**
	 * Returns a set property value or the default value;
	 * @param key mapped key name
	 */
	@Override
	public String getProperty(String key) {
		return properties.get(key);
	}

	@Override
	public void removeProperty(String key) {
		properties.remove(key);
	}

	@Override
	public Set<String> getPropertyNames() {
		return properties.keySet();
	}

	@Override
	public Set<Map.Entry<String, String>> namePropertyPairs() {
		return properties.entrySet();
	}

	@Override
	public int size() {
		return properties.size();
	}

	@Override
	public boolean hasProperty(final String key) {
		return properties.containsKey(key);
	}

	/**
	 * Set a property if not already set
	 * @param key
	 * @param value
	 * @return
	 */
	public MutablePropertyHolder setProperty(String key, String value) {
		checkLock();
		/*
		allow overwrite?
		if(this.properties.containsKey(key)) {
		    throw new IllegalArgumentException("Property "+key+" already set");
		}
		*/
		this.properties.put(key, value);
		return this;
	}

	@Override
	public boolean allRequiredPropertiesSet() {
		return requiredProperties == null ? true : properties.keySet().containsAll(requiredProperties);
	}

	@Override
	public void expectAllRequiredPropertiesSet() throws MissingPropertyException {
		if (!allRequiredPropertiesSet()) {
			List<String> missingProperties = new ArrayList<String>(requiredProperties.size());
			missingProperties.addAll(this.requiredProperties.stream().filter(requiredProperty -> !properties.containsKey(requiredProperty)).collect(Collectors.toList()));
			throw new MissingPropertyException(missingProperties);
		}
	}

	@Override
	public Set<String> getRequiredPropertyNames() {
		return requiredProperties != null ? requiredProperties : new TreeSet<String>();
	}

	@Override
	public void setPropertiesFrom(final PropertyHolder holder, final boolean overwrite) {
		checkLock();
		holder.namePropertyPairs().forEach(p -> this.properties.put(p.getKey(), p.getValue()));
	}

	private void checkLock() {
		if (locked) {
			throw new IllegalStateException("Configuration properties are already locked.");
		}
	}

	@Override
	public boolean isLocked() {
		return locked;
	}

	public void lock() {
		this.locked = true;
	}
}
