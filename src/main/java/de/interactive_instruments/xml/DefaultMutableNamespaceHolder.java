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
package de.interactive_instruments.xml;

import java.util.*;

/**
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
public final class DefaultMutableNamespaceHolder extends AbstractNamespaceHolder implements MutableNamespaceHolder {

    private final String defaultNamespace;

    public DefaultMutableNamespaceHolder() {
        this.defaultNamespace = null;
    }

    /**
     * Copies the namespaces from an existing namespace holder
     *
     * @param holder
     */
    public DefaultMutableNamespaceHolder(final NamespaceHolder holder) {
        super(new LinkedHashMap(holder.getNamespacesAsMap()));
        this.defaultNamespace = null;
    }

    public DefaultMutableNamespaceHolder(final String defaultNamespace, final NamespaceHolder holder) {
        super(new LinkedHashMap(holder.getNamespacesAsMap()));
        this.defaultNamespace = defaultNamespace;
    }

    @Override
    public void addNamespaceUriAndPrefix(String namespaceUri, String prefix) {
        final String nsUri = this.prefixMappings.get(prefix);
        if (nsUri == null) {
            this.prefixMappings.put(prefix, namespaceUri);
            final Collection<String> prefixes = this.namespacesUriMappings.get(namespaceUri);
            if (prefixes != null) {
                prefixes.add(prefix);
            } else {
                this.namespacesUriMappings.put(namespaceUri, new ArrayList<String>() {
                    {
                        add(prefix);
                    }
                });
            }
        } else if (!nsUri.equals(namespaceUri)) {
            throw new IllegalArgumentException("The prefix '" + prefix + "' is already mapped to namespace '" + nsUri + "'");
        }
    }

    @Override
    public String getDefaultNamespaceUri() {
        return this.defaultNamespace;
    }

}
