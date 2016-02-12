/**
 * Copyright 2016 interactive instruments GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.interactive_instruments;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * XML Namespace holder
 *
 * @author J. Herrmann ( herrmann <aT) interactive-instruments (doT> de )
 */
public interface NamespaceHolder {

    /**
     * Returns a Map with a namespace uri -> namespace prefix mapping
     * @return namespace uri -> namespace prefix map
     */
    Map<String,String> getNamespacesAsMap();

    default boolean hasPrefixForNamespace(final String namespaceUri) {
        return getNamespacesAsMap().containsKey(namespaceUri);
    }


    /**
     * Returns a Entryset with a namespace uri -> namespace prefix mapping
     * @return namespace uri -> namespace prefix entry set
     */
    default Set<Map.Entry<String, String>> getEntrySet() {
        return getNamespacesAsMap().entrySet();
    }

    /**
     * Returns the prefix for a known namespace URI or null if unknown
     * @param namespaceUri
     * @return namespace URI or null
     */
    default String getPrefixForNamespaceUri(String namespaceUri) {
        return getNamespacesAsMap().get(namespaceUri);
    }

    /**
     * Returns the number of known namespace URI mappings
     * @return size
     */
    default int namespaceMappingSize() {
        return getNamespacesAsMap().size();
    }

    /**
     * Namespaces are returned in the format:
     * xmlns:<namespacePrefix>='<namespaceURL>'
     */
    default String getXmlnsDeclarations() {
        return getEntrySet().stream().
                map( e -> "xmlns:"+e.getValue()+"='"+e.getKey()+"'").
                collect(Collectors.joining(" "));
    }

    /**
     * Namespaces are returned in the format:
     * declare namespace <namespacePrefix>='<namespaceURL>';
     */
    default String getDeclarationsForXPathExpressions() {
        return getEntrySet().stream().
                map( e -> "declare namespace "+e.getValue()+"='"+e.getKey()+"';"+System.getProperty("line.separator")).
                collect(Collectors.joining(" "));
    }
}
