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
import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.Map;

import javax.xml.namespace.QName;

import org.junit.Test;

import de.interactive_instruments.xml.NamespaceBuilder;
import de.interactive_instruments.xml.NamespaceHolder;

/**
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
public class NamespaceBuilderTest {

	@Test
	public void testGetAsQName() {

		final NamespaceBuilder builder = NamespaceBuilder.newInstance();

		builder.addNamespaceUriAndPrefix("http://namespace1", "ns1")
				.addNamespaceUriAndPrefix("http://namespace2", "ns2")
				.addNamespaceUriAndPrefix("http://namespace3", "ns3")
				.addNamespaceUriAndPrefix("http://namespace3", "ns3.3");

		{
			final QName qname = builder.getAsQName("{http://namespace1}localPart");
			assertNotNull(qname);
			assertEquals("localPart", qname.getLocalPart());
			assertEquals("http://namespace1", qname.getNamespaceURI());
			assertEquals("ns1", qname.getPrefix());
		}

		{
			final QName qname = builder.getAsQName("{http://namespace1}@localPart");
			assertNotNull(qname);
			assertEquals("@localPart", qname.getLocalPart());
			assertEquals("http://namespace1", qname.getNamespaceURI());
			assertEquals("ns1", qname.getPrefix());
		}

		{
			final QName qname = builder.getAsQName("{http://unknownNamespace}localPart");
			assertNotNull(qname);
			assertEquals("localPart", qname.getLocalPart());
			assertEquals("http://unknownNamespace", qname.getNamespaceURI());
			assertEquals("", qname.getPrefix());
		}

		{
			final QName qname = builder.getAsQName("localPart");
			assertNotNull(qname);
			assertEquals("localPart", qname.getLocalPart());
			assertEquals("", qname.getNamespaceURI());
			assertEquals("", qname.getPrefix());
		}

		{
			final QName qname = builder.getAsQName("@localPart");
			assertNotNull(qname);
			assertEquals("@localPart", qname.getLocalPart());
			assertEquals("", qname.getNamespaceURI());
			assertEquals("", qname.getPrefix());
		}

		{
			final QName qname = builder.getAsQName("@prefix:localPart");
			assertNotNull(qname);
			assertEquals("@localPart", qname.getLocalPart());
			assertEquals("", qname.getNamespaceURI());
			assertEquals("prefix", qname.getPrefix());
		}

		{
			final QName qname = builder.getAsQName("ns3:localPart");
			assertNotNull(qname);
			assertEquals("localPart", qname.getLocalPart());
			assertEquals("http://namespace3", qname.getNamespaceURI());
			assertEquals("ns3", qname.getPrefix());
		}

	}

	@Test
	public void testUnknownNamespace() {
		final NamespaceBuilder builder = NamespaceBuilder.newInstance();

		builder.addNamespaceUriAndPrefix("http://namespace1/gml", "gml")
				.addNamespaceUriAndPrefix("http://namespace2/wfs", "wfs");

		assertNull(builder.getParentNamespaceHolder());

		assertEquals("gml", builder.getPrefix("http://namespace1/gml"));
		assertEquals("http://namespace1/gml", builder.getNamespaceURI("gml"));
		assertEquals("wfs", builder.getPrefix("http://namespace2/wfs"));
		assertEquals("http://namespace2/wfs", builder.getNamespaceURI("wfs"));
		assertNull(builder.getPrefix("http://namespace3/bla"));
		assertNull(builder.getNamespaceURI("bla"));
		assertEquals(2, builder.prefixesSize());

		{
			final NamespaceHolder ns1 = builder.build();
			assertEquals("gml", ns1.getPrefix("http://namespace1/gml"));
			assertEquals("http://namespace1/gml", ns1.getNamespaceURI("gml"));
			assertEquals("wfs", ns1.getPrefix("http://namespace2/wfs"));
			assertEquals("http://namespace2/wfs", ns1.getNamespaceURI("wfs"));
			assertNull(ns1.getPrefix("http://namespace3/bla"));
			assertNull(ns1.getNamespaceURI("bla"));
			assertEquals(2, ns1.prefixesSize());

			builder.addNamespaceUri("http://namespace3/bla");

			assertEquals(2, builder.prefixesSize());
			assertEquals(2, ns1.prefixesSize());
		}

		assertEquals("bla", builder.getPrefixForNamespaceUriOrGenerate("http://namespace3/bla"));

		{
			final NamespaceHolder ns2 = builder.build();
			assertEquals("gml", ns2.getPrefix("http://namespace1/gml"));
			assertEquals("http://namespace1/gml", ns2.getNamespaceURI("gml"));
			assertEquals("wfs", ns2.getPrefix("http://namespace2/wfs"));
			assertEquals("http://namespace2/wfs", ns2.getNamespaceURI("wfs"));
			assertEquals("bla", ns2.getPrefix("http://namespace3/bla"));
			assertEquals("http://namespace3/bla", ns2.getNamespaceURI("bla"));
			assertEquals(3, ns2.prefixesSize());

			builder.addNamespaceUri("http://namespace3/bla");

			assertEquals(2, builder.prefixesSize());
			assertEquals(3, ns2.prefixesSize());
		}
	}

	@Test
	public void testRedifinition() {
		final NamespaceBuilder builder = NamespaceBuilder.newInstance();

		builder.addNamespaceUriAndPrefix("http://namespace3", "ns3.3");
		builder.addNamespaceUriAndPrefix("http://namespace3", "ns3.4");

		{
			boolean exceptionThrown = false;
			try {
				builder.addNamespaceUriAndPrefix("http://namespace2", "ns3.4");
			} catch (IllegalArgumentException e) {
				exceptionThrown = true;
			}
			assertTrue(exceptionThrown);
		}

		builder.addNamespaceUriAndPrefix("http://namespace3", "ns3.4");
	}

	@Test
	public void testChildBuilder() {
		final NamespaceBuilder rootBuilder = NamespaceBuilder.newInstance();

		rootBuilder.addNamespaceUriAndPrefix("http://namespace3", "ns3.3");
		assertNull(rootBuilder.addNamespaceUriAndPrefix("http://namespace3", "ns3.4").getParentNamespaceHolder());
		assertNull(rootBuilder.addNamespaceUriAndPrefix("http://namespace3", "ns3.4").getParentNamespaceHolder());

		final NamespaceBuilder childBuilder = rootBuilder.addNamespaceUriAndPrefixContextAware("http://namespace2", "ns3.4");
		assertNotNull(childBuilder);
		assertNotNull(childBuilder.getParentNamespaceHolder());

		assertEquals("http://namespace3", rootBuilder.getNamespaceURI("ns3.4"));
		assertEquals("http://namespace3", rootBuilder.getNamespaceURI("ns3.3"));
		assertEquals(2, rootBuilder.prefixesSize());

		assertEquals("http://namespace2", childBuilder.getNamespaceURI("ns3.4"));
		assertEquals("http://namespace3", childBuilder.getNamespaceURI("ns3.3"));
		assertEquals(2, childBuilder.prefixesSize());

		rootBuilder.addNamespaceUriAndPrefix("http://namespace4", "ns4");
		childBuilder.addNamespaceUriAndPrefix("http://namespace5", "ns5");

		assertEquals(3, rootBuilder.prefixesSize());
		assertEquals(4, childBuilder.prefixesSize());

		assertEquals("http://namespace4", rootBuilder.getNamespaceURI("ns4"));
		assertNull(rootBuilder.getNamespaceURI("ns5"));

		assertEquals("http://namespace5", childBuilder.getNamespaceURI("ns5"));
		assertEquals("http://namespace4", childBuilder.getNamespaceURI("ns4"));

		rootBuilder.addNamespaceUri("http://namespace/bla");
		childBuilder.addNamespaceUri("http://namespace/bli");

		{
			final NamespaceHolder ns1 = rootBuilder.build();

			assertNull(ns1.getNamespaceURI("ns5"));
			assertEquals("http://namespace4", ns1.getNamespaceURI("ns4"));
			assertEquals("http://namespace/bla", ns1.getNamespaceURI("bla"));
			assertNull(ns1.getNamespaceURI("bli"));

			assertEquals(3, rootBuilder.prefixesSize());
			assertEquals(4, ns1.prefixesSize());
		}

		{
			final NamespaceHolder ns2 = childBuilder.build();

			assertEquals("http://namespace5", ns2.getNamespaceURI("ns5"));
			assertEquals("http://namespace4", ns2.getNamespaceURI("ns4"));
			assertEquals("http://namespace/bla", ns2.getNamespaceURI("bla"));
			assertEquals("http://namespace/bli", ns2.getNamespaceURI("bli"));
			assertEquals(4, childBuilder.prefixesSize());
			assertEquals(6, ns2.prefixesSize());
		}
	}

	@Test
	public void testImmutability() {
		final NamespaceBuilder rootBuilder = NamespaceBuilder.newInstance();
		rootBuilder.addNamespaceUriAndPrefix("http://namespace3", "ns3.3");

		final Map<String, Iterable<String>> map = rootBuilder.getNamespacesAsMap();
		final Iterable<String> prefixes = map.get("http://namespace3");
		assertNotNull(prefixes);
		assertEquals("ns3.3", prefixes.iterator().next());

		boolean exceptionThrown = false;
		try {
			map.put("ns4", Collections.singleton("http://test"));
		} catch (UnsupportedOperationException e) {
			exceptionThrown = true;
		}
		assertTrue(exceptionThrown);
	}

}
