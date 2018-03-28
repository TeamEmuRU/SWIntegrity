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
		
		//init reader
		Input input = new Input();
		//TODO: remove usage of fileList
		List<String> fileList = new LinkedList<String>(); //a list of files designated by the user


		if (args.length == 0) {
			//If no arguments specified, process all files with known extensions
			//in the current directory
			input.addFiles(input.getAllFilesInDirectory(System.getProperty("user.dir")));
			input.analyze();
		}
		//If arguments are specified
		else 
		{
			//iterate through tag arguments and handle them accordingly
			boolean tagZone = true;
			int nonTagsStart = 0;
			for (int i = 0; i < args.length && tagZone; i++) 
			{
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
			if (!tagZone) 
			{
				boolean nameFragment = false;	//Flag for whether the current argument is part of a file or 
												//dir name (one that contains whitespace)
				File f = new File("");
				//This starts after the tags are processed
				for (int i = nonTagsStart; i < args.length; i++) 
				{
					//If the current argument is part of a file or dir name,
					//add the word to the rest of the file name, and skip the rest of the for loop
					//Else if the flag is set to false, then assign the current argument as a filename
					//and proceed as normal
					if(nameFragment)		
					{
						File temp = new File(args[i]);
						if(!temp.isFile() && !temp.isDirectory())
						{
							//nameFragment remains true
							f = new File(f.toString() + " " + args[i]);
							notifyUser(f.toString());
						}
						//If f is a file or directory, then add it to the input and set the nameFragment flag to false
						if(f.isFile()) 
						{
							//TODO: Remove usage of fileList
							fileList.add(f.getAbsolutePath());
							input.addFiles(fileList);
							notifyUser(f.toString() + " is a file ");
							nameFragment = false;
							continue;
						} 
						else if(f.isDirectory())
						{
							input.getAllFilesInDirectory(f.getAbsolutePath());
							notifyUser("add all files in dir " + f.toString());
							nameFragment = false;
							continue;
						}
					}

					f = new File(args[i]);
						
					//If f is a file or directory, then add it to the input and set the nameFragment flag to false
					if (f.isFile()) 
					{
						fileList.add(args[i]);
						input.addFiles(fileList);
						notifyUser(f.toString() + " is a file ");
						nameFragment = false;
					} 
					else if (f.isDirectory())
					{
						input.getAllFilesInDirectory(args[i]);
						notifyUser("add all files in dir " + f.toString() + " ");
						nameFragment = false;
					} 
					else 
					{
						nameFragment = true;
					}//end if
				}//end for
			}
		}
			input.analyze();
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
