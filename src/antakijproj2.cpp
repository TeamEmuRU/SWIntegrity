#include<pthread.h>
#include<stdio.h>

//create the global list and starting indices
enum {LIST_SIZE=10};
int first = 0;
int second = 5;
int list[LIST_SIZE] = {7, 12, 19, 3, 18, 4, 2, 6, 15, 8};
int final[LIST_SIZE];

/** Sorting algorithm to be called by threads **/
void *bubbleSort(void *startIndex)
{
	//create list boundaries
	int start = (int) startIndex;
	int end = start + ((LIST_SIZE/2) - 1);
	//sort
	int endOfUnsorted = end;
	while(endOfUnsorted != start)
	{
		for(int i=start; i<endOfUnsorted; i++)
		{
			if(list[i]>list[i+1])
			{
				int temp = list[i];
				list[i] = list[i+1];
				list[i+1] = temp;
			}
		}
	endOfUnsorted--;
	}
	printf("Sublist:\n");
	while(start <= end)
	{
		printf("%d\n", list[start]);
		start++;
	}
}

/** Parent thread that delegates work to threads and merges threads **/
int main(int argc, char *argv[])
{	
	//print unsorted list
	printf("Unsorted list:\n");
	for(int a=0; a<10; a++)
	{
		printf("%d\n", list[a]);
	}	

	//create thread objects
	pthread_t thread1, thread2;
	
	//start the threads
	pthread_create(&thread1, NULL, *bubbleSort, (void *) first);
	pthread_create(&thread2, NULL, *bubbleSort, (void *) second);
	//wait for the threads to finish
	pthread_join(thread1, NULL);
	pthread_join(thread2, NULL);

	//merge sublists into final sorted list
	int halfway = second - 1;
	int counter = 0;
	while(first <= halfway && second < LIST_SIZE)
	{
		if(list[first]<list[second])
		{
			final[counter] = list[first];
			first++;
		}
		else
		{
			final[counter] = list[second];
			second++;
		}
	counter++;
	}
	
	int beginning;
	int boundary;
	if(second == LIST_SIZE)
	{
		beginning = first;
		boundary = halfway;
	}
	else
	{
		beginning = second;
		boundary = LIST_SIZE-1;
	}
	for(int n = beginning; n<=boundary; n++)
	{
		final[counter] = list[n];
		counter++;
	}

	//print final list
	printf("Final list:\n");
	for(int j=0; j<10; j++)
	{
		printf("%d\n", final[j]);
	}
	return 0;
}
