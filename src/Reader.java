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
	public Reader() {
		// TODO Auto-generated constructor stub
		adaFiles=new LinkedList<>();
		javaFiles=new LinkedList<>();
		cppFiles=new LinkedList<>();
		otherFiles=new LinkedList<>();
	}
	//important to input class only, is used to take the list of files given, sort them and notify the user of each file type
	public void read(List<String> filenames) {
		//sort files depending on their extension
		sortByType(filenames);

		if(input!=null)
			input.notify("Files found: Java: "+javaFiles.size()+" Ada: "+adaFiles.size()+" c++: "+cppFiles.size()+" other: "+otherFiles.size());
		else 
			System.out.println("Files found: Java: "+javaFiles.size()+" Ada: "+adaFiles.size()+" c++: "+cppFiles.size()+" other: "+otherFiles.size());
		if(javaFiles.size()>0)
			openJava();
		if(adaFiles.size()>0)
			openAda();
		if(cppFiles.size()>0)
			openCpp();
		//TODO special case for other



		


	}
	public void openJava() {
		for(String s:javaFiles) {
			String file=openFile(s);
			System.out.println(file); //for demonstartion purposes
			//TODO analyze
		}
		System.out.println("all java files read");
	}
	public void openAda() {
		for(String s:adaFiles) {
			String file=openFile(s);
			System.out.println(file);//for demonstartion purposes
			//TODO analyze
		}
		System.out.println("all ada files read");
	}
	public void openCpp() {
		for(String s:cppFiles) {
			String file=openFile(s);
			System.out.println(file);//for demonstartion purposes
			//TODO analyze
		}
		System.out.println("all c++ files read");
	}

	private String openFile(String fileName) {
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
			System.out.println("File not Found.");
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}



	}
	//checks file location to see if it exists
	public boolean fileExists(String name) {
		try {
			fr = new FileReader(name);
			return true;
		}
		catch(Exception e) {
			System.out.println("File not found.");
			return false;
		}

	}
	//opens a single file and returns a string that is the contents of the file
	public void openSingleFile(String name) {
		if(!fileExists(name)) {
			return;
		}
		List<String> files= new LinkedList<String>();

		files.add(name);
		//sorts file for analysis
		read(files);
	}
	//sorts all files into seperate types based on their extensions
	public void sortByType(List<String> filenames) {
		for (String s:filenames){
			String[] temp=s.split("\\.");
			if(temp.length>1) {
				if((temp[1].equals("java"))) {
					javaFiles.add(s);
				}
				else if((temp[1].equals("cpp"))) {
					cppFiles.add(s);
				}
				else if((temp[1].equals("adb"))) {
					adaFiles.add(s);
				}
				else {
					otherFiles.add(s);
				}	
			}
		}
	}

}
