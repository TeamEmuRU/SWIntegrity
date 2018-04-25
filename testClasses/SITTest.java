import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;


class SITTest {

	@Test
	void test() {
		Input input=new Input();
		String args[]= {"-j","C:\\Users\\Tyler\\Desktop\\Test\\SWIntegrity\\hello.java"};
		input.processInput(args);
		
	}

}
