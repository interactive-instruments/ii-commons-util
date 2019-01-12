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
package de.interactive_instruments.properties;

import java.util.Set;

/**
 * Get specific classes of properties
 *
 * @author herrmann@interactive-instruments.de.
 */
public interface ClassifyingPropertyHolder extends PropertyHolder {

    /**
     * Filters specific properties in a property hierarchy. Filtered properties are returned without the property classification.
     *
     * @param classification
     * @return
     */
    ClassifyingPropertyHolder getFlattenedPropertiesByClassification(final String classification);

    /**
     * Filters specific properties in a property hierarchy. Filtered properties are returned with the full property hierarchy.
     *
     * @param classification
     * @return
     */
    ClassifyingPropertyHolder getPropertiesByClassification(final String classification);

    /**
     * Get all property names on the first level of the property hierarchy.
     *
     * For the properties foo.bar, bar2 and prop.x.y a set with the values foo and prop are returned.
     *
     * @return
     */
    Set<String> getFirstLevelClassifications();

}
