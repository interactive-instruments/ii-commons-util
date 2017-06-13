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
package de.interactive_instruments.exceptions;

import de.interactive_instruments.properties.PropertyUtils;
import org.slf4j.LoggerFactory;

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
