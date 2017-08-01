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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

/**
 * An object for comparing version numbers with respect to the
 * major, minor and bugfix version number.
 *
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 *
 */
@XmlRootElement(name = "Version", namespace = II_Constants.II_COMMON_UTILS_NS)
public class Version implements ImmutableVersion {

	private int major;
	private int minor;
	private int bugfix;
	private boolean snapshot;
	/**
	 * MAJRO.MINOR(.optional BUGFIX)(.optional build)(optional -SNAPSHOT)
	 */
	private final static Pattern versionPattern = Pattern.compile("(\\d+)\\.(\\d+)(\\.\\d+)?(\\.\\d+)?(-SNAPSHOT)?");

	public Version() {
		minor = 1;
	}

	public Version(int major, int minor, int bugfix) {
		this.major = major;
		this.minor = minor;
		this.bugfix = bugfix;
	}

	public Version(int major, int minor, int bugfix, boolean snapshot) {
		this.major = major;
		this.minor = minor;
		this.bugfix = bugfix;
		this.snapshot = snapshot;
	}

	public Version(final Version version) {
		this.major = version.major;
		this.minor = version.minor;
		this.bugfix = version.bugfix;
		this.snapshot = version.snapshot;
	}

	/**
	 * Parse a version String and construct a new Version
	 * object
	 *
	 * @param version string to parse
	 * @throws IllegalArgumentException
	 */
	public Version(final String version) throws IllegalArgumentException {
		set(version);
	}

	/**
	 * Parse a version String and return a new Version object
	 *
	 * @param version string to parse
	 * @return a new Version object
	 * @throws IllegalArgumentException
	 */
	public static Version parse(String version) throws IllegalArgumentException {
		return new Version(version);
	}

	/**
	 * Parse and set Version form string
	 *
	 * @return a new Version object
	 * @throws IllegalArgumentException
	 */
	public void set(String version) throws IllegalArgumentException {
		final Matcher versionMatcher = versionPattern.matcher(version);
		if (!versionMatcher.matches()) {
			throw new IllegalArgumentException(
					"A version in the format MAJOR.MINOR or MAJOR.MINOR.PATCH is required: \"" + version + "\"");
		}
		try {
			major = Integer.parseInt(versionMatcher.group(1));
			minor = Integer.parseInt(versionMatcher.group(2));
			final String bugfixStr = versionMatcher.group(3);
			if (!SUtils.isNullOrEmpty(bugfixStr)) {
				bugfix = Integer.parseInt(bugfixStr.substring(1));
			}
		} catch (final NumberFormatException e) {
			throw new IllegalArgumentException(
					"A version in the format MAJOR.MINOR or MAJOR.MINOR.PATCH is required: \"" + version + "\"");
		}
		if (versionMatcher.group(5) != null) {
			snapshot = true;
		}
	}

	/**
	 * Returns true if the version is equal to the compared version
	 *
	 * @param version the version to be compared
	 * @return  {@code true} if this object is the same as the version argument AND
	 * 					the major, minor and bugfix versions are the same;
	 *          {@code false} otherwise.
	 */
	@Override
	public boolean equals(Object version) {
		if (!(version instanceof Version)) {
			return false;
		}
		final Version v = (Version) version;
		return this.major == v.major &&
				this.minor == v.minor &&
				this.bugfix == v.bugfix &&
				this.snapshot == v.snapshot;
	}

	@XmlValue
	@Override
	public String getAsString() {
		return toString();
	}

	@Override
	public int getMajorVersion() {
		return this.major;
	}

	@Override
	public int getMinorVersion() {
		return this.minor;
	}

	@Override
	public int getBugfixVersion() {
		return this.bugfix;
	}

	@Override
	public boolean isSnapshot() {
		return snapshot;
	}

	void setString(String ver) throws IllegalArgumentException {
		set(ver);
	}

	@Override
	public String toString() {
		return this.major + "." + this.minor + "." + this.bugfix;
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	/**
	 * Increments the bugfix version number
	 *
	 * @return this bugfix incremented object
	 */
	public Version incBugfix() {
		this.bugfix++;
		return this;
	}

}
