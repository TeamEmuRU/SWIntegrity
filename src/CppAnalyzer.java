/**
 * The CppAnalyzer parses a file into its variables and literals,
 * then utilizes this information to aid in analyzing the file against vulnerabilities
 * 
 * @author Abby Beizer
 * @author Jamie Tyler Walder
 */


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

public class CppAnalyzer extends Analyzer
{
	private List<Variable> variablesList;
	private Set<String> keywords;
	//literals of the file
	private List<String> literals;
	private String fileContents;

	
	
	/**
	 * Default constructor
	 */
	public CppAnalyzer()
	{
		super();
		//Instantiate variables
		variablesList = new LinkedList<Variable>();
		keywords = new HashSet<>();
		literals=new LinkedList<>();
		//Create keyword set
		createKeywordSet();
	}
	
	@Override
	public void parse(String filename) 
	{
	
		String file = openFile(filename);			
		file = flattenCode(file);
		//TODO: remove print statement
		System.out.print(file);
		extractVariables(file);
		extractLiterals(file);
		this.fileContents=file;
		//TODO remove print statement
		System.out.println(variablesList);
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
		keywords.add("cout");
		keywords.add("<<");
		keywords.add(">>");
		keywords.add("cin");
		keywords.add("#include");
		keywords.add(":");
		
		//TODO add the rest
		//Can use http://en.cppreference.com/w/cpp/keyword as a reference
	}
	/**
	 * Scans files and pulls out all literals 
	 * @param file file contents to be scanned
	 */
	public void extractLiterals(String file) 
	{
		//removes escape characters and splits the file at spaces
		String[] words = file.replace("\\\"", "").replace("\\","").split(" ");
		
		String literal = "";
		//Flag to track whether the current word is a part of a string literal
		boolean inLiteral = false;
		//For each word in the file, if the word occurs between two quotation marks
		//then it is a string literal
		for(String word : words) 
		{
			if(word.equals("\"")) 
			{
				inLiteral =! inLiteral;
				
				//If the current word ends the literal, add the literal to the list
				//and clear the variable to capture the next literal in the file
				if(!inLiteral) 
				{
					literals.add(literal);
					literal = "";
				}
			}
			//If the string is part of a literal, concatenate the word to the rest of the phrase
			if(inLiteral) 
			{
				literal += (word + " ");
			}
		}//end for
	}
	
	
	/**
	 * a method that transforms code into an easier to work with format
	 * @param s code which need to be formatted
	 * @return a string containing all significant symbols separated by spaces
	 */
	public String flattenCode(String s) 
	{
		
		/* First, remove any comments from the file */	
		//removes single-line and multi-line comments in the format /* --- */
		s = s.replaceAll("/\\*((?:.*\\r?\\n?)*)\\*/", "");
		//removes single-line comments in the format "//"
		s = s.replaceAll("//.*\n", "");
		
		/* Next, add spaces around special characters to make the next steps of processing easier */
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
		finalString = finalString.replace("+", " + ");
		finalString = finalString.replace("\"", " \" ");
		finalString = finalString.replace(",", " , ");
		finalString = finalString.replace("-", " - ");
		finalString = finalString.replace("/", " / ");
		finalString = finalString.replace("%", " % ");
		finalString = finalString.replace("*", " * ");
		
		
		//Create an array of all words separated by a space
		String words[] = finalString.split(" ");
		//Transform the array into an ArrayList
		ArrayList<String> list = new ArrayList<>(Arrays.asList(words));
		Iterator<String> itty = list.iterator();
		String result = "";
		while(itty.hasNext()) 
		{
			String word = itty.next();
			//Remove any blank words in the ArrayList
			if(!word.equals(""))
			{
				result += (word + " ");
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
		s = flattenCode(s);
		s = s.replace("\\\"", "");
		//Create an array of all words separated by a space
		String words[] = s.split(" ");
		
		//Transform the array into an ArrayList
		ArrayList<String> list = new ArrayList<>(Arrays.asList(words));
		//Initialize an iterator
		Iterator<String> itty = list.iterator();
		
		//set a flag to keep track of when we are in a string
		boolean stringFlag = false;
		//the following loop removes strings. In order to detect variables, strings must not be present
		while(itty.hasNext()) 
		{
			String word = itty.next();
			
			//if we run into the beginning of a string, enable the flag and remove the first character
			//The flag is disabled again at the end of the String
			if(word.equals("\"")) 
			{
				stringFlag = !stringFlag;
				itty.remove();
			}
			//while inside of a string, remove the word from the list
			else if(stringFlag) 
			{
				itty.remove();
			}
		}//end while
		
		//put words back into an array
		words = list.toArray(new String[list.size()]);
		
		Stack<String> scopes=new Stack<>();	// list to store scopes
		int scopeID = 0;	//a scope id to differentiate scopes
		
		//search each word for matching pattern
		for(int i = 0; (i < words.length - 2); i++) 
		{
			//if the first two words are not keywords, this may signify a variable declaration
			if( !keywords.contains(words[i]) && !keywords.contains(words[i+1]) ) 
			{
				//we check for validity of class name and var name as well as it not being a method name by checking for the (
				//TODO: Right now, the check for && isValidName(words[i]) is repetitive after the check for !keywords.contains(words[i])
				if( (!words[i+2].contains("(")) && isValidName(words[i]) )
				{
					//if it is, we declare the variable name:type
					variablesList.add(new Variable(words[i+1], words[i], scopes.toString(), i));
				}
			}
			//add a new scope if the keywords is found
			if(words[i].equals("class")||(!keywords.contains(words[i]) && !keywords.contains(words[i+1])&&words[i+2].equals("("))) 
			{
				scopes.push(words[i+1]+"-"+scopeID);
				scopeID++;
					
			}
			else if(words[i].equals("if")||words[i].equals("else")||words[i].equals("for")||words[i].equals("while")||words[i].equals("try")||words[i].equals("catch")) 
			{
				scopes.push(words[i]+"-"+scopeID);
				scopeID++;
			}
			//exit scope if character is found
			else if(words[i].equals("}")) 
			{
				scopes.pop();
			}
			else if(words[i].equals("#include")) 
			{
				//skip word if import
				i++;
			}
			//check to see if this is an assignment
			else if(words[i].equals("=")&&!words[i+1].equals("=")) 
			{
				//create stack to keep track of temporary scopes
				Stack<String> temp = new Stack<>();
				//flag if the variable was found in scope
				boolean found = false;
				//if it is get the variable
				while(!found) 
				{
					for(Variable v:variablesList) 
					{
						//if the variable name and scope match add the assignment
						if(v.getName().equals(words[i-1])&&v.getScope().equals(scopes.toString())) 
						{
							//start a place to capture everything after the initialization
							int place=i+1;
							//start string to capture assignment
							String assignment = "";
							//capture assignment until ; is reached
							while(!words[place].equals(";")) 
							{
								assignment+=words[place];
								place++;
							}
							//add word to assignments
							v.getAssignments().put(i, assignment);
							//we found the variable, so set flag to true
							found = true;
						}
						//if we didn't find it, back out a scope and try again
					}
					if(!found&&!scopes.isEmpty()) 
					{
						temp.push(scopes.pop());
					}
				}//end while
				
				//repopulate the scope appropriately
				while(!temp.isEmpty()) 
				{
					scopes.push(temp.pop());
				}
			}
		}//end for
	}

	
	/**
         * Given a search string, returns whether or not that string was found within a literal
         * @param search the string to look for
	 * @return true if the search string was found within a literal
         */
	private boolean literalContains(String search) {
		for(String literal:literals) {
			if(literal.contains(search)) {
				return true;
			}
		}
		return false;
	}
	public boolean fileContains(String search) {
		
			return fileContents.contains(search);
		
	}

	
	@Override
	protected void analyze(String filename) {
		parse(filename);
	}

	private void sqlCppAnalyze(String fileName) {
		
		String DBkeywords[] = {"SELECT", "UNION", "WHERE", "FROM", "HAVING", "JOIN", "ORDER BY"}; //A List of key words used in SQL
		//a list of the most common c++ libraries to use for databases
		String DBlibraries[] = {"MYSQL","SQL","SQLAPI", "SQLITE3","SOCI", "OTL", "LMDB++", "DTL", "LMDB", "MONGOXX"};
		String contents = "";
		String currentLine = "";
		boolean badSQL = false; // a boolean to see if this vulnerability exists
		
		try{
			BufferedReader br = new BufferedReader(new FileReader(fileName));
		
			while((currentLine = br.readLine()) != null){
				contents += currentLine;
			}
			
			//change everything to uppercase to deal with case sensitivity
			contents = contents.toUpperCase();
			
			//check to see if a database library was used for c++,
			//if you find one, there is a possibility for SQL injection
			//if you don't, then there's a strong indicator that there won't be any SQL injections
			for(String lib : DBlibraries) {	
				if(contents.contains(lib)){
					
					for(String word : DBkeywords){//iterates through the key word list to see if they
								      //appear in the string of the program code.
					
						//search for keywords that might indicate an SQL statement	
						if(contents.contains(word)){
							
							//if keywords were found, check to see if the program calls for user input
							if(contents.contains("CIN >>")){
								badSQL = true;
							}
								
							
						} 													     
					}
				} 
			}
		}
		catch (IOException e){
			System.out.println("FileNotFoundException in "
					+ "c++ SQL analyze");
		}
		//Display whether possible sql injections were detected
		System.out.println("At risk for possible SQL injections: "+badSQL);
	}

	private class Variable{
		String name;
		String type;
		String scope;
		//Line on which this variable is created 
		int symbolNumber;
		//line on which the assignment is followed by the reference
		Map<Integer,String> assignments;
		
		public Variable(String name, String type, String scope, int line) {
			super();
			this.name = name;
			this.type = type;
			this.scope = scope;
			this.symbolNumber = line;
			assignments=new HashMap<>();
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public String getScope() {
			return scope;
		}
		public void setScope(String scope) {
			this.scope = scope;
		}
		public int getLine() {
			return symbolNumber;
		}
		public void setLine(int line) {
			this.symbolNumber = line;
		}
		public Map<Integer, String> getAssignments() {
			return assignments;
		}
		public void setAssignments(Map<Integer, String> assignments) {
			this.assignments = assignments;
		}
		
		@Override
		public String toString() {
			return "Variable [name=" + name + ", type=" + type + ", scope=" + scope + ", symbolNumber=" + symbolNumber
					+ ", assignments=" + assignments +  "]";
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			result = prime * result + ((scope == null) ? 0 : scope.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) 
		{
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Variable other = (Variable) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			if (scope == null) {
				if (other.scope != null)
					return false;
			} else if (!scope.equals(other.scope))
				return false;
			return true;
		}
		private CppAnalyzer getOuterType() {
			return CppAnalyzer.this;
		}
		
		
		
		
		
	}

}
