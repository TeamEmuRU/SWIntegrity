import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * The AdaAnalyzer parses Ada files for variable names and scopes, and runs the file against known vulnerabilities.
 * @author Abby Beizer
 * @author Jamie Tyler Walder
 *
 */

public class AdaAnalyzer extends Analyzer{
	Map<String,Variable> variables;
	Map<String,Variable> externalVariables;
	Map<String,Pointer> pointers;
	List<String> externalFunctionCalls;
	List<String> literals;
	//mapping from symbol to line lookup
	Map<Integer,Integer> symbolToLine;
	//symbols that are recognized as part of words
	Set<String> specialSymbols;
	Set<String> keyWords;
	Set<String> accessTypes;
	String rawCode;
	

	/**
	 * Constructor which creates lists necessary for collection of data.
	 */
	public AdaAnalyzer() {
		super();
		
		this.variables = new HashMap<>();
		this.literals = new LinkedList<>();
		this.symbolToLine = new HashMap<>();
		this.externalVariables=new HashMap<>();
		this.externalFunctionCalls=new LinkedList<>();
		//initialize lists of character and keywords
		String[] specialSymbols= {":",";",")","(","+","-","/","\\","*","\\\"","\n","\t","\r","=",".","\""};
		String[] keyWords= {"abort","else","new","return","abs","elsif","not","reverse",
				"abstract","end","null","accept","entry","select","access","exception","of",
				"separate","aliased","exit","or","subtype","all","others","synchronized","and",
				"for","out","array","function","overriding","tagged","at","task","generic","package",
				"terminate","begin","goto","pragma","then","body","private","type","if","procedure",
				"case","in","protected","until","constant","interface","use","is","raise","declare",
				"range","when","delay","limited","record","while","delta","loop","rem","with","digits",
				"renames","do","mod","requeue","xor"};
		//init sets and add the lists to the set for quicker lookup
		this.specialSymbols=new HashSet<>();
		this.keyWords=new HashSet<>();
		this.specialSymbols.addAll(Arrays.asList(specialSymbols));
		this.keyWords.addAll(Arrays.asList(keyWords));
		this.accessTypes=new HashSet<String>();
	}
	
	/**
	 * Clears all existing data from each list used by AdaAnalyzer's parsing functions
	 */
	private void clearAll() {
		this.variables = new HashMap<>();
		this.literals = new LinkedList<>();
		this.symbolToLine = new HashMap<>();
		this.externalVariables=new HashMap<>();
		this.externalFunctionCalls=new LinkedList<>();
		this.accessTypes=new HashSet<String>();
		
	}

	/**
	 * Override Analyzer.parse
	 * Opens a file and parses it for variable names
	 * @param filename The path of the file to parse
	 */
	@Override
	public void parse(String filename) {
		
		clearAll();
		String fileContents=openFile(filename);
		rawCode = fileContents;
		fileContents=flattenCodeAndMap(fileContents);
		extractVariables(fileContents);
	}
	
	/**
	 * Checks whether a word is an Ada reserved keyword or symbol 
	 * @param s Word to check
	 * @return false if the word is a valid variable name
	 */
	public boolean isVarName(String s) {
		for(String symbol:specialSymbols) {
			if(s.contains(symbol)||s.contains(" ")) {
				return false;
			}
		}
		if(Character.isDigit(s.charAt(0))||s.contains("__")) {
			return false;
		}
		return !keyWords.contains(s)&&!specialSymbols.contains(s);
	}
	
