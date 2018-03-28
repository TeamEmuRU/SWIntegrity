/**
 * The Input class validates files and organizes them into lists
 * based on their file types
 * 
 * @author Abby Beizer
 * @author Jamie Tyler Walder
 */


import java.io.File;
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
		adaFiles = new LinkedList<>();
		javaFiles = new LinkedList<>();
		cppFiles = new LinkedList<>();
	}
	
	
	
	
	///////////////////////////
	//	Process User Input   //
	//						 //
	///////////////////////////
	
	public void processInput(String[] args)
	{
		if (args.length == 0) {
			//If no arguments specified, process all files with known extensions
			//in the current directory
			this.addFiles(getAllFilesInDirectory(System.getProperty("user.dir")));
			this.analyze();
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
						addJavaFilesInDirectory(System.getProperty("user.dir"));
						break;
					case "-a":
						addAdaFilesInDirectory(System.getProperty("user.dir"));
						break;
					case "-c":
						addCppFilesInDirectory(System.getProperty("user.dir"));
						break;
					case "-r":
						addFiles(getAllFilesInDirectoryAndSubDirectories(System.getProperty("user.dir")));
						break;
					case "-all":
						addFiles(getAllFilesInDirectory(System.getProperty("user.dir")));
						break;
					case "-help":
					case "?":
						SIT.displayHelp();
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
				boolean hasTag = false;	//Flag for whether the next argument is a modifying tag for the
											//current file or directory
				File f = new File("");
				
				//This starts after the tags are processed
				for (int i = nonTagsStart; i < args.length; i++) 
				{
					//If the previous run of the loop detected that this argument was a tag,
					//Move right to the next argument
					if(hasTag)
					{
						hasTag = false;
						continue;
					}
					
					//If the current argument is part of a file or dir name,
					//add the word to the rest of the file name, and skip the rest of the for loop
					//Else if the flag is set to false, then add the previous filename to the input and
					//process the current argument
					//Then proceed as normal
					if(nameFragment)	
					{
						File temp = new File(args[i]);
						//If temp is not a file or directory on its own
						if(!temp.isFile() && !temp.isDirectory())
						{
							//nameFragment remains true
							f = new File(f.toString() + " " + args[i]);
						}
						//If the newly-concatenated f is a file or directory,
						//add it to the input and set the nameFragment flag to false
						if(f.isFile() || f.isDirectory()) 
						{
							try
							{
								hasTag = detectTag(f.getAbsolutePath(), args[i + 1]);
							}
							catch(ArrayIndexOutOfBoundsException ar)
							{
								hasTag = detectTag(f.getAbsolutePath(), "");
							}
							
							nameFragment = false;
						} 
					}
					
					//Process only the current argument
					f = new File(args[i]);
						
					//If f is a file or directory, then add it to the input and set the nameFragment flag to false
					if (f.isFile() || f.isDirectory()) 
					{
						try
						{
							hasTag = detectTag(f.getAbsolutePath(), args[i + 1]);
						}
						catch(ArrayIndexOutOfBoundsException ar)
						{
							hasTag = detectTag(f.getAbsolutePath(), "");
						}
						
						nameFragment = false;
					} 
					else 
					{
						nameFragment = true;
					}//end if
				}//end for
			}
		}
			this.analyze();
	}
	
	/**
	 * If the tag matches a known filetype tag, then the file is added to the appropriate list as if it were that type
	 * @param filename The name of the file currently being processed
	 * @param argument The argument following the current filename
	 * @return True if argument is a tag
	 */
	private boolean detectTag(String filename, String argument)
	{	
		File f = new File(filename);
	
		switch (argument) {
		//Default is if the next argument is not a tag for this file
		case "-j":
		{
			if(f.isFile())
				javaFiles.add(filename);
			else if(f.isDirectory())
				addJavaFilesInDirectory(filename);
			else
				SIT.notifyUser("Invalid file or directory name");
			return true;
		}
		case "-a":
		{
			SIT.notifyUser("Added " + filename + "\n");
			if(f.isFile())
				adaFiles.add(filename);
			else if(f.isDirectory())
				addAdaFilesInDirectory(filename);
			else
				SIT.notifyUser("Invalid file or directory name");
			return true;
		}
		case "-c":
		{
			if(f.isFile())
				cppFiles.add(filename);
			else if(f.isDirectory())
				addCppFilesInDirectory(filename);
			else
				SIT.notifyUser("Invalid file or directory name");
			return true;
		}
		case "-r":
		{
			if(f.isFile())
				SIT.notifyUser("Invalid argument for " + filename);
			else if (f.isDirectory())
				this.addFiles(this.getAllFilesInDirectoryAndSubDirectories(filename));
			else
				SIT.notifyUser("Invalid file or directory name");
		}
		case "-all":
			if(f.isFile())
				SIT.notifyUser("Invalid argument for " + filename);
			else if (f.isDirectory())
				this.addFiles(this.getAllFilesInDirectory(filename));
			else
				SIT.notifyUser("Invalid file or directory name");
		default:
			if(argument.startsWith("-"))
			{
				SIT.notifyUser("The program has encountered an invalid argument\n");
				return true;
			}
			else
			{
				if(f.isFile())
					addFile(filename);
				else if(f.isDirectory())
					getAllFilesInDirectory(filename);
				else
					SIT.notifyUser("Invalid file or directory name");
				return false;
			}
		}
	}
	
	
	
	
	////////////////////////////////
	//	Adding and sorting files  //
	//							  //
	////////////////////////////////
	
	/**
	 * Add any number of files to the input class.
	 * Calls the sortByType function
	 * @param filenames A list of files to add
	 */
	public void addFiles(List<String> filenames)
	{
		//sort files depending on their extension
		//and add files to the appropriate linked list
		sortByType(filenames);
	}
	
	/**
	 * Add a single file to the input class
	 * Calls the sortByType function
	 * @param filename A single file to add
	 */
	private void addFile(String filename)
	{
		List<String> fileList = new LinkedList<>();
		fileList.add(filename);
		sortByType(fileList);
	}
	
	/**
	 * Separate files by extension and add each file to its appropriate file list
	 * If the extension is not recognized, do not add it to any list.
	 * @param filenames Names of the that need to be sorted
	 */
	//TODO move these extensions into an enum class
	private void sortByType(List<String> filenames) {
		//check each file's extension
		for (String s : filenames)
		{
			//Validate the file before sorting
			if(validFile(s))
			{
				//checks for java extensions
				if(isJavaFile(s)) 
				{
					javaFiles.add(s);
				}
				//checks for c++ extensions
				else if(isCppFile(s)) 
				{
					cppFiles.add(s);
				}
				//checks for Ada extensions
				else if(isAdaFile(s)) 
				{
					adaFiles.add(s);
				}
			}	
		}
	}
	
	/**
	 * Collects the filenames of all files in current directory.
	 * The files are then sorted into their appropriate lists
	 * @param dir The directory to gather all files from
	 * @return a reader that contains the files
	 */
	//TODO: Make this function private to the Input class. Replace its usage in main
	public List<String> getAllFilesInDirectory(String dir) {
		//gather information on folder
		File folder = new File(dir);
		
		//gather individual
		File[] listOfFiles = folder.listFiles();
		
		//add file names to list 
		List<String> fileNames = new LinkedList<>();
		
		if(listOfFiles.length > 0)
		{
			for(File f : listOfFiles) 
			{
				if(f.isFile())
				{
					fileNames.add(f.getAbsolutePath());
				}
				
			}
			//addFiles(fileNames);
			
			//sort that list by extension
		}
		
		return fileNames;
	}
	
	
	/**
	 * Helper method for addAllFilesInDirectoryAndSubDirectories, this method gathers all directories in a directory, much in the same way 
	 * addAllFilesInDirectory does
	 * @param dir the directory you wish to call this method on
	 * @return a list of directories in the given directory
	 */
	private List<String> getAllDirectoriesInDirectory(String dir){
		//create a new file for director
		File folder=new File(dir);
		//get file names
		File[] listOfFiles = folder.listFiles();
		//create new list to hold directory names
		List<String> fileNames = new LinkedList<>();
		//collect all directories
		for(File f : listOfFiles) 
		{
			if(f.isDirectory())
			{
				fileNames.add(f.getAbsolutePath());
			}
			
		}
		return fileNames;
	}
	/**
	 * This method is used to recursively call a directory and get all files within that Directory as well as all files inside directories within those 
	 * directories
	 * @param dir the path of the directory you wish to call the method on 
	 * @return all files within that directory and it's subdirectories
	 */
	public List<String> getAllFilesInDirectoryAndSubDirectories(String dir) {
		//gather all files in this directory
		List<String> allFiles=getAllFilesInDirectory(dir);
		//gather all directories within this directory
		List<String> allDirectories=getAllDirectoriesInDirectory(dir);
		//for each subdirectory, gather all files within that directory and it's subdirectories and then add them to the list of 
		//files from the directory that came before it 
		for(String subDir:allDirectories) {
			//the recursive call will continue until it hits the bottom of the directory tree, at which case get all Directories in directory will return zero 
			//and this for loop wont run 
			allFiles.addAll(getAllFilesInDirectoryAndSubDirectories(subDir));
		}
		return allFiles;
		
	}
	
	/**
	 * Search through directory and collect all Java files
	 * @param dir The directory to pull files from
	 */
	//TODO: Make this function private to the Input class. Replace its usage in main
	public void addJavaFilesInDirectory(String dir) 
	{
		List<String> filenames = getAllFilesInDirectory(dir);
		for(String name:filenames) 
		{
			if(isJavaFile(name)) 
			{
				javaFiles.add(name);
			}
		}
	}
	
	/**
	 * Search through directory and collect all Ada files
	 * @param dir The directory to pull files from
	 */
	//TODO: Make this function private to the Input class. Replace its usage in main
	public void addAdaFilesInDirectory(String dir) 
	{
		List<String> filenames = getAllFilesInDirectory(dir);
		for(String name:filenames) 
		{
			if(isAdaFile(name)) 
			{
				adaFiles.add(name);
			}
		}
	}
	
	/**
	 * Search through directory and collect all C++ files
	 * @param dir The directory to pull files from
	 */
	//TODO: Make this function private to the Input class. Replace its usage in main
	public void addCppFilesInDirectory(String dir) {
		List<String> filenames =  getAllFilesInDirectory(dir);
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
	//TODO: Decide if we need this function --> extension validation is done in other methods
	//We could use isFile() rather than having another method call.
	public boolean validFile(String name) {
		
		boolean valid = false;
	
		//try to open the file with the given path
		//if it fails then it will throw an exception and notify the user that it does not exist
		//Return true if we make it through this line and false if not
		File f = new File(name);
		if(f.isFile())
		{
			valid = true;
		}
		else
		{
			SIT.notifyUser(name + " not found.");
		}
		//TODO: validate file extension
		return valid;
	}
	
	/**
	 * Validate whether a file is a Java file
	 * @param filename Name of file to validate
	 * @return true if the file is a Java file
	 */
	public boolean isJavaFile(String filename) {
		//split the file basses on a .
		String[] temp = filename.split("\\.");
		//if there is an extension(the splits leads to more than one)
		//Use the last split to evaluate the extension (covers absolute paths)
		if(temp.length>1) {
			int split = temp.length - 1;
			return temp[split].equals("java");
		}
		return false;
	}
	/**
	 * Validate whether a file is an Ada file
	 * @param filename Name of file to validate
	 * @return true if the file is an Ada file
	 */
	public boolean isAdaFile(String filename) {
		
		//split the file bases on a .
		String[] temp=filename.split("\\.");
		//if there is an extension(the splits leads to more than one)
		//Use the last split to evaluate the extension (covers absolute paths)
		if(temp.length > 1) {
			int split = temp.length - 1;
			return temp[split].equals("adb")||temp[split].equals("ada")||temp[split].equals("ada");
		}
		return false;
		
	}
	/**
	 * Validate whether a file is a C++ file
	 * @param filename Name of file to validate
	 * @return true if the file is a C++ file
	 */
	//TODO: Accept header files in this method or in a separate method
	public boolean isCppFile(String filename) {
		
		//split the file basses on a .
		String[] temp=filename.split("\\.");
		//if there is an extension(the splits leads to more than one)
		//Use the last split to evaluate the extension (covers absolute paths)
		if(temp.length > 1) {
			int split = temp.length - 1;
			return temp[split].equals("cpp")||temp[split].equalsIgnoreCase("cxx")||temp[split].equalsIgnoreCase("C")
					||temp[split].equalsIgnoreCase("cc")||temp[split].equalsIgnoreCase("c++");
		}
		return false;
	}
	
	
	
	
	
	///////////////////////////
	//	 Analysis of Files   //
	//						 //
	///////////////////////////
	
	/**
	 * Analyzes sorted lists of files for vulnerabilities
	 */
	public void analyze() {
		if(javaFiles.size()>0)
			analyzeJava();
		if(adaFiles.size()>0)
			analyzeAda();
		if(cppFiles.size()>0)
			analyzeCpp();
		//TODO special case for other
	}
	
	/**
	 * Analyzes Java files for vulnerabilities by invoking an Analyzer object
	 */
	private void analyzeJava() {
		//notify user of amount of files in list
		SIT.notifyUser(javaFiles.size() + " Java Files Found");
		
		//set the analyzer to the appropriate type
		analyzer = new JavaAnalyzer();
		
		//analyze each file
		for(String filename : javaFiles) {
			analyzer.analyze(filename);
			SIT.notifyUser(filename + " has been analyzed.");
		}
		System.out.println("all java files read");
	}
	
	/**
	 * Analyzes Ada files for vulnerabilities by invoking an Analyzer object
	 */
	private void analyzeAda() {
		//notify user of amount of files in list
		SIT.notifyUser(adaFiles.size()+" Ada Files Found");
		
		//set the analyzer to the appropriate type
		analyzer = new AdaAnalyzer();
		
		//analyze each file
		for(String filename : adaFiles) {
			analyzer.analyze(filename);
			SIT.notifyUser(filename+" has been analyzed.");
			//TODO analyze
		}
		SIT.notifyUser("all ada files read");
	}
	/**
	 * Analyzes C++ files for vulnerabilities by invoking an Analyzer object
	 */
	private void analyzeCpp() {
		//notify user of amount of files in list
		SIT.notifyUser(cppFiles.size() + " C++ Files Found");
		
		//set the analyzer to the appropriate type
		analyzer= new CppAnalyzer();
		
		//analyze each file
		for(String filename : cppFiles) {
			analyzer.analyze(filename);
			SIT.notifyUser(filename+" has been analyzed.");
			//TODO analyze
		}
		SIT.notifyUser("all c++ files read");
	}
	


}
