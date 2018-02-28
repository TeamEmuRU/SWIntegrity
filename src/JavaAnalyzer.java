
public class JavaAnalyzer extends Analyzer {

	@Override
	public void parse(String filename) {
		// TODO Auto-generated method stub
		String fileContents = openFile(filename);
		SIT.notifyUser(fileContents);
	}

	@Override
	protected void analyze(String filename) {
		// TODO Auto-generated method stub
		parse(filename);
	}

}
