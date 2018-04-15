/**
 * The driver of the SIT project. Is responsible for acting as the user interface and handling output.
 * 
 * @version 0.1
 * 
 * @author Joseph Antaki
 * @author Abby Beizer
 * @author Jamie Tyler Walder
 */

import java.io.File;
import java.util.*;




public class SIT {
	

	
	/**
	 * @param args Takes a series of tags, followed by a list of file or directory names.
	 * Possible tags include -j,-a,-c designating the languages Java, Ada and C++ respectively,
	 * -r to search all subfolders in the current directory, and -help or ? to display help information.
	 * If no arguments are entered, all files will be selected by default.
	 */
	public static void main(String[] args) {
		Input input = new Input();
		input.processInput(args);
	}


	/**
	 * A static method called by other classes to present output to the user
	 * 
	 * @param message A String to print to the user interface
	 */
	public static void notifyUser(String message) {
		System.out.println(message);
	}
	
	
	/**
	 * Collects a response from the user to a given prompt.
	 * The calling class must specify what valid responses are
	 * @param message The prompting message
	 * @param validResponses A list of acceptable responses that should be handled by the calling class upon return  
	 * @return The appropriate response provided by the user
	 */
	public static String getResponse(String message,List<String> validResponses) {
		//start scanner
		Scanner scanner = new Scanner(System.in);
		//set a response
		String response = "";

		boolean invalid = true;
		//while the user doesn't provide this method with a valid response it will prompt them for a correct response
		while(invalid) {
			System.out.println(message);
			//collect next line
			response = scanner.nextLine();
			//check for valid response
			invalid =! validResponses.contains(response);
			//yell at them if they don't get it right
			if(invalid) {
				System.out.println("invalid response.");
			}
		}
		scanner.close();
		return response;
	}
	
	/**
	 * Displays valid commands for using this application to the user
	 */
	public static void displayHelp()
	{
		notifyUser("");
		notifyUser("This Software Integrity Tester (SIT) analyzes Java, Ada, and C++ source code");
		notifyUser("for security weaknesses and vulnerabilities.\n");
		notifyUser("Usage: SIT <language tags> <directories and filenames>\n");
		notifyUser("Language Tags:");
		notifyUser("\tLanguage tags specify the file types accepted by the current run of the SIT.");
		notifyUser("\tDesignating files which do not conform to the specified tags will generate an error.");
		notifyUser("\tAt least one language tag from the following is required and must precede any file paths:");
		notifyUser("\t-j\tJava");
		notifyUser("\t-a\tAda");
		notifyUser("\t-c\tC++");
		notifyUser("\tMultiple language tags may be specified, separated by spaces.");
		notifyUser("\t(ex. \"SIT -j -a <files>\" will allow both Java and Ada files to be analyzed)");
		notifyUser("");
		notifyUser("Directories and Files:");
		notifyUser("\tIf no directory or file paths are specified, the files in the current directory will be analyzed.");
		notifyUser("\tDesignating a directory analyzes all files in that directory which conform to the specified language tags.");
		notifyUser("\tAny number of directory and file paths may be specified.");
		notifyUser("");
		notifyUser("Additional Commands:");
		notifyUser("\t-r\tWhen this command follows the path of a directory, the SIT will analyze all files in that directory");
		notifyUser("\t\tand all of its subdirectories which conform to the specified language tags.");
		notifyUser("");
	}
}
