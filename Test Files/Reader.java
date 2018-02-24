import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Reader {
	Input input;
	private FileReader fr;
	private BufferedReader br;
	private List<String> adaFiles;
	private List<String> javaFiles;
	private List<String> cppFiles;
	private List<String> otherFiles;
	public Reader(Input input) {
		// TODO Auto-generated constructor stub
		this.input=input;
		adaFiles=new LinkedList<>();
		javaFiles=new LinkedList<>();
		cppFiles=new LinkedList<>();
		otherFiles=new LinkedList<>();
	}
	public void read(List<String> filenames) {
		//sort files depending on their extension
		for (String s:filenames){
			if((s.split("."))[1].equals("java")) {
				javaFiles.add(s);
			}
			else if((s.split("."))[1].equals("cpp")) {
				cppFiles.add(s);
			}
			else if((s.split("."))[1].equals("adb")) {
				adaFiles.add(s);
			}
			else {
				otherFiles.add(s);
			}
		}
		
		
		//TODO analyze each file
			
		
	}
	private String openFile(String fileName) {
		// TODO Auto-generated method stub
		try {
			fr = new FileReader(fileName);
			br = new BufferedReader(fr);
			String file="";
			String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null) {
				file+=sCurrentLine;
			}
			return file;

			
			}
		catch(FileNotFoundException e) {
			input.notify("File not Found");
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		
	}

}
