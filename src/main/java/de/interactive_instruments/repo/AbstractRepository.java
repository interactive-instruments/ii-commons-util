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

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import de.interactive_instruments.Credentials;
import de.interactive_instruments.IFile;

/**
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
public abstract class AbstractRepository implements Repository {

	protected final URI remoteUri;
	protected final Credentials credentials;
	protected final IFile localDir;
	protected final Set<String> groupFilter;
	protected final Map<String, Artifact> localArtifacts = new HashMap<>();

	protected AbstractRepository(final IFile localDir, final URI remoteUri, final Credentials credentials,
			final Set<String> groupFilter) {
		this.localDir = localDir;
		this.remoteUri = remoteUri;
		this.credentials = credentials;
		this.groupFilter = groupFilter;
	}

	@Override
	final public URI getRemote() {
		return remoteUri;
	}

	@Override
	final public IFile getLocalDirectory() {
		return localDir;
	}

	@Override
	final public Map<String, Artifact> getLocalArtifacts() {
		return localArtifacts;
	}
}
