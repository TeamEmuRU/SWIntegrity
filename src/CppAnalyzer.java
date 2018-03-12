/**
 * The CppAnalyzer parses 
 * 
 * @author Abby Beizer
 */


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class CppAnalyzer extends Analyzer
{

	private List<String> variablesList;
	
	/**
	 * Default constructor
	 */
	public CppAnalyzer()
	{
		super();
		variablesList = new LinkedList<String>();
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
				
				//removes single-line comments in the format "//"
				line = line.replaceAll("//.*$", "");
				//removes single-line comments in the format "/* --- */"
				line = line.replaceAll("/\\*.*\\*/", "");
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
				else
				{
					//variables
					//conditional statements
				}
				
			
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
