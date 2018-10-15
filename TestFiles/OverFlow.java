public class OverFlow {


    public static int add(int a, int b) {
        return a + b;
    }
    public static void main(String args[]) {

    int a = 5000000;
    int b = 1000000;
    int c = 500;


    if((long)(a +b) <Integer.MAX_VALUE)

    {
        c = a + b;
    }

    int someInt = add(a, b);
    char j = 'a' + 'b';

    c = c + b;
}
}