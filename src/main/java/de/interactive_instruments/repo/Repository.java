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

import de.interactive_instruments.IFile;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.Set;

/**
 * Repository
 *
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
public interface Repository {

	/**
	 * Get path to local directory
	 *
	 * @return
	 */
	IFile getLocalDirectory();

	/**
	 * List artifacts in the local directory
	 *
	 * @return
	 */
	Map<String, Artifact> getLocalArtifacts();

	/**
	 * Get remote repository URI
	 *
	 * @return
	 */
	URI getRemote();

	/**
	 * Get remote artifacts in repository
	 *
	 * @return
	 */
	Map<String, Artifact> getRemoteArtifacts(boolean latestVersionOnly);

	interface SyncCmd {
		/**
		 * Synchronizes the local repo with the remote repo
		 * @throws IOException
		 */
		void sync() throws IOException;

		/**
		 * Returns the artifacts that will be synced
		 * @return
		 */
		Set<Artifact> getCandidates();
	}

	/**
	 * Sync with remote repository, returns all updated artifacts
	 *
	 * @return
	 */
	SyncCmd prepareSync();

}
