public class OverFlow {

    public static void main(String args[]) {

    int a = 5000000;
    int b = 1000000;
    int c = 500;


    if((long)(a +b) <Integer.MAX_VALUE)

    {
        c = a + b;
    }


    char j = 'a' + 'b';

    c = c + b;
}
}