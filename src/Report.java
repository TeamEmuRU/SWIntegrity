import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 
 * @author Sean Lawton & Jamie Tyler Walder
 */
public class Report {
	
	private static final Report instance = new Report();
	static List<Info> information = new LinkedList<Info>();
	
	private Report()
	{
		
	}
	
	public static Report getInstance()
	{
		return instance;
	}
	
	public static void writeReport() 
	{
		String fileName = "report.txt";
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		try
		{
			PrintWriter outputStream = new PrintWriter(fileName);
			outputStream.println("Software Intgrity Tester Vulnerability Report");
			outputStream.println(" ");
			outputStream.println("SIT Version 1.0.0");
			outputStream.println("Date: " + dtf.format(now));
			outputStream.println(" ");
			outputStream.println("Files Analyzed:" + numberOfFiles());
			Iterator<String> itty = fileNames().iterator();
			while(itty.hasNext())
			{
				outputStream.println("	" + itty.next());
			}
			outputStream.println(" ");
			outputStream.println("Warnings:");
			Iterator<Info> iter = information.iterator();
			while(iter.hasNext())
			{
				Info temp = iter.next();
				outputStream.println("	" + temp.getFile());
				outputStream.println("	" + temp.getLang() + temp.getVuln() + "[" + temp.getRisk() + "]: Lines " + temp.getLines());
				outputStream.println("	Solution:" + temp.getSolution());
				outputStream.println(" ");
			}
			outputStream.println(" ");
			outputStream.println("Summary:");
			outputStream.println("	Files Analyzed:" + numberOfFiles());
			for(Entry<String, Integer> e:vulNumbers().entrySet()) {
				outputStream.println("	Number of " + e.getKey() + " Occurances: " + e.getValue())
			}
			outputStream.close();
			System.out.println("Done");
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
	}
	
	public void clear()
	{
		information.clear();
	}
	
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
	
	public static HashMap<String,Integer> vulNumbers()
	{
		HashMap<String,Integer> vulns = new HashMap<String,Integer>();
		Iterator<Info> itty = information.iterator();
		while(itty.hasNext())
		{
			Info temp = itty.next();
			if(vulns.containsKey(temp.getVuln()))
			{
				vulns.put(temp.getLang() + " " + temp.getVuln(), vulns.get(temp.getVuln()+1));
			}
			else
			{
				vulns.put(temp.getLang() + " " + temp.getVuln(), 1);
			}
		}
		return vulns;
	}
	
	public static void addVuln(String file, String lang, String vuln, List<Integer> lines, String risk, String solution) 
	{
		information.add(new Info(file, lang, vuln, lines, risk, solution));
	}
}
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
