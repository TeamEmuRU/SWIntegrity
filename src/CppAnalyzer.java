/**
 * The CppAnalyzer parses 
 * 
 * @author Abby Beizer
 */


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CppAnalyzer extends Analyzer
{
	private List<String> variablesList;
	private Set<String> keywords;
	
	
	/**
	 * Default constructor
	 */
	public CppAnalyzer()
	{
		super();
		//Instantiate variables
		variablesList = new LinkedList<String>();
		keywords = new HashSet<>();
		
		//Create keyword set
		createKeywordSet();
	}
	
	/**
	 * Creates a set of reserved C++ keywords.
	 * Words that appear within this set are not recognized as valid class or variable names.
	 */
	private void createKeywordSet() 
	{
		keywords.add("public");
		keywords.add("private");
		keywords.add("protected");
		keywords.add("static");
		keywords.add("final");
		keywords.add("void");
		keywords.add("if");
		keywords.add("elif");
		keywords.add("else");
		keywords.add("extends");
		keywords.add("import");
		keywords.add("abstract");
		keywords.add("new");
		keywords.add("null");
		keywords.add("try");
		keywords.add("catch");
		keywords.add("class");
		keywords.add("return");
		keywords.add(";");
		keywords.add("(");
		keywords.add(")");
		keywords.add("{");
		keywords.add("}");
		keywords.add("=");
		keywords.add("+");
		keywords.add("%");
		keywords.add("/");
		keywords.add("-");
		keywords.add("\"");
		//TODO add the rest
		//Can use http://en.cppreference.com/w/cpp/keyword as a reference
	}
	
	/**
	 * a method that transforms code into an easier to work with format
	 * @param s code which need to be formatted
	 * @return a string containing all significant symbols seperated by spaces
	 */
	public String flattenCode(String s) 
	{
		String finalString = s.trim().replace("\n", "").replace("\t","").replaceAll("\r", "");
		//Separate all special characters		
		finalString = finalString.replace("=", " = ");
		finalString = finalString.replace("(", " ( ");
		finalString = finalString.replace(")", " ) ");
		finalString = finalString.replace(";", " ; ");
		finalString = finalString.replace("{", " { ");
		finalString = finalString.replace("}", " } ");
		finalString = finalString.replace("[", " [ ");
		finalString = finalString.replace("]", " ] ");
		finalString = finalString.replace(":", " : ");
		finalString = finalString.replace(":", " : ");
		finalString = finalString.replace("\"", " \" ");
		finalString = finalString.replace(",", " , ");
		//Create an array of all words separated by a space
		String words[] = finalString.split(" ");
		//Transform the array into an ArrayList
		ArrayList<String> list = new ArrayList<>(Arrays.asList(words));
		Iterator<String> itty = list.iterator();
		String result="";
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
	 * Identifies whether a given String can be used as a variable or class name without conflicting with reserved C++ keywords.
	 * @param name The String to validate
	 * @return true if the String does not conflict with the list of reserved C++ keywords.
	 */
	private boolean isValidName(String name) 
	{
		boolean valid = true;
		//Set the flag to false if the name conflicts with reserved keywords
		if(keywords.contains(name))
		{
			valid = false;
		}
		
		return valid;
	}

	/**
	 * extracts variable names and their types from a cleaned string
	 * @param s formatted code to pull variables from
	 */
	public void extractVariables(String s) 
	{
		//Create an array of all words separated by a space
		String words[] = s.split(" ");
		//Transform the array into an ArrayList
		ArrayList<String> list = new ArrayList<>(Arrays.asList(words));
		//start iterator
		Iterator<String> itty = list.iterator();
		//set a switch keep track of when we are in a string
		boolean stringSwitch = false;
		//the following loop removes strings, it is unfortunate but in order to detect varaibles strings must not be present
		while(itty.hasNext()) 
		{
			String word = itty.next();
			
			//if we run into the begining of a string we turn the switch on and remove the first character, this gets activated again at the end of the switch
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
		//search each word for mathing pattern
		for(int i = 0; (i < words.length - 2);i++) 
		{
			//if the first two wards are not keywords we check further
			if( !keywords.contains(words[i]) && !keywords.contains(words[i+1]) ) 
			{
				//we check for validity of class name and var name as well as it not being a mthod name by checking for the (
				if( (!words[i+2].contains("(")) && isValidName(words[i]))
				{
					//if it is we declare the variable name:type
					variablesList.add(words[i+1] + ":" + words[i]);
				}
			}
		}//end for
	}
	
	@Override
	public void parse(String filename) {
		
		/*
		 * First, remove any comments from the line
		 * 		TODO: ignore comment symbols that are used as string literals
		 * TODO: Process the line for variable names
		 */
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			String line;
			boolean mComment = false;	//whether the current line occurs between the symbols /* and */
			
			//Runs until end of file
			while( (line = br.readLine()) != null)
			{
				/* First, remove any comments from the line */
				
				//If the previous line began or continued a multi-line comment
				if(mComment)
				{
					//If the current line terminates the comment,
					//remove all text before the terminating character
					//and set flag to false
					if(line.contains("*/"))
					{
						line = line.replaceAll("^.*\\*/", "");
						mComment = false;
					}
					//if the current line does not terminate the comment,
					//then it continues to the next line
					else
					{
						continue;
					}
				}
				
				//removes single-line comments in the format "/* --- */"
				line = line.replaceAll("/\\*.*\\*/", "");
				//removes single-line comments in the format "//"
				line = line.replaceAll("//.*$", "");
			
				//removes all characters following "/*" and flags the next line as a continuation of a multi-line comment
				if(line.contains("/*"))
				{
					line = line.replaceAll("/\\*.*$", "");
					mComment = true;
				}
				
				//After removal of comments, process remaining characters
				
				//ignore lines that are blank
				if(line.length() == 0) {
					continue;
				}
				
				
				/* Then locate conditional statements */
					//TODO: Conditional statements
				
				
				/* Then locate variables */
				
				//Split the line into an array of individual words
				String[] split = line.split(" ");
				for(int i = 0; i < split.length - 1; i++)
				{
					//Look for patterns in each word
					//The labeled switch cases catch primitive types
					switch(split[i])
					{
					//TODO: Do something with the variables found
					//type name = value;
					case "bool"		: { variablesList.add(split[i + 1]); break; }
					case "char"		: { variablesList.add(split[i + 1]); break; }
					case "int"		: { variablesList.add(split[i + 1]); break; }
					case "float"	: { variablesList.add(split[i + 1]); break; }
					case "double"	: { variablesList.add(split[i + 1]); break; }
					case "wchar_t"	: { variablesList.add(split[i + 1]); break; }
					//default case catches any non-primitive types
					default : {
						try {
							//Formats for declaration include
							//Var name;			`	--> name = Var(); 	or	name = new Var();
							//Var name = Var();		-->				name = new Var();
							
							//To resolve this issue, check to see if there is only one remaining word before a semicolon is encountered
							//If this is true, then the next word is the name of a variable
							//catches cases Var foo; and Var foo ;
							//TODO: Properly handle this ArrayIndexOutOfBoundsException check before attempting to evaluate i + 2
							if(split[i + 1].contains(";") || split[i + 2].equals(";"))
							{
								variablesList.add(split[i + 1]);
							}
							//If the next word is followed by an assignment, then the next word is the name of a variable
							//catches cases Var foo=bar; 	and   Var foo= bar; 	and   Var foo = bar;
							//TODO: Properly handle this ArrayIndexOutOfBoundsException check before attempting to evaluate i + 2
							else if(split[i + 1].contains("=") || split[i + 2].equals("="))
							{
								variablesList.add(split[i + 1]);
							}
						}
						catch(ArrayIndexOutOfBoundsException ai)
						{
							continue;
						}
					}//end default
					}//end switch statement
				}//end for loop	
			
			}
			
		
			

		}
		catch(FileNotFoundException fnf)	//from FileReader
		{
			SIT.notifyUser("Eror: File " + filename + " could not be parsed.");
		}
		catch(IOException io)	//from BufferedReader
		{
			SIT.notifyUser("Error reading the contents of " + filename + "." );
		}
		
	}

	
	@Override
	protected void analyze(String filename) {
		// TODO Auto-generated method stub
		
	}

}
