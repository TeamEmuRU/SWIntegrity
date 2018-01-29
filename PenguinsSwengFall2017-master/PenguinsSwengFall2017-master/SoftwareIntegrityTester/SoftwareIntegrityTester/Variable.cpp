#include "Variable.h"
#include <iostream>

using namespace System;
using namespace System::Windows::Forms;
using namespace System::Collections::Generic;

Variable::Variable(String^ name, String^ type, int decLine) {
	lineUse = gcnew List<int>();
	this->name = name;
	this->type = type;
	this->declarationLine = decLine;
}
void Variable::addLineUse(int line) {
	this->lineUse->Add(line);
}
void Variable::setLineUse(List<int>^ use) {
	this->lineUse = use;
}
String^ Variable::getName() {
	return name;
}
String^ Variable::getType() {
	return type;
}
int Variable::getDecLine() {
	return declarationLine;
}
List<int>^ Variable::getLineUse() {
	return lineUse;
}