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

/**
 * Functions for compressing/decompressing an array of indices to a long data
 * type.
 *
 * The algorithm is based on the assumption that a passed array of indices
 * contains multiple '1' values. For each '1' value one bit is set in a long
 * data type. A value greater than '1' must be initiated by a leading zero.
 * Each number can have a max size of MAX_NUMBER_SIZE.
 *
 * Null indices are not supported.
 *
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
public class IndexCompression {

	// Max number size
	private final static int MAX_NUMBER_SIZE = 2048;
	// In bit
	private final static int MAX_NUMBER_BIT_COUNT = Integer.SIZE - Integer.numberOfLeadingZeros(MAX_NUMBER_SIZE);

	/**
	 * Compress multiple indices to a long data type
	 *
	 * @param indices array with multiple indices
	 * @return long data type
	 */
	public static long compress(final int[] indices) {
		long compressed = 0L;
		// must be long otherwise shifts only 32 bits are shifted without casting
		long pos = 0;
		// Null indices can be ignored, if they are at the end of the array
		boolean nullIndex = false;
		for (final int index : indices) {
			if (index == 0) {
				nullIndex = true;
			} else {
				if (nullIndex) {
					throw new IllegalArgumentException("Null indices are not supported");
				}
				if (index == 1) {
					compressed |= (1L << pos);
				} else {
					if (index >= MAX_NUMBER_SIZE) {
						throw new IllegalStateException("Compression failed (max number size " +
								MAX_NUMBER_SIZE + " exceeded): " + index);
					}
					// bit string starts with a zero
					compressed |= ((long) index << ++pos);
					// append separator
					pos = pos + MAX_NUMBER_BIT_COUNT;
				}
				++pos;
			}
		}
		if (pos >= Long.SIZE) {
			final StringBuilder indicesStr = new StringBuilder();
			for (final int index : indices) {
				indicesStr.append(index);
				indicesStr.append(" ");
			}
			throw new IllegalStateException("Compression failed (data type size exceeded): " + indicesStr.toString());
		}
		return compressed;
	}

	/**
	 * Uncompress a long data type
	 *
	 * @param compressedIndices
	 * @return
	 */
	public static int[] uncompress(final long compressedIndices) {
		final int[] array = new int[Long.SIZE - Integer.numberOfLeadingZeros((int) compressedIndices)];
		int arrayPos = 0;
		int number = 0;
		// Values greater 0 indicate that the Zero Number Start Bit was found and
		// reflects the numbers bit position with an index
		int numberPos = 0;
		for (int i = 0; i < Long.SIZE; i++) {
			final int bit = (int) ((compressedIndices >> i) & 1);
			if (bit == 0) {
				if (numberPos > MAX_NUMBER_BIT_COUNT) {
					// write current number
					array[arrayPos] = number;
					++arrayPos;
					number = 0;
					numberPos = 0;
				} else {
					// switch into number mode or count bit position
					++numberPos;
				}
			} else if (numberPos > 0) {
				// in number mode
				number |= 1 << numberPos++ - 1;
			} else {
				// not in number mode, just write current 1
				array[arrayPos] = 1;
				++arrayPos;
			}
		}
		return array;
	}

}
