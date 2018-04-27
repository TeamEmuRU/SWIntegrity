import java.util.regex.*;

public class $est{
	public static void main(String[] args){
		Pattern Dealloca$torMethod = Pattern.compile("gr[ae]y");
		String currentLine = "The grey mouse";
		Matcher matcher = Dealloca$torMethod.matcher(currentLine);
		while(matcher.find()){
			System.out.println(matcher.group());
		}
	}
}