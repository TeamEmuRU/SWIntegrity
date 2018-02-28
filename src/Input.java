/**
 * The Input class validates files and organizes them into lists
 * based on their file types
 * 
 * @author Jamie Tyler Walder
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;


public class Input {

	private Analyzer analyzer;	
	private List<String> adaFiles; 	//a collection of Ada files
	private List<String> javaFiles;	//a collection of java files
	private List<String> cppFiles;	//a collection of c++ files
	
	/**
	 * Default Constructor
	 * Initializes each file list
	 */
	public Input() {
		// TODO Auto-generated constructor stub
		adaFiles = new LinkedList<>();
		javaFiles = new LinkedList<>();
		cppFiles = new LinkedList<>();
	}
	
	/**
	 * Analyzes sorted lists of files for vulnerabilities
	 * @param filenames A list that of filenames
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
	 * Analyzes Java files for vulnerabilities by invoking an Analyzer object
	 */
	public void analyzeJava() {
		//notify user of amount of files in list
		SIT.notifyUser(javaFiles.size()+" Java Files Found");
		
		//set the analyzer to the appropriate type
		analyzer = new JavaAnalyzer();
		
		//analyze each file
		for(String filename:javaFiles) {
			analyzer.analyze(filename);
			SIT.notifyUser(filename+" has been analyzed.");
			//TODO analyze
		}
		System.out.println("all java files read");
	}
	/**
	 * Analyzes Ada files for vulnerabilities by invoking an Analyzer object
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
	 * Analyzes C++ files for vulnerabilities by invoking an Analyzer object
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
	
	/**
	 * Collects the filenames of all files in current directory.
	 * The files are then sorted into their appropriate lists
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
	 * Search through directory and collect all Java files
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
	 * Search through directory and collect all Ada files
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
	 * Search through directory and collect all C++ files
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
	 * Validates whether the given file exists and has a file extension
	 * that can be handled by the SIT.
	 * @param name A filename
	 * @return true if the file exists and has a valid extension
	 */
	public boolean fileExists(String name) {
		try {
			//try to open the file with the given path
			//if it fails then it will throw an exception and notify the user that it does not exist
			//Return true if we make it through this line and false if not
			FileReader f = new FileReader(name);
			f.close();
			
			//TODO: validate file extension
			
			return true;
		}
		catch(Exception e) {
			SIT.notifyUser(name + " not found.");
			return false;
		}
	}
	
	/**
	 * Validates and analyzes a single file
	 * @param name A filename
	 */
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
			SIT.notifyUser(name + "does not have a valid file extension.");
		}
	}
	
	/**
	 * Separate files by extension. 
	 * If the extension is not recognized, do not add it to any list.
	 * @param filenames Names of the that need to be sorted
	 */
	//TODO move these extensions into an enum class
	//TODO: make this function private to the input class, remove from main
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
	 * Validate whether a file is a Java file
	 * @param filename Name of file to validate
	 * @return true if the file is a Java file
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
	 * Validate whether a file is an Ada file
	 * @param filename Name of file to validate
	 * @return true if the file is an Ada file
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
	 * Validate whether a file is a C++ file
	 * @param filename Name of file to validate
	 * @return true if the file is a C++ file
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
