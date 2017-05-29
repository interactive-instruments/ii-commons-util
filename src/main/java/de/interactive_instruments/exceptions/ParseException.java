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
package de.interactive_instruments.exceptions;

/**
 * II specific parse exception
 *
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
public class ParseException extends java.text.ParseException {

	private final String target;

	public ParseException(final String errMessage, final String target, final int errorOffset) {
		super(errMessage, errorOffset);
		this.target = target;
	}

	public ParseException(final String errMessage, final String target) {
		super(errMessage, 0);
		this.target = target;
	}

	public String getTarget() {
		return target;
	}
}