	/**
	 * Extracts variables and literals from a file
	 * @param file The path of the file to be parsed, must be flattened to work
	 */
	public void extractVariables(String file) {
		SIT.notifyUser("Extracting Variables");
		//split the file by spaces for easy manipulation
		String[] words=file.split(" ");
		//set a scope id to define scopes
		int scopeID=0;
		//set a stack to track scopes
		Stack<String> scopes=new Stack<>();
		//set a varaible to track if the extracter is in a literal
		boolean inLiteral=false;
		//set variable to build literal
		String literal="";
		//begin looking through each word
		for(int i=0;i<words.length;i++) {
			//check to see if this character is the begining or the end of a literal
			if(words[i].equals("\"")) {
				//switch the literal tracker on/off
				inLiteral=!inLiteral;
				//remove all extra qoutes in literal
				literal=literal.replace("\"", "");
				//ignore literal if it is empty
				if(!literal.equals("")) {
					//add literal to list
					literals.add(literal);
					literal="";
				}
			}
			//analyze words if not in literal
			if(!inLiteral) {
				//check to see if literal is a variable initialization
			if(i+2<words.length&&isVarName(words[i])&&words[i+1].equals(":")&&isVarName(words[i+2])) {
				if(!this.accessTypes.contains(words[i+2])) {
					variables.put(words[i]+scopes.toString(),new Variable(words[i],words[i+2],scopes.toString(),symbolToLine.get(i)));
				}
				else {
					pointers.put(words[i]+scopes.toString(),new Pointer(words[i],words[i+2],scopes.toString(),symbolToLine.get(i)));
				}
				int temp=i-1;
				while(!words[temp].equals("is")&&!words[temp].equals(";")) {
					if(words[temp].equals(",")&&isVarName(words[temp-1])) {
						if(!this.accessTypes.contains(words[i+2])) {
							variables.put(words[i]+scopes.toString(),new Variable(words[i],words[i+2],scopes.toString(),symbolToLine.get(i)));
						}
						else {
							pointers.put(words[i]+scopes.toString(),new Pointer(words[i],words[i+2],scopes.toString(),symbolToLine.get(i)));
						}
					}
					temp--;
				}
			}
			try {
				//check for the end of a scope and pop if so
				if (words[i].equals("end") || words[i].equals("else") || words[i].equals("elsif")) {
					scopes.pop();
				}
				//check to see if the word is the begining of an anonimous scope and add scope to stack
				if ((words[i].equals("if") || words[i].equals("else") || words[i].equals("elif") || words[i].equals("loop") || words[i].equals("case")) && !words[i + 1].equals(";")) {
					scopes.push(words[i] + "-" + scopeID);
					scopeID++;
				}
				//check to see if the word is the begining of a named scope and that scope to the stack
				if (words[i].equals("function") || words[i].equals("procedure") || words[i].equals("body")) {
					scopes.push(words[i + 1] + "-" + scopeID);
					scopeID++;
				}
			}
			catch(Exception e){}
			//look for external varialbes
			if(words[i].equals(".")&&isVarName(words[i+1])&&isVarName(words[i-1])&&!words[i+2].equals("(")&&!scopes.isEmpty()) {
				externalVariables.put(words[i-1]+words[i]+words[i+1],new Variable(words[i-1]+words[i]+words[i+1], null, null, symbolToLine.get(i)));
			}
			if(words[i].equals(".")&&isVarName(words[i+1])&&isVarName(words[i-1])&&words[i+2].equals("(")&&!scopes.isEmpty()) {
				externalFunctionCalls.add(words[i+1]);
			}
			//check for assignment
			if(words[i].equals(":")&&words[i+1].equals("=")) {
				//set stack to catch scopes f we need to 
				Stack<String> tempScope=new Stack<>();
				//flag for found variable
				boolean found=false;
				//keep looking until we find the variable
				while(!found&&!scopes.isEmpty()) {
				//check each variable
					Variable var=variables.get(words[i-1]+scopes.toString());
					if(var!=null) {
						//check each variable to see if this words is that variable
						if(var.getName().equals(words[i-1])&&var.getScope().equals(scopes.toString())) {
							//start loop to collect assignment
							int index=i+2;
							//set assigment to capture assignment
							String assignment="";
							//loop and collect until end of statement 
							while(!words[index].equals(";")) {
								assignment+=words[index]+" ";
								index++;
							}
							//add assignment to list and record it's line
							var.getAssignments().put(symbolToLine.get(i), assignment);
							//signify that we found it
							found=true;
						}
					}
					//back up a scope if we couldnt find the variable
					if(!found&&!scopes.isEmpty()) {
						tempScope.push(scopes.pop());
					}
				}
				while(!found&&!scopes.isEmpty()) {
					//check each variable
						Variable var=pointers.get(words[i-1]+scopes.toString());
						if(var!=null) {
							//check each variable to see if this words is that variable
							if(var.getName().equals(words[i-1])&&var.getScope().equals(scopes.toString())) {
								//start loop to collect assignment
								int index=i+2;
								//set assigment to capture assignment
								String assignment="";
								//loop and collect until end of statement 
								while(!words[index].equals(";")) {
									assignment+=words[index]+" ";
									index++;
								}
								//add assignment to list and record it's line
								var.getAssignments().put(symbolToLine.get(i), assignment);
								//signify that we found it
								found=true;
							}
						}
						//back up a scope if we couldnt find the variable
						if(!found&&!scopes.isEmpty()) {
							tempScope.push(scopes.pop());
						}
					}
				//revert the scopes if need be
				while(!tempScope.isEmpty()) {
					scopes.push(tempScope.pop());
				}
				//it must be an external variable then 
				if(!found) {
					Variable var= externalVariables.get(words[i-3]+words[i-2]+words[i-1]);
					if(var!=null) {
					int index=i+1;
					//set assigment to capture assignment
					String assignment="";
					//loop and collect until end of statement 
					while(!words[index].equals(";")) {
						assignment+=words[index]+" ";
						index++;
					}
					//add assignment to list and record it'sline
					
					var.getAssignments().put(symbolToLine.get(i), assignment);
					//signify that we found it
					found=true;
					}
				}
			}
			}
			else {
				//other wise since we're in a literal we add the word to the string
				literal+=words[i];
			}
			//check to see if an access type is declared
			if(i+4<words.length&&words[i].equalsIgnoreCase("type")&&isVarName(words[i+1])&&words[i+2].equalsIgnoreCase("is")&&words[i+3].equalsIgnoreCase("Access")) {
				this.accessTypes.add(words[i+1]);
			}
		}
	}
	
