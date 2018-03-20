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
	//make sure to remake to private
	List<String> variablesList;
	private Set<String> keywords;
	
	
	
	
	/**
	 * Default constructor
	 */
	public CppAnalyzer()
	{
		super();
		variablesList = new LinkedList<String>();
		keywords=createKeywordSet();
	}
	
	private Set<String> createKeywordSet() {
		// TODO Auto-generated method stub
		Set<String> keywords=new HashSet<>();
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
		
		
		return keywords;
	}
	public String flattenCode(String s) {
		String finalString=s.trim().replace("\n", "").replace("\t","").replaceAll("\r", "");
				
		finalString=finalString.replace("=", " = ");
		finalString=finalString.replace("(", " ( ");
		finalString=finalString.replace(")", " ) ");
		finalString=finalString.replace(";", " ; ");
		finalString=finalString.replace("{", " { ");
		finalString=finalString.replace("}", " } ");
		finalString=finalString.replace("[", " [ ");
		finalString=finalString.replace("]", " ] ");
		finalString=finalString.replace(":", " : ");
		finalString=finalString.replace(":", " : ");
		finalString=finalString.replace("\"", " \" ");
		finalString=finalString.replace(",", " , ");
		
		
		
		return finalString;
	}
	
	public boolean isCodeBreak(char c) {
		return c=='{'||c=='}'||c==';'||c==':'||c=='=';
	}
	public String removeExtras(String s) {
		return s.replace(")", "");
	}
	public boolean isValidVarName(String name) {
		if(keywords.contains(name))
			return false;
		

		return true;
	}
	public boolean isValidClassName(String name) {
		if(keywords.contains(name))
			return false;
		return true;
	}
	public void extractVariables(String s) {
		String words[]=s.split(" ");
		ArrayList<String> list=new ArrayList<>(Arrays.asList(words));
		Iterator<String> itty=list.iterator();
		boolean stringSwitch=false;
		while(itty.hasNext()) {
			String word=itty.next();
			if(word.equals(""))
				itty.remove();
			else if(word.equals("\"")) {
				stringSwitch=!stringSwitch;
				itty.remove();
			}
			else if(stringSwitch&&!word.equals("\"")) {
				itty.remove();
			}
			
		}
		
		words=list.toArray(new String[list.size()]);
		for(int i=0;i<words.length-2;i++) {
			if(!keywords.contains(words[i])&&!keywords.contains(words[i+1])) {
				if((!words[i+2].contains("("))&&isValidClassName(words[i])&&isValidVarName(words[i+1])) {
					variablesList.add(words[i+1]+":"+words[i]);
				}
			}
		}
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
