package de.interactive_instruments;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class IFileTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() throws IOException {
		IFile reportDir = new IFile("/bla");

		IFile malFile1 = reportDir.secureExpandPathDown("..\\bli");
		IFile malFile2 = reportDir.secureExpandPathDown("..//bli");
		IFile malFile3 = reportDir.secureExpandPathDown("..\\\\bli");
		IFile malFile4 = reportDir.secureExpandPathDown("..////bli");
		
		assertFalse(malFile1.toPath().toAbsolutePath().toString().contains(".."));
		assertFalse(malFile2.toPath().toAbsolutePath().toString().contains(".."));
		assertFalse(malFile3.toPath().toAbsolutePath().toString().contains(".."));
		assertFalse(malFile4.toPath().toAbsolutePath().toString().contains(".."));
		
	}

}
