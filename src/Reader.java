import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Reader {
	Input input;
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
			SWint.notifyUser("Files found: Java: "+javaFiles.size()+" Ada: "+adaFiles.size()+" c++: "+cppFiles.size()+" other: "+otherFiles.size());
		if(javaFiles.size()>0)
			openJava();
		if(adaFiles.size()>0)
			openAda();
		if(cppFiles.size()>0)
			openCpp();
		//TODO special case for other
		}
	public void openJava() {
		SWint.notifyUser(javaFiles.size()+" Java Files Found");
		for(String s:javaFiles) {
			String file=openFile(s);
			SWint.notifyUser(file); //for demonstartion purposes
			//TODO analyze
		}
		System.out.println("all java files read");
	}
	public void openAda() {
		SWint.notifyUser(adaFiles.size()+" Ada Files Found");
		for(String s:adaFiles) {
			String file=openFile(s);
			SWint.notifyUser(file);//for demonstartion purposes
			//TODO analyze
		}
		SWint.notifyUser("all ada files read");
	}
	public void openCpp() {
		SWint.notifyUser(cppFiles.size()+" C++ Files Found");
		for(String s:cppFiles) {
			String file=openFile(s);
			System.out.println(file);//for demonstartion purposes
			//TODO analyze
		}
		SWint.notifyUser("all c++ files read");
	}
	public void openOther() {
		List<String>validResponses=new ArrayList<String>();
		validResponses.add("-j");
		validResponses.add("-a");
		validResponses.add("-c");
		validResponses.add("-d");
		;
		String s;
		for(Iterator<String> itty=otherFiles.iterator();itty.hasNext();) {
			s=itty.next();
			String response=SWint.getResponse("What kindof file is "+s+"? -j for java, -a for ada, -c for c++ or -d to discard",validResponses);
			if(response.equals("-j")) {
				javaFiles.add(s);
			}
			else if(response.equals("-a")) {
				adaFiles.add(s);
			}
			else if(response.equals("-c")) {
				cppFiles.add(s);
			}
			itty.remove();

			
		}
	}

	private String openFile(String fileName) {
		try {
			
			br = new BufferedReader(new FileReader(fileName));
			String file="";
			String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null) {
				file+=sCurrentLine;
			}
			return file;


		}
		catch(FileNotFoundException e) {
			SWint.notifyUser(fileName+" not Found.");
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
			new FileReader(name);
			return true;
		}
		catch(Exception e) {
			SWint.notifyUser(name+" not found.");
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
				if(temp[1].equals("java")) {
					javaFiles.add(s);
				}
				else if(temp[1].equals("cpp")||temp[1].equalsIgnoreCase("cxx")||temp[1].equalsIgnoreCase("c")
						||temp[1].equalsIgnoreCase("cc")||temp[1].equalsIgnoreCase("c++")) {
					cppFiles.add(s);
				}
				else if(temp[1].equals("adb")||temp[1].equals("ada")) {
					adaFiles.add(s);
				}
				else {
					otherFiles.add(s);
				}	
				//TODO find a compiled list of extensions
			}
		}
	}

}
