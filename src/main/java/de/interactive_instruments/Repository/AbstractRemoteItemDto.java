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

import de.interactive_instruments.IFile;
import de.interactive_instruments.ImmutableVersion;
import de.interactive_instruments.Version;
import de.interactive_instruments.model.std.Id;

import java.net.URI;
import java.util.UUID;

/**
 * Abstract remote item data transfer object class
 *
 * Derived classes must implement the DTO pattern.
 *
 * @author herrmann@interactive-instruments.de.
 */
public abstract class AbstractRemoteItemDto implements RemoteRepositoryItem {

  protected final URI remoteUri;
  protected final Id id;
  protected final String label;
  protected final Version version;
  protected IFile file;

  AbstractRemoteItemDto(final URI remoteUri, final Id id, final String label, final Version version) {
    this.remoteUri = remoteUri;
    this.id = id;
    this.label = label;
    this.version = version;
  }

  @Override public URI getUri() {
    return remoteUri;
  }

  @Override public IFile getLocal() {
    return file;
  }

  @Override public boolean isAvailable() {
    return file!=null && file.exists();
  }

  @Override public Id getId() {
    return id;
  }

  @Override public String getLabel() {
    return label;
  }

  @Override public ImmutableVersion getVersion() {
    return version;
  }
}
