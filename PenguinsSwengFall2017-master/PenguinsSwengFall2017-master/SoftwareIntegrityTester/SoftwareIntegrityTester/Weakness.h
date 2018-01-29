#pragma once

using namespace System;
ref class Weakness
{
	public:
		String^ toString();
		String^ ToString() override;
		String^ getWreport();
		String^ getName();
		String^ getCweLink();
		enum class Priority { Low, Normal, Critical };
		enum class Risk { Low, Med, High };

		Weakness();
		Weakness(String^ name, Risk risk);
		Weakness(String^ name, Risk risk, int^ cwe);
	private:
		String^ name; 
		Priority priority;
		Risk risk;
		String^ solution;
		String^ wreport;
		int^ cwe;
};

