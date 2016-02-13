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

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;

/**
 * An interface for an item in a remote repository.
 *
 * @author J. Herrmann ( herrmann <aT) interactive-instruments (doT> de )
 */
public interface RemoteRepositoryItem extends RepositoryItem {

  /**
   * The URI of the item in the remote repository.
   *
   * @return
   */
  URI getRemoteUri();

  /**
   * Gets the remote item and deploys it on the local system.
   *
   * @return file on the local system.
   * @throws IOException if download fails
   */
  IFile makeAvailable(final Path path) throws IOException;

  /**
   * Get file on local system.
   *
   * @return file on local system; null if isAvailable() is false;
   */
  IFile getLocal();

  /**
   * Checks if the item is available on the local system.
   *
   * @return true if item is available on local system; false otherwise
   */
  boolean isAvailable();
}
