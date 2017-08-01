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
package de.interactive_instruments;

/**
 * An interface for comparing version numbers with respect to the
 * major, minor and bugfix version number.
 *
 * @author herrmann@interactive-instruments.de.
 */
public interface ImmutableVersion extends Comparable<ImmutableVersion> {

	int getMajorVersion();

	int getMinorVersion();

	int getBugfixVersion();

	default boolean isSnapshot() {
		return false;
	}

	String getAsString();

	default String getAsStringWithExtension() {
		if (!isSnapshot()) {
			return getAsString();
		} else {
			return getAsString() + "-SNAPSHOT";
		}
	}

	/**
	 * Compare a version
	 * @param version the version to be compared
	 * @return a negative integer, zero, or a positive integer as this
	 * object is less than, equal to, or greater than the specified version.
	 */
	@Override
	default int compareTo(ImmutableVersion version) {
		if (version == null) {
			throw new IllegalArgumentException("Version is null!");
		}
		if (getMajorVersion() < version.getMajorVersion()) {
			return -1;
		} else if (getMajorVersion() > version.getMajorVersion()) {
			return 1;
		}
		if (getMinorVersion() < version.getMinorVersion()) {
			return -1;
		} else if (getMinorVersion() > version.getMinorVersion()) {
			return 1;
		}
		if (getBugfixVersion() < version.getBugfixVersion()) {
			return -1;
		} else if (getBugfixVersion() > version.getBugfixVersion()) {
			return 1;
		}
		if (isSnapshot() && !version.isSnapshot()) {
			return -1;
		} else if (!isSnapshot() && version.isSnapshot()) {
			return 1;
		}
		return 0;
	}
}
