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
