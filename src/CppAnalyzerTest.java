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
		c.analyze("C:\\Users\\tyler\\eclipse-workspace\\SWIntegrity\\TestFiles\\hello.cpp");
		
	}

}
