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

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.spi.LoggingEvent;

import de.interactive_instruments.IFile;
import de.interactive_instruments.exceptions.ExcUtils;

/**
 * @author J. Herrmann ( herrmann <aT) interactive-instruments (doT> de )
 */
public class WLogAppender extends DefaultBufferedAppender {

	private final String logFile;

	public WLogAppender(final String file, int minMessagSizeHolding) throws IOException {
		super(minMessagSizeHolding);

		this.logFile = file;
	}

	@Override
	protected void append(LoggingEvent event) {
		super.append(event);
		synchronized (this.logFile) {
			BufferedWriter bw = null;
			try {
				bw = new BufferedWriter(new FileWriter(logFile, true));
				final SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS");
				bw.write(df.format(new Date(event.getTimeStamp())) + "  " +
						event.getMessage().toString() + System.lineSeparator());
				bw.flush();
			} catch (IOException e) {
				ExcUtils.supress(e);
			} finally {
				IFile.closeQuietly(bw);
			}
		}
	}

}
