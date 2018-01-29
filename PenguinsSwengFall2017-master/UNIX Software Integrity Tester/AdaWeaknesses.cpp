#include <iostream>
#include <fstream>
#include <string>
#include "AdaWeaknesses.h"

using namespace std;

AdaWeaknesses::AdaWeaknesses() {

}

void AdaWeaknesses::checkForWeakness1(std::ofstream& mystream, string fileContents) {
  mystream << "\nUninitialized Variables:\n";

  //Keywords:
  string assignment = ":=";
  string declaration = ": ";
  //initial pos
  int assignmentPos = fileContents.find(assignment);
  int declarationPos = fileContents.find(declaration);

  if (declarationPos == -1) {
    mystream << "\n\tNo Variables Declared";
  }

  if (assignmentPos == -1) {
    mystream << "\n\tNo Variables Assigned.";
  }

  while (declarationPos != -1) {

    //Get line num
    int lineNum = 0;
    int newlinePos = fileContents.find("\n");
    int lastNewline = newlinePos;
    while (newlinePos < declarationPos) {
      lineNum++;
      lastNewline = newlinePos;
      newlinePos = fileContents.find("\n", newlinePos+1);
    }


    string name = fileContents.substr(lastNewline+2, declarationPos-lastNewline);
    mystream << "\n\tDeclaration " << name << " found at line " << lineNum;
    
    declarationPos = fileContents.find(declaration, declarationPos+1);
  }

  while (assignmentPos != -1) {

    //Get line num
    int lineNum = 0;
    int newlinePos = fileContents.find("\n");
    while (newlinePos < assignmentPos) {
      lineNum++;
      newlinePos = fileContents.find("\n", newlinePos+1);
    }

    //find var name;
    int seekPos = assignmentPos-2;
    while (!(fileContents.substr(seekPos,1) == " ")) {
      seekPos--;
    }

    string varName = fileContents.substr(seekPos, assignmentPos-seekPos);
    mystream << "\n\tVariable \"" << varName << "\" assigned at line " << lineNum;

    assignmentPos = fileContents.find(assignment, assignmentPos+1);
  }

  
  mystream << "\n";
}

void AdaWeaknesses::checkForWeakness2(std::ofstream& mystream, string fileContents) {
  mystream << "\nProcedure Tracking:\n";

  //Keywords
  string procedureKW = "procedure";

  //initial position
  int procedurePos = fileContents.find(procedureKW);

  //In case of none:
  if (procedurePos == -1) {
    mystream << "\n\tNo procedures found.";
  }

  //Loop
  while (procedurePos != -1)
  {
    //Get all text after "procedure", then find the name of the procedure
    string textAfterProcedureKW = fileContents.substr(procedurePos+10);
    string procedureName = textAfterProcedureKW.substr(0, textAfterProcedureKW.find(" "));

    //Write Name of Procedure to Stream
    mystream << "\n\n\tProcedure: " << procedureName;

    int sameProcedureNextPosition = textAfterProcedureKW.substr(10+procedureName.length()).find(procedureName);

    if (sameProcedureNextPosition != -1) {
      mystream << "\n\t\tProcedure has use.";
    } else {
      mystream << "\n\t\tProcedure DOES NOT have use";
    }

    procedurePos = fileContents.find(procedureKW, procedurePos+1);
      
  }  
  mystream << "\n";
}

void AdaWeaknesses::checkForWeakness3(std::ofstream& mystream, string fileContents) {
  mystream << "\nDivision by Zero:\n";

  //Keywords
  string division = "/";
  string zero = "0";

  //initial position
  int divisionPos = fileContents.find(division);

  //In case of none:
  if (divisionPos == -1) {
    mystream << "\n\tNo division found";
  }

  while (divisionPos != -1) {

    int lineNum = 0;
    //Get Line Num
    int newLinePos = fileContents.find("\n");
    while (newLinePos < divisionPos) {
      lineNum++;
      newLinePos = fileContents.find("\n", newLinePos+1);
    }  

    mystream << "\n\tDivision occurs at Line " << lineNum;
    mystream << "\n\t\tPlease check to ensure equation cannot evaluate to 0";
    divisionPos = fileContents.find(division, divisionPos+1);
  }
  mystream << "\n";
}

void AdaWeaknesses::checkForWeakness4(std::ofstream& mystream, string fileContents) {
  mystream << "\nConcurrency:\n";

  //Keywords
  string entry = "entry";
  string accept = "accept";

  int entryPos = fileContents.find(entry);
  int acceptPos = fileContents.find(accept);

  if (entryPos == -1) {
    mystream << "\n\tNo instances of \"entry\" found";
  }

  if (acceptPos == -1) {
    mystream << "\n\tNo instances of \"accept\" found";
  }

  while (entryPos != -1) {

    //get line num
    int lineNum = 0;
    int newlinePos = fileContents.find("\n");
    int lastNewline = newlinePos;
    while (newlinePos < entryPos) {
      lineNum++;
      lastNewline = newlinePos;
      newlinePos = fileContents.find("\n", newlinePos+1);
    }

    string lineOfCode = fileContents.substr(lastNewline+2, (newlinePos-(lastNewline+2)));
    mystream << "\n\t\"entry\" found at line " << lineNum;
    mystream << "\n\t\t Line: " << lineOfCode;
    mystream << "\n\tPlease understand concurrency and how to use \"entry\"";

    entryPos = fileContents.find(entry, entryPos+1);
  }

  while (acceptPos != -1) {

    //get line num
    int lineNum = 0;
    int newlinePos = fileContents.find("\n");
    int lastNewline = newlinePos;
    while (newlinePos < acceptPos) {
      lineNum++;
      lastNewline = newlinePos;
      newlinePos = fileContents.find("\n", newlinePos+1);
    }

    string lineOfCode = fileContents.substr(lastNewline+2, (newlinePos-(lastNewline+2)));
    mystream << "\n\t\"accept\" found at line " << lineNum;
    mystream << "\n\t\t Line: " << lineOfCode;
    mystream << "\n\tPlease understand concurrency and how to use \"accept\"";

    acceptPos = fileContents.find(accept, acceptPos+1);
    
  }  

  mystream << "\n";
}

