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
package de.interactive_instruments;

/**
 * An interface for comparing version numbers with respect to the major, minor and bugfix version number.
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
     *
     * @param version
     *            the version to be compared
     * @return a negative integer, zero, or a positive integer as this object is less than, equal to, or greater than the specified version.
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
