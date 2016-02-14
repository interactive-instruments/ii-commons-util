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

import de.interactive_instruments.exceptions.ExcUtils;

import java.util.UUID;

/**
 * The default Id factory implementation for constructing DefaultId objects.
 *
 * @author J. Herrmann ( herrmann <aT) interactive-instruments (doT> de )
 */
public class DefaultIdFactory implements IdFactory {

  /**
   * Default C'tor
   */
  DefaultIdFactory() {
  }

  @Override
  public Id createRandomUuid() {
    return new DefaultId(UUID.randomUUID().toString());
  }

  @Override
  public Id createFromStrAndPreserve(String s) {
    return new DefaultId(s);
  }

  @Override
  public Id createFromStrAsUuid(final String s) {
    try {
      if(s.length()==36) {
        return new DefaultId(UUID.fromString(s));
      }
    } catch (IllegalArgumentException e) {
      ExcUtils.supress(e);
    }
    return new DefaultId(UUID.nameUUIDFromBytes(s.getBytes()));
  }

  @Override
  public Id createFromUuid(UUID uuid) { return new DefaultId(uuid); }
}
