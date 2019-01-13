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
package de.interactive_instruments.io;

import java.io.File;
import java.util.regex.Pattern;

/**
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
public class DefaultFileIgnoreFilter implements MultiFileFilter {

    private static Pattern pattern = Pattern.compile(".*(\\.DS_Store|\\.git.*|\\.localized|desktop.ini|Thumbs.db|__MACOSX)");

    public static boolean acceptFile(final File p) {
        return !pattern.matcher(p.getAbsolutePath()).matches();
    }

    private final static DefaultFileIgnoreFilter INSTANCE = new DefaultFileIgnoreFilter();

    public static MultiFileFilter getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean accept(final File p) {
        return acceptFile(p);
    }
}
