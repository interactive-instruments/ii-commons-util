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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import de.interactive_instruments.MimeTypeUtils;
import de.interactive_instruments.exceptions.MimeTypeUtilsException;

/**
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
public class ContentTypeFilter implements MultiFileFilter {

    private final Set<String> allowedMimeTypes;

    public ContentTypeFilter(final Set<String> allowedMimeTypes) {
        this.allowedMimeTypes = Objects.requireNonNull(allowedMimeTypes);
    }

    public ContentTypeFilter(final String... allowedMimeTypes) {
        this.allowedMimeTypes = new HashSet<>(Arrays.asList(allowedMimeTypes));
    }

    public Set<String> getAllowedMimeTypes() {
        return allowedMimeTypes;
    }

    public boolean accept(final String mimeType) {
        return allowedMimeTypes.contains(mimeType);
    }

    @Override
    public boolean accept(final File path) {
        try {
            return allowedMimeTypes.contains(MimeTypeUtils.detectMimeType(path));
        } catch (MimeTypeUtilsException e) {
            return false;
        }
    }
}
