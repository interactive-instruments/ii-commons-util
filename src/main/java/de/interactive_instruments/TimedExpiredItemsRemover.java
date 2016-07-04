/**
 * Copyright 2010-2016 interactive instruments GmbH
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

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import de.interactive_instruments.exceptions.ExcUtils;

/**
 * @author J. Herrmann ( herrmann <aT) interactive-instruments (doT> de )
 */
public class TimedExpiredItemsRemover extends TimerTask {

	private List<ExpItemHldRemTimes> holders = new ArrayList<>();

	private static class ExpItemHldRemTimes {
		private final ExpirationItemHolder holder;
		private final long maxTime;
		private final TimeUnit timeUnit;

		public ExpItemHldRemTimes(ExpirationItemHolder holder, long maxTime, TimeUnit timeUnit) {
			this.holder = holder;
			this.maxTime = maxTime;
			this.timeUnit = timeUnit;
		}
	}

	@Override
	public void run() {
		try {
			if (holders != null && holders.isEmpty()) {
				holders.forEach(e -> e.holder.removeExpiredItems(e.maxTime, e.timeUnit));
			}
		} catch (Exception e) {
			ExcUtils.suppress(e);
		}
	}

	public void addExpirationItemHolder(ExpirationItemHolder holder, long maxTime, TimeUnit unit) {
		if (holder == null) {
			throw new IllegalArgumentException("ExpirationItemHolder is null");
		}
		holders.add(new ExpItemHldRemTimes(holder, maxTime, unit));
	}
}
