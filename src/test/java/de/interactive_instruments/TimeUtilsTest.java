/**
 * Copyright 2017 European Union, interactive instruments GmbH
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

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Test;

/**
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
public class TimeUtilsTest {

	@Test
	public void testMilisAsMinsSeconds() {
		assertEquals("3sec", TimeUtils.milisAsMinsSeconds(3650));
		assertEquals("50ms", TimeUtils.milisAsMinsSeconds(50));
		assertEquals("0ms", TimeUtils.milisAsMinsSeconds(0));
		assertEquals("1min 20sec", TimeUtils.milisAsMinsSeconds(80000));
		assertEquals("133min 20sec", TimeUtils.milisAsMinsSeconds(8000000));
	}

	@Test
	public void testMilisAsHrMins() {
		assertEquals("3sec", TimeUtils.milisAsHrMins(3650));
		assertEquals("1sec", TimeUtils.milisAsHrMins(50));
		assertEquals("0sec", TimeUtils.milisAsHrMins(0));
		assertEquals("1min", TimeUtils.milisAsHrMins(80000));
		assertEquals("13min", TimeUtils.milisAsHrMins(800000));
		assertEquals("2hr 13min", TimeUtils.milisAsHrMins(8000000));
	}

	@Test
	public void testDateToStr() {
		assertEquals("2017-06-22T16:54:38+02:00", TimeUtils.dateToIsoString(new Date(1498143278000L)));
		assertEquals(1498143278000L, TimeUtils.string8601ToDate("2017-06-22T16:54:38+02:00").getTime());
	}
}
