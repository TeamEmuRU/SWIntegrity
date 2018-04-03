import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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
    @Before
    public void setUp() {
        ja = new JavaAnalyzer();
    }

    /**
     * Tears down the test fixture.
     * Called after every test case method.
     */
    @After
    public void tearDown(){}

    /**
     * Tests that variables are extracted correctly, and that
     */
    @Test
    public void testExtractVariables() {
        ja.analyze("/root/SWIntegrity/src/SIT.java");
    }
}