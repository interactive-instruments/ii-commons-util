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
package de.interactive_instruments.repo;

import java.io.IOException;
import java.net.URI;
import java.util.Set;

import de.interactive_instruments.Credentials;
import de.interactive_instruments.IFile;

/**
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
public interface RepositoryFactory {

	Repository createRepository(final IFile localDir, final URI uri, final Credentials credentials,
			final Set<String> groupFilter) throws IOException;

	Repository createRepository(final IFile localDir, final URI uri, final Credentials credentials) throws IOException;

	default Repository createRepository(final IFile localDir, final URI uri) throws IOException {
		return createRepository(localDir, uri, null);
	}
}
