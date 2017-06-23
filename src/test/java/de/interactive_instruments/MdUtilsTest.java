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

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Test;

import de.interactive_instruments.exceptions.MimeTypeUtilsException;

/**
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
public class MdUtilsTest {

	@Test
	public void testFnvChecksum1() throws IOException, MimeTypeUtilsException, URISyntaxException {
		final MdUtils.FnvChecksum checksum = new MdUtils.FnvChecksum();
		checksum.update("foo".getBytes());
		assertEquals("DCB27518FED9D577", checksum.toString());
		checksum.update("bar".getBytes());
		assertEquals("85944171F73967E8", checksum.toString());
	}

	@Test
	public void testFnvChecksum2() throws IOException, MimeTypeUtilsException, URISyntaxException {
		final MdUtils.FnvChecksum checksum = new MdUtils.FnvChecksum();
		checksum.update("bar".getBytes());
		assertEquals("003934191339461A", checksum.toString());
	}

	@Test
	public void testFnvChecksumAsHexStr() throws IOException, MimeTypeUtilsException, URISyntaxException {
		assertEquals("003934191339461A", MdUtils.checksumAsHexStr("bar".getBytes()));
	}

}
