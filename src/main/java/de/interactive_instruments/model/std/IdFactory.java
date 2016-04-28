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

package de.interactive_instruments.model.std;

import java.util.UUID;

/**
 * A factory interface for constructing Id objects.
 *
 * @author J. Herrmann ( herrmann <aT) interactive-instruments (doT> de )
 */
public interface IdFactory {

  /**
   * Creates a random UUID
   *
   * @return new Id object
   */
  Id createRandomUuid();

  /**
   * Create an Id object from a String, preserves the String as identifier.
   *
   * @param str an id string
   * @return new Id object which holds the string
   */
  Id createFromStrAndPreserve(String str);

  /**
   * Create an UUID from the string standard representation.
   *
   * @param str a string
   * @return new Id object which holds an UUID
   */
  Id createFromStrAsUuid(String str);

  /**
   * Creates an Id object from an UUID, preserves the UUID
   *
   * @param uuid
   * @return
   */
  Id createFromUuid(UUID uuid);

  static IdFactory getDefault() {
    return new DefaultIdFactory();
  }
}