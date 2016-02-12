package de.interactive_instruments.jaxb;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.StringWriter;

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
