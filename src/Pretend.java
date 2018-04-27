import java.util.ArrayList;

public class Pretend {

    //@Override
    public static void main(String[] args) {
        //AdaAnalyzer aana = (AdaAnalyzer) ana;
        ArrayList<Integer> lineNumbers = new ArrayList<>();
        /*
         * Case 1: ensuring relative path traversal cannot be used maliciously.
         * If the programmer is checking for and removing the suspicious string "../" or "..\" (a blacklisting technique)
         * then they are most likely not using a whitelist, which is more secure.
         * Even if "../" or "..\" removal is somehow being used in tandem with a whitelist, the string removal is easily circumvented
         * by an attacker inputting the string ".../...//", which resolves to "../". Thus, the string removal is unnecessary.
         */
        String contents = "public class Test { public static void main ( String [ ] args ) { String gotIt = takeUserInput ( ) ; if ( gotIt . contains ( \" . . / \" ) ) { System . out . println ( \" no you cant do that \" ) ; } if ( gotIt . contains ( \" / \" ) | | gotIt . contains( \" \\ \" ) ) { System . out . println ( \" dumbass \" ) } } }";
        String[] temp = contents.split(" ");
        //Map<Integer, Integer> stl = new HashMap<>(aana.getSymbolToLine());

         for(int i=0; i<temp.length-2; i++){
            if((temp[i].equals(".") && temp[i+1].equals(".")) && (temp[i].equals("/") || temp[i].equals("\\"))) {
                lineNumbers.add(contents.indexOf(temp[i]));
            }
        }
        /*
         * Case 2: ensuring absolute path traversal cannot be used maliciously
         * If the programmer is checking user input for "/" or "\" to prevent the user from specifying
         * an arbitrary directory (a blacklisting technique) then they are likely not using a whitelist, which is more secure.
         */
        //checking if "/" or "\" is being specifically sought out as a String, ensuring we're not catching other slashes that may be in the code
        //if(aana.getLiterals().contains("/") || aana.getLiterals().contains("\\")){
            for(int i=0; i<temp.length; i++){
                if((contents.charAt(i) == '.' && contents.charAt(i+1) == '.') && (contents.charAt(i+2) == '/' || contents.charAt(i+2) == '\\')) {
                    lineNumbers.add(contents.indexOf(temp[i]));
		    System.out.println(temp[i]);
                }
            }
        
        System.out.println(lineNumbers);
    }
}