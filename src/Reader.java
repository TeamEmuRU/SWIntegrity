import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
/**
 * a class that is given file names and who task is to open them and then pass them to the appropriate parsers
 * @author Tyler
 *
 */

public class Reader {
	/**
	 * br-a reader for files
	 * adaFiles- a collection of selected Ada files
	 * javaFiles- a collection of selected java files
	 * cppfiles- a collection of selected java files
	 * otherFiles- a collection of other files that need to be sorted
	 * 
	 */
	private BufferedReader br;
	private List<String> adaFiles;
	private List<String> javaFiles;
	private List<String> cppFiles;
	private List<String> otherFiles;
	/**
	 * Initialize all the holding lists
	 */
	public Reader() {
		// TODO Auto-generated constructor stub
		adaFiles=new LinkedList<>();
		javaFiles=new LinkedList<>();
		cppFiles=new LinkedList<>();
		otherFiles=new LinkedList<>();
	}
	/**
	 * A method that sorts a list of ambiguous files and opens them appropriately
	 * @param filenames a list that of filenames that are sorted and then opened
	 */
	public void read(List<String> filenames) {
		//sort files depending on their extension
		sortByType(filenames);
		if(javaFiles.size()>0)
			openJava();
		if(adaFiles.size()>0)
			openAda();
		if(cppFiles.size()>0)
			openCpp();
		//TODO special case for other
		}
	/**
	 * a method that opens java files
	 */
	public void openJava() {
		//notify user of amount of files in list
		SWint.notifyUser(javaFiles.size()+" Java Files Found");
		//open each file
		for(String s:javaFiles) {
			String file=openFile(s);
			SWint.notifyUser(file); //for demonstration purposes
			//TODO analyze
		}
		System.out.println("all java files read");
	}
	/**
	 * a method that opens Ada files
	 */
	public void openAda() {
		//notify user of amount of files in list
		SWint.notifyUser(adaFiles.size()+" Ada Files Found");
		//open each file
		for(String s:adaFiles) {
			String file=openFile(s);
			SWint.notifyUser(file);//for demonstration purposes
			//TODO analyze
		}
		SWint.notifyUser("all ada files read");
	}
	/**
	 * a method that opens c++ files
	 */
	public void openCpp() {
		//notify user of amount of files in list
		SWint.notifyUser(cppFiles.size()+" C++ Files Found");
		//open each file
		for(String s:cppFiles) {
			String file=openFile(s);
			System.out.println(file);//for demonstration purposes
			//TODO analyze
		}
		SWint.notifyUser("all c++ files read");
	}
	/**
	 * a method that prompts the user to which type each file is then sorts and opens them appropriately with each response
	 */
	public void openOther() {
		//generate a list of valid responses
		List<String>validResponses=new ArrayList<String>();
		validResponses.add("-j");
		validResponses.add("-a");
		validResponses.add("-c");
		validResponses.add("-d");
		//set a string to capture each filename
		String fileName;
		//set iterator and continue to iterate while there is another element
		/**
		 * this loop goes through each of the ambiguous files asks the user what they are and adds them to the 
		 * appropriate list
		 */
		for(Iterator<String> itty=otherFiles.iterator();itty.hasNext();) {
			//grab next file name
			fileName=itty.next();
			//prompt user and collect response
			String response=SWint.getResponse("What kindof file is "+fileName+"? -j for java, -a for ada, -c for c++ or -d to discard",validResponses);
			//depending on the users response place the file name into it's corresponding list
			if(response.equals("-j")) {
				javaFiles.add(fileName);
			}
			else if(response.equals("-a")) {
				adaFiles.add(fileName);
			}
			else if(response.equals("-c")) {
				cppFiles.add(fileName);
			}
			//since we sorted the filename, we can remove it from the list of ambiguous files
			itty.remove();

			
		}
	}
	/**
	 * this method opens a file with a given file name and returns it's contents
	 * @param fileName the file you wish to open
	 * @return the contents of the file
	 */

	private String openFile(String fileName) {
		try {
			//use buffer reader to open the file
			br = new BufferedReader(new FileReader(fileName));
			//set an empty string to capture the contents of the file
			String file="";
			//set a variable to capture the individual lines of the project
			String sCurrentLine;
			//while the file has lines take those individual line and added to the total files content
			while ((sCurrentLine = br.readLine()) != null) {
				file+=sCurrentLine;
			}
			return file;


		}
		catch(FileNotFoundException e) {
			//if there was an error opening a file we notify the user
			SWint.notifyUser(fileName+" not Found.");
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}



	}
	/**
	 * checks if the given file exists
	 * @param name name of the given files
	 * @return if the file exists or not
	 */
	public boolean fileExists(String name) {
		try {
			//try to open the file with the given path, if it fails then it will throw an exception and notify the user that it does not exist
			//Return true if we make it through this line and false if not
			new FileReader(name);
			return true;
		}
		catch(Exception e) {
			SWint.notifyUser(name+" not found.");
			return false;
		}

	}
	/**
	 * mode used to open individual file from the SWint
	 * @param name of given file
	 */
	//TODO make this method great again
	public void openSingleFile(String name) {
		//does the file exist
		if(!fileExists(name)) {
			return;
		}
		//if so add it to a list 
		List<String> files= new LinkedList<String>();

		files.add(name);
		//sorts file for analysis
		//pass that list to read
		read(files);
	}
	/**
	 * Separate files by extension and if it it cannot be found add it to other for further processing
	 * @param filenames names of the that need to be sorted
	 */
	public void sortByType(List<String> filenames) {
		//check each file's extension
		for (String s:filenames){
			//split the file basses on a .
			String[] temp=s.split("\\.");
			//if there is an extension(the splits leads to a only one string)
			if(temp.length>1) {
				//checks for java extensions
				if(temp[1].equals("java")) {
					javaFiles.add(s);
				}
				//checks for c++ extensions
				//TODO change c to C
				else if(temp[1].equals("cpp")||temp[1].equalsIgnoreCase("cxx")||temp[1].equalsIgnoreCase("C")
						||temp[1].equalsIgnoreCase("cc")||temp[1].equalsIgnoreCase("c++")) {
					cppFiles.add(s);
				}
				//checks for Ada extensions
				else if(temp[1].equals("adb")||temp[1].equals("ada")) {
					adaFiles.add(s);
				}
				//otherwise sort into other category
				else {
					otherFiles.add(s);
				}	
				
			}
		}
	}

}