	/**
	 * Flattens code and records symbol's lines
	 * @param file The path of the file to flatten
	 * @return a string with no new lines, and with a space separating every symbol
	 */
	public String flattenCodeAndMap(String file) {
		//add spaces in between special symbols and other words
		for(String symbol:specialSymbols) {
			file=file.replace(symbol, " "+symbol+" ");
		}
		//remove tabs
		file=file.replaceAll("\t", "");
		//set string for the result of this method
		String result="";
		//set symbol id to keep track of symbols
		int symbolID=0;
		//set line id to keep track of lines
		int lineID=1;
		//set flag to track comments
		boolean inComment=false;
		//turn string into array
		String words[]=file.split(" ");
		//create array for non empty strings
		ArrayList<String> words2=new ArrayList<>(words.length);
		//remove spaces
		for(String word :words) {
			if(!word.equals(""))
				words2.add(word);
		}
		//go through each word
		for(int i=0;i<words2.size();i++) {
			//if it is a cooment begin to ignore this line
			if(words2.get(i).equals("-")&&words2.get(i+1).equals("-")) {
				inComment=true;
			}
			//when the end of the line is reached, turn the comment tracker off and increment line counter
			if(words2.get(i).equals("\n")) {
				lineID++;
				if(inComment)
					inComment=false;
			}
			//ignore \r if it is in a comment or if it is empty
			else if(words2.get(i).equals("\r"));
			else if(inComment);
			else if(words2.get(i).equals(""));
			//add word to result and track its line
			else {
				result+=words2.get(i)+" ";
				symbolToLine.put(symbolID, lineID);
				symbolID++;
			}
		}
		return result.trim();
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
				if(fields[2].equals("ADA"))				//The "language" field must match ADA
				{
					SIT.notifyUser("Looking For "+fields[1]+"...");
					List<Integer> lines=callVulnerability(fields[5]);		//Field  is the name of the vulnerability's class
					if(lines.size()>0) {
						Report.addVuln(filename, "Ada", fields[1], lines, fields[3], fields[4]);
					}
				}
			}	
		} catch (ClassNotFoundException e) {
			
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
			
			e.printStackTrace();
		}
	}
	
	
	public Map<String, Variable> getVariables() {
		return variables;
	}
	public Map<String, Variable> getExternalVariables() {
		return externalVariables;
	}
	public List<String> getExternalFunctionCalls() {
		return externalFunctionCalls;
	}
	public List<String> getLiterals() {
		return literals;
	}
	public Map<Integer, Integer> getSymbolToLine() {
		return symbolToLine;
	}
	public Set<String> getSpecialSymbols() {
		return specialSymbols;
	}
	public Set<String> getKeyWords() {
		return keyWords;
	}

	/**
	 * This internal class represents a Variable in the parsed source code
	 * @author Jamie Tyler Walder
	 *
	 */
	class Variable{
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
		
		/**
		 * Returns a String containing the name, type, scope, symbolNumber, and assignments of the Variable
		 */
		@Override
		public String toString() {
			return "Variable [name=" + name + ", type=" + type + ", scope=" + scope + ", symbolNumber=" + symbolNumber
					+ ", assignments=" + assignments +  "]";
		}
		
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
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
	}
	
	/**
	 * This internal class represents pointer variables and their scopes
	 * @author Jamie Tyler Walder
	 *
	 */
	private class Pointer extends Variable{
		List<Integer> deletions;
		public Pointer(String name, String type, String scope, int line) {
			super(name, type, scope, line);
			deletions=new LinkedList<>();
			
		}
		public List<Integer> getDeletions() {
			return deletions;
		}
		public void addDeletion(int symbol) {
			deletions.add(symbol);
		}
		
		
	}

}
