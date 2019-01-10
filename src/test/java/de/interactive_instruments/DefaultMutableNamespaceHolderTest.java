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
package de.interactive_instruments;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.Map;

import javax.xml.namespace.QName;

import org.junit.Before;
import org.junit.Test;

import de.interactive_instruments.xml.DefaultMutableNamespaceHolder;

/**
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
public class DefaultMutableNamespaceHolderTest {

	private DefaultMutableNamespaceHolder holder = new DefaultMutableNamespaceHolder();

	@Before
	public void setUp() {
		holder = new DefaultMutableNamespaceHolder();
		holder.addNamespaceUriAndPrefix("http://namespace1", "ns1");
		holder.addNamespaceUriAndPrefix("http://namespace2", "ns2");
		holder.addNamespaceUriAndPrefix("http://namespace3", "ns3");
		holder.addNamespaceUriAndPrefix("http://namespace3", "ns3.3");
	}

	@Test
	public void testGetAsQName() {
		{
			final QName qname = holder.getAsQName("{http://namespace1}localPart");
			assertNotNull(qname);
			assertEquals("localPart", qname.getLocalPart());
			assertEquals("http://namespace1", qname.getNamespaceURI());
			assertEquals("ns1", qname.getPrefix());
		}

		{
			final QName qname = holder.getAsQName("{http://namespace1}@localPart");
			assertNotNull(qname);
			assertEquals("@localPart", qname.getLocalPart());
			assertEquals("http://namespace1", qname.getNamespaceURI());
			assertEquals("ns1", qname.getPrefix());
		}

		{
			final QName qname = holder.getAsQName("{http://unknownNamespace}localPart");
			assertNotNull(qname);
			assertEquals("localPart", qname.getLocalPart());
			assertEquals("http://unknownNamespace", qname.getNamespaceURI());
			assertEquals("", qname.getPrefix());
		}

		{
			final QName qname = holder.getAsQName("localPart");
			assertNotNull(qname);
			assertEquals("localPart", qname.getLocalPart());
			assertEquals("", qname.getNamespaceURI());
			assertEquals("", qname.getPrefix());
		}

		{
			final QName qname = holder.getAsQName("@localPart");
			assertNotNull(qname);
			assertEquals("@localPart", qname.getLocalPart());
			assertEquals("", qname.getNamespaceURI());
			assertEquals("", qname.getPrefix());
		}

		{
			final QName qname = holder.getAsQName("@prefix:localPart");
			assertNotNull(qname);
			assertEquals("@localPart", qname.getLocalPart());
			assertEquals("", qname.getNamespaceURI());
			assertEquals("prefix", qname.getPrefix());
		}

		{
			final QName qname = holder.getAsQName("ns3:localPart");
			assertNotNull(qname);
			assertEquals("localPart", qname.getLocalPart());
			assertEquals("http://namespace3", qname.getNamespaceURI());
			assertEquals("ns3", qname.getPrefix());
		}
	}

	@Test
	public void testRedifinition() {
		holder.addNamespaceUriAndPrefix("http://namespace3", "ns3.3");
		holder.addNamespaceUriAndPrefix("http://namespace3", "ns3.4");

		{
			boolean exceptionThrown = false;
			try {
				holder.addNamespaceUriAndPrefix("http://namespace2", "ns3.4");
			} catch (IllegalArgumentException e) {
				exceptionThrown = true;
			}
			assertTrue(exceptionThrown);
		}

		holder.addNamespaceUriAndPrefix("http://namespace3", "ns3.4");
	}

	@Test
	public void testImmutability() {
		final Map<String, Iterable<String>> map = holder.getNamespacesAsMap();
		final Iterable<String> prefixes = map.get("http://namespace1");
		assertNotNull(prefixes);
		assertEquals("ns1", prefixes.iterator().next());

		boolean exceptionThrown = false;
		try {
			map.put("ns999", Collections.singleton("http://test"));
		} catch (UnsupportedOperationException e) {
			exceptionThrown = true;
		}
		assertTrue(exceptionThrown);
	}

}
