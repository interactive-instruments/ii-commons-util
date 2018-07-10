/**
 * Copyright 2017-2018 European Union, interactive instruments GmbH
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

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
public class TimedExpiredItemsRemover extends TimerTask {

	private final List<ExpItemHldRemTimes> holders = new ArrayList<>();
	private static final Logger logger = LoggerFactory.getLogger(TimedExpiredItemsRemover.class);

	private static class ExpItemHldRemTimes {
		private final ExpirationItemHolder holder;
		private final long maxTime;
		private final TimeUnit timeUnit;

		public ExpItemHldRemTimes(final ExpirationItemHolder holder, final long maxTime, final TimeUnit timeUnit) {
			this.holder = holder;
			this.maxTime = maxTime;
			this.timeUnit = timeUnit;
		}
	}

	@Override
	public void run() {
		try {
			for (final ExpItemHldRemTimes e : holders) {
				e.holder.removeExpiredItems(e.maxTime, e.timeUnit);
			}
		} catch (final Exception e) {
			logger.error("Expiration Item Holder threw exception: ", e);
		}
	}

	public void addExpirationItemHolder(ExpirationItemHolder holder, long maxTime, TimeUnit unit) {
		if (holder == null) {
			throw new IllegalArgumentException("ExpirationItemHolder is null");
		}
		holders.add(new ExpItemHldRemTimes(holder, maxTime, unit));
	}
}
