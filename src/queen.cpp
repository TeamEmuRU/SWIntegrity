				
//**************************************
// Name: a Recursion : 8 Queens On a Chess Board
// Description:the program find a place to put queens on a chess board in any size ,using recursion.
// By: Alon Ben David (from psc cd)
//
// Side Effects:a great example of recursions .
//**************************************

/* this program made by alon ben david */
#include <stdio.h>
#define N 8
int Chess(char Arr[N][N] , int row);
int check(char Arr[N][N],int row,int line);
//double count;
int main()
{
char chess[N][N]={0};
	
Chess(chess,0);/* The call to the function*/
{
int i,y;
		
for(i=0;i<N;++i)/*prints the result*/
{
printf("\n\t\t\t");
for(y=0;y<N;++y)
{
if(chess[i][y]==0)
printf("x ");
else
printf("%c ",chess[i][y]);	
}
}
}
printf("\n");
}
int Chess(char Arr[N][N] , int row)
{
int line=0;
	
//printf("%f\n",count++);
if(row==N)
return 1;
		
while(line < N)
{
if(check(Arr,row,line)) /*check the row*/
{
Arr[row][line]='Q'; /*puts a queen on the board*/
if(Chess(Arr,row+1))/*the recursion*/
return 1;
Arr[row][line]=0;/*clears the last change if*/
}/*returned 0 from the recursion*/
line++;
}
return 0;
}
int check(char Arr[N][N],int row,int line)
{/*check just the left size of the board*/
int r,l;
	
r=row;
l=line;
while(r >= 0 && l >= 0)
{
if(Arr[r][l]=='Q')
return 0;
--r;
--l;
}
	
l=line;
r=row;
while(l < N && r >= 0)
{
if(Arr[r][l]=='Q')
return 0;
++l;
--r;
}
	
l=line;
r=row;
while(r >= 0)
{
if(Arr[r][l]=='Q')
return 0;
--r;	
}
return 1;
}