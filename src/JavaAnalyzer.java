/**
 * The JavaAnalyzer parses java source code files by keeping a list of variables in use.
 *
 * @author Joseph Antaki
 * @author Abby Beizer
 * @author Jamie Tyler Walder
 */

import com.sun.org.apache.xpath.internal.operations.VariableSafeAbsRef;

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
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.HashMap;
import java.util.Map;

public class JavaAnalyzer extends Analyzer {
	private Set<Variable> variablesList;
	private Set<String> literalsList;
	private Set<String> typeList;
	private HashMap<String, ArrayList<Integer>> symbolToLine;
	private HashMap<String, HashMap<Integer, String>> someMap;
	private HashMap<Integer, String> lineToLine;
	private Pattern NON_ALPHA_NUMERIC;
	private int lineNumber = 1;
	private String fileContents;
	private String rawContents;


	/**
	 * The main constructor. Initializes list of variables.
	 */
	public JavaAnalyzer() {

		//instantiate map of variables
		variablesList = new HashSet<>();
		literalsList = new HashSet<>();
		typeList = new HashSet<>();
		symbolToLine = new HashMap<>();
		someMap = new HashMap<>();
		lineToLine = new HashMap<>();
		createTypeList();
		//Create a regex pattern to catch special characters
		String regex = "[\\W&&\\S]";
		NON_ALPHA_NUMERIC = Pattern.compile(regex);
	}

	private void createTypeList(){
		typeList.add("int");
		typeList.add("char");
		typeList.add("boolean");
		typeList.add("short");
		typeList.add("byte");
		typeList.add("long");
		typeList.add("float");
		typeList.add("double");
	}

	/**
	 * Returns the set of variables.
	 *
	 * @return the set of variables.
	 */
	public Set<Variable> getVariables() {
		return variablesList;
	}

	/**
	 * Returns the set of String literals.
	 *
	 * @return the set of String literals .
	 */
	public Set<String> getLiteralsList() {
		return literalsList;
	}

	/**
	 * Reads an array of characters, extracts String literals represented inside, and stores them.
	 *
	 * @param arr an array of characters
	 */
	private void extractLiterals(char[] arr) {
		boolean inString = false;
		String literal = "";
		for (int i = 0; i < arr.length; i++) {
			if (arr[i] == '\"') {
				inString = !inString;
				if (literal.length() != 0) {
					literalsList.add(literal);
					literal = "";
				}
			} else if (inString) {
				literal += arr[i];
			}
		}
	}

	/**
	 * Pads non-alphanumeric characters with spaces to make the code easier to work with
	 *
	 * @param s the line of code to format
	 * @return the formatted line of code
	 */
	public String flattenCode(String s) {
		//Remove all whitespace characters that are not regular spaces
		String finalString = s.trim().replaceAll("[\\n|\\t|\\r|\\f]", "");
		//Separate all special characters
		Matcher match = NON_ALPHA_NUMERIC.matcher(finalString);
		Pattern alpha = Pattern.compile("\\.?\\w+");
		Matcher mm = alpha.matcher(finalString);
		while(mm.find()){
			if(symbolToLine.get(mm.group()) == null){
				ArrayList<Integer> temp = new ArrayList<>();
				temp.add(lineNumber);
				symbolToLine.put(mm.group(), temp);
			}
			else{
				symbolToLine.get(mm.group()).add(lineNumber);
			}
		}
		String found = "";
		while (match.find()) {
			found = match.group();
			finalString = finalString.replace(found, " " + found + " ");
		}
		//Create an array of all words separated by a space
		String words[] = finalString.split(" ");
		//Transform the array into an ArrayList
		ArrayList<String> list = new ArrayList<>(Arrays.asList(words));
		Iterator<String> itty = list.iterator();
		String result = "";
		while (itty.hasNext()) {
			String word = itty.next();
			//Remove any blank words in the ArrayList
			if (!word.equals("")) {
				result += word + " ";
			}
		}
		return result;
	}


	/**
	 * Extracts variable names from a line of code
	 *
	 * @param s The line of code to extract variables from
	 */
	public void extractVariables(String s, int linenum) {

		s = s.replace(" \\ t", "");
		//Create an array of all words separated by a space
		String words[] = s.split(" ");
		//Transform the array into an ArrayList
		ArrayList<String> list = new ArrayList<>(Arrays.asList(words));
		//start iterator
		Iterator<String> itty = list.iterator();
		//set a switch to keep track of when we are in a string.
		boolean stringSwitch = false;
		HashMap<Integer, String> assignments = new HashMap<Integer, String>();
		//the following loop removes Strings. In order to detect variables strings must not be present
		while (itty.hasNext()) {
			String word = itty.next();
			//if we run into the beginning of a string we turn on the switch and remove the quote character, this happens again at the end of the String
			if (word.equals("\"")) {
				stringSwitch = !stringSwitch;
				itty.remove();
			}
			//while inside of a string we remove it
			else if (stringSwitch && !word.equals("\"")) {
				itty.remove();
			}

		}
		//put words back into an array
		words = list.toArray(new String[list.size()]);

		int scopeID = 0;	//a scope id to differentiate scopes
		String className = "[A-Z_$][\\w$]*";
		String variableName = "[\\w$]+";
		int scope = 0;
		for(int i = 0; (i <= words.length - 4); i++) {
			Variable v = null;
			String name = "";
			String type = "";
			String assignment = "";
			if(words[i].equals("{")){
				scope++;
			}
			else if(words[i].equals("}")){
				scope--;
			}
			else{
				if((typeList.contains(words[i]) || words[i].matches(className)) && words[i+1].matches(variableName) &&  (words[i+2].equals(";") || words[i+2].equals("="))){

					name = words[i+1];
					type = words[i];
					if(words[i+2].equals("=")){
						int place = i+3;
						while(place<words.length&&place>-1&&!(words[place].equals(";")) && !(words[place] == null)) {
							assignment += words[place] + " ";
							place++;
						}

					}

					if ((v = containsVarName(name)) == null) {
						v = new Variable(name, type, Integer.toString(scope), linenum);
					}
					else {
						v.getAssignments().put(linenum ,assignment);
					}
				}
				else if((typeList.contains(words[i]) || words[i].matches(className)) && words[i+1].matches(variableName) && words[i+2].equals("[") && words[i+3].equals("]") && (words[i+4].equals("=") || words[i+4].equals(";"))){

					name = words[i+1];
					type = words[i] + "[]";
					if(words[i+4].equals("=")){
						int place = i+5;
						while(!(words[place].equals(";")) && !(words[place] == null)){
							assignment += words[place] + " ";
							place++;
						}

						if ((v = containsVarName(name)) == null) {
							v = new Variable(name, type, Integer.toString(scope), linenum);
						}
						else {
							v.getAssignments().put(linenum ,assignment);
						}
					}
				}
				else if((typeList.contains(words[i]) || words[i].matches(className)) && words[i+1].equals("[") && words[i+2].equals("]") && words[i+3].equals(variableName) && (words[i+4].equals("=") || words[i+4].equals(";"))){

					name = words[i+3];
					type = words[i] + "[]";
					if(words[i+4].equals("=")){
						int place = i+5;
						while(!(words[place].equals(";")) && !(words[place] == null)){
							assignment += words[place] + " ";
							place++;
						}

						if ((v = containsVarName(name)) == null) {
							v = new Variable(name, type, Integer.toString(scope), linenum);
						}
						else {
							v.getAssignments().put(linenum ,assignment);
						}

					}

				}
				else if (varEq(words[i])) {
					if(words[i+1].contains("=") || words[i+1].equals("=")){
						int place = i+2;
						while(place<words.length&&place>-1&&!(words[place].equals(";")) && !(words[place] == null)) {
							assignment += words[place] + " ";
							place++;
						}
						for (Variable vr: variablesList) {
							if (vr.getName().equals(words[i])) {
								v = vr;
								break;
							}
						}
						v.getAssignments().put(linenum,assignment.trim().replaceAll(";","") );
						return;
					}
				}
				else if((typeList.contains(words[i]) || words[i].matches(className)) && words[i+1].equals("<")){
					int place = i+2;
					String rest = "";
					while(!(words[place].equals(">"))){
						rest += words[place];
						place++;
					}
					name = words[++place];
					type = words[i] + "<" + rest + ">";
					if(words[++place].equals("=")){
						place++;
						while(!(words[place].equals(";"))){
							assignment += words[place] + " ";
						}

						if (someMap.size() == 0) {
							someMap.put(name, new HashMap<>());
						}
						for (String vv: someMap.keySet()) {
							if (vv.trim().equals(name.trim())) {
								someMap.get(vv).put(linenum, assignment);
							}
							else {
								someMap.put(vv, new HashMap<>());
							}
						}
					}
				}

				if(!(name.equals(""))){

					if (v == null)
						v = new Variable(name, type, Integer.toString(scope), i);
					variablesList.add(v);
				}
			}

		}
	}

