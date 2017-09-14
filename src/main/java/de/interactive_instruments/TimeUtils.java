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

import java.text.ParseException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

import org.apache.commons.lang3.time.FastDateFormat;

/**
 * Time Utilities
 *
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
public final class TimeUtils {

	// thread safe (in contrast to SimpleDateFormat)
	public static final FastDateFormat ISO_DATETIME_TIME_ZONE_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ssZZ");

	private TimeUtils() {}

	/**
	 * Returns the deltaTime as formatted String with
	 * hours and/or minutes or as seconds
	 *
	 * @param deltaTime
	 * @return
	 */
	public static String milisAsHrMins(final long deltaTime) {
		if (deltaTime == 0) {
			return "0sec";
		}
		long minutes = (deltaTime / 1000) / 60;
		final long hours = minutes / 60;
		minutes = minutes - (hours * 60);

		if (hours == 0 && minutes == 0) {
			return Math.max(1, (deltaTime / 1000) % 60) + "sec";
		}

		final StringBuffer duration = new StringBuffer();
		if (hours > 0) {
			duration.append(hours);
			duration.append("hr");
			if (minutes > 0) {
				duration.append(" ");
			}
		}
		if (minutes > 0) {
			duration.append(minutes);
			duration.append("min");
		}
		return duration.toString();
	}

	/**
	 * Returns the deltaTime as formatted String with
	 * minutes and/or seconds or as milliseconds
	 *
	 * @param deltaTime
	 * @return
	 */
	public static String milisAsMinsSeconds(final long deltaTime) {
		final int seconds = (int) ((deltaTime / 1000) % 60);
		final int minutes = (int) ((deltaTime / 1000) / 60);

		if (minutes == 0 && seconds == 0) {
			return deltaTime + "ms";
		}
		final StringBuffer duration = new StringBuffer();
		if (minutes > 0) {
			duration.append(minutes);
			duration.append("min");
			if (seconds > 0) {
				duration.append(" ");
			}
		}
		if (seconds > 0) {
			duration.append(seconds);
			duration.append("sec");
		}
		return duration.toString();
	}

	public static String currentDurationAsHrMins(final long startTime) {
		final long stopTime = System.currentTimeMillis();
		return milisAsHrMins(stopTime - startTime);
	}

	public static String currentDurationAsMinsSeconds(final long startTime) {
		final long stopTime = System.currentTimeMillis();
		return milisAsMinsSeconds(stopTime - startTime);
	}

	public static String dateToIsoString(final Date date) {
		return ISO_DATETIME_TIME_ZONE_FORMAT.format(date);
	}

	public static Date string8601ToDate(final String str) {
		try {
			return ISO_DATETIME_TIME_ZONE_FORMAT.parse(str);
		} catch (ParseException e) {
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * Calculate the initial delay for a target time. Returns the delay in
	 * seconds, use <code>TimeUnit.SECONDS.toMillis()</code> to convert it
	 * to milliseconds.
	 *
	 * @param targetHour target hour
	 * @param targetMin target minutes
	 * @param targetSec target seconds
	 *
	 * @return target delay in seconds
	 */
	public static long calcDelay(final int targetHour, final int targetMin, final int targetSec) {
		final ZonedDateTime zonedNow = ZonedDateTime.of(LocalDateTime.now(), ZoneId.systemDefault());
		ZonedDateTime zonedNextTarget = zonedNow.withHour(targetHour).withMinute(targetMin).withSecond(targetSec);
		if (zonedNow.compareTo(zonedNextTarget) > 0)
			zonedNextTarget = zonedNextTarget.plusDays(1);
		return Duration.between(zonedNow, zonedNextTarget).getSeconds();
	}
}
