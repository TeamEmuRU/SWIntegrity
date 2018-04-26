// Example code that calls os commands
import java.lang.*;
import java.io.*;
class ProcessDemo
{
    public static void main(String arg[]) throws IOException, Exception
    {
        // creating the process
        Runtime r = Runtime.getRuntime();
         
        // shell script for loop from 1 to 3
        String[] nargs = {"sh", "-c", "for i in 1 2 3; do echo $i; done"};               
        Process p = r.exec(nargs);
     
        BufferedReader is = 
            new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line;
         
        // reading the output
        while ((line = is.readLine()) != null)
         
        System.out.println(line);
    }
}