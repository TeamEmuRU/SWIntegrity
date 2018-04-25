import java.util.LinkedList;
import java.util.List;

public class ReportTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		List<Integer> lines = new LinkedList<Integer>();
		lines.add(1);
		lines.add(5);
		lines.add(21);
		List<String> files=new LinkedList<>();
		files.add("f/er");
		files.add("f/hi");
		Report.filesAnalyzed(files);
		Report.addVuln("f/er", "ADA", "pickles", lines, "High", "fix it");
		Report.addVuln("f/hi", "C++", "cucumber", lines, "Medium", "don't touch it!");
		Report.writeReport();
		
	}

}
