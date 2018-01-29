#include <cstdio>
#include <iostream>
#include <string>
#include <fstream>
#include <streambuf>
#include <time.h>
#include "AdaWeaknesses.h"

#define SEPERATOR "---------------------------------\n"
#define VERSION "1.0.0.0"

using namespace std;

void runTester();
void selectFiles();
void viewFiles();
void changeSettings();
void displayHelp();
string currentDateTime();

std::string fileContents [10];

//Create Ada Weakness Object
AdaWeaknesses wc;

bool filters [10];
std::string nameOfFilters[10] = {"Uninitialized Variables", "Unused Procedures", "Division by 0", "Dead Code", "Numeric Overflow", "Range Constraint Violation", "Expression Always True", "Expression Always False", "Redundant Conditionals", "Infinite Loops"};

int numItemsInArray = 0;
std::string filePath = "/home";  //default to home root because linux and don't care

int main ()
{
  
  //Initialize filters array
  for (int i = 0; i < sizeof(filters); i++) {
    filters[i] = true; //Initialize to true so everything will be checked
  }
  
  printf("Software Integrity Tester\n");

  int input = 0;

  do {

    printf("\n1) Run Tester");
    printf("\n2) Choose Files To Run Tester On");
    printf("\n3) View Selected Files");
    printf("\n4) Change Settings");
    printf("\n5) Help");
    printf("\n6) Exit");

    printf("\nType number of desired option: ");
    cin >> input;
    printf("\n");

    switch (input)
    {

      case 1:
	runTester();
	break;
      case 2:
	selectFiles();
	break;
      case 3:
	viewFiles();
	break;
      case 4:
	changeSettings();
	break;
      case 5:
	displayHelp();
	break;
      case 6:
        break;
      default:
	printf("Invalid Input\n");
	input = 5;
	displayHelp();
    }
    
  } while (input != 6);

  return 0;
}

string currentDateTime() {
  time_t now = time(0);
  struct tm tstruct;
  char buf[80];
  tstruct = *localtime(&now);
  strftime(buf, sizeof(buf), "%Y-%m-%d.%X", &tstruct);
  return buf;
}

void runTester() {
  printf("\n");

  // Make sure they have a file in the queue
  if (numItemsInArray == 0) {
    printf("Error: No files in queue");
  } else {

    //If so, do everything:

    //Create output file at desired path
    string outputName = currentDateTime() + ".txt";
    string outputPath = filePath + "/" + outputName;

    ofstream myfile;
    myfile.open(outputName.c_str());
    myfile << "Software Integrity Tester";
    myfile << "\n\tVersion: " << VERSION;
    myfile << "\n" << SEPERATOR;
    myfile << "\n";
    
    myfile << "\n" << "Files scanned:";
    //write name of all files scanned
    for (int i = 0; i < numItemsInArray; i++) {
      myfile << "\n\t" << (i+1) << ") " << fileContents[i];
    }
    myfile << "\n" << SEPERATOR;
    myfile << "\n";

    myfile << "\n" << "Weaknesses checked:";
    //write name(s) of all weaknesses checked for
    for (int i = 0; i < sizeof(filters); i++) {
      if (filters[i] == 1) {
	myfile << "\n\t" << nameOfFilters[i];
      }
    }
    myfile << "\n" << SEPERATOR;
    
    //looping if statement, check each weakness for each file using boolean array
    for (int i = 0; i < numItemsInArray; i++) {
      myfile << "\n" << SEPERATOR;
      myfile << "\n" << fileContents[i];
      myfile << "\n" << SEPERATOR;
      myfile << "\n";

      //Copy the file into a string to pass along
      string fileText = "";
      string line;
      ifstream infile (fileContents[i].c_str());
      if (infile.is_open()) {
	while (getline(infile,line)) {
	  fileText = fileText + "\n" + line;
	}
	infile.close();
      }
      
      if (filters[0] == 1) {
	wc.checkForWeakness1(myfile, fileText);
      }

      if (filters[1] == 1) {
	wc.checkForWeakness2(myfile, fileText);
      }

      if (filters[2] == 1) {
	wc.checkForWeakness3(myfile, fileText);
      }

      if (filters[3] == 1) {
	wc.checkForWeakness4(myfile, fileText);
      }

      if (filters[4] == 1) {
	wc.checkForWeakness5(myfile, fileText);
      }

      if (filters[5] == 1) {
	wc.checkForWeakness6(myfile, fileText);
      }

      if (filters[6] == 1) {
	wc.checkForWeakness7(myfile, fileText);
      }

      if (filters[7] == 1) {
	wc.checkForWeakness8(myfile, fileText);
      }

      if (filters[8] == 1) {
	wc.checkForWeakness9(myfile, fileText);
      }

      if (filters[9] == 1) {
	wc.checkForWeakness10(myfile, fileText);
      }

      myfile << "\n" << SEPERATOR;
	  
    }
    //end loop
    myfile.close();
    
  }

  printf("\n");
  
}

/*
 * SelectFiles Method:
 *
 * This method prompts the user to enter the full file path. It then tries to
 * put the file name in the array if there is space.
 */
void selectFiles() {
  
  printf("\n");

  string fileName = "";
  printf("\tEnter name of file to be scanned (full file path): ");
  cin >> fileName;

  if (numItemsInArray < sizeof(fileContents)/32) {  //Array size is in bytes lol, therefore 10 strings = 320 bytes.
    fileContents[numItemsInArray] = fileName;
    numItemsInArray++;
  } else {
    cout << "Error: Max Number of Files in Queue" << endl;
  }
  
  printf("\n");
}

/*
 * ViewFiles Method:
 *
 * This method display the files with numeric labels. It then prompts the user
 * if they wish to remove a file. If so, they input a number (based on the labels)
 * and it is removed, and then the user is asked again (by recursively calling the method.
 */
void viewFiles() {
  printf("\n");

  cout << "\tFile Queue:" << endl;

  for (int i = 0; i < numItemsInArray; i++) {
    cout << "\t" << (i+1) << ") " << fileContents[i] << endl;
  }

  //Option to remove files
  if (numItemsInArray > 0) {
    string yesOrNo = "N";
    printf("Would you like to remove a file from this list? (Y/N): ");
    cin >> yesOrNo;

    if ((yesOrNo.compare("Y") == 0 || yesOrNo.compare("y") == 0)) {

      int fileNum = 0;
      printf("Please enter number of file to remove (from list above): ");
      cin >> fileNum;
      fileNum--;

      if (fileNum >= 0 && fileNum < numItemsInArray) {

	fileContents[fileNum] = "";
	numItemsInArray--;
      
	while (fileNum < numItemsInArray) {
	  fileContents[fileNum] = fileContents[fileNum+1];
	  fileNum++;
	}
      
      } else {
	printf("Invalid number");
      }

      printf("\n");
      viewFiles();
    
    }
  }

}

/*
 * ChangeSettings Method:
 *
 * 
 */
void changeSettings() {

  //Submenu
  int subMenuInput = 0;

  do {

     printf("\nCurrent Filters:\n");

     for (int i = 0; i < sizeof(filters); i++) {
       if (filters[i] == 1) {
	 cout << (i+1) << ") " << nameOfFilters[i] << ": " << "ON" << endl;
       } else {
	 cout << (i+1) << ") " << nameOfFilters[i] << ": " << "OFF" << endl;
       }
     }
    
    printf("\n1) Change Filters");
    printf("\n2) Exit");

    printf("\nType number of desired option: ");
    cin >> subMenuInput;
    printf("\n");

    int filterNumToToggle = 0;

    switch (subMenuInput)
    {


      //Change Filters
      case 1:
        printf("Enter number of filter to toggle: ");
        cin >> filterNumToToggle;
	filterNumToToggle--;

	if (filterNumToToggle >= 0 && filterNumToToggle < sizeof(filters)) {
	  filters[filterNumToToggle] = !filters[filterNumToToggle];
	} else {
	  printf("\nInvalid number!");
	}
	printf("\n");
	break;
      case 2:
	break;
      case 3:;
	break;
      default:
	printf("\nInvalid Input\n");
	subMenuInput = 2;
    }
    
  } while (subMenuInput != 2);
  
  printf("\n");
}


/*
 * DisplayHelp Method:
 *
 * This method displays how each menu option works.
 */
void displayHelp() {
  printf("Help:");

  //Display a lot of strings

  printf("\n\tHow to use:");
  printf("\n\t\tMenu Option 1 will run the tester on all the files given by the user, using the filters the user selected, and creating an output file where in a user specified location");
  printf("\n\t\tMenu Option 2 will prompt the user to enter the full file path of a file which they want to be scanned. This is saved by the program and used to determine which files the tester will run on when the 1st menu option is selected.");
  printf("\n\t\tMenu Option 3 will display the files currently stored by the program, and then ask the user if they would like to remove a file from the saved list. If they say yes, then the user can input which file they would like to remove.");
  printf("\n\t\tMenu Option 4 will show the user which filters are currently going to be used when the tester runs, and wherer the current output file destination is. The user has the option of choosing filters and also changing the output location.");
  printf("\n\t\tMenu Option 5 brings up this help menu.");

  printf("\n\n\tVersion 0.5.2.2");
  printf("\n");
}
