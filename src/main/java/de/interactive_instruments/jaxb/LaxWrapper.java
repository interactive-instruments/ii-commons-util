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
package de.interactive_instruments.jaxb;

/**
 * @author J. Herrmann ( herrmann <aT) interactive-instruments (doT> de )
 */

import java.util.*;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class LaxWrapper<T> {

	private List<T> items;

	public LaxWrapper() {
		items = new ArrayList<T>();
	}

	public LaxWrapper(List<T> items) {
		this.items = items;
	}

	@XmlAnyElement(lax = true)
	public List<T> getItems() {
		return items;
	}

	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer("LaxWrapper{");
		sb.append("items={");
		for (T item : items) {
			sb.append("{").append(item).append("}");
		}
		sb.append('}');
		return sb.toString();
	}
}
