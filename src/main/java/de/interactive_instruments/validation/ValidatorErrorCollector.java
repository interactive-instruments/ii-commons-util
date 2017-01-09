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
package de.interactive_instruments.validation;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import de.interactive_instruments.Releasable;

/**
 * A synchronized collector for the error messages of the SchemaValidator
 *
 * @author J. Herrmann ( herrmann <aT) interactive-instruments (doT> de )
 */
class ValidatorErrorCollector implements Releasable {

	private final StringBuilder sb = new StringBuilder();
	private final int maxErrors;
	private final Map<File, Integer> errorsPerFile = new ConcurrentSkipListMap<>();

	/**
	 * Saves an error message
	 *
	 * @param str message
	 */
	void collectError(final String str, final File file, final int errorsInFile) {
		if (errorsPerFile.size() < maxErrors) {
			synchronized (this) {
				sb.append(str);
			}
		}
		errorsPerFile.put(file, errorsInFile);
	}

	/**
	 * Returns all errors as concatenated string.
	 *
	 * @return concatenated error messages
	 */
	public String getErrorMessages() {
		if (!errorsPerFile.isEmpty()) {
			int errorsPerFileCounter = 0;
			for (final Map.Entry<File, Integer> e : errorsPerFile.entrySet()) {
				if (errorsPerFileCounter < maxErrors) {
					sb.append(e.getValue());
					sb.append(" errors in file ");
					sb.append(e.getKey().getName());
					errorsPerFileCounter++;
				} else {
					sb.append((errorsPerFileCounter - maxErrors));
					sb.append(" additional error messages skipped!");
					break;
				}
			}
		}
		final int numberOfErrors = errorsPerFile.size();
		return sb.toString() + (numberOfErrors < maxErrors ? "" : System.lineSeparator() +
				(numberOfErrors - maxErrors) + " additional error messages were skipped!");
	}

	/**
	 * Returns the number of errors.
	 *
	 * @return
	 */
	public int getErrorCount() {
		return errorsPerFile.size();
	}

	/**
	 * Default constructor.
	 *
	 * @param maxErrors maximum number of errors that will be saved
	 */
	public ValidatorErrorCollector(int maxErrors) {
		this.maxErrors = maxErrors;
	}

	/**
	 * Inner error handler for SAX errors, associated with one file
	 */
	static class ValidatorErrorHandler implements ErrorHandler, Releasable {

		private final File file;
		private final ValidatorErrorCollector callback;
		private int errorsInFile = 0;
		private final StringBuilder lSb = new StringBuilder();

		ValidatorErrorHandler(final File file, final ValidatorErrorCollector callback) {
			this.file = file;
			this.callback = callback;
		}

		private void logError(final String severity, final SAXParseException e) {
			++errorsInFile;
			lSb.append(severity).append(" in file ").append(file.getName()).append(" line ").append(e.getLineNumber()).append(" column ").append(e.getColumnNumber()).append(" : ").append(System.lineSeparator()).append(e.toString()).append(System.lineSeparator());
		}

		void logUnknownError() {
			++errorsInFile;
			lSb.append("FATAL UNKNOWN ERROR in file ").append(file.getName());
		}

		@Override
		public void warning(final SAXParseException e) throws SAXException {
			// todo log missing schema location
		}

		@Override
		public void error(SAXParseException e) throws SAXException {
			logError("ERROR", e);
		}

		@Override
		public void fatalError(SAXParseException e) throws SAXException {
			logError("FATAL ERROR", e);
		}

		@Override
		public void release() {
			if (errorsInFile > 0) {
				callback.collectError(lSb.toString(), file, errorsInFile);
			}
		}

		public boolean hasErrors() {
			return errorsInFile > 0;
		}

	}

	@Override
	public void release() {
		errorsPerFile.clear();
	}

	/**
	 * Creates a new error handler
	 *
	 * @param file file which is checked
	 *
	 * @return a SAX error handler
	 */
	ValidatorErrorHandler newErrorHandler(final File file) {
		return new ValidatorErrorHandler(file, this);
	}
}
