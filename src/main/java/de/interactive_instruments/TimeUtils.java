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

import java.text.ParseException;
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
}
