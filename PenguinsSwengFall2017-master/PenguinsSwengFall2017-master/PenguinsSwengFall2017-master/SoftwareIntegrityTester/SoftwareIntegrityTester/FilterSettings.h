#pragma once
/******************************************
/* File:  FilterSettings.h
/*
/* Description:  The FilterSettigns Form will display a checkbox list of weaknesses. By default, all will be checked and ran. The user
/*				 can select and deselect weaknesses to customize how they want the S.I.T. to run and analyze files. Each weakness also
/*				 has a description and a link to it's CWE online.
/*
/* Programmer:  Brandon Gordon, James Meredith, Cole Christensen, Tyler Ligenzowski, Joe Dementri
/* 30 OCT 2017  FILTERSETTINGS
*******************************************/

#include "Weakness.h"
namespace SoftwareIntegrityTester {

	using namespace System;
	using namespace System::ComponentModel;
	using namespace System::Collections;
	using namespace System::Windows::Forms;
	using namespace System::Data;
	using namespace System::Drawing;
	using namespace System::Collections::Generic;

	/// <summary>
	/// Summary for FilterSettings
	/// </summary>
	public ref class FilterSettings : public System::Windows::Forms::Form
	{
		List<Weakness^>^ weaknessList = gcnew List<Weakness^>();
	public:
		FilterSettings(List<Weakness^>^ weakList)
		{
			InitializeComponent();
			//
			//TODO: Add the constructor code here
			//
			weaknessList = weakList;
			for each (Weakness^ w in weaknessList)
			{
				wList->Items->Add(w);
			}
			//all checkboxes are checked by default
			for (int i = 0; i < wList->Items->Count; i++)
			{
				wList->SetItemChecked(i, true);
			}


			propLabel->Visible = false;
			linkLabel1->Visible = false;
			linkLabel1->LinkClicked += gcnew LinkLabelLinkClickedEventHandler(this, &FilterSettings::linkLabel1Clicked);
		}

	protected:
		/// <summary>
		/// Clean up any resources being used.
		/// </summary>
		~FilterSettings()
		{
			if (components)
			{
				delete components;
			}
		}
	private: System::Windows::Forms::CheckedListBox^  wList;
	protected:

	private: System::Windows::Forms::Label^  label1;
	private: System::Windows::Forms::Label^  propLabel;
	private: System::Windows::Forms::LinkLabel^  linkLabel1;
	protected:

	private:
		/// <summary>
		/// Required designer variable.
		/// </summary>
		System::ComponentModel::Container ^components;

#pragma region Windows Form Designer generated code
		/// <summary>
		/// Required method for Designer support - do not modify
		/// the contents of this method with the code editor.
		/// </summary>
		void InitializeComponent(void)
		{
			this->wList = (gcnew System::Windows::Forms::CheckedListBox());
			this->label1 = (gcnew System::Windows::Forms::Label());
			this->propLabel = (gcnew System::Windows::Forms::Label());
			this->linkLabel1 = (gcnew System::Windows::Forms::LinkLabel());
			this->SuspendLayout();
			// 
			// wList
			// 
			this->wList->FormattingEnabled = true;
			this->wList->Location = System::Drawing::Point(18, 19);
			this->wList->Margin = System::Windows::Forms::Padding(4, 4, 4, 4);
			this->wList->Name = L"wList";
			this->wList->Size = System::Drawing::Size(528, 576);
			this->wList->TabIndex = 0;
			this->wList->SelectedIndexChanged += gcnew System::EventHandler(this, &FilterSettings::wList_SelectedIndexChanged);
			// 
			// label1
			// 
			this->label1->AutoSize = true;
			this->label1->Font = (gcnew System::Drawing::Font(L"Microsoft Sans Serif", 12, System::Drawing::FontStyle::Regular, System::Drawing::GraphicsUnit::Point,
				static_cast<System::Byte>(0)));
			this->label1->Location = System::Drawing::Point(588, 19);
			this->label1->Margin = System::Windows::Forms::Padding(4, 0, 4, 0);
			this->label1->Name = L"label1";
			this->label1->Size = System::Drawing::Size(171, 37);
			this->label1->TabIndex = 1;
			this->label1->Text = L"Properties:";
			// 
			// propLabel
			// 
			this->propLabel->AutoSize = true;
			this->propLabel->Font = (gcnew System::Drawing::Font(L"Microsoft Sans Serif", 10.2F, System::Drawing::FontStyle::Regular, System::Drawing::GraphicsUnit::Point,
				static_cast<System::Byte>(0)));
			this->propLabel->Location = System::Drawing::Point(588, 88);
			this->propLabel->Margin = System::Windows::Forms::Padding(4, 0, 4, 0);
			this->propLabel->Name = L"propLabel";
			this->propLabel->Size = System::Drawing::Size(0, 32);
			this->propLabel->TabIndex = 2;
			// 
			// linkLabel1
			// 
			this->linkLabel1->AutoSize = true;
			this->linkLabel1->Location = System::Drawing::Point(590, 346);
			this->linkLabel1->Margin = System::Windows::Forms::Padding(6, 0, 6, 0);
			this->linkLabel1->Name = L"linkLabel1";
			this->linkLabel1->Size = System::Drawing::Size(110, 25);
			this->linkLabel1->TabIndex = 3;
			this->linkLabel1->TabStop = true;
			this->linkLabel1->Text = L"linkLabel1";
			this->linkLabel1->LinkClicked += gcnew System::Windows::Forms::LinkLabelLinkClickedEventHandler(this, &FilterSettings::linkLabel1_LinkClicked);
			// 
			// FilterSettings
			// 
			this->AutoScaleDimensions = System::Drawing::SizeF(12, 25);
			this->AutoScaleMode = System::Windows::Forms::AutoScaleMode::Font;
			this->ClientSize = System::Drawing::Size(1318, 715);
			this->Controls->Add(this->linkLabel1);
			this->Controls->Add(this->propLabel);
			this->Controls->Add(this->label1);
			this->Controls->Add(this->wList);
			this->Margin = System::Windows::Forms::Padding(4, 4, 4, 4);
			this->Name = L"FilterSettings";
			this->Text = L"FilterSettings";
			this->ResumeLayout(false);
			this->PerformLayout();

		}
#pragma endregion

		private: System::Void linkLabel1Clicked(System::Object^  sender, LinkLabelLinkClickedEventArgs^ e)
		{
			linkLabel1->LinkVisited = true;
			System::Diagnostics::Process::Start(weaknessList[wList->SelectedIndex]->getCweLink());
		}
	private: System::Void wList_SelectedIndexChanged(System::Object^  sender, System::EventArgs^  e) {
		Weakness^ w = weaknessList[wList->SelectedIndex];
		propLabel->Visible = true;
		propLabel->Text = w->toString();
		if (!w->getCweLink()->IsNullOrEmpty(w->getCweLink())) {
			linkLabel1->Visible = true;
			linkLabel1->LinkVisited = false;
			linkLabel1->Text = w->getCweLink();
		}
		else {
			linkLabel1->Visible = false;
		}
	}
	private: System::Void linkLabel1_LinkClicked(System::Object^  sender, System::Windows::Forms::LinkLabelLinkClickedEventArgs^  e) {
	}
};
}
