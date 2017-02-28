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

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

import de.interactive_instruments.exceptions.ImmutableLockException;

/**
 * With the ImmutableLock object an object can be put in a final state
 * by using checkLock in the setter methods of the object.
 *
 * TODO checkLock Annotation
 *
 * @author J. Herrmann ( herrmann <aT) interactive-instruments (doT> de )
 *
 */
@XmlRootElement(name = "FinalState")
public class ImmutableLock {

	@XmlValue
	private boolean locked;

	public ImmutableLock() {
		this.locked = false;
	}

	public void setLock() {
		this.locked = true;
	}

	public void checkLock(Object caller) throws ImmutableLockException {
		if (this.locked) {
			throw new ImmutableLockException(caller);
		}
	}

	public boolean isLocked() {
		return locked;
	}
}
