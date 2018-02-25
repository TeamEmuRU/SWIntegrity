import java.io.File;
import java.util.*;
/**
 * The driver of the SIT project, it takes in the arguments and passes them accordingly as well as 
 * acts as the 'VIEW' for our project
 * @author Tyler
 *
 */

public class SWint {
	public SWint() {
		
		
	}
	/**
	 * 
	 * @param args takes no or one arguments, -j,-a,-c to designate each language type java, ada and c++ respectively 
	 * 	no arguments default all files being checked */
	

	public static void main(String[] args) {
		Reader r=new Reader();
		//gather information on folder
		File folder = new File(System.getProperty("user.dir"));
		//gather individual
		File[] listOfFiles = folder.listFiles();
		//add file names to list 
		List<String> fileNames=new LinkedList<>();
		for(File f:listOfFiles) {
			fileNames.add(f.getAbsolutePath());
		}
		//sort that list by extension
		r.sortByType(fileNames);
		if(args.length>0) {
			//open current directory and view files
			//depending on the specification open the appropriate group
		if(args[0].equals("-j")) {
			//TODO get all java files
			r.openJava();
		}
		else if(args[0].equals("-a")) {
			//TODO get all ada Files
			r.openAda();
		}
		else if(args[0].equals("-c")) {
			//TODO get all c++ files
			r.openCpp();
		}
		else if(args[0].equals("-all")) {
			r.openOther();
			r.openCpp();
			r.openAda();
			r.openJava();


		}
		//then is should be a file name 
		else {
			
			r.openSingleFile(args[0]);
		}
		}
		else {
			//default is to open all files
			r.openOther();
			r.openCpp();
			r.openAda();
			r.openJava();
			//System.out.println("Invalid Input.");
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
			//barret them if they dont get it right
			if(invalid) {
				System.out.println("invalid response.");
			}
		}
		return response;
	}

}
