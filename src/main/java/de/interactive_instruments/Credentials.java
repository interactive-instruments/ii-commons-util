/**
 * Copyright 2017 European Union, interactive instruments GmbH
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
package de.interactive_instruments;

import java.util.Map;
import java.util.Objects;

import de.interactive_instruments.properties.Properties;
import de.interactive_instruments.properties.PropertyHolder;

/**
 *
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
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
