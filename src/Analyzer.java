import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public abstract class Analyzer {
	private BufferedReader br;
	/**
	 * this method opens a file with a given file name and returns it's contents
	 * @param fileName the file you wish to open
	 * @return the contents of the file
	 */
	public String openFile(String fileName) {
		try {
			//use buffer reader to open the file
			br = new BufferedReader(new FileReader(fileName));
			//set an empty string to capture the contents of the file
			String file="";
			//set a variable to capture the individual lines of the project
			String sCurrentLine;
			//while the file has lines take those individual line and added to the total files content
			while ((sCurrentLine = br.readLine()) != null) {
				file+=sCurrentLine;
			}
			return file;


		}
		catch(FileNotFoundException e) {
			//if there was an error opening a file we notify the user
			SIT.notifyUser(fileName+" not Found.");
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
	



	}
	abstract public void parse(String filename);
	abstract protected void analyze(String filename);
}
