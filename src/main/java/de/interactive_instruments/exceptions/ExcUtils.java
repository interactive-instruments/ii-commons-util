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
package de.interactive_instruments.exceptions;

import org.slf4j.LoggerFactory;

import de.interactive_instruments.properties.PropertyUtils;

public class ExcUtils {

	public final static boolean PRINT_SUPPRESSED_EXCEPTIONS = PropertyUtils.getenvOrProperty(
			"II_EXCEPTIONS_PRINT_SUPPRESSED", "false").equals("true");

	public static void suppress(final Exception e) {
		if (PRINT_SUPPRESSED_EXCEPTIONS) {
			System.err.println("------------- Stacktrace -------------");
			e.printStackTrace(System.err);
			LoggerFactory.getLogger("ROOT").error("SUPPRESSED", "SUPPRESSED EXCEPTION: ", e);
			System.err.println("--------------------------------------");
		}
	}

	public static void suppress(final Throwable e) {
		if (PRINT_SUPPRESSED_EXCEPTIONS) {
			System.err.println("------------- Stacktrace -------------");
			e.printStackTrace(System.err);
			System.err.println("--------------------------------------");
			LoggerFactory.getLogger("ROOT").error("SUPPRESSED", "SUPPRESSED EXCEPTION: ", e);
		}
	}
}
