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
package de.interactive_instruments.logging;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Layout;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.LoggingEvent;

public class DefaultBufferedAppender extends AppenderSkeleton implements BufferedLogAppender {

	// One list for every logger
	private final List<String> log;
	private final int messagSizeHolding;

	public DefaultBufferedAppender(int minMessagSizeHolding) {
		// For a better performance the buffer has the double size
		this.messagSizeHolding = minMessagSizeHolding * 2;
		log = new ArrayList<String>(messagSizeHolding);
		this.layout = new PatternLayout("[%d{HH:mm:ss,SSS}]  %m");
	}

	@Override
	public synchronized List<String> getLastMessages() {
		final List<String> logMessage = new ArrayList<String>(log);
		log.clear();
		return logMessage;
	}

	@Override
	public boolean hasNewMessages() {
		return !log.isEmpty();
	}

	@Override
	protected void append(final LoggingEvent event) {
		if (log.size() == messagSizeHolding) {
			// The list is full, remove old messages by deleting the half list.
			log.subList(0, messagSizeHolding / 2).clear();
		}
		log.add(layout.format(event));
	}

	@Override
	public void close() {}

	@Override
	public boolean requiresLayout() {
		// Because we use the SimpleJSONLayout as default..
		return false;
	}

	@Override
	public void setLayout(Layout layout) {
		// Ignore user specific layout yet

		// Maybe accept JSON Layouts (interface JSONLoggingLayout) in
		// future versions if needed...
	}
}
