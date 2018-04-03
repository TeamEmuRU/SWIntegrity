import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CppAnalyzerTest {
	
	@BeforeEach
	void setUp() throws Exception {
	}

//	@Test
//	void test() {
//		CppAnalyzer c=new CppAnalyzer();
//		c.analyze("/Users/KristelleLucero/Documents/Software Engineering 2018/SWIntegrity/Test Files/sqlExample.cpp");
//		c.analyze("/Users/KristelleLucero/Documents/Software Engineering 2018/SWIntegrity/Test Files/sqlExample2.cpp");
//	}

	@Test
	void TestSqlCppAnalyze() {
		CppAnalyzer c=new CppAnalyzer();
		c.analyze("/Users/KristelleLucero/Documents/Software Engineering 2018/SWIntegrity/Test Files/sqlExample.cpp");
		c.sqlCppAnalyze("/Users/KristelleLucero/Documents/Software Engineering 2018/SWIntegrity/Test Files/sqlExample.cpp");
		c.analyze("/Users/KristelleLucero/Documents/Software Engineering 2018/SWIntegrity/Test Files/sqlExample.cpp");
		c.sqlCppAnalyze("/Users/KristelleLucero/Documents/Software Engineering 2018/SWIntegrity/Test Files/sqlExample2.cpp");
	}
	

}
