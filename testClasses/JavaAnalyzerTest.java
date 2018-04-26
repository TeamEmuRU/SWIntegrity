import org.junit.jupiter.api.Test;

//import org.junit.Before;
//import org.junit.Test;

/**
 * The test class for JavaAnalyzer
 * @author Joseph Antaki
 */

public class JavaAnalyzerTest {
    private JavaAnalyzer ja;


/**
 * Default constructor for JavaAnalyzerTest
 */
    public JavaAnalyzerTest(){}

    /**
     * Sets up the test fixture.
     * Called before every test case method.
     */
    //@BeforeEach
    public void setUp() {
        ja = new JavaAnalyzer();
    }

//    /**
//     * Tests that variables and literals are extracted correctly.
//     */
//    //@Test
//    public void testVariablesAndLiterals() {
//        ja.analyze("C:\\Users\\Joe\\Documents\\SWIntegrity\\src\\SIT.java");
//    }
//    
    /**
     * Tests the sql detection method
     */
    @Test
	void test() {
    	ja = new JavaAnalyzer();
		System.out.println(ja.flattenCode(ja.openFile("src/JavaAnalyzer.java")));
		
	}
}