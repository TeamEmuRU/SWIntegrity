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
     
    // No more a dangling pointer
    //ptr = NULL;
}