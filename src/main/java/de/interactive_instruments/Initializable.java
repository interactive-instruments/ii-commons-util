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

import de.interactive_instruments.exceptions.InitializationException;
import de.interactive_instruments.exceptions.InvalidStateTransitionException;
import de.interactive_instruments.exceptions.config.ConfigurationException;

/**
 * Interface for initializing objects
 *
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
public interface Initializable {

    /**
     * Initialized the Object
     *
     * @throws ConfigurationException
     *             if the configuration options are invalid
     * @throws InitializationException
     *             if the initialization failed
     * @throws InvalidStateTransitionException
     *             if Object is already initialized
     */
    void init() throws ConfigurationException, InitializationException, InvalidStateTransitionException;

    /**
     * Checks if the Object is initialized
     *
     * @return true if object is initialized, false otherwise
     */
    boolean isInitialized();
}