	private boolean varEq(String s) {
		for (Variable v : variablesList) {
			if (v.getName().equals(s))
				return true;
		}
		return false;
	}
	/**
	 * Clears all lists of information to prepare the JavaAnalyzer for analyzing the next file.
	 */
	public void clearAll(){
		variablesList.clear();
		literalsList.clear();
		symbolToLine.clear();
		lineNumber = 1;
		fileContents = "";
		rawContents = "";
	}
	/**
	 * Removes comments and extracts variables from source code.
	 * @param filename the file to be parsed
	 */
	@Override
	public void parse(String filename) {
		clearAll();
		StringBuilder fileBuilder = new StringBuilder();
		StringBuilder rawBuilder = new StringBuilder();
		String line = "";
		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			String[] cleanLine = null;
			boolean mComment = false;    //whether the current line is a continuation of a multi-line comment

			//Runs until end of file
			while ((line = br.readLine()) != null) {
				rawBuilder.append(line); //build up our raw representation of the file
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
						lineNumber++;
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
					lineNumber++;
					continue;
				}

				/* Second, extract the literals and variables: */
				extractVariables(line.trim(), lineNumber);
				extractLiterals(line.toCharArray());
				lineToLine.put(lineNumber, line.trim().replace("null", ""));
				line = flattenCode(line);
				fileBuilder.append(line);
				lineNumber++;
			}
			br.close();
			this.rawContents = rawBuilder.toString();
		} catch (FileNotFoundException fnf) {    //from FileReader
			SIT.notifyUser("Error: File " + filename + " could not be parsed.");
		} catch (IOException io) {    //from BufferedReader
			SIT.notifyUser("Error reading the contents of " + filename + ".");
		}
		fileContents=fileBuilder.toString();
		//temporary fix
		Iterator<Variable> it = variablesList.iterator();
		Matcher m;
		while (it.hasNext()) {
			Variable element = it.next();
			m = NON_ALPHA_NUMERIC.matcher(element.getName());
			if (m.find()) {
				it.remove();
			}
		}
	}

	private Variable containsVarName(String s) {
		for (Variable v: variablesList) {
			if (v.getName().equals(s)) {
				return v;
			}
		}
		return null;
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
				if(fields[2].equals("JAVA"))				//The "language" field must match ADA
				{
					SIT.notifyUser("Looking For "+fields[1]+"...");
					List<Integer> lines=callVulnerability(fields[5]);		//Field  is the name of the vulnerability's class
					if(lines.size()>0) {
						Report.addVuln(filename, "Java", fields[1], lines, fields[3], fields[4]);
					}
				}
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (FileNotFoundException e){
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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


	public Set<String> getTypeList() {
		return typeList;
	}

	public HashMap<String, ArrayList<Integer>> getSymbolToLine() {
		return symbolToLine;
	}

	public Pattern getNON_ALPHA_NUMERIC() {
		return NON_ALPHA_NUMERIC;
	}

	public String getFileContents() {
		return fileContents;
	}
	public String getRawContents(){
		return rawContents;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	/**
	 * This internal class represents a Variable in the parsed source code
	 * @author Jamie Tyler Walder
	 *
	 */
	public class Variable {
		String name;
		String type;
		String scope;
		//Line on which this variable is created
		int lineNumber;
		//line on which the assignment is followed by the reference
		HashMap<Integer, String> assignments;

		public Variable(String name, String type, String scope, int line) {
			super();
			this.name = name;
			this.type = type;
			this.scope = scope;
			this.lineNumber = line;
			this.assignments = new HashMap<>();
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

		public HashMap<Integer, String> getAssignments() {
			return assignments;
		}

		public void setAssignments(HashMap<Integer, String> assignments) {
			this.assignments = assignments;
		}

		/**
		 * Returns a String containing the name, type, scope, symbolNumber, and assignments of the Variable
		 */
		@Override
		public String toString() {
			return "Variable [name=" + name + ", type=" + type + ", scope=" + scope + ", symbolNumber=" + lineNumber
					+ ", assignments=" + assignments + "]";
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

		private JavaAnalyzer getOuterType() {
			return JavaAnalyzer.this;
		}
	}

	public HashMap<Integer, String> getLineToLine() {
		return lineToLine;
	}

	public String getUntilLine(int line) {
		String code = "";
		for (int i = 0; i <= line; i++) {
			if (lineToLine.get(i) != null)
				code += lineToLine.get(i);
		}
		return code;
	}

	public String getBetweenLines(int l1, int l2) {
		String temp = "";

		for (int i = l1; i <= l2; i++) {
			if (lineToLine.get(i) != null)
				temp += lineToLine.get(i);
		}
		return temp;
	}
	public HashMap<String, HashMap<Integer, String>> getSomeMap() {
		return someMap;
	}
}