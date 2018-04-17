import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CppAnalyzerTest {
	
	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void test() {
		CppAnalyzer c=new CppAnalyzer();
		//c.analyze("TestFiles/hello.cpp");

	}

	@Test
	void TestSqlCppAnalyze() {
		CppAnalyzer c=new CppAnalyzer();
		c.analyze("TestFiles/danglingPointerTest.cpp");
	
		c.analyze("TestFiles/danglingPointerTest1.cpp");
	
		c.analyze("TestFiles/danglingPointerTest3.cpp");
		
		c.analyze("TestFiles/sqlExample2.cpp");
	
		c.analyze("TestFiles/sqlExample.cpp");
		
		
	}
	

}
