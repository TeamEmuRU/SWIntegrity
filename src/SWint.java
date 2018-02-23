import java.io.File;
import java.util.*;


public class SWint {
	static Input input;
	public SWint() {
		
		
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Reader r=new Reader();
		if(args.length>0) {
			//open current directory and view files
			if(args[0].charAt(0)=='-') {
				File folder = new File(System.getProperty("user.dir"));
				File[] listOfFiles = folder.listFiles();
				//add file names to list 
				List<String> fileNames=new LinkedList<>();
				for(File f:listOfFiles) {
					fileNames.add(f.getAbsolutePath());
				}
				//sort that list by extension
				r.sortByType(fileNames);
			}
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
		//then is should be a file name 
		else {
			
			r.openSingleFile(args[0]);
		}
		}
		else {
			//my baby
			//input=new Input();
			//input.run();
			System.out.println("Invalid Input.");
		}
	}

}
