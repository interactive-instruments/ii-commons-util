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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
public class JarUtils {
    private JarUtils() {}

    /**
     * Returns a list of all class names in a jar
     */
    public static List<String> scanForClassNames(final File jar) throws IOException {
        // The trick is here to not load the Jar with the class loader but to extract it as ZIP
        final List<String> classNames = new ArrayList<String>();
        ZipInputStream zip = null;
        try {
            zip = new ZipInputStream(new FileInputStream(jar));
            for (ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()) {
                if (entry.getName().endsWith(".class") && !entry.isDirectory()) {
                    StringBuilder className = new StringBuilder();

                    // Extract full qualified class name from the path entry by
                    // replacing / with .
                    for (String part : entry.getName().split("/")) {
                        if (className.length() != 0) {
                            className.append(".");
                        }
                        className.append(part);
                        if (part.endsWith(".class")) {
                            className.setLength(className.length() - ".class".length());
                        }
                    }
                    classNames.add(className.toString());
                }
            }
        } finally {
            IoUtils.closeQuietly(zip);
        }
        return classNames;
    }

    public static Manifest getManifest(final File jar) throws IOException {
        try (final JarInputStream jarStream = new JarInputStream(new FileInputStream(jar))) {
            return jarStream.getManifest();
        }
    }

}
