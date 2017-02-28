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
package de.interactive_instruments.model.std;

import de.interactive_instruments.Releasable;

/**
 * An abstract class for managing objects that implement the RetrievableItem and the Releasable
 * interface.
 *
 * @author J. Herrmann ( herrmann <aT) interactive-instruments (doT> de )
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
