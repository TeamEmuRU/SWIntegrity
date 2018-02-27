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
	public void analyzeAll(List<String> filenames) {
		//sort files depending on their extension
		sortByType(filenames);
		
		analyzeJava();
		analyzeAda();
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
	public List<String> collectAllFilesInDirectory() {
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
		return fileNames;
	}
	/**
	 * search through directory and collect all java files
	 */
	public void collectAllJavaFiles() {
		List<String> filenames=collectAllFilesInDirectory();
		for(String name:filenames) {
			if(isJavaFile(name)) {
				javaFiles.add(name);
			}
		}
	}
	/**
	 * search through directory and collect all ada files
	 */
	public void collectAllAdaFiles() {
		List<String> filenames=collectAllFilesInDirectory();
		for(String name:filenames) {
			if(isAdaFile(name)) {
				adaFiles.add(name);
			}
		}
	}
	/**
	 * search through directory and collect all java files
	 */
	public void collectAllCppFiles() {
		List<String> filenames=collectAllFilesInDirectory();
		for(String name:filenames) {
			if(isCppFile(name)) {
				cppFiles.add(name);
			}
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
			FileReader f=new FileReader(name);
			f.close();
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
		//check to see which type it is
		if(isJavaFile(name)) {
			javaFiles.add(name);
			analyzeJava();
		}
		else if(isAdaFile(name)) {
			adaFiles.add(name);
			analyzeAda();
		}
		else if(isCppFile(name)) {
			cppFiles.add(name);
			analyzeCpp();
		}
		else {
			SIT.notifyUser(name+"is not a valid file type.");
		}
	}
	/**
	 * Separate files by extension and if it it cannot be found add it to other for further processing
	 * @param filenames names of the that need to be sorted
	 */
	//TODO move these extensions into a JSON file
	public void sortByType(List<String> filenames) {
		//check each file's extension
		for (String s:filenames){
				//checks for java extensions
				if(isJavaFile(s)) {
					javaFiles.add(s);
				}
				//checks for c++ extensions
				//TODO change c to C
				else if(isAdaFile(s)) {
					cppFiles.add(s);
				}
				//checks for Ada extensions
				else if(isCppFile(s)) {
					adaFiles.add(s);
				}
				
			
		}
		
	}
	/**
	 * check to see if file is java file
	 * @param filename- name to check
	 * @return if the file is a java file
	 */
	public boolean isJavaFile(String filename) {
		//split the file basses on a .
		String[] temp=filename.split("\\.");
		//if there is an extension(the splits leads to a only one string)
		if(temp.length>1) {
			return temp[1].equals("java");
		}
		return false;
	}
	/**
	 * check to see if file is ada file
	 * @param filename- name to check
	 * @return if the file is a ada file
	 */
	public boolean isAdaFile(String filename) {
		
		//split the file basses on a .
		String[] temp=filename.split("\\.");
		//if there is an extension(the splits leads to a only one string)
		if(temp.length>1) {
			return temp[1].equals("adb")||temp[1].equals("ada")||temp[1].equals("ada");
		}
		return false;
		
	}
	/**
	 * check to see if file is c++ file
	 * @param filename- name to check
	 * @return if the file is a c++ file
	 */
	public boolean isCppFile(String filename) {
		
		//split the file basses on a .
		String[] temp=filename.split("\\.");
		//if there is an extension(the splits leads to a only one string)
		if(temp.length>1) {
			return temp[1].equals("cpp")||temp[1].equalsIgnoreCase("cxx")||temp[1].equalsIgnoreCase("C")
					||temp[1].equalsIgnoreCase("cc")||temp[1].equalsIgnoreCase("c++");
		}
		return false;
	}

}
