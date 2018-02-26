import java.io.File;
import java.util.*;
/**
 * The driver of the SIT project, it takes in the arguments and passes them accordingly as well as 
 * acts as the 'VIEW' for our project
 * @author Tyler
 *
 */

public class SIT {
	public SIT() {
	}
	/**
	 * 
	 * @param args takes no or one arguments, -j,-a,-c to designate each language type java, ada and c++ respectively 
	 * 	no arguments default all files being checked */


	public static void main(String[] args) {
		//init reader
		Input input=new Input();
		if(args.length==1) {
			//open current directory and view files
			
			//depending on the specification open the appropriate group
			if(args[0].equals("-j")) {
				//TODO get all java files
				input.collectAllFilesInDIrectory();
				input.analyzeJava();
			}
			else if(args[0].equals("-a")) {
				//TODO get all Ada Files
				input.collectAllFilesInDIrectory();
				input.analyzeAda();
			}
			else if(args[0].equals("-c")) {
				//TODO get all c++ files
				input.collectAllFilesInDIrectory();
				input.analyzeCpp();
			}
			else if(args[0].equals("-all")) {
				input.collectAllFilesInDIrectory();
				input.analyzeCpp();
				input.analyzeAda();
				input.analyzeJava();


			}
			//then is should be a file name 
			else if(args[0].contains(".")) {
				input.analyzeSingleFile(args[0]);
			}
			else {
				notifyUser("Invalid Input");
			}
		}
		else if(args.length==0){
			//default is to open all files
			input.collectAllFilesInDIrectory();
			input.analyzeCpp();
			input.analyzeAda();
			input.analyzeJava();
			//System.out.println("Invalid Input.");
		}
		else {
			notifyUser("Invalid Input");
		}

	}

	//TODO create method that allows us to notify the user, link all print lines to this
	/**
	 * A static method called by other classes in order to update the user
	 * 
	 * @param message prints the parameter passed in 
	 */
	public static void notifyUser(String message) {
		System.out.println(message);
	}
	/**
	 * Takes in a message to prompt a user and a list of valid responses this method only exits when 
	 * the user provides it with a valid response
	 * @param message prompting message
	 * @param validResponses a list of acceptable responses that should be handled by the calling class upon return  
	 * @return the appropriate response provided by the user
	 * the calling class must specify what the options are
	 */
	public static String getResponse(String message,List<String> validResponses) {
		//start scanner
		Scanner scanner=new Scanner(System.in);
		//set a response
		String response="";

		boolean invalid=true;
		//while the user doesn't provide this method with a valid response it will prompt them for a correct response
		while(invalid) {
			System.out.println(message);
			//collect next line
			response=scanner.nextLine();
			//check for valid response
			invalid=!validResponses.contains(response);
			//yell at them if they don't get it right
			if(invalid) {
				System.out.println("invalid response.");
			}
		}
		return response;
	}

}
