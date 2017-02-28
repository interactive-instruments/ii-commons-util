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

import java.util.Map;
import java.util.Objects;

import de.interactive_instruments.properties.Properties;
import de.interactive_instruments.properties.PropertyHolder;

/**
 * @author J. Herrmann ( herrmann <aT) interactive-instruments (doT> de )
 */
public class Credentials {

	private final String username;
	private final String password;

	/**
	 * Constructs new Credentials, when both username and password are not set
	 * the method isEmpty() returns true.
	 *
	 * @param username
	 * @param password
	 */
	public Credentials(final String username, final String password) {
		this.username = username;
		this.password = password;
	}

	public static Credentials fromProperties(final PropertyHolder holder) {
		if (holder == null || !holder.hasProperty("username")) {
			return null;
		}
		return new Credentials(
				holder.getProperty("username"), holder.getProperty("password"));
	}

	public static Credentials fromProperties(final Properties properties) {
		if (properties == null || !properties.hasProperty("username")) {
			return null;
		}
		return new Credentials(
				properties.getProperty("username"), properties.getProperty("password"));
	}

	public static Credentials fromMap(final Map<String, String> map) {
		if (map == null || !map.containsKey("username")) {
			return null;
		}
		return new Credentials(
				map.get("username"), map.get("password"));
	}

	public String getUsername() {
		return username;
	}

	public boolean checkPassword(final String password) {
		return Objects.equals(this.password, password);
	}

	public String getPassword() {
		return password;
	}

	public boolean isEmpty() {
		return SUtils.isNullOrEmpty(username) && SUtils.isNullOrEmpty(password);
	}

	public String toBasicAuth() {
		final String userpass = username + ":" + password;
		return "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary(
				userpass.getBytes());
	}

	@Override
	public String toString() {
		return this.username + ":*******";
	}
}
