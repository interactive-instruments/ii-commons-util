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
package de.interactive_instruments.jaxb;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.junit.Test;

import de.interactive_instruments.container.CLenFileFactory;
import de.interactive_instruments.container.LazyLoadContainer;
import de.interactive_instruments.container.StringDataContainer;
import de.interactive_instruments.container.UrlReferenceContainer;
import de.interactive_instruments.exceptions.ContainerFactoryException;
import de.interactive_instruments.exceptions.InitializationException;
import de.interactive_instruments.exceptions.InvalidStateTransitionException;
import de.interactive_instruments.exceptions.config.ConfigurationException;

/**
 * @author J. Herrmann ( herrmann <aT) interactive-instruments (doT> de )
 */
public class ContainerTest {

	String testStr = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
			"<theClass>\n" +
			"    <StrContainer xsi:type=\"stringDataContainer\" name=\"StrContainer\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">THE STRING</StrContainer>\n"
			+
			"    <UrlContainer xsi:type=\"urlReferenceContainer\" name=\"UrlContainer\" referenceURL=\"http://nowhere\" loadDataOnDemand=\"false\" size=\"-1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>\n"
			+
			"    <Containers>\n" +
			"        <containers xsi:type=\"stringDataContainer\" name=\"StrContainer\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">THE STRING</containers>\n"
			+
			"    </Containers>\n" +
			"</theClass>\n";

	@XmlRootElement
	static class TheClass {

		@XmlElement(name = "StrContainer")
		LazyLoadContainer strContainer;

		@XmlElement(name = "UrlContainer")
		LazyLoadContainer urlContainer;

		@XmlElementWrapper(name = "Containers")
		List<LazyLoadContainer> containers;

		TheClass() throws MalformedURLException, ContainerFactoryException, ConfigurationException,
				InvalidStateTransitionException, InitializationException {
			CLenFileFactory factory = new CLenFileFactory();
			factory.init();
			strContainer = factory.create("StrContainer", "THE STRING");
			urlContainer = new UrlReferenceContainer("UrlContainer", new URL("http://nowhere"), "text/html", false);
			containers = new ArrayList<LazyLoadContainer>() {
				{
					add(strContainer);
				}
			};
		}
	}

	@Test
	public void testMarshal() throws JAXBException, IOException, InvalidStateTransitionException, ContainerFactoryException,
			ConfigurationException, InitializationException {
		TheClass theClass = new TheClass();
		String result = JaxbTestUtils.marshal(theClass, TheClass.class, StringDataContainer.class, UrlReferenceContainer.class);
		System.out.println(result);

		assertEquals(testStr, result);
	}

	@Test
	public void testUnmarshal() throws JAXBException, IOException {

		TheClass c = (TheClass) JaxbTestUtils.unmarshal(testStr, TheClass.class, StringDataContainer.class,
				UrlReferenceContainer.class);

		assertEquals("StrContainer", c.strContainer.getName());
		assertEquals("THE STRING", c.strContainer.getAsString());
		assertEquals(false, c.strContainer.isReference());

		assertEquals("UrlContainer", c.urlContainer.getName());
		assertEquals(true, c.urlContainer.isReference());

		assertEquals("StrContainer", c.containers.get(0).getName());
	}
}
