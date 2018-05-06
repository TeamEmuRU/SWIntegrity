import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class JavaAnalyzerTest {

    @Test
    public void test(){
        JavaAnalyzer jana = new JavaAnalyzer();
        jana.parse("src/SIT.java");
        ArrayList<JavaAnalyzer.Variable> vars = new ArrayList<>(jana.getVariables());
        assertEquals(5, vars.size());
        assertTrue(vars.get(4).getName().equals("input"));
        assertTrue(vars.get(4).getType().equals("Input"));
        assertTrue(vars.get(4).getScope().equals("2"));

        assertTrue(vars.get(2).getName().equals("response"));
        assertTrue(vars.get(2).getType().equals("String"));
        assertTrue(vars.get(2).getScope().equals("2"));

        for(JavaAnalyzer.Variable v : vars){
            assertFalse(v.getName().equals("String"));
        }

        assertEquals(21, jana.getLiteralsList().size());
    }
}