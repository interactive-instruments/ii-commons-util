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
package de.interactive_instruments.model.std;

import de.interactive_instruments.Releasable;

/**
 * An abstract class for managing objects that implement the RetrievableItem and the Releasable
 * interface.
 *
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
public abstract class ManagedItemRegistry<T extends RetrievableItem & Releasable> implements Releasable {

	private final DefaultRetrievableItemMap<T> managedObjects = new DefaultRetrievableItemMap<T>();

	/**
	 * Registers a managed item
	 *
	 * @param managedItem item to register
	 */
	public void register(T managedItem) {
		managedObjects.put(managedItem);
	}

	/**
	 * Unregisters the managed item.
	 *
	 * NOTE: does not call release() method of the managed object
	 *
	 * @param managedItem item to unregister
	 */
	public void unregister(T managedItem) {
		this.managedObjects.remove(managedItem.getId());
	}

	/**
	 * Gets the managed item
	 *
	 * @param id managed item Id
	 * @return managed item
	 */
	public T getById(final Id id) {
		return managedObjects.get(id);
	}

	/**
	 * Releases all managed items
	 */
	@Override
	public void release() {
		managedObjects.values().forEach(m -> m.release());
		managedObjects.clear();
	}

	@Override
	public void finalize() {
		release();
	}
}
