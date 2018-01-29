#ifndef ADAWEAKNESSES_H
#define ADAWEAKNESSES_H

#include <cstdio>
#include <iostream>
#include <fstream> 
#include <streambuf>
#include <string>

using namespace std;

class AdaWeaknesses
{
  public:

  //constructor (needs nothing)
  AdaWeaknesses();

  //weaknesses
  void checkForWeakness1(std::ofstream& mystream, string fileContents);
  void checkForWeakness2(std::ofstream& mystream, string fileContents);
  void checkForWeakness3(std::ofstream& mystream, string fileContents);
  void checkForWeakness4(std::ofstream& mystream, string fileContents);
  void checkForWeakness5(std::ofstream& mystream, string fileContents);
  void checkForWeakness6(std::ofstream& mystream, string fileContents);
  void checkForWeakness7(std::ofstream& mystream, string fileContents);
  void checkForWeakness8(std::ofstream& mystream, string fileContents);
  void checkForWeakness9(std::ofstream& mystream, string fileContents);
  void checkForWeakness10(std::ofstream& mystream, string fileContents);
};

#endif
