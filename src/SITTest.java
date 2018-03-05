import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;


class SITTest {

	@Test
	void test() {
		Input input=new Input();
		List<String> files=input.getAllFilesInDirectoryAndSubDirectories("C:\\Users\\tyler\\eclipse-workspace\\SWIntegrity\\Test Files");
		System.out.println(files.size());
		System.out.println(files);
	}

}