void AdaWeaknesses::checkForWeakness5(std::ofstream& mystream, string fileContents) {
  mystream << "\nNumeric Overflow\n";

  //Keywords
  string first = "'First";
  string last = "'Last";

  //initial position
  int foundPos = fileContents.find(first);

  //In case of none
  if (foundPos == -1) {
    mystream << "\n\tNo instances of 'First found";
  }

  while (foundPos != -1) {
    string assignmentOperator = ":=";
    string declarationOperator = ":";
    string newLineOperator = "\n";

    //Get Line Num
    int lineNum = 0;
    int newLinePos = fileContents.find("\n");
    while (newLinePos < foundPos) {
      lineNum++;
      newLinePos = fileContents.find("\n", newLinePos+1);
    }
    
    //find last assn op before passing
    int assnOpPos = fileContents.find(assignmentOperator);
    int lastAssnOpPos = assnOpPos;
    while (assnOpPos != -1 && assnOpPos < foundPos) {
      lastAssnOpPos = assnOpPos;
      assnOpPos = fileContents.find(assignmentOperator, assnOpPos+1);
    }

    //find last decl op before passing
    int declOpPos = fileContents.find(declarationOperator);
    int lastDeclOpPos = declOpPos;
    while (declOpPos != -1 && declOpPos < foundPos) {
      lastDeclOpPos = declOpPos;
      declOpPos = fileContents.find(declarationOperator, declOpPos+1);
    }

    //find last newline before passing
    int newlinePos = fileContents.find(newLineOperator);
    int lastNewlinePos = newlinePos;
    while (newlinePos != -1 && newlinePos < foundPos) {
      lastNewlinePos = newlinePos;
      newlinePos = fileContents.find(newLineOperator, newlinePos+1);
    }

    int lengthOfVarName  = lastDeclOpPos - lastNewlinePos;
    string varName = fileContents.substr(lastNewlinePos, lengthOfVarName);

    mystream << "\n\tVariable(s) assigned with 'First: " << varName;

    foundPos = fileContents.find(first, foundPos+1);
  }

  //---------------------------------------------------------------
  // EYE BREAk
  //---------------------------------------------------------------

  //initial position
  foundPos = fileContents.find(last);

  //If not found
  if (foundPos == -1) {
    mystream << "\n\tNo instances of 'Last found";
  }
  
  while (foundPos != -1) {
    string assignmentOperator = ":=";
    string declarationOperator = ":";
    string newLineOperator = "\n";

    //Get Line Num
    int lineNum = 0;
    int newLinePos = fileContents.find("\n");
    while (newLinePos < foundPos) {
      lineNum++;
      newLinePos = fileContents.find("\n", newLinePos+1);
    }
    
    //find last assn op before passing
    int assnOpPos = fileContents.find(assignmentOperator);
    int lastAssnOpPos = assnOpPos;
    while (assnOpPos != -1 && assnOpPos < foundPos) {
      lastAssnOpPos = assnOpPos;
      assnOpPos = fileContents.find(assignmentOperator, assnOpPos+1);
    }

    //find last decl op before passing
    int declOpPos = fileContents.find(declarationOperator);
    int lastDeclOpPos = declOpPos;
    while (declOpPos != -1 && declOpPos < foundPos) {
      lastDeclOpPos = declOpPos;
      declOpPos = fileContents.find(declarationOperator, declOpPos+1);
    }

    //find last newline before passing
    int newlinePos = fileContents.find(newLineOperator);
    int lastNewlinePos = newlinePos;
    while (newlinePos != -1 && newlinePos < foundPos) {
      lastNewlinePos = newlinePos;
      newlinePos = fileContents.find(newLineOperator, newlinePos+1);
    }

    int lengthOfVarName  = lastDeclOpPos - lastNewlinePos;
    string varName = fileContents.substr(lastNewlinePos, lengthOfVarName);

    mystream << "\n\tVariable(s) assigned with 'Last: " << varName;

    foundPos = fileContents.find(last, foundPos+1);
  }
  
  mystream << "\n";
}

void AdaWeaknesses::checkForWeakness6(std::ofstream& mystream, string fileContents) {
  mystream << "\nRange Constraints:\n";

  //Keywords
  string range = "..";

  //Initial Position
  int rangePos = fileContents.find(range);

  //if not found
  if (rangePos == -1) {
    mystream << "\n\tNo Range Constraints found.";
  }

  while (rangePos != -1) {
    
    //Get Line Num of RangePos
    int lineNum = 0;
    int newLinePos = fileContents.find("\n");
    while (newLinePos < rangePos) {
      lineNum++;
      newLinePos = fileContents.find("\n", newLinePos+1);
    }

    //if array kw found
    string arrayKW = "array";
    int arrayPos = fileContents.find(arrayKW);
    int lastArrayPos = arrayPos;
    while (arrayPos != -1 && arrayPos < rangePos) {
      lastArrayPos = arrayPos;
      arrayPos = fileContents.find(arrayKW, arrayPos+1);
    }

    //Get Line Num of Array KW
    int arrayLineNum = 0;
    int secondNewLinePos = fileContents.find("\n");
    while (secondNewLinePos < lastArrayPos) {
      arrayLineNum++;
      secondNewLinePos = fileContents.find("\n", secondNewLinePos+1);
    }

    //If array KW is on same line as ..
    if (lineNum == arrayLineNum) {

      
      //Get Left Parenthesis Pos
      string leftPar = "(";
      int leftParPos = fileContents.find(leftPar);
      int lastLeftParPos = leftParPos;
      while (leftParPos != -1 && leftParPos  < rangePos) {
	lastLeftParPos = leftParPos;
	leftParPos = fileContents.find(leftPar, leftParPos+1);
      }

      //Get Right Parenthesis
      string rightPar = ")";
      int rightParPos = fileContents.find(rightPar);
      while (rightParPos != -1 && rightParPos  < rangePos) {
	rightParPos = fileContents.find(rightPar, rightParPos+1);
      }

      string constraint = fileContents.substr(lastLeftParPos, (rightParPos-lastLeftParPos)+1);
      mystream << "\n\tArray on Line " << lineNum << " has range constraints: " << constraint;

      
    } //end if

    rangePos = fileContents.find(range, rangePos+1);
  } //end while

  mystream << "\n";
}

