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
	public void extractLiterals(String file) {
		String[] words=file.replace("\\\"", "").replace("\\","").split(" ");
		String litteral="";
		boolean inLiteral=false;
		for(String word:words) {
			if(word.equals("\"")) {
				inLiteral=!inLiteral;
				if(!inLiteral) {
					literals.add(litteral);
					litteral="";
				}
			}
			if(inLiteral) {
				litteral+=word+" ";
			}
		}
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
		String result="";
		while(itty.hasNext()) {
			String word = itty.next();
			//Remove any blank words in the ArrayList
			if(!word.equals(""))
			{
				result += word + " ";
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
		s=s.replace("\\\"", "");
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
		//create list to store scopes
		Stack<String> scopes=new Stack<>();
		//create a scope id to differentiate scopes
		int scopeID=0;
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
					variablesList.add(new Variable(words[i+1], words[i], scopes.toString(),i ));
				}
			}
			//add a new scope if the a new scope keywords is found
			if(words[i].equals("class")||(!keywords.contains(words[i]) && !keywords.contains(words[i+1])&&words[i+2].equals("("))) {
				scopes.push(words[i+1]+"-"+scopeID);
				scopeID++;
					
				}
			else if(words[i].equals("if")||words[i].equals("else")||words[i].equals("for")||words[i].equals("while")||words[i].equals("try")||words[i].equals("catch")) {
				scopes.push(words[i]+"-"+scopeID);
				scopeID++;
			}
			//exit scope if character is found
			else if(words[i].equals("}")) {
				scopes.pop();
			}
			else if(words[i].equals("#include")) {
				//skip word if import
				i++;
			}
			//check to see if this is an assigment
			else if(words[i].equals("=")&&!words[i+1].equals("=")) {
				//if it is get the varaible
				for(Variable v:variablesList) {
					//if the variable name and scope match add the assignment
					if(v.getName().equals(words[i-1])&&v.getScope().equals(scopes.toString())) {
						//start a place to capture everything after the initialization
						int place=i+1;
						//start string to capture assignment
						String assignment="";
						//capture assignment until ; is reached
						while(!words[place].equals(";")) {
							assignment+=words[place];
							place++;
						}
						//add word to assignments
						v.getAssignments().put(i, assignment);
				}
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
		
		String file="";
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
				//TODO: Prevent opening file twice
				
				file+=line;
			}
			String s=flattenCode(file);
			System.out.print(s);
			extractVariables(s);
			extractLiterals(s);
			//TODO remove me!
			System.out.println(variablesList);
			System.out.println(literals);
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
		parse(filename);
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
		public boolean equals(Object obj) {
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
