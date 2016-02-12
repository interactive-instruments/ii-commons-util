/**
 * Copyright 2016 interactive instruments GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.interactive_instruments;

import de.interactive_instruments.properties.PropertyHolder;

import java.util.Map;

/**
 * @author J. Herrmann ( herrmann <aT) interactive-instruments (doT> de )
 */
public class Credentials {

    private final String username;
    private String password;

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
       return holder==null ? null : new Credentials(
               holder.getProperty("username"), holder.getProperty("password"));
   }

    public static Credentials fromMap(final Map<String,String> map) {
        return map==null ? null : new Credentials(
                map.get("username"), map.get("password"));
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
        return super.toString()+" "+this.username;
    }
}
