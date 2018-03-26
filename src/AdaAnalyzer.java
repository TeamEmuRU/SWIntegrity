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
	Map<Integer,Integer> symbolToLine;
	//symbols that are recognized as part of words
	Set<String> specialSymbols;
	Set<String> keyWords;
	
	
	

	public AdaAnalyzer() {
		super();
		this.variables = new LinkedList<>();
		this.literals = new LinkedList<>();
		this.symbolToLine = new HashMap<>();
		String[] specialSymbols= {":",";",")","(","+","-","/","\\","*","\\\"","\n","\t","\r","=","."};
		String[] keyWords= {"abort","else","new","return","abs","elsif","not","reverse",
				"abstract","end","null","accept","entry","select","access","exception","of",
				"separate","aliased","exit","or","subtype","all","others","synchronized","and",
				"for","out","array","function","overriding","tagged","at","task","generic","package",
				"terminate","begin","goto","pragma","then","body","private","type","if","procedure",
				"case","in","protected","until","constant","interface","use","is","raise","declare",
				"range","when","delay","limited","record","while","delta","loop","rem","with","digits",
				"renames","do","mod","requeue","xor"};
		this.specialSymbols=new HashSet<>();
		this.keyWords=new HashSet<>();
		this.specialSymbols.addAll(Arrays.asList(specialSymbols));
		this.keyWords.addAll(Arrays.asList(keyWords));
		
	}
	@Override
	public void parse(String filename) {
		// TODO Auto-generated method stub
		String fileContents=openFile(filename);
		fileContents=flattenCodeAndMap(fileContents);
		extractVariables(fileContents);
	}
	public boolean isVarName(String s) {
		return !keyWords.contains(s)&&!specialSymbols.contains(s);
	}
	public void extractVariables(String file) {
		String[] words=file.split(" ");
		int scopeID=0;
		Stack<String> scopes=new Stack<>();
		for(int i=0;i<words.length;i++) {
			if(i+2<words.length&&isVarName(words[i])&&words[i+1].equals(":")&&isVarName(words[i+2])) {
				variables.add(new Variable(words[i],words[i+2],scopes.toString(),symbolToLine.get(i)));
			}
			if(words[i].equals("end")||words[i].equals("else")||words[i].equals("elsif")) {
				scopes.pop();
			}
			if((words[i].equals("if")||words[i].equals("else")||words[i].equals("elif")||words[i].equals("loop")||words[i].equals("case"))&&!words[i+1].equals(";")) {
				scopes.push(words[i]+scopeID);
				scopeID++;
			}
			if(words[i].equals("function")||words[i].equals("procedure")||words[i].equals("body")){
				scopes.push(words[i+1]+scopeID);
				scopeID++;
			}
		}
		System.out.println(variables);
	}
	public String flattenCodeAndMap(String file) {
		for(String symbol:specialSymbols) {
			file=file.replace(symbol, " "+symbol+" ");
		}
		file=file.replaceAll("\t", "");
		String result="";
		int symbolID=0;
		int lineID=1;
		boolean inComment=false;
		String words[]=file.split(" ");
		ArrayList<String> words2=new ArrayList<>(words.length);
		//remove spaces
		for(String word :words) {
			if(!word.equals(""))
				words2.add(word);
		}
		
		for(int i=0;i<words2.size();i++) {
			if(words2.get(i).equals("-")&&words2.get(i+1).equals("-")) {
				inComment=true;
			}
			if(words2.get(i).equals("\n")||words2.get(i).equals("\r")) {
				lineID++;
				if(inComment)
					inComment=false;
			}
			else if(inComment);
			else if(words2.get(i).equals(""));
			else {
				result+=words2.get(i)+" ";
				symbolToLine.put(symbolID, lineID);
				symbolID++;
			}
			//System.out.println(words[i]);
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
