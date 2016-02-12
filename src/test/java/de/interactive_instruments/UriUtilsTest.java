package de.interactive_instruments;

import static org.junit.Assert.*;

import java.io.File;
import java.net.URI;

import org.junit.Before;
import org.junit.Test;

public class UriUtilsTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testGetParent() {
				
		{
			File testFile = new File("/FOO/BAR/file.txt");
			URI testUri = testFile.toURI();
			assertTrue( (UriUtils.getParent(testUri).getPath()).contains("FOO") );
			assertTrue( (UriUtils.getParent(testUri).getPath()).contains("BAR") );
			assertTrue( !(UriUtils.getParent(testUri).getPath()).contains("file.txt") );
		}
		
		{
			// no "/" at the end
			File testFile2 = new File("/FOO/BAR");
			URI testUri2 = testFile2.toURI();
			assertTrue( (UriUtils.getParent(testUri2).getPath()).contains("FOO") );
			assertTrue( !(UriUtils.getParent(testUri2).getPath()).contains("BAR") );
		}

		{
			// "/" at the end
			File testFile3 = new File("/FOO/BAR/");
			URI testUri3 = testFile3.toURI();
			assertTrue( (UriUtils.getParent(testUri3).getPath()).contains("FOO") );
			assertTrue( !(UriUtils.getParent(testUri3).getPath()).contains("BAR") );
		}
	}
	
	@Test
	public void testGetParentLevel() {
				
		{
			File testFile = new File("/FOO/BAR/file.txt");
			URI testUri = testFile.toURI();
			assertTrue( (UriUtils.getParent(testUri,2).getPath()).contains("FOO") );
			assertTrue( !(UriUtils.getParent(testUri,2).getPath()).contains("BAR") );
			assertTrue( !(UriUtils.getParent(testUri,2).getPath()).contains("file.txt") );
		}
		
		{
			File testFile2 = new File("/FOO/BAR/BAR2/BAR3");
			URI testUri2 = testFile2.toURI();
			assertTrue( (UriUtils.getParent(testUri2,3).getPath()).contains("FOO") );
			assertTrue( !(UriUtils.getParent(testUri2,3).getPath()).contains("BAR") );
		}
	}

}
