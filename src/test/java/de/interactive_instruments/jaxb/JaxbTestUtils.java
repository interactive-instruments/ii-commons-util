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

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

/**
 * @author J. Herrmann ( herrmann <aT) interactive-instruments (doT> de )
 */
public class JaxbTestUtils {

	private JaxbTestUtils() {

	}

	static String marshal(Object o, Class... classesToBeBound) throws JAXBException {
		final Marshaller m = JAXBContext.newInstance(classesToBeBound).createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		final StringWriter sw = new StringWriter();
		m.marshal(o, sw);
		return sw.toString();
	}

	static Object unmarshal(String str, Class... classesToBeBound) throws JAXBException {
		final Unmarshaller um = JAXBContext.newInstance(classesToBeBound).createUnmarshaller();
		return um.unmarshal(new StringReader(str));
	}
}
