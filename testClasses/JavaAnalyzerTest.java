import org.junit.Test;

import static org.junit.Assert.*;

public class JavaAnalyzerTest {
    @Test
    public void test(){
        JavaAnalyzer jana = new JavaAnalyzer();
        jana.parse("TestFiles/sqlExample.java");
    }
}