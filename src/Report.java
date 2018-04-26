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
	
	public static void writeReport() 
	{
		SIT.notifyUser("Saving Results to File...");
		new File("Reports").mkdirs(); 
		long time=new Date().getTime();
		
		String fileName = "Reports/"+time+"report.txt";
		
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		try
		{
			PrintWriter outputStream = new PrintWriter(fileName);
			outputStream.println("Software Intgrity Tester Vulnerability Report");
			outputStream.println(" ");
			outputStream.println("SIT Version 1.0.0");//TODO should be pulled from config file
			outputStream.println("Date: " + dtf.format(now));
			outputStream.println(" ");
			outputStream.println("Files Analyzed: " + numberOfFiles());
			Iterator<String> itty = files.iterator();
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
				Iterator<Info> it = information.iterator();
				while(it.hasNext())
				{
					Info temp2 = it.next();
					if(temp2.getFile().equals(temp))
					{
						outputStream.println("	" + temp2.getLang() + " " + temp2.getVuln() + "[" + temp2.getRisk() + "]: Lines " + temp2.getLines());
						outputStream.println("	Solution: " + temp2.getSolution());
						outputStream.println(" ");
					}
				}
			}
			outputStream.println("Summary:");
			for(String file: files)
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
			for(Entry<String, Integer> e:vulNumbers().entrySet()) {
				outputStream.println("	Number of " + e.getKey() + " Occurrences: " + e.getValue());
			}
			HashMap<String,Integer> risk = riskNumbers();
			outputStream.println("	High Risk Warnings: " + risk.get("High") + "	Medium Risk Warnings: " + risk.get("Medium") + "	Low Risk Warnings: " + risk.get("Low"));
			outputStream.close(); 
			SIT.notifyUser("Saved Results to Reports\\"+time+"report.txt");
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
	}
	
	public static void filesAnalyzed(List<String> fileNames)
	{
		files = fileNames;
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
	
	public static HashMap<String,Integer> riskNumbers()
	{
		HashMap<String,Integer> risks = new HashMap<String,Integer>();
		Iterator<Info> itty = information.iterator();
		while(itty.hasNext())
		{
			Info temp = itty.next();
			if(risks.containsKey(temp.getVuln()))
			{
				risks.put(temp.getRisk(), risks.get(temp.getRisk()+1));
			}
			else
			{
				risks.put(temp.getRisk(), 1);
			}
		}
		return risks;
	}
	
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
