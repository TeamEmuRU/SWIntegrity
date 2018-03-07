/**
 * The driver of the SIT project. Is responsible for acting as the user interface and handling output.
 * 
 * @author Jamie Tyler Walder
 * @author Abby Beizer
 */

import java.io.File;
import java.util.*;

public class SIT {
	
	/**
	 * @param args Takes one or fewer arguments. -j,-a,-c designate the languages Java, Ada and C++ respectively 
	 * If no arguments are entered, all files will be selected by default.
	 */
	public static void main(String[] args) {
		
		//init reader
		Input input = new Input();
		List<String> fileList = new LinkedList<String>(); //a list of files designated by the user
		
		
		if(args.length == 0)
		{
			//If no arguments specified, process all files with known extensions
			//in the current directory
			input.addFiles(input.getAllFilesInDirectory(System.getProperty("user.dir")));
			input.analyze();
		}
		//If arguments are specified
		else 
		{
			//depending on the first argument, process files of the specified type
			//java
			if(args[0].equals("-j")) 
			{
				//All files of this type
				if(args.length == 1)
				{
					input.addJavaFilesInDirectory(System.getProperty("user.dir"));	//By default, searches the current directory
				}
				//Multiple files selected by name
				else
				{
					for(int i = 1; i < args.length; i++)
					{
						fileList.add(args[i]);
					}
					input.addFiles(fileList);
				}
				input.analyze();
			}
			//ada
			else if(args[0].equals("-a")) {
				//All files of this type
				if(args.length == 1)
				{
					input.addAdaFilesInDirectory(System.getProperty("user.dir"));
				}
				//Multiple files selected by name
				else
				{
					for(int i = 1; i < args.length; i++)
					{
						fileList.add(args[i]);
					}
					input.addFiles(fileList);
				}
				input.analyze();
			}
			//c++
			else if(args[0].equals("-c")) {
				//All files of this type
				if(args.length == 1)
				{
					input.addCppFilesInDirectory(System.getProperty("user.dir"));
				}
				//Multiple files selected by name
				else
				{
					for(int i = 1; i < args.length; i++)
					{
						fileList.add(args[i]);
					}
					input.addFiles(fileList);
				}
				input.analyze();
			}
			//all files
			else if(args[0].equals("-r")) {
				if(args.length == 1)
				{
					input.addFiles(input.getAllFilesInDirectoryAndSubDirectories(System.getProperty("user.dir")));
				}
				else {
					
					for(int i = 1; i < args.length; i++)
					{
						fileList.addAll(input.getAllFilesInDirectoryAndSubDirectories(args[i]));
					}
					input.addFiles(fileList);
					}
				
				
				input.analyze();
			}
			//display help information
			else if(args[0].equals("-help") || args[0].equals("?")){
				notifyUser("This Software Integrity Tester (SIT) analyzes Java, Ada, and C++ source code");
				notifyUser("for security weaknesses and vulnerabilities.\n");
				notifyUser("Usage: SIT <options> <directory name or filenames>");
				notifyUser("where possible options include:");
				notifyUser("(No options or directory/files specified)");
				notifyUser("	Analyze all supported source code files in the current directory");
				notifyUser("-j");
				notifyUser("	Analyze all Java source code files in the specified directory");
				notifyUser("-a");
				notifyUser("	Analyze all Ada source code files in the specified directory");
				notifyUser("-c");
				notifyUser("	Analyze all C++ source code and header files in the specified directory");
				notifyUser("-r");
				notifyUser("	Analyze all specified source code files in the specified directory, and"); 
				notifyUser("	all of its subdirectories");
			}
			//then it should be a file name 
			//TODO: Use File class to detect whether this is a directory
			else
			{
				File f = new File(args[0]);
				if(f.isFile())
				{
					fileList.add(args[0]);
					input.addFiles(fileList);
					input.analyze();
				}
				else if(f.isDirectory())
				{
					input.getAllFilesInDirectory(args[0]);
					input.analyze();
				}
				else
				{
					notifyUser("One or more arguments were invalid input");
				}
				
			}
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
}
