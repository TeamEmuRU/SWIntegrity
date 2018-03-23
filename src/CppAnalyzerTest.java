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
		c.parse("C:\\Users\\Tyler\\Desktop\\Test\\SWIntegrity\\Test Files\\hello.cpp");
		
	}

}
