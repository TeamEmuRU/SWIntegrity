import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

public class AdaAnalyzer extends Analyzer{
	List<Variable> variables;
	List<String> literals;
	//mapping from symbol to line lookup
	Map<Integer,Integer> symbolToLine;
	//symbols that are recognized as part of words
	Set<String> specialSymbols;
	Set<String> keyWords;
	
	
	
	/**
	 * constructor for ada analyzer class, it creates the lists necessary for collection of data
	 */
	public AdaAnalyzer() {
		super();
		
		this.variables = new LinkedList<>();
		this.literals = new LinkedList<>();
		this.symbolToLine = new HashMap<>();
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
		
	}
	@Override
	/**
	 * parses file for important information like variables and literals
	 * 
	 */
	public void parse(String filename) {
		// TODO Auto-generated method stub
		String fileContents=openFile(filename);
		fileContents=flattenCodeAndMap(fileContents);
		extractVariables(fileContents);
	}
	/**
	 * checks if words is keyword or symbol 
	 * @param s word to check
	 * @return if word is a proper variable name
	 */
	public boolean isVarName(String s) {
		return !keyWords.contains(s)&&!specialSymbols.contains(s);
	}
	/**
	 * pulls variables and literals from file
	 * @param file file to be analyzed, must be flattened to work
	 */
	public void extractVariables(String file) {
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
				variables.add(new Variable(words[i],words[i+2],scopes.toString(),symbolToLine.get(i)));
			}
			//check for the end of a scope and pop if so
			if(words[i].equals("end")||words[i].equals("else")||words[i].equals("elsif")) {
				scopes.pop();
			}
			//check to see if the word is the begining of an anonimous scope and add scope to stack
			if((words[i].equals("if")||words[i].equals("else")||words[i].equals("elif")||words[i].equals("loop")||words[i].equals("case"))&&!words[i+1].equals(";")) {
				scopes.push(words[i]+"-"+scopeID);
				scopeID++;
			}
			//check to see if the word is the begining of a named scope and that scope to the stack
			if(words[i].equals("function")||words[i].equals("procedure")||words[i].equals("body")){
				scopes.push(words[i+1]+"-"+scopeID);
				scopeID++;
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
					for(Variable var:variables) {
						//check each variable to see if this words is that variable
						if(var.getName().equals(words[i-1])&&var.getScope().equals(scopes.toString())) {
							//start loop to collect assignment
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
					//back up a scope if we couldnt find the variable
					if(!found&&!scopes.isEmpty()) {
						tempScope.push(scopes.pop());
					}
					
				}
				//revert the scopes if need be
				while(!tempScope.isEmpty()) {
					scopes.push(tempScope.pop());
				}
			}
			}
			else {
				//other wise since we're in a literal we add the word to the string
				literal+=words[i];
			}
		}
		System.out.println(variables);
		System.out.println(literals);
	}
	/**
	 * flattens code and records symbol's lines
	 * @param file file to flatten
	 * @return a string with no new lines with a space seperating every symbol
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
		
		
		return result;
	}

	@Override
	protected void analyze(String filename) {
		// TODO Auto-generated method stub
		
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

}
