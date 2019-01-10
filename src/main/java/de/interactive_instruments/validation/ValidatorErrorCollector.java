/**
 * Copyright 2017-2019 European Union, interactive instruments GmbH
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
package de.interactive_instruments.validation;

import java.io.File;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicLong;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import de.interactive_instruments.Releasable;

/**
 * A synchronized collector for the error messages of the SchemaValidator
 *
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
class ValidatorErrorCollector implements Releasable {

	private final StringBuilder sb = new StringBuilder();
	private final int maxErrors;
	private final AtomicLong errorCounter = new AtomicLong(0);
	private final AtomicLong testedFiles = new AtomicLong(0);
	private final Map<File, Integer> errorsPerFile = new ConcurrentSkipListMap<>();

	/**
	 * Saves an error message
	 *
	 * @param str message
	 */
	void collectError(final String str, final File file, final int errorsInFile) {
		if (errorCounter.getAndAdd(errorsInFile) < maxErrors) {
			sb.append(str);
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
			final Iterator<Map.Entry<File, Integer>> iterator = errorsPerFile.entrySet().iterator();
			// list at least 25 invalid files
			long maxErrorsPerFileCounter = maxErrors + 25;
			long errorsPerFileCounter = errorCounter.get() < maxErrors ? errorCounter.get() : maxErrors;
			for (; iterator.hasNext(); errorsPerFileCounter++) {
				final Map.Entry<File, Integer> e = iterator.next();
				if (errorsPerFileCounter < maxErrorsPerFileCounter) {
					sb.append(e.getValue());
					sb.append(" errors in file ");
					sb.append(e.getKey().getName());
					if (iterator.hasNext()) {
						sb.append(System.lineSeparator());
					}
				} else {
					break;
				}
			}
		}
		final long c = errorCounter.get();
		if (c <= maxErrors) {
			return sb.toString();
		} else {
			final StringBuilder out = new StringBuilder(sb.length() + 128);
			out.append(sb);
			out.append(System.lineSeparator());
			out.append(c - maxErrors);
			out.append(" additional error messages were skipped. ");
			final long f = testedFiles.get();
			if (f == errorsPerFile.size()) {
				out.append("None of the ");
				out.append(f);
				out.append(" files is schema-valid.");
			} else {
				out.append(+errorsPerFile.size());
				out.append(" files of ");
				out.append(f);
				out.append(" (");
				final NumberFormat percentFormat = NumberFormat.getPercentInstance();
				percentFormat.setMinimumFractionDigits(1);
				percentFormat.setMaximumFractionDigits(2);
				out.append(percentFormat.format(((double) errorsPerFile.size()) / ((double) f)));
				out.append(") are not schema-valid.");
			}
			return out.toString();
		}
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
		// avoid errorCounter sync between threads
		private final long approxMax;
		private final StringBuilder lSb = new StringBuilder();

		ValidatorErrorHandler(final File file, final ValidatorErrorCollector callback) {
			this.file = file;
			this.callback = callback;
			approxMax = callback.errorCounter.get();
		}

		private void logError(final String severity, final SAXParseException e) {
			++errorsInFile;
			if (errorsInFile < approxMax) {
				final String message = e.getMessage();
				lSb.append(severity).append(" in file ").append(file.getName()).append("( line ").append(e.getLineNumber())
						.append(", column ").append(e.getColumnNumber()).append(") : ");
				if (message.length() > 130) {
					lSb.append(System.lineSeparator());
				}
				lSb.append(message).append(System.lineSeparator());
			}
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
			logError("Error", e);
		}

		@Override
		public void fatalError(SAXParseException e) throws SAXException {
			logError("Fatal error", e);
		}

		@Override
		public void release() {
			if (errorsInFile > 0) {
				callback.collectError(lSb.toString(), file, errorsInFile);
			}
			callback.testedFiles.incrementAndGet();
		}

		public boolean hasErrors() {
			return errorsInFile > 0;
		}

	}

	public Set<File> getSkippedFiles() {
		return this.errorsPerFile.keySet();
	}

	@Override
	public void release() {
		errorCounter.set(0);
		testedFiles.set(0);
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
