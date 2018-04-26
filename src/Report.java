import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * This class created the Report text file that has all relevant information about the files that were analyzed
 * 
 * @author Sean Lawton & Jamie Tyler Walder
 */
public class Report {
	
	private static final Report instance = new Report();
	static List<Info> information = new LinkedList<Info>();
	static List<String> files = new LinkedList<String>();
	
	private Report()
	{
		
	}
	
	public static Report getInstance()
	{
		return instance;
	}
	
	/**
	 * This method creates the report text file and prints all relevant information about the files analyzed
	 */
	public static void writeReport() 
	{
		SIT.notifyUser("Saving Results to File...");
		new File("Reports").mkdirs(); 
		//acquires the time and date to put in the report text file
		long time=new Date().getTime();
		//Titles the report text file to display the time and date in the name
		String fileName = System.getProperty("user.dir")+"/Reports/"+time+"report.txt";
		//Puts the date and time into a format that is more recognizable
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		try
		{
			PrintWriter outputStream = new PrintWriter(fileName);//creates the different text lines printed
			outputStream.println("Software Integrity Tester Vulnerability Report");
			outputStream.println(" ");
			outputStream.println("SIT Version 1.0.0");//TODO should be pulled from config file
			outputStream.println("Date: " + dtf.format(now));
			outputStream.println(" ");
			outputStream.println("Files Analyzed: " + numberOfFiles());
			Iterator<String> itty = files.iterator();//this iterates through the list of all the files and prints each file name
			while(itty.hasNext())
			{
				outputStream.println("	" + itty.next());
			}
			outputStream.println(" ");
			outputStream.println("Warnings:");
			Iterator<String> iter = fileNames().iterator();
			while(iter.hasNext())
			{
				String temp = iter.next();
				outputStream.println("	" + temp);
				Iterator<Info> it = information.iterator();//this iterator is to get all the vulnerabilities found in a single file
				while(it.hasNext())
				{
					Info temp2 = it.next();
					if(temp2.getFile().equals(temp))//checks the individual Info classes in the information list to find each vulnerability in a file
					{
						outputStream.println("	" + temp2.getLang() + " " + temp2.getVuln() + "[" + temp2.getRisk() + "]: Lines " + temp2.getLines());
						outputStream.println("	Solution: " + temp2.getSolution());
						outputStream.println(" ");
					}
				}
			}
			outputStream.println("Summary:");
			for(String file: files)//prints each file name and the number of warnings found in each
			{
				outputStream.println("	" + file);
				if(riskPerFile(file)==1)
				{
					outputStream.println("		" + riskPerFile(file) + " Warning");
				}
				else
				{
					outputStream.println("		" + riskPerFile(file) + " Warnings");
				}
				outputStream.println(" ");
			}
			outputStream.println("Total Summary:");
			outputStream.println("	Files Analyzed: " + numberOfFiles());
			for(Entry<String, Integer> e:vulNumbers().entrySet())//prints the number of times each vulnerability occurs per language
			{
				outputStream.println("	Number of " + e.getKey() + " Occurrences: " + e.getValue());
			}
			HashMap<String,Integer> risk = riskNumbers();//prints the number of vulnerabilities at different risk levels 
			outputStream.println("	High Risk Warnings: " + risk.get("High") + "	Medium Risk Warnings: " + risk.get("Medium") + "	Low Risk Warnings: " + risk.get("Low"));
			outputStream.close(); 
			SIT.notifyUser("Saved Results to "+fileName);
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * adds files to a local list of files
	 */
	public static void filesAnalyzed(List<String> fileNames)
	{
		files = fileNames;
	}
	/**
	 * clears the information list of all Info classes
	 */
	public void clear()
	{
		information.clear();
	}
	/**
	 * @return number of files analyzed
	 */
	public static int numberOfFiles()
	{
		HashSet<String> files = new HashSet<String>();
		Iterator<Info> itty = information.iterator();
		while(itty.hasNext())
		{
			files.add(itty.next().getFile());
		}
		return files.size();
	}
	/**
	 * 
	 * @return HashSet containing the name of each file
	 */
	public static HashSet<String> fileNames()
	{
		HashSet<String> names = new HashSet<String>();
		Iterator<Info> itty = information.iterator();
		while(itty.hasNext())
		{
			names.add(itty.next().getFile());
		}
		return names;
	}
	/**
	 * @return HashMap with the Key being the Language and Vulnerability in the Info class from the infomation list and the Value being the number of times it occurs
	 */
	public static HashMap<String,Integer> vulNumbers()
	{
		HashMap<String,Integer> vulns = new HashMap<String,Integer>();
		Iterator<Info> itty = information.iterator();
		while(itty.hasNext())
		{
			Info temp = itty.next();
			if(vulns.containsKey(temp.getLang() + " " + temp.getVuln()))
			{
				vulns.put(temp.getLang() + " " + temp.getVuln(), vulns.get(temp.getLang() + " " + temp.getVuln())+1);//adds to the Value for each time this vulnerability is found
			}
			else
			{
				vulns.put(temp.getLang() + " " + temp.getVuln(), 1);//creates the entry in the HashMap for the vulnerability if its the first time its found
			}
		}
		return vulns;
	}
	/**
	 * @return HashMap with the Key being the level of risk of the vulnerability and the Value being the number of times a vulnerability of that level occurred
	 */
	public static HashMap<String,Integer> riskNumbers()
	{
		HashMap<String,Integer> risks = new HashMap<String,Integer>();
		Iterator<Info> itty = information.iterator();
		while(itty.hasNext())
		{
			Info temp = itty.next();
			if(risks.containsKey(temp.getRisk()))
			{
				risks.put(temp.getRisk(), risks.get(temp.getRisk())+1);
			}
			else  
			{
				risks.put(temp.getRisk(), 1);
			}
		}
		return risks;
	}
	/**
	 * @param file, the file name
	 * @return the number vulnerabilities found in the file
	 */
	public static int riskPerFile(String file)
	{
		int i = 0;
		for(Info inf: information)
		{
			if(inf.getFile().equals(file))
			{
				i++;
			}
		}
		return i;
	}
	/**
	 * @param file, the name of the file
	 * @param lang, the language the file was written in
	 * @param vuln, the vulnerability found in the file
	 * @param lines,list of line(s) the vulnerability were found on
	 * @param risk, level of risk the vulnerability has
	 * @param solution, the strategy that should be used to avoid this vulnerability
	 */
	public static void addVuln(String file, String lang, String vuln, List<Integer> lines, String risk, String solution) 
	{
		information.add(new Info(file, lang, vuln, lines, risk, solution));
	}
}
/**
 * class that holds the information relevant to the vulnerability found
 * 
 * @author Sean Lawton
 */
class Info 
	{
		String file;
		String lang;
		String vuln;
		List<Integer> lines;
		String risk;
		String solution;
	
	public Info(String file, String lang, String vuln, List<Integer> lines, String risk, String solution) {
			super();
			this.file = file;
			this.lang = lang;
			this.vuln = vuln;
			this.lines = lines;
			this.risk = risk;
			this.solution = solution;
		}
	public String getFile() {
		return file;
	}
	public String getLang() {
		return lang;
	}
	public String getVuln() {
		return vuln;
	}
	public List<Integer> getLines() {
		return lines;
	}
	public String getRisk() {
		return risk;
	}
	public String getSolution() {
		return solution;
	}

}
