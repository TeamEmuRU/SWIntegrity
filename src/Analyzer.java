/**
 * This abstract class handles file operations limited to
 * opening and parsing files, and analyzing file contents
 * for vulnerabilities.
 *
 * @author Jamie Tyler Walder
 */

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public abstract class Analyzer {
	
	private BufferedReader br;
	protected static String jarPath = "file://./vulnerabilities.jar"; 
	
	protected static String configPath=Analyzer.class.getResource("config.csv").getPath();
	
	/**
	 * Open a file with a given file name and return its contents
	 * @param fileName The file to open
	 * @return The contents of the file
	 */
	public String openFile(String fileName) {
		
		try {
			//use buffered reader to open the file
			br = new BufferedReader(new FileReader(fileName));
			//set an empty string to capture the contents of the file
			String file = "";
			//set a variable to capture the individual lines of the project
			String sCurrentLine;
			//while the file has lines take each line and add it to the return String
			while ((sCurrentLine = br.readLine()) != null) {
				file += sCurrentLine + "\n";
			}
			
			return file;
			
		}
		
		catch(FileNotFoundException e) {
			//Thrown by BufferedReader
			//if there was an error opening a file, notify the user
			SIT.notifyUser(fileName + " not Found.");
			return null;
		} catch (IOException e) {
			//Thrown by BufferedReader
			SIT.notifyUser("Error opening " + fileName);
			e.printStackTrace();
			return null;
		}
	}
	
	//These methods will be overwritten by classes which extend the Analyzer
	abstract public void parse(String filename);
	abstract protected void analyze(String filename);
}
