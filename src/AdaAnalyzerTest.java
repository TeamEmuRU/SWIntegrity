import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AdaAnalyzerTest {
	AdaAnalyzer ana;
	String file;
	@BeforeEach
	void setUp() throws Exception {
		ana=new AdaAnalyzer();
		file=ana.openFile("Test Files\\hello.adb");
		file=ana.flattenCodeAndMap(file);
		ana.extractVariables(file);
		testExtractVariables();
	}

	@Test
	void testParse() {
		ana.parse("Test Files\\hello.adb");
		assertTrue(ana.getVariables().size()==1);
		assertTrue(ana.getExternalVariables().size()==0);
		assertTrue(ana.getLiterals().size()==1);
		assertTrue(ana.getExternalFunctionCalls().size()==0);
	}

	@Test
	void testAnalyze() {
		fail("Not yet implemented");
	}

	@Test
	void testIsVarName() {
		assertTrue(ana.isVarName("Ada"));
		assertTrue(ana.isVarName("ADA"));
		assertTrue(ana.isVarName("Ada_Compiler"));
		assertTrue(ana.isVarName("The_Year_1776"));
		assertTrue(ana.isVarName("a1b2c3d4e5f6"));
		assertFalse(ana.isVarName("12_times_each"));
		assertFalse(ana.isVarName("This__is__neat"));
		assertFalse(ana.isVarName("This is neat"));
		assertFalse(ana.isVarName("Ada_\"tutorial\""));
		
		
	}

	@Test
	void testExtractVariables() {
		assertTrue(ana.getVariables().size()==1);
		assertTrue(ana.getExternalVariables().size()==0);
		assertTrue(ana.getLiterals().size()==1);
		assertTrue(ana.getExternalFunctionCalls().size()==0);
		
		
		
	}

	@Test
	void testFlattenCodeAndMap() {
		
		assertTrue(file.equals("with Ada . Text_IO ; use Ada . Text_IO ; procedure Hello is begin Put_Line ( \" Hello WORLD! \" ) ; i : Integer ; i : = 0 ; end Hello ;"));
	}

}