void AdaWeaknesses::checkForWeakness7(std::ofstream& mystream, string fileContents) {
  mystream << "\nExpression Alwasys True:\n";

  //Keywords
  string ifKW = "if";
  string thenKW = "then";

  //Initial Position
  int thenPos = fileContents.find(thenKW);

  //If Not Found
  if (thenPos == -1) { 
    mystream << "\n\tNo If-Then Statements Found.";
  }

  //Find all if then statements
  while (thenPos != -1) {

    //Get Line Num
    int lineNum = 0;
    int newlinePos = fileContents.find("\n");
    while (newlinePos < thenPos) {
      lineNum++;
      newlinePos = fileContents.find("\n", newlinePos+1);
    }

    //Find position of if KW
    int ifPos = fileContents.find(ifKW);
    int lastIfPos = ifPos;
    while (ifPos != -1 && ifPos < thenPos) {
      lastIfPos = ifPos;
      ifPos = fileContents.find(ifKW, ifPos+1);
    }

    //Pull out everything between if and then
    int beginningPos = lastIfPos+3;
    string condExp = fileContents.substr(beginningPos, (thenPos-beginningPos));

    mystream << "\n\tIf-Then Condition " << condExp << " found on line: " << lineNum;
    mystream << "\n\t\tPlease make sure that " << condExp << " will not always be true.";

    thenPos = fileContents.find(thenKW, thenPos+1);
	   
  }  
  mystream << "\n";
}

void AdaWeaknesses::checkForWeakness8(std::ofstream& mystream, string fileContents) {
  mystream << "\nExpression Always False:\n";

  //Keywords
  string ifKW = "if";
  string thenKW = "then";

  //Initial Position
  int thenPos = fileContents.find(thenKW);

  //Find all if then statements
  while (thenPos != -1) {

    //Get Line Num
    int lineNum = 0;
    int newlinePos = fileContents.find("\n");
    while (newlinePos < thenPos) {
      lineNum++;
      newlinePos = fileContents.find("\n", newlinePos+1);
    }

    //Find position of if KW
    int ifPos = fileContents.find(ifKW);
    int lastIfPos = ifPos;
    while (ifPos != -1 && ifPos < thenPos) {
      lastIfPos = ifPos;
      ifPos = fileContents.find(ifKW, ifPos+1);
    }

    //Pull out everything between if and then
    int beginningPos = lastIfPos+3;
    string condExp = fileContents.substr(beginningPos, (thenPos-beginningPos));

    mystream << "\n\tIf-Then Condition " << condExp << " found on line: " << lineNum;
    mystream << "\n\t\tPlease make sure that " << condExp << " will not always be false.";

    thenPos = fileContents.find(thenKW, thenPos+1);
	   
  }
  mystream << "\n";
}

