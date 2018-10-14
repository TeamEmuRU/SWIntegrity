public class OverFlow {

    int a = 5000000;
    int b = 1000000;
    int c = 500;


    if ((long) (a + b) < Integer.MAX_VALUE) {
        c = a + b;
    }


    char j = 'a' + 'b';
    a = 10000;
    b = 10000;
    short x = 55 + 10000;
    byte z = 256 + 246;




    c = c + b;
}