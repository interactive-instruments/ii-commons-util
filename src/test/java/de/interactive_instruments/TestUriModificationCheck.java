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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Test;

/**
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
public class TestUriModificationCheck {

	@Test
	public void testModificationCheckNonChangingHash() throws URISyntaxException, IOException {
		final URI url = new URI("http://www.interactive-instruments.de");
		final UriModificationCheck check = new UriModificationCheck(url, null);
		assertNull(check.getIfModified());
	}

	@Test
	public void testModificationCheckChangedHash() throws URISyntaxException, IOException {
		final URI url = new URI("https://time.is");
		final UriModificationCheck check = new UriModificationCheck(url, null);
		assertNotNull(check.getIfModified());
	}
}
