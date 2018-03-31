/**
 * The JavaAnalyzer parses java source code files by keeping a list of variables in use.
 *
 * @author Joseph Antaki
 * @author Abby Beizer
 * @author Jamie Tyler Walder
 */

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class JavaAnalyzer extends Analyzer {
	private Set<String> variablesList;
	private Set<String> literalsList;
	private Pattern NON_ALPHA_NUMERIC;


    /**
     * The main constructor. Initializes list of variables.
     */
	public JavaAnalyzer() {
		super();
		//instantiate map of variables
		variablesList = new HashSet<>();
		literalsList = new HashSet<>();
		//Create a regex pattern to catch special characters
		String regex = "[\\W&&\\S]";
		NON_ALPHA_NUMERIC = Pattern.compile(regex);
	}

    /**
    * Returns the set of variables.
	* @return the set of variables.
    */
	public Set<String> getVariablesList() {
		return variablesList;
	}
	
	/**
    * Returns the set of String literals.
	* @return the set of String literals .
    */
	public Set<String> getLiteralsList() {
		return literalsList;
	}
	
    /**
	* Reads an array of characters, extracts String literals represented inside, and stores them.
	* @param arr an array of characters
    */
	private void extractLiterals(char[] arr) 
	{
	    boolean inString = false;
	    String literal = "";
	    for(int i=0; i<arr.length; i++) 
	    {
	        if(arr[i] == '\"') 
	        {
			    inString = !inString;
			    if(literal.length() != 0) 
			    {
					literalsList.add(literal);
					literal = "";
			    }
	        }
	        else if(inString) 
	        {
	        	literal += arr[i];
	        }
	    }
	}

    /**
     * Pads non-alphanumeric characters with spaces to make the code easier to work with
     * @param s the line of code to format
     * @return the formatted line of code
     */
	public String flattenCode(String s)
	{
		//Remove all whitespace characters that are not regular spaces
		String finalString = s.trim().replaceAll("[\n|\t|\r|\f]", "");
		//Separate all special characters
		Matcher match = NON_ALPHA_NUMERIC.matcher(finalString);
		String found = "";
		while(match.find()) {
			found = match.group();
			finalString = finalString.replace(found, " "+found+" ");
		}
		//Create an array of all words separated by a space
		String words[] = finalString.split(" ");
		//Transform the array into an ArrayList
		ArrayList<String> list = new ArrayList<>(Arrays.asList(words));
		Iterator<String> itty = list.iterator();
		String result = "";
		while(itty.hasNext()) {
			String word = itty.next();
			//Remove any blank words in the ArrayList
			if(!word.equals(""))
			{
				result+=word+" ";
			}
		}
		return result.trim();
	}

    /**
     * Adds all variable names of a given type to the list of variables
     * @param arr An array of string tokens that make up a line of code
     * @param type The type of variable to pick out
     */
	private void searchForType(String[] arr, String type) {
        for (int i = 0; i < arr.length - 2; i++) {
            if (arr[i].equals(type) && !arr[i+2].equals("(")) {
                if(arr[i+1].equals("[") && !arr[i+3].equals(".")) {
                    variablesList.add(arr[i+3]);
                }
                else {
                    variablesList.add(arr[i + 1]);
                }
            }
        }
    }

    /**
     * Extracts variable names from a line of code
     * @param s The line of code to extract variables from
     */
    public void extractVariables(String s)
    {
		//Create an array of all words separated by a space
		String words[] = s.split(" ");
		//Transform the array into an ArrayList
		ArrayList<String> list = new ArrayList<>(Arrays.asList(words));
		//start iterator
		Iterator<String> itty = list.iterator();
		//set a switch to keep track of when we are in a string.
		boolean stringSwitch = false;
		//the following loop removes Strings. In order to detect variables strings must not be present
		while(itty.hasNext())
		{
			String word = itty.next();

			//if we run into the beginning of a string we turn on the switch and remove the quote character, this happens again at the end of the String
			if(word.equals("\""))
			{
				stringSwitch = !stringSwitch;
				itty.remove();
			}
			//while inside of a string we remove it
			else if(stringSwitch && !word.equals("\""))
			{
				itty.remove();
			}

		}
		//put words back into an array
		words = list.toArray(new String[list.size()]);
        //extract String and primitive variables
        searchForType(words, "String");
        searchForType(words, "boolean");
        searchForType(words, "byte");
        searchForType(words, "char");
        searchForType(words, "short");
        searchForType(words, "int");
        searchForType(words, "long");
        searchForType(words, "float");
        searchForType(words, "double");
        //extract all other variables
        for (int i = 2; i < words.length; i++) {
            if((!words[i-1].equals(")"))&&(words[i].equals("=") || words[i].equals(";")) && Character.isUpperCase(words[i-2].charAt(0))){
                if(words[i-1].equals(">")) {
					variablesList.add(words[i-2]);
				}
				else {
					variablesList.add(words[i - 1]);
				}
            }
        }
    }

	@Override
	/*
	 * Removes comments and extracts variables from source code.
	 * @param filename the file to be parsed
	 */
	public void parse(String filename) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			String line;
			String[] cleanLine=null;
			boolean mComment = false;    //whether the current line is a continuation of a multi-line comment

			//Runs until end of file
			while ((line = br.readLine()) != null) {
				/* First, remove any comments from the line */

				//If the previous line began or continued a multi-line comment
				if (mComment) {
					//If the current line terminates the comment,
					//remove all text before the terminating character
					//and set flag to false
					if (line.contains("*/")) {
						line = line.replaceAll("^.*\\*/", "");
						mComment = false;
					}
					//if the current line does not terminate the comment,
					//then it continues to the next line
					else {
						continue;
					}
				}

				//removes single-line comments in the format "/* --- */"
				line = line.replaceAll("/\\*.*\\*/", "");
				//removes single-line comments in the format "//"
				line = line.replaceAll("//.*$", "");

				//removes all characters following "/*" and flags the next line as a continuation of a multi-line comment
				if (line.contains("/*")) {
					line = line.replaceAll("/\\*.*$", "");
					mComment = true;
				}

				//After removal of comments, process remaining characters

				//ignore lines that are blank
				if (line.length() == 0) {
					continue;
				}

				/* Second, extract the literals and variables: */
				
				extractLiterals(line.toCharArray());
				line = flattenCode(line);
				extractVariables(line);
				//temporary fix
				Iterator<String> it = variablesList.iterator();
				Matcher m;
				while(it.hasNext()) {
					String element = it.next();
					m = NON_ALPHA_NUMERIC.matcher(element);
					if(m.matches()) {
						it.remove();
					}
				}

			}
            }
			catch(FileNotFoundException fnf){	//from FileReader
				SIT.notifyUser("Error: File " + filename + " could not be parsed.");
			}
			catch(IOException io){	//from BufferedReader
				SIT.notifyUser("Error reading the contents of " + filename + "." );
			}
			System.out.println(variablesList);
	    //System.out.print("Literals: ");
	    //System.out.println(literalsList);
	}

