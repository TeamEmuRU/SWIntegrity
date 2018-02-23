import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
/*This class is responsible for communicating to the user*/
public class Input {
	Scanner scanner;
	List<String> fileNames;
	BufferedReader br;
	FileReader fr;
	Reader reader;
	public Input() {
		fileNames=new LinkedList<>();
		reader=new Reader(this);
	}
	public void run() {
		scanner=new Scanner(System.in);
		boolean run=true;
		while(run) {
		System.out.println("Please input the file you would like to Analyze: ");
		 
		String fileName=System.getProperty("user.dir")+"\\"+scanner.nextLine();
		System.out.println(fileName);
		//TODO check to see if file is in directory
		if(reader.fileExists(fileName)) {
			fileNames.add(fileName);
		}
		
		System.out.println("Analyze?(y/n)");
		String response=scanner.nextLine();
		if(response.equals("y")) {
			reader.read(fileNames);
		}
		System.out.println("Exit?(y/n)");
		String exit=scanner.nextLine();
		if(exit.equals("y")) {
			run=false;
		}
		
		
		
		}
	}
	
	public boolean confirm(String message) {
		System.out.println(message+" (y/n)");
		return scanner.nextLine().equals("y");
		
	}
	public void notify(String message) {
		System.out.println(message);
		
	}
	
}	
