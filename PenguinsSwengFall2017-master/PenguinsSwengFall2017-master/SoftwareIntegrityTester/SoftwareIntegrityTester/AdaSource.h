#pragma once
#include "Variable.h"

using namespace System::Collections::Generic;
using namespace System;
ref class AdaSource
{
private:
	void removeComments();
	void getVars();
	void getMethods();
	void getVarUse();
	void getLines();

public:
	AdaSource(String^ file);
private:
	String^ file;
	String^ delComment = "--";
	String^ delif = "if";
	String^ delthen = "then";
	List<String^>^ variableTypes;
	List<Variable^>^ variables;
	List<String^>^ lines;

};