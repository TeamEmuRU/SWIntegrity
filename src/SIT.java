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
		
		
		if(args.length == 0){
			//If no arguments specified, print a help statement
			notifyUser("Enter \"help\" for valid commands.");
		}
		//If arguments are specified
		else {
			//depending on the first argument, process files of the specified type
			//java
			if(args[0].equals("-j")) {
				//All files of this type
				if(args.length == 1)
				{
					input.collectAllJavaFiles();
				}
				//Multiple files selected by name
				else
				{
					List<String> fileList = new LinkedList<String>();
					for(int i = 1; i < args.length; i++)
					{
						fileList.add(args[i]);
					}
					input.sortByType(fileList);
				}
				input.analyzeJava();
			}
			//ada
			else if(args[0].equals("-a")) {
				//All files of this type
				if(args.length == 1)
				{
					input.collectAllAdaFiles();
				}
				//Multiple files selected by name
				else
				{
					List<String> fileList = new LinkedList<String>();
					for(int i = 1; i < args.length; i++)
					{
						fileList.add(args[i]);
					}
					input.sortByType(fileList);
				}
				input.analyzeAda();
			}
			//c++
			else if(args[0].equals("-c")) {
				//All files of this type
				if(args.length == 1)
				{
					input.collectAllCppFiles();
				}
				//Multiple files selected by name
				else
				{
					List<String> fileList = new LinkedList<String>();
					for(int i = 1; i < args.length; i++)
					{
						fileList.add(args[i]);
					}
					input.sortByType(fileList);
				}
				input.analyzeCpp();
			}
			//all files
			else if(args[0].equals("-all")) {
				List<String> filenames = input.collectAllFilesInDirectory();
				input.analyzeAll(filenames);
			}
			//then it should be a file name 
			//TODO: Use File class to detect whether this is a directory
			else if(args[0].contains(".")) {
				input.analyzeSingleFile(args[0]);
			}
			else {
				notifyUser("Invalid Input");
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
		return response;
	}
}