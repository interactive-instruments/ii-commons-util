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
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
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
