/*
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

package de.interactive_instruments.Repository;

import de.interactive_instruments.Versionable;

import java.util.UUID;

/**
 * An interface for an item in a repository.
 *
 * @author J. Herrmann ( herrmann <aT) interactive-instruments (doT> de )
 */
public interface RepositoryItem extends Versionable {

  /**
   * Unique ID of the item in the repository.
   *
   * NOTE: the ID may not change when the version of the item changes!
   *
   * @return unique ID of the item as UUID
   */
  UUID getId();

  /**
   * Label of the item in the repository.
   *
   * @return label of the item as string
   */
  String getLabel();

  /**
   * Compares this object with the specified RepositoryItem object for order by
   * comparing the ID, the version and the label.
   *
   * Returns a negative integer, zero, or a positive integer as this object is less
   * than, equal to, or greater than the specified object.
   *
   * @param item Repository item to compare with.
   * @return  a negative integer, zero, or a positive integer as this object
   *          is less than, equal to, or greater than the specified object.
   */
  @Override default int compareTo(final Object item) {
    final RepositoryItem rItem = (RepositoryItem)item;
    final int rItemId = getId().compareTo(rItem.getId());
    if(rItemId!=0) {
      return rItemId;
    }
    final int rItemVersion = getLabel().compareTo(rItem.getLabel());
    if(rItemVersion!=0) {
      return rItemVersion;
    }
    final int rItemLabel = getLabel().compareTo(rItem.getLabel());
    if(rItemLabel!=0) {
      return rItemLabel;
    }
    return 0;
  }
}
