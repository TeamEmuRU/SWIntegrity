import java.util.regex.*;

public class Test{
	public static void main(String[] args){
		String meme = "Character";
		Pattern pat = Pattern.compile("[A-Z_$][\\w$]*");
		Matcher match = pat.matcher(meme);
		if(match.matches()){
			System.out.println(match.group());
		}
	}
}