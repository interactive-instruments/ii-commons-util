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
package de.interactive_instruments.io;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
public class RemoveBomReader {

	private static final char[] UTF32BE = {0x0000, 0xFEFF};
	private static final char[] UTF32LE = {0xFFFE, 0x0000};
	private static final char[] UTF16BE = {0xFEFF};
	private static final char[] UTF16LE = {0xFFFE};
	private static final char[] UTF8 = {0xEFBB, 0xBF};

	/**
	 * Removes the problematic UTF-8 byte order mark
	 *
	 * @param reader Reader
	 * @param bom    bom sequence
	 *
	 * @return true if bom has been removed
	 *
	 * @throws IOException reader error
	 */
	private static boolean removeBOM(Reader reader, char[] bom) throws IOException {
		int bomLength = bom.length;
		reader.mark(bomLength);
		char[] possibleBOM = new char[bomLength];
		reader.read(possibleBOM);
		for (int x = 0; x < bomLength; x++) {
			if ((int) bom[x] != (int) possibleBOM[x]) {
				reader.reset();
				return false;
			}
		}
		return true;
	}

	/**
	 * Removes the problematic UTF-8 byte order mark
	 *
	 * @param reader Reader
	 *
	 * @return true if bom has been removed
	 *
	 * @throws IOException reader error
	 */
	private static void removeBOM(Reader reader) throws IOException {
		if (removeBOM(reader, UTF32BE)) {
			return;
		}
		if (removeBOM(reader, UTF32LE)) {
			return;
		}
		if (removeBOM(reader, UTF16BE)) {
			return;
		}
		if (removeBOM(reader, UTF16LE)) {
			return;
		}
		if (removeBOM(reader, UTF8)) {
			return;
		}
	}

	/**
	 * New reader from a file without BOM
	 *
	 * @param inputStream inputStream
	 *
	 * @return cleaned reader
	 *
	 * @throws IOException Reader error
	 */
	public static BufferedReader getRemovedBomReader(final InputStream inputStream) throws IOException {
		final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
		removeBOM(bufferedReader);
		return bufferedReader;
	}
}
