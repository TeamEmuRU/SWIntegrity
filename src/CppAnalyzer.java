/**
 * The CppAnalyzer parses a file into its variables and literals,
 * then utilizes this information to aid in analyzing the file against vulnerabilities
 * 
 * @author Abby Beizer
 * @author Jamie Tyler Walder
 */

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
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
	//this map keeps track of which significant symbol(non comments) reside on which lines
	private Map<Integer,Integer> symbolToLine;
	private List<Pointer> pointersList;

	/**
	 * Constructor which creates lists necessary for collection of data.
	 */
	public CppAnalyzer()
	{
		super();
		//Instantiate variables
		variablesList = new LinkedList<Variable>();
		keywords = new HashSet<>();
		literals=new LinkedList<>();
		symbolToLine=new HashMap<>();
		pointersList=new LinkedList<>();
		//Create keyword set
		createKeywordSet();
	}
	
	/**
	 * Clears all existing data from each list used by AdaAnalyzer's parsing functions
	 */
	private void clearAll() {
		variablesList = new LinkedList<Variable>();
		literals=new LinkedList<>();
		symbolToLine=new HashMap<>();
		fileContents="";
		pointersList=new LinkedList<>();
	}
	
	/**
	 * Override Analyzer.parse
	 * Opens a file and parses it for variable names
	 * @param filename The path of the file to parse
	 */
	@Override
	public void parse(String filename) 
	{
		//clear all gathered data
		clearAll();
		String file = openFile(filename);
		
		file = flattenCode(file);
		this.fileContents=file;
		//System.out.println(file);
		
		extractVariables(file);
		extractLiterals(file);
		
	
	}
	
	/**
	 * Creates a set of reserved C++ keywords.
	 * Words that appear within this set are not recognized as valid class or variable names.
	 */
	private void createKeywordSet() 
	{
		String[] words= {"alignas","alignof","and","and_eq","asm","atomic_cancel","atomic_commit","atomic_noexcept","auto","bitand","bitor","break","case","catch","class","compl","concept","const","constexpr","const_cast","continue","co_await","co_return","co_yield","decltype","default","delete","do","double","dynamic_cast","else","enum","explicit","export","extern","false","for","friend","goto","if","import","inline(1)","module","mutable","namespace","new","noexcept","not","not_eqv","nullptr","operator","or","or_eq","private","protected","public","register","reinterpret_cast","requires","return","sizeof","static","static_assert","static_cast","struct","switch","synchronized","template","this","thread_local","throw","true","try","typedef","typeid","typename","union","virtual","volatile","while","xor","xor_eq","if","elif","else","endif","defined","ifdef","ifndef","define","undef","include","line","error","pragma","override","final","transaction_safe","transaction_safe_dynamic"};
		for(String word:words) {
			keywords.add(word);
		}

		keywords.add(";");
		keywords.add("(");
		keywords.add(")");
		keywords.add("{");
		keywords.add("}");
		keywords.add("[");
		keywords.add("]");
		keywords.add("||");
		keywords.add("&&");
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
		keywords.add(",");
		keywords.add(":");
		keywords.add("*");
		keywords.add("'");
		keywords.add("\'");
		keywords.add("!");
		keywords.add("<");
		keywords.add(">");
		
		
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
		//s = s.replaceAll("/\\*((?:.*\\r?\\n?)*)\\*/", "");
		//removes single-line comments in the format "//"
		//s = s.replaceAll("//.*\n", "");
		
		//Separate all special characters		
		s = s.replace("=", " = ");
		s = s.replace("(", " ( ");
		s = s.replace(")", " ) ");
		s = s.replace(";", " ; ");
		s = s.replace("{", " { ");
		s = s.replace("}", " } ");
		s = s.replace("[", " [ ");
		s = s.replace("]", " ] ");
		s = s.replace(":", " : ");
		s = s.replace("+", " + ");
		s = s.replace("\"", " \" ");
		s = s.replace("'", " ' ");
		s = s.replace("\'", " \' ");
		s = s.replace(",", " , ");
		s = s.replace("-", " - ");
		s = s.replace("/", " / ");
		s = s.replace("%", " % ");
		s = s.replace("*", " * ");	
		s = s.replace("\n", " \n ");
		s = s.replace("\t", " \t ");	
		s = s.replace("\r", "");
		s = s.replace("<", " < ");
		s = s.replace(">", " > ");
		
		//split strings		
		String[] temp=s.split(" ");
		ArrayList<String> words=new ArrayList<>(temp.length);
		//remove spaces
		for(String word:temp) {
			if(!word.equals("")) {
				words.add(word);
			}
		}
		//set in comment variable, significant symbol number and line number
		boolean inComment=false;
		int symbolID=0;
		int lineID=1;
		//set variable for final result
		String finalString="";
		//search through string
		for(int index=0;index<words.size();index++) {
			//if comment start switch to ignore
			if(words.get(index).equals("/")&&words.get(index+1).equals("*")) {
				inComment=true;
			}
			//if end of comment turn swithc off
			else if(words.get(index).equals("*")&&words.get(index+1).equals("/")) {
				inComment=false;
				index++;
			}
			//if line comment jump to end of line
			else if(words.get(index).equals("/")&&words.get(index+1).equals("/")) {
				while(!words.get(index).equals("\n")) {
					index++;
				}
				lineID++;
			}
			//if end of line increment line number
			else if(words.get(index).equals("\n")) {
				lineID++;
			}
			//if tab ignore
			else if(words.get(index).equals("\t"));
			//if we arent in comment add that string to the final and incremtn symbol ID
			else if(!inComment) {
				if(!words.get(index).equals(""))	{
					finalString+=words.get(index)+" ";
					symbolToLine.put(symbolID, lineID);
					symbolID++;
				}
			}	
		}
		
		return finalString.trim();
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
		SIT.notifyUser("Extracting Variables");
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
					variablesList.add(new Variable(words[i+1], words[i], scopes.toString(), symbolToLine.get(i)));
					
				}
				//look for multivaraible declaration on the same line
				int place=i+1;
				while(!words[place].equals(";")) {
					if(words[place].equals(",")&&isValidName(words[place+1])){
						variablesList.add(new Variable(words[place+1], words[i], scopes.toString(), symbolToLine.get(place+1)));
					}
					place++;
				}
			}
			//this takes care of varaibles that have instantiaed that take in generic types
			if(!keywords.contains(words[i]) && words[i+1].equals("<")) {
				int place=i+1;
				String type="";
				while(!words[place].equals(";")) {
					type+=words[place];
					if(words[place].equals(">")) {
						variablesList.add(new Variable(words[place+1], words[i]+type, scopes.toString(), symbolToLine.get(place)));
						break;
					}
					place++;
				}
			}
			//if we are statically declaring a list push a fake scope for consistancy
			if(words[i].equals("=")&&words[i+1].equals("{")) {
				scopes.push("");
			}
			//add a new scope if the keywords is found
			if(words[i].equals("class")||(!keywords.contains(words[i]) && !keywords.contains(words[i+1])&&words[i+2].equals("("))) 
			{
				int temp=i+1;
				while(!words[temp].equals(";")) {
					if(words[temp].equals("{")) {
						scopes.push(words[i+1]+"-"+scopeID);
						scopeID++;
						break;
					}
					temp++;
				}	
			}
			else if(words[i].equals("if")||words[i].equals("else")||words[i].equals("for")||words[i].equals("while")||words[i].equals("try")||words[i].equals("catch")) 
			{
				scopes.push(words[i]+"-"+scopeID);
				scopeID++;
			}
			//exit scope if character is found
			else if(!scopes.isEmpty()&&words[i].equals("}")) 
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
				Stack<String> temp = new Stack<>();
				//flag if the variable was found in scope
				boolean found = false;
				//if it is get the variable
				while(!found&&!scopes.isEmpty()) 
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
								assignment+=words[place]+" ";
								place++;
							}
							//add word to assignments
							v.getAssignments().put(symbolToLine.get(i), assignment.trim());
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
				temp = new Stack<>();
				//flag if the variable was found in scope
				found = false;
				//if it is get the variable
				while(!found&&!scopes.isEmpty()) 
				{
					for(Pointer v:pointersList) 
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
								assignment+=words[place]+" ";
								place++;
							}
							//add word to assignments
							v.getAssignments().put(symbolToLine.get(i), assignment.trim());
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
			else if(isValidName(words[i])&&words[i+1].equals("*")&&isValidName(words[i+2])&&!scopes.isEmpty()) 
			{
				int pos;
				pos = i;
				while(pos>0&&!words[pos].equals("="))
				{
					if(words[pos].equals("{")||words[pos].equals(";"))
					{
						pointersList.add(new Pointer(words[i+2],words[i],scopes.toString(),symbolToLine.get(i)));
						break;
					}
					pos--;
				}
			}
			else if(words[i].equals("delete")&&isValidName(words[i+1]))
			{
				
				
				//create stack to keep track of temporary scopes
				Stack<String> temp = new Stack<>();
				//flag if the variable was found in scope
				boolean found = false;
				//if it is get the variable
				while(!found&&!scopes.isEmpty()) 
				{
					for(Pointer v:pointersList) 
					{
						//if the variable name and scope match add the assignment
						if(v.getName().equals(words[i+1])&&v.getScope().equals(scopes.toString())) 
						{
							v.addToDeletion(i+1);
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
	
	/**
	 * Searches the file for a phrase and returns whether the file contains that phrase
	 * @param search A String to search the file for
	 * @return true if the file contains the search String
	 */
	public boolean fileContains(String search) {
		
			return fileContents.contains(search);	
	}

	/**
	 * This method loads a jar file containing all known SIT vulnerabilities
	 * The appropriate vulnerability is located within the jar, and its run function is called
	 * in order to analyze this file for that vulnerability.
	 * @param className The name of the vulnerability to locate within the jar file
	 * @return A list containing the line numbers in which the vulnerability was found
	 * @throws ClassNotFoundException
	 * @throws MalformedURLException
	 * @throws NoSuchMethodException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws InstantiationException
	 */
	private List<Integer> callVulnerability(String className) throws ClassNotFoundException, MalformedURLException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException
	{
		//Create a new URL class using the jarPath variable stored in Analyzer
		URL temp = new URL(jarPath);
		URL[] jar = {temp};
		
		//Load the jar file and find the correct vulnerability within this jar
		URLClassLoader jarLoader = new URLClassLoader(jar);
		Class c = jarLoader.loadClass(className);
		//Find the run method for the vulnerability
		Method m = c.getDeclaredMethod("run", Analyzer.class);
		return (List<Integer>) (m.invoke(c.newInstance(), this));
	}
	
	/**
	 * Parses a file, then runs known vulnerability algorithms against the file and
	 * reports any detected vulnerabilities to a Reporter class.
	 * @param filename The path of the file to analyze
	 */
	@Override
	protected void analyze(String filename) {
		//Parse the file for its variables
		parse(filename);
		SIT.notifyUser("Looking For Vulnerabilities");
		//Accost the CSV file and shake it down for its tasty vulnerabilities
		try {
			BufferedReader br = new BufferedReader(new FileReader(configPath));
			String config = br.readLine();	//ignore the CSV header
			
			while((config = br.readLine())!=null&&!config.equals(",,,,,"))	//If the current line of the CVS is not null
			{
				String[] fields = config.split(",");	//Split at the comma because CSV
				if(fields[2].equals("C++"))				//The "language" field must match ADA
				{
					SIT.notifyUser("Looking For "+fields[1]+"...");
					List<Integer> lines=callVulnerability(fields[5]);		//Field  is the name of the vulnerability's class
					if(lines.size()>0) {
						Report.addVuln(filename, "C++", fields[1], lines, fields[3], fields[4]);
					}		
				}
			}
		} catch (ClassNotFoundException e) {
			// 
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			
			e.printStackTrace();
		} catch (SecurityException e) {
			
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
		
			e.printStackTrace();
		} catch (InvocationTargetException e) {
		
			e.printStackTrace();
		} catch (InstantiationException e) {
			
			e.printStackTrace();
		}catch (FileNotFoundException e){
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public List<Variable> getVariables() {
		return variablesList;
	}
	public Set<String> getKeywords() {
		return keywords;
	}
	public List<String> getLiterals() {
		return literals;
	}
	public String getFileContents() {
		return fileContents;
	}
	public Map<Integer, Integer> getSymbolToLine() {
		return symbolToLine;
	}
	public List<Pointer> getPointersList() {
		return pointersList;
	}

	/**
	 * This internal class represents a Variable in the parsed source code
	 * @author Jamie Tyler Walder
	 *
	 */
	public class Variable{
		String name;
		String type;
		String scope;
		//Line on which this variable is created 
		int lineNumber;
		//line on which the assignment is followed by the reference
		Map<Integer,String> assignments;
		
		public Variable(String name, String type, String scope, int line) {
			super();
			this.name = name;
			this.type = type;
			this.scope = scope;
			this.lineNumber = line;
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
			return lineNumber;
		}
		public void setLine(int line) {
			this.lineNumber = line;
		}
		public Map<Integer, String> getAssignments() {
			return assignments;
		}
		public void setAssignments(Map<Integer, String> assignments) {
			this.assignments = assignments;
		}
		
		/**
		 * Returns a String containing the name, type, scope, symbolNumber, and assignments of the Variable
		 */
		@Override
		public String toString() {
			return "Variable [name=" + name + ", type=" + type + ", scope=" + scope + ", symbolNumber=" + lineNumber
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
	
	/**
	 * This internal class represents pointer variables and their scopes
	 * @author Jamie Tyler Walder
	 *
	 */
	public class Pointer extends Variable
	{
		List<Integer> deletions;

		public Pointer(String name, String type, String scope, int line)
		{
			super(name, type, scope, line);
			deletions = new LinkedList<Integer>();
		}
		public List<Integer> getDeletions() {
			return deletions;
		}

		public void setDeletions(List<Integer> deletions) {
			this.deletions = deletions;
		}
		
		public void addToDeletion(int symbolNumber)
		{
			deletions.add(symbolNumber);
		}
		
	}
	
	
	
}


