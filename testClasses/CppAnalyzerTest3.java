import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class CppAnalyzerTest3 {
	CppAnalyzer cpp=new CppAnalyzer();
	/*@Test
	void testParse() {
		
		cpp.parse("TestFiles/hello.java");
		assertTrue(cpp.getFileContents().equals("import java.util. * ; public class hello { public static void main ( String [ ] args ) { System.out.println ( \" Hello Word \" ) ; } }"));
		
	}

	@Test
	void testAnalyze() {
		cpp.analyze("TestFiles/hello.java");
		assertTrue(cpp.getFileContents().equals("import java.util. * ; public class hello { public static void main ( String [ ] args ) { System.out.println ( \" Hello Word \" ) ; } }"));
		assertTrue(cpp.getVariablesList().size()==4);
	}*/

	@Test
	void testCppAnalyzer() {
		fail("Not yet implemented");
	}

	@Test
	void testExtractLiterals() {
		fail("Not yet implemented");
	}

	@Test
	void testFlattenCode() {
		fail("Not yet implemented");
	}

	@Test
	void testExtractVariables() {
		fail("Not yet implemented");
	}

	@Test
	void testFileContains() {
		fail("Not yet implemented");
	}

}
