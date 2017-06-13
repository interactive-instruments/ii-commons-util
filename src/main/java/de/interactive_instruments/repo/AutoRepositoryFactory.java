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

import de.interactive_instruments.Credentials;
import de.interactive_instruments.IFile;

import java.io.IOException;
import java.net.URI;
import java.util.ServiceLoader;
import java.util.Set;

/**
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
public class AutoRepositoryFactory implements RepositoryFactory {

	private final ServiceLoader<RepositoryFactoryService> services;

	public AutoRepositoryFactory() {
		services = ServiceLoader.load(RepositoryFactoryService.class);
	}

	@Override
	public Repository createRepository(final IFile localDir, final URI uri, final Credentials credentials,
			final Set<String> groupFilter) throws IOException {
		for (final RepositoryFactoryService service : services) {
			if (service.canHandle(uri, credentials)) {
				return service.createRepository(localDir, uri, credentials, groupFilter);
			}
		}
		return null;
	}

	@Override
	public Repository createRepository(final IFile localDir, final URI uri, final Credentials credentials) throws IOException {
		for (final RepositoryFactoryService service : services) {
			if (service.canHandle(uri, credentials)) {
				return service.createRepository(localDir, uri, credentials);
			}
		}
		return null;
	}
}