void AdaWeaknesses::checkForWeakness9(std::ofstream& mystream, string fileContents) {
  mystream << "\nRedundant Conditionals\n";

  //Look for and or or
  string andKW = "and";
  string orKW = " or";
  string xorKW = "xor";
  string neq = "/=";
  string eq = "=";
  string lt = "<";
  string lteq = "<=";
  string gt = ">";
  string gteq = ">=";
  string ifKW = "if";
  string thenKW = "then";

  //Initial Position
  int thenPos = fileContents.find(thenKW);

  //Find all if then statements
  while (thenPos != -1) {

    //Get Line Num
    int lineNum = 0;
    int newlinePos = fileContents.find("\n");
    while (newlinePos < thenPos) {
      lineNum++;
      newlinePos = fileContents.find("\n", newlinePos+1);
    }

    //Find position of if KW
    int ifPos = fileContents.find(ifKW);
    int lastIfPos = ifPos;
    while (ifPos != -1 && ifPos < thenPos) {
      lastIfPos = ifPos;
      ifPos = fileContents.find(ifKW, ifPos+1);
    }

    //Pull out everything between if and then
    int beginningPos = lastIfPos+3;
    string condExp = fileContents.substr(beginningPos, (thenPos-beginningPos));

    //pull out each var in string that isn't a delimiter

    string leftBooleanOp;
    string rightBooleanOp;

    //--------
    //AND
    //--------
    int andPos = condExp.find(andKW);
    if (andPos != -1) {
      //find space before left side
      int spacePos = condExp.find(" ");
      
      if (spacePos == -1) {
	
	leftBooleanOp = condExp.substr(0, andPos);
	
      } else {
	
	leftBooleanOp = condExp.substr(spacePos, (andPos-spacePos));
	
      }

      //find space after right side
      int rightSpacePos = condExp.find(" ", andPos+4);

      if (rightSpacePos == -1) {
	
	rightBooleanOp = condExp.substr(andPos+3);
	
      } else {

	rightBooleanOp = condExp.substr(andPos+3, (rightSpacePos-(andPos+3)));
	
      }

      mystream << "\n\tAND Expression Found at Line " << lineNum;
      mystream << "\n\t\tLeft Side: " << leftBooleanOp;
      mystream << "\n\t\tRight Side: " << rightBooleanOp;

      
    }

    //-------
    //OR
    //-------
    int orPos = condExp.find(orKW);
    if (orPos != -1) {
      //find space before left side
      int spacePos = condExp.find(" ");
      
      if (spacePos == -1) {
	
	leftBooleanOp = condExp.substr(0, orPos);
	
      } else {
	
	leftBooleanOp = condExp.substr(spacePos, (orPos-spacePos));
	
      }

      //find space after right side
      int rightSpacePos = condExp.find(" ", orPos+4);

      if (rightSpacePos == -1) {
	
	rightBooleanOp = condExp.substr(orPos+3);
	
      } else {

	rightBooleanOp = condExp.substr(orPos+3, (rightSpacePos-(orPos+3)));
	
      }

      mystream << "\n\tOR Expression Found at Line " << lineNum;
      mystream << "\n\t\tLeft Side: " << leftBooleanOp;
      mystream << "\n\t\tRight Side: " << rightBooleanOp;

      
    }

    //------
    //XOR
    //------
    
    int xorPos = condExp.find(xorKW);
    if (xorPos != -1) {
      //find space before left side
      int spacePos = condExp.find(" ");
      
      if (spacePos == -1) {
	
	leftBooleanOp = condExp.substr(0, xorPos);
	
      } else {
	
	leftBooleanOp = condExp.substr(spacePos, (xorPos-spacePos));
	
      }

      //find space after right side
      int rightSpacePos = condExp.find(" ", xorPos+4);

      if (rightSpacePos == -1) {
	
	rightBooleanOp = condExp.substr(xorPos+3);
	
      } else {

	rightBooleanOp = condExp.substr(xorPos+3, (rightSpacePos-(xorPos+3)));
	
      }

      mystream << "\n\tXOR Expression Found on Line " << lineNum;
      mystream << "\n\t\tLeft Side: " << leftBooleanOp;
      mystream << "\n\t\tRight Side: " << rightBooleanOp;

      
    }


    //TODO Later:
    int neqPos = condExp.find(neq);
    int eqPos = condExp.find(eq);
    int ltPos = condExp.find(lt);
    int lteqPos = condExp.find(lteq);
    int gtPos = condExp.find(gt);
    int gteqPos = condExp.find(gteq);

    

    thenPos = fileContents.find(thenKW, thenPos+1);
	   
  }
  mystream << "\n";
}

void AdaWeaknesses::checkForWeakness10(std::ofstream& mystream, string fileContents) {
  mystream << "\nInfinite Loops:\n";

  //Keywords
  string loopKW = "loop";
  string endLoop = "end loop";

  //Initial
  int loopPos = fileContents.find(loopKW);

  //if no loops
  if (loopPos == -1) {
    mystream << "\nNo loops found.";
  }

  //
  while (loopPos != -1) {

    //Get Line Num
    int lineNum = 0;
    int newlinePos = fileContents.find("\n");
    while (newlinePos < loopPos) {
      lineNum++;
      newlinePos = fileContents.find("\n", newlinePos+1);
    }

    //Make sure current line isnt while, for, endloop
    //find while
    int whilePos = fileContents.find("while");
    int lastWhilePos = whilePos;
    while (whilePos < loopPos && whilePos != -1) {
      lastWhilePos = whilePos;
      whilePos = fileContents.find("while", whilePos+1);
    }

    //find while line num
    int whilePosLine = 0;
    int whileNewlinePos = fileContents.find("\n");
    while (whileNewlinePos < lastWhilePos) {
      whilePosLine++;
      whileNewlinePos = fileContents.find("\n", whileNewlinePos+1);
    }

    //find for
    int forPos = fileContents.find("for");
    int lastForPos = forPos;
    while (forPos < loopPos && forPos != -1) {
      lastForPos = forPos;
      forPos = fileContents.find("for", forPos+1);
    }

    //find for line num
    int forPosLine = 0;
    int forNewlinePos = fileContents.find("\n");
    while (forNewlinePos < lastForPos) {
      forPosLine++;
      forNewlinePos = fileContents.find("\n", forNewlinePos+1);
    }

    //find end
    int endPos = fileContents.find("end loop");
    int lastEndPos = endPos;
    while (endPos < loopPos && endPos != -1) {
      lastEndPos = endPos;
      endPos = fileContents.find("end loop", endPos+1);
    }

    //find end line num
    int endPosLine = 0;
    int endNewlinePos = fileContents.find("\n");
    while (endNewlinePos < lastEndPos) {
      endPosLine++;
      endNewlinePos = fileContents.find("\n", endNewlinePos+1);
    }

    //Determines if loop is while,foor, or end loop instead of just loop
    if (whilePosLine != lineNum && forPosLine != lineNum && endPosLine != lineNum) {

      //find end loop
      int endLoopPos = fileContents.find(endLoop);
      while (endLoopPos < loopPos) {
	endLoopPos = fileContents.find(endLoop, endLoopPos+1);
      }

      //Get Line Num of Loop
      int endLineNum = 0;
      int otherNewlinePos = fileContents.find("\n");
      while (otherNewlinePos < endLoopPos) {
	endLineNum++;
	otherNewlinePos = fileContents.find("\n", otherNewlinePos+1);
      }

      string insideOfLoop = fileContents.substr(loopPos+4, (endLoopPos-(loopPos+4)));

      mystream << "\n\tLoop from Lines " << lineNum << " - " << endLineNum; 

      //see if loop has exit case
      int exitPos = insideOfLoop.find("exit");

      if (exitPos == -1) {
	mystream << "\n\t\tLoop DOES NOT have Exit case";
      } else {
	mystream << "\n\t\tLoop HAS Exit case";
      }

    }

    loopPos = fileContents.find(loopKW, loopPos+1);
  }
    
  mystream << "\n";
}
