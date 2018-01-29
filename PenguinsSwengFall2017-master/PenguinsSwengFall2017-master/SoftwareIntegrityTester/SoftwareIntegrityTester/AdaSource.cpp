#include "AdaSource.h"
#include "Variable.h"
#include <iostream>

using namespace System;
using namespace System::Windows::Forms;
using namespace System::Collections::Generic;

AdaSource::AdaSource(String^ file) {
	variableTypes = gcnew List<String^>();
	variables = gcnew List<Variable^>();
	lines = gcnew List<String^>();
	variableTypes->Add("Integer");
	variableTypes->Add("Float");
	variableTypes->Add("Duration");
	variableTypes->Add("Character");
	variableTypes->Add("String");
	variableTypes->Add("Boolean");

	this->file = file;
	//remove comments from file
	removeComments();
	//find all variables
	getVars();
	//find all methods
	getMethods();
	//populate lines list with all lines
	getLines();
	//find lines that variables are used and put them in their variable object
	getVarUse();

	for each (Variable^ v in variables)
	{
		Console::WriteLine(v->getName() + "(" + v->getType() + ") line: " + v->getDecLine());
		String^ lines = "";
		for each (int line in v->getLineUse())
		{
			lines += line + ", ";
		}
		Console::WriteLine(lines);
	}
}

void AdaSource::removeComments() {
	//tells whether the previous character was part of a comment delimeter
	bool prev = false;
	//stores the index of the start of a comment
	int startindex = -1;
	//stores the index of the end of a comment
	int endindex = -1;
	for (int i = 0; i < file->Length; i++) {
		Char c = file[i];

		if (c == '-') {
			if (prev) {
				startindex = i - 1;
				prev = false;
			}
			else {
				prev = true;
				startindex = -1;
			}
		}
		else if (c == '\n') {
			endindex = i;
			if ((startindex != -1) && (endindex > startindex)) {
				file = file->Remove(startindex, endindex - startindex);
				i = startindex - 1;
				startindex = -1;
			}
			prev = false;
		}
		else {
			prev = false;
		}
	}
}

void AdaSource::getVars()
{
	bool potentalVar = false;
	int startindex = -1;
	int endindex = -1;
	int lastln = 0;
	int line = 1;
	for (int i = 0; i < file->Length; i++) {
		Char c = file[i];

		if (c == ':') {
			if (!potentalVar) {
				potentalVar = true;
				endindex = i - 1;
			}
		}
		else if (c == '\n') {
			if (potentalVar) {
				for each (String^ var in variableTypes)
				{
					if (file->Substring(lastln, i - lastln)->Contains(var)) {
						startindex = lastln + 1;
						String^ name = file->Substring(startindex, endindex - startindex + 1)->Replace("\t", "");
						if (name->Contains(",")) {
							array<String^>^ names = name->Split(',');
							for each (String^ n in names)
							{
								variables->Add(gcnew Variable(n, var, line));
							}
						}
						else {
							variables->Add(gcnew Variable(name, var, line));
						}
					}
				}
			}
			lastln = i;
			line++;
			potentalVar = false;
		}
	}
}
void AdaSource::getVarUse() {
	for each (Variable^ var in variables)
	{
		int i = 1;
		for each (String^ l in lines) {
			if (l->Contains(var->getName())) {
				if (i != var->getDecLine()) {
					var->addLineUse(i);
				}
			}
			i++;
		}
	}
}

void AdaSource::getMethods() {

}
void AdaSource::getLines() {
	int line = 1;
	String^ s = "";
	for (int i = 0; i < file->Length; i++) {
		Char c = file[i];

		s += c;
		if (c == '\n') {
			lines->Add(s);
			line++;
			s = "";
		}
	}
}
/*
void AdaSource::lookExpression() {
int startindex = file->IndexOf(delif);
int count = file->Substring(startindex)->IndexOf(delthen);
String^ expression = file->Substring(startindex, count);
for each (String^ var in vars)
{
if (expression->Contains(var)) {
checkAlwaysTrue(var);
}
}
}
*/