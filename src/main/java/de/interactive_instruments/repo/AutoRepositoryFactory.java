/**
 * Copyright 2017-2019 European Union, interactive instruments GmbH
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
package de.interactive_instruments.repo;

import java.io.IOException;
import java.net.URI;
import java.util.ServiceLoader;
import java.util.Set;

import de.interactive_instruments.Credentials;
import de.interactive_instruments.IFile;

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