//	@Override
//	protected void analyze(String filename) {
//		parse(filename);
//	}
	
	
	
	//This is the java SQL injection vulnerability; hard-coded into the program until the database is implemented
	@Override
    /**
     * Method that analyzes a file for possible vulnerability to SQL injections 
     * @param fileName the name of the file to be analyzed
     */
	public void analyze(String filename) {
		parse(filename);
		
	}
	
	private void sqlVuln(String fileName)
	{
		String DBkeywords[] = {"SELECT", "UNION", "WHERE", "FROM", "HAVING", "JOIN", "ORDER BY"}; //a list of key words used in SQL
		String keyInMethods[] = {".NEXT",".READ", ".GET"}; //a list of methods used to obtain input from the user, list can be extended later
		String contents = "";
		String currentLine = "";
		boolean badSQL = false; // a boolean to see if this vulnerability exists
		
		try{
			BufferedReader br = new BufferedReader(new FileReader(fileName));
		
			while((currentLine = br.readLine()) != null){
				contents += currentLine;
			}
			//checks the two java library api imports see if being used
			//if no then skips all analysis to return false no vulnerabilities
			if(contents.contains("java.sql") || contents.contains("jdbc")){
				contents = contents.toUpperCase();
				
				for(String word : DBkeywords){//iterates through the key word list to see if they
											  //appear in the string of the program code.
					
					//if said keyword appears, checks for specific
					// statements that hackers use for SQL Injection.
					// %00 is a null byte used by attackers in many different
					// types of vulnerabilities.
					if(contents.contains(word)){
						
//						if(contents.contains("1=1") || contents.contains("%00") || contents.contains("'")){
//							badSQL = true;									    
//						}	
						
						//if keywords were found, check to see if the program collects user input
						for(String inputWord : keyInMethods){
							if(contents.contains(inputWord)){
								badSQL = true;
							}
							
						}
					} 													     
				}
			} 
		}
		catch (IOException e){
			System.out.println("FileNotFoundException in "
					+ "Java SQL analyze");
		}
		
		//Display whether or possible sql injections were detected
		System.out.println("At risk of SQL injection: "+badSQL);
	}


}
