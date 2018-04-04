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
		c.analyze("../TestFiles/hello.cpp");

	}

	@Test
	void TestSqlCppAnalyze() {
		CppAnalyzer c=new CppAnalyzer();
		c.analyze("../TestFiles/sqlExample.cpp");
		c.sqlCppAnalyze("../TestFiles/sqlExample.cpp");
	}
	

}
