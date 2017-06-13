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

import de.interactive_instruments.Version;

/**
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
public abstract class AbstractArtifact implements Artifact {

	protected final String id;
	protected final String name;
	protected final String groupname;
	protected final Version version;

	protected AbstractArtifact(final String id, final String name, final String groupname, final Version version) {
		this.id = id;
		this.name = name;
		this.groupname = groupname;
		this.version = version;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}

	public String getGroupname() {
		return groupname;
	}

	@Override
	public Version getVersion() {
		return version;
	}
}
