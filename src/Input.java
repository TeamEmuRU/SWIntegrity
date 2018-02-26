import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
/**
 * a class who's objective is to collect files names in the folder and pass those filenames to the appropriate analyzer
 * @author Tyler
 *
 */

public class Input {
	/**
	 * analyzer a variable that will hold the appopriate parser/analyzer
	 * adaFiles- a collection of selected Ada files
	 * javaFiles- a collection of selected java files
	 * cppfiles- a collection of selected java files
	 * otherFiles- a collection of other files that need to be sorted
	 * 
	 */
	private Analyzer analyzer;
	private List<String> adaFiles;
	private List<String> javaFiles;
	private List<String> cppFiles;
	/**
	 * Initialize all the holding lists
	 */
	public Input() {
		// TODO Auto-generated constructor stub
		adaFiles=new LinkedList<>();
		javaFiles=new LinkedList<>();
		cppFiles=new LinkedList<>();
	}
	/**
	 * A method that sorts a list of ambiguous files and opens them appropriately
	 * @param filenames-a list that of filenames that are sorted and then opened
	 */
	public void read(List<String> filenames) {
		//sort files depending on their extension
		sortByType(filenames);
		if(javaFiles.size()>0)
			analyzeJava();
		if(adaFiles.size()>0)
			analyzeAda();
		if(cppFiles.size()>0)
			analyzeCpp();
		//TODO special case for other
	}
	/**
	 * a method that sends java files to the analyzer
	 */
	public void analyzeJava() {
		//notify user of amount of files in list
		SIT.notifyUser(javaFiles.size()+" Java Files Found");
		//set the analyzer to the appropriate type
		analyzer=new JavaAnalyzer();
		//analyze each file
		for(String filename:javaFiles) {
			analyzer.analyze(filename);
			SIT.notifyUser(filename+" has been analyzed.");
			//TODO analyze
		}
		System.out.println("all java files read");
	}
	/**
	 * a method that sends ada files to the analyzer
	 */
	public void analyzeAda() {
		//notify user of amount of files in list
		SIT.notifyUser(adaFiles.size()+" Ada Files Found");
		
		//set the analyzer to the appropriate type
		analyzer=new AdaAnalyzer();
		//analyze each file
		for(String filename:adaFiles) {
			analyzer.analyze(filename);
			SIT.notifyUser(filename+" has been analyzed.");
			//TODO analyze
		}
		SIT.notifyUser("all ada files read");
	}
	/**
	 * a method that sends Cpp files to the analyzer
	 */
	public void analyzeCpp() {
		//notify user of amount of files in list
		SIT.notifyUser(cppFiles.size()+" C++ Files Found");
		//set the analyzer to the appropriate type
		analyzer=new CppAnalyzer();
		//analyze each file
		for(String filename:cppFiles) {
			analyzer.analyze(filename);
			SIT.notifyUser(filename+" has been analyzed.");
			//TODO analyze
		}
		SIT.notifyUser("all c++ files read");
	}
	/**collects the file names of all files in current directory and sends it to the reader to be sorted
	 * @return a reader that contains the files
	 */
	public void collectAllFilesInDIrectory() {
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
		sortByType(fileNames);
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
			SIT.notifyUser(name+" not found.");
			return false;
		}

	}
	/**
	 * mode used to open individual file from the SWint
	 * @param name of given file
	 */
	//TODO make this method great again
	public void analyzeSingleFile(String name) {
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
				else if(temp[1].equals("adb")||temp[1].equals("ada")||temp[1].equals("ada")) {
					adaFiles.add(s);
				}
				//otherwise sort into other category
				else {
					SIT.notifyUser(s+" is not an appropriate file type.");
				}
				
			}
		}
	}

}
