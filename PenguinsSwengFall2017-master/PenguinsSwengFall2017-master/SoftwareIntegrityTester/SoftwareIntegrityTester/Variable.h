#pragma once

using namespace System::Collections::Generic;
using namespace System;
ref class Variable
{
public:
	void setLineUse(List<int>^ use);
	void addLineUse(int line);
	String^ getName();
	String^ getType();
	int getDecLine();
	List<int>^ getLineUse();
	Variable(String^ name, String^ type, int decLine);
private:
	String^ name;
	List<int>^ lineUse;
	String^ type;
	int declarationLine;

};