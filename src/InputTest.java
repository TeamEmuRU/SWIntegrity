import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class InputTest {

	@Test
	void specifyFileTypeFromCurrentDirectory() 
	{
		Input i = new Input();
		String[] s = ("Test Files\\hello.txt -j").split(" ");
		i.processInput(s);
	}
	
	@Test
	void specifyFileTypeFromRelativePath() 
	{
		Input i = new Input();
		String[] s = (".\\Test Files\\hello.txt -j").split(" ");
		i.processInput(s);
	}
	
	@Test
	void analyzeDirectoryForSingleFileType()
	{
		Input i = new Input();
		String[] s = ("Test Files -a").split(" ");	
		i.processInput(s);
	}
	
	@Test
	void analyzeDirectoryForMultipleFileTypes()
	{
		//Only analyzes Ada and Java files in the folder
		//Skips C++ files
		Input i = new Input();
		String[] s = ("Test Files -a Test Files -j").split(" ");	
		i.processInput(s); 
	}
	
	@Test
	void analyzeGrandchildDirectory()
	{
		//User can traverse directories below the current dir's children
		//Directories can also contain spaces
		Input i = new Input();
		String[] s = ("Test Files\\New Folder").split(" ");	
		i.processInput(s); 
	}
	
	@Test
	void analyzeMultipleDirectories()
	{
		Input i = new Input();
		String[] s = ("Test Files -all Test Files\\New Folder -all").split(" ");	
		i.processInput(s); 
	}
	
}
