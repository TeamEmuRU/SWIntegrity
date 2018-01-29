#include "Weakness.h"
#include <iostream>

	using namespace System;
	using namespace System::Windows::Forms;
	using namespace System::Collections::Generic;		

		Weakness::Weakness() {
			name = "default name";
			priority = Priority::Normal;
			risk = Risk::Low;
			solution = "";
			cwe = System::Int32(-1);
		}
		Weakness::Weakness(String^ name, Risk risk) : Weakness() {
			this->name = name;
			this->risk = risk;
		}
		Weakness::Weakness(String^ name, Risk risk, int^ cwe) : Weakness(){
			this->name = name;
			this->risk = risk;
			this->cwe = cwe;
		}

		//weakness report text for this weakness
		String^ Weakness::getWreport() {
			return wreport;
		}
		String^ Weakness::getName() {
			return name;
		}
		String^ Weakness::getCweLink() {
			if (cwe->CompareTo(-1).Equals(1)) {
				return gcnew System::String("https://cwe.mitre.org/data/definitions/" + cwe + ".html");
			}
			return "";
		}

		String^ Weakness::toString() {
			String^ s = "";
			if (cwe->CompareTo(-1).Equals(1))
				s += "CWE-" + cwe + ": ";			
			s += name + "\nRisk: " + risk.ToString();
			if (!solution->IsNullOrWhiteSpace(solution))
				s += "\nSolution:\n" + solution + "\n";
			
			return s;
			}
		String^ Weakness::ToString() {
			return name;
		}
