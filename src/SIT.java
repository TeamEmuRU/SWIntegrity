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
	 * -r to search all subfolders in the currect directory, and -help or ? to display help information.
	 * If no arguments are entered, all files will be selected by default.
	 */
	public static void main(String[] args) {

		//init reader
		Input input = new Input();
		List<String> fileList = new LinkedList<String>(); //a list of files designated by the user


		if (args.length == 0) {
			//If no arguments specified, process all files with known extensions
			//in the current directory
			input.addFiles(input.getAllFilesInDirectory(System.getProperty("user.dir")));
			input.analyze();
		}
		//If arguments are specified
		else {
			//iterate through tag arguments and handle them accordingly
			boolean tagZone = true;
			int nonTagsStart = 0;
			for (int i = 0; i < args.length && tagZone; i++) {
				switch (args[i]) {
					//By default, searches the current directory
					case "-j":
						input.addJavaFilesInDirectory(System.getProperty("user.dir"));
						break;
					case "-a":
						input.addAdaFilesInDirectory(System.getProperty("user.dir"));
						break;
					case "-c":
						input.addCppFilesInDirectory(System.getProperty("user.dir"));
						break;
					case "-r":
						input.addFiles(input.getAllFilesInDirectoryAndSubDirectories(System.getProperty("user.dir")));
						break;
					case "-help":
					case "?":
						displayHelp();
						break;
					default:
						tagZone = false;
						nonTagsStart = i;
						break;
				}
			}
			//add explicit file or directory names to fileList, if the user supplied them
			if (!tagZone) {
				for (int i = nonTagsStart; i < args.length; i++) {
					File f = new File(args[i]);
					if (f.isFile()) {
						fileList.add(args[i]);
						input.addFiles(fileList);
					} else if (f.isDirectory()) {
						input.getAllFilesInDirectory(args[i]);
					} else {
						notifyUser("One or more arguments were invalid input");
					}
				}
			}
			input.analyze();
		}
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
	private static void displayHelp()
	{
		notifyUser("");
		notifyUser("This Software Integrity Tester (SIT) analyzes Java, Ada, and C++ source code");
		notifyUser("for security weaknesses and vulnerabilities.\n");
		notifyUser("Usage: SIT <options> <directory name or filenames>");
		notifyUser("where possible options include:");
		notifyUser("");
		notifyUser("(No options or directory/files specified)");
		notifyUser("	Analyze all supported source code files in the current directory");
		notifyUser("");
		notifyUser("-j  Analyze all Java source code files in the specified directory");
		notifyUser("-a  Analyze all Ada source code files in the specified directory");
		notifyUser("-c  Analyze all C++ source code and header files in the specified directory");
		notifyUser("-r  Analyze all specified source code files in the specified directory, and");
		notifyUser("    all of its subdirectories");
		notifyUser("");
	}
}
