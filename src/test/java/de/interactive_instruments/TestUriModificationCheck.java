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
