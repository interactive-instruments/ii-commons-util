package de.interactive_instruments.jaxb;


import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import de.interactive_instruments.Version;
import de.interactive_instruments.jaxb.adapters.MapToListAdapter;
import de.interactive_instruments.properties.*;
import de.interactive_instruments.properties.Properties;
import org.junit.Before;
import org.junit.Test;

import de.interactive_instruments.IFile;

import static org.junit.Assert.*;

public class MapToListAdapterTest {


	@XmlRootElement
	static class TheClass2 {
		public String str;
		
		TheClass2() { }
		
		TheClass2(String str) {
			this.str=str;
		}
	}
	
	
	@XmlRootElement
	static class TheClass {
        @XmlElement
        Version version = new Version();

        @XmlElement
        Properties properties;

		@XmlJavaTypeAdapter(MapToListAdapter.class)
		Map<String, String> assertions = new LinkedHashMap<String, String>();
			
		@XmlJavaTypeAdapter(MapToListAdapter.class)
		Map<String, List<TheClass2>> requirements = new LinkedHashMap<>();

		public TheClass() {
            properties = new Properties().setProperty("KEY", "VALUE");

			assertions.put("ID.1", "Assertion.1");
            assertions.put("ID.2", "Assertion.2");
			
			List<TheClass2> list = new ArrayList<TheClass2>();
			list.add(new TheClass2("Requirement.1"));
			list.add(new TheClass2("Requirement.2"));
			requirements.put("ID.2", list);
			
			List<TheClass2> list2 = new ArrayList<TheClass2>();
			list2.add(new TheClass2("Requirement.3"));
			list2.add(new TheClass2("Requirement.4"));
			requirements.put("ID.3", list2);
		}
	}

    static String testStr = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<theClass xmlns:ii=\"http://www.interactive-instruments.de/ii/1.0\">\n" +
            "    <version>0.1.0</version>\n" +
            "    <properties>\n" +
            "        <ii:Items>\n" +
            "            <ii:Item name=\"KEY\">\n" +
            "                <ii:value xsi:type=\"xs:string\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">VALUE</ii:value>\n" +
            "            </ii:Item>\n" +
            "        </ii:Items>\n" +
            "    </properties>\n" +
            "    <assertions>\n" +
            "        <ii:Item name=\"ID.1\">\n" +
            "            <ii:value xsi:type=\"xs:string\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">Assertion.1</ii:value>\n" +
            "        </ii:Item>\n" +
            "        <ii:Item name=\"ID.2\">\n" +
            "            <ii:value xsi:type=\"xs:string\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">Assertion.2</ii:value>\n" +
            "        </ii:Item>\n" +
            "    </assertions>\n" +
            "    <requirements>\n" +
            "        <ii:Collection name=\"ID.2\">\n" +
            "            <ii:Item>\n" +
            "                <ii:value xsi:type=\"theClass2\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
            "                    <str>Requirement.1</str>\n" +
            "                </ii:value>\n" +
            "                <ii:value xsi:type=\"theClass2\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
            "                    <str>Requirement.2</str>\n" +
            "                </ii:value>\n" +
            "            </ii:Item>\n" +
            "        </ii:Collection>\n" +
            "        <ii:Collection name=\"ID.3\">\n" +
            "            <ii:Item>\n" +
            "                <ii:value xsi:type=\"theClass2\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
            "                    <str>Requirement.3</str>\n" +
            "                </ii:value>\n" +
            "                <ii:value xsi:type=\"theClass2\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
            "                    <str>Requirement.4</str>\n" +
            "                </ii:value>\n" +
            "            </ii:Item>\n" +
            "        </ii:Collection>\n" +
            "    </requirements>\n" +
            "</theClass>\n";
	
	@Before
	public void setUp() throws Exception {

	}

	@Test
	public void testMarshal() throws JAXBException, IOException {
		TheClass theClass = new TheClass();
		String result = JaxbTestUtils.marshal(theClass, TheClass.class, ArrayList.class, TheClass2.class);
		System.out.println(result);

        assertEquals(testStr, result);
    }

    @Test
    public void testUnmarshal() throws JAXBException, IOException {

        TheClass c = (TheClass) JaxbTestUtils.unmarshal(testStr, TheClass.class, ArrayList.class, TheClass2.class);

        assertEquals("0.1.0", c.version.toString());

        assertEquals("Assertion.1" ,c.assertions.get("ID.1"));
        assertEquals("Assertion.2" ,c.assertions.get("ID.2"));

        assertEquals("Requirement.1" ,c.requirements.get("ID.2").get(0).str );
        assertEquals("Requirement.2" ,c.requirements.get("ID.2").get(1).str );

        assertEquals("Requirement.3" ,c.requirements.get("ID.3").get(0).str );
        assertEquals("Requirement.4" ,c.requirements.get("ID.3").get(1).str );
    }

}

