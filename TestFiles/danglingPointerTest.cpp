// Deallocating a memory pointed by ptr causes
// dangling pointer
#include <stdlib.h>
#include <stdio.h>
int main()
{
    int *ptr = new int[10];
 
    // After below free call, ptr becomes a 
    // dangling pointer
    delete ptr; 
    static const std::list<std::string> myList = {"a", "b", "c"};
    int i=2;
    int x,y=0,z;
     
    // No more a dangling pointer
    //ptr = NULL;
}
