#pragma once
/******************************************
/* File:  MainForm.h
/*
/* Description:  The Main form will be the heart of the program. It will contain a menu bar, with options to set filters
/*				 and see a help box. It will have a settings button that opens the settings form, an open button which allows files
/*				 to be selected and placed in the fileList to be analyzed, and a run button which starts the S.I.T.
/*
/* Programmer:  Brandon Gordon, James Meredith, Cole Christensen, Tyler Ligenzowski, Joe Dementri
/* 14 OCT 2017  MAINFORM
*******************************************/

#include <msclr\marshal_cppstd.h>
#include "Weakness.h"
#include "UserSettings.h"
#include "FilterSettings.h"
#include <iostream>
#include <iomanip>
#include <fstream>
#include <msclr\marshal_cppstd.h>#include <stdlib.h>
#include <string>
#include <msclr\marshal_cppstd.h>

using namespace System;
using namespace msclr::interop;

#define VERSION "0.1.0"
#define VERSIONTEXT "Put Version Text Here"
#define HELPTEXT "To run the integrity checker:\n\n1) Click Open, then browse and select the file(s) to be checked.\n\n2) If applicable, click Filter by Weakness in the Filter menu and choose which weaknesses to filter.\n\n3) Click Run."
#define DEBUG true
#define SEPERATOR "---------------------------------\n"

namespace SoftwareIntegrityTester {

	using namespace System;
	using namespace System::Windows::Forms;
	using namespace System::Collections::Generic;
	
	/// <summary>
	/// Summary for MainForm
	/// </summary>
	public ref class MainForm : public System::Windows::Forms::Form
	{
		//display version pop-up when app loads
		bool versionOnStart = false;

		//stores file content for all files that are opened
		List<String^>^fileContentList = gcnew List<String^>();

		//stores full file path of all the files that are opened
		List<String^>^ filepathList = gcnew List<String^>();

		private: System::Windows::Forms::ToolStripMenuItem^  aboutToolStripMenuItem;
		private: System::Windows::Forms::ToolStripMenuItem^  filterByWeaknessToolStripMenuItem;

		List<Weakness^>^ weaknessList = gcnew List<Weakness^>();
		UserSettings^ userSettingsUI = gcnew UserSettings();

		//Creates folder for user settings along with output desination file
		String^ settingsPath = Environment::GetFolderPath(Environment::SpecialFolder::Desktop) + "/" + "settings";
		String^ location = settingsPath + "/" + "location.txt";

	public:
		MainForm(void)
		{
			InitializeComponent();

			versionLabel->Text = "Version " + VERSION;

			if (DEBUG)
				AllocConsole();

			//Create the weaknesses and add them to the weakness list
			weaknessList->Add(gcnew Weakness(gcnew System::String("Improper Handling of Undefined Values"), Weakness::Risk::High, 232));
			weaknessList->Add(gcnew Weakness(gcnew System::String("Use of uninitialized variable"), Weakness::Risk::High, 457));
			weaknessList->Add(gcnew Weakness(gcnew System::String("Null pointer dereference"), Weakness::Risk::High, 476));
			weaknessList->Add(gcnew Weakness(gcnew System::String("Dead code"), Weakness::Risk::High, 561));
			weaknessList->Add(gcnew Weakness(gcnew System::String("Infinite loop"), Weakness::Risk::High, 835));
			weaknessList->Add(gcnew Weakness(gcnew System::String("Incorrect synchronization"), Weakness::Risk::High, 821));
			weaknessList->Add(gcnew Weakness(gcnew System::String("Missing synchronization"), Weakness::Risk::High, 820));
			weaknessList->Add(gcnew Weakness(gcnew System::String("Incorrect Calculation"), Weakness::Risk::High, 682));
			weaknessList->Add(gcnew Weakness(gcnew System::String("Expression is always true"), Weakness::Risk::High, 571));
			weaknessList->Add(gcnew Weakness(gcnew System::String("Expression is always false"), Weakness::Risk::High, 570));
			weaknessList->Add(gcnew Weakness(gcnew System::String("Unused or redundant assignement"), Weakness::Risk::High, 563));
			weaknessList->Add(gcnew Weakness(gcnew System::String("Division by zero"), Weakness::Risk::High, 369));
			weaknessList->Add(gcnew Weakness(gcnew System::String("Race condition"), Weakness::Risk::High, 457));
			weaknessList->Add(gcnew Weakness(gcnew System::String("Integer Overflow"), Weakness::Risk::High, 190));
			weaknessList->Add(gcnew Weakness(gcnew System::String("Integer Underflow"), Weakness::Risk::High, 191));		

		}

	protected:
		/// <summary>
		/// Clean up any resources being used.
		/// </summary>
		~MainForm()
		{
			if (components)
			{
				delete components;
			}
		}

	/*
	*	Open Button Clicked:
	*
	*	Load file explorer, when a file is opened, load it and display it into the fileList.
	*	Behind the scenes, read the file in the console window to show it is being read correctly.
	*/
	private: System::Void openButton_Click(System::Object^  sender, System::EventArgs^  e) {
		if (openFileDialog1->ShowDialog() == System::Windows::Forms::DialogResult::OK)
		{
			if (userSettingsUI->useFullFilePath) {
				fileList->Items->Add(openFileDialog1->FileName);
				
			}
			else {
				fileList->Items->Add(openFileDialog1->SafeFileName);
				
			}
			
			filepathList->Add(openFileDialog1->FileName);

			String^ temp = "";
			System::IO::StreamReader^ sr = gcnew
				System::IO::StreamReader(openFileDialog1->FileName);
			temp = sr->ReadToEnd();
			fileContentList->Add(temp);
			sr->Close();
			if (DEBUG) {
				Console::WriteLine(
					"ADDED:\n" +
					temp + "\n" +
					openFileDialog1->FileName + "\n" +
					openFileDialog1->SafeFileName + "\n" +
					SEPERATOR);
			}
		}
	}

	/*
	*	Run Button Clicked:
	*
	*	If no files are selected, show a warning and don't run. Otherwise, start going through and scanning the files for weaknesses.
	*/
	private: System::Void runButton_Click(System::Object^  sender, System::EventArgs^  e) {
		if (fileList->Items->Count == 0)
		{
			MessageBox::Show("Select Files to Run Test!");
		}
		else {
			
			//Test Case:
			String^ fileName = fileList->Items[0]->ToString();
			std::ifstream infile{ msclr::interop::marshal_as<std::string>(fileName) };
			std::string file_contents{ std::istreambuf_iterator<char>(infile), std::istreambuf_iterator<char>() };
			System::Console::WriteLine("This is the test case string right here:");
			System::Console::WriteLine(fileName);
			System::Console::WriteLine(marshal_as<String^>(file_contents));


			String^ outputName = System::String::Format("{0:yyyy - MM - dd_hh - mm - ss - tt}", System::DateTime::Now) + ".txt";
			String^ outputPath = userSettingsUI->getPath() + "/" + outputName;
			if (!System::IO::File::Exists(outputPath)) {
				System::IO::File::CreateText(outputPath)->Close();
			}
			System::IO::StreamWriter^ sw = gcnew System::IO::StreamWriter(outputPath);
			sw->WriteLine("Software Integrity Tester Version " + VERSION);
			sw->WriteLine("Files Scanned: ");
			for (int i = 0; i < fileList->Items->Count; i++) {
				sw->WriteLine(fileList->Items[i]->ToString() + "  ");
			}
			sw->WriteLine(SEPERATOR);
			sw->WriteLine("Weaknesses Searched For: ");
			for (int i = 0; i < weaknessList->Count; i++) 
			{
				sw->WriteLine(weaknessList[i]->ToString());
			}
			sw->WriteLine(SEPERATOR);
			sw->Close();
			//we can throw this code in another method called generate report or something and have the actual scan in here, or just have the scan run after and 
			//append the vulnerabilities onto the file.
			//also we need the actual scan before we can generate a report on what has weaknesses and what doesn't
		}
	}
	public: bool isDisplayPathChecked = false; //Boolean to check if check box in settings is checked!

	/*
	*	Left Clicked to Remove Selected Files:
	*
	*	Gather the index of the selected files in the box, remove them from the fileList and from the data structure containing the weaknesses.
	*
	*/
	private: System::Void removeSelectedFilesToolStripMenuItem_Click(System::Object^  sender, System::EventArgs^  e) {
		for (int i = fileList->SelectedIndices->Count - 1; i >= 0; i--) {
			int index = fileList->SelectedIndices[i];
			if (DEBUG) {
				Console::WriteLine(
					"REMOVING: index " + fileList->SelectedIndices[i] + "\n" +
					fileContentList[index] + "\n" +
					filepathList[index] + "\n" +
					fileList->Items[index]->ToString() + "\n");
				if (i == 0)
					Console::WriteLine(SEPERATOR);
			}

			fileContentList->RemoveAt(index);
			filepathList->RemoveAt(index);
			fileList->Items->RemoveAt(index);
		}
	}

	private: System::Void filterToolStripMenuItem_Click(System::Object^  sender, System::EventArgs^  e) {
	}

	/*
	*	On Start:
	*
	*	If we set a boolean to true, it wil show the message box containing the version number and such when started. Currently set to false
	*/
	private: System::Void MainForm_Shown(System::Object^  sender, System::EventArgs^  e) {
		if (versionOnStart)
			MessageBox::Show(VERSION + "\n" + VERSIONTEXT);
	}

	
	/*
	*	When Help menu option is click
	*
	*	Show the message box with the helptext
	*/
	private: System::Void aboutToolStripMenuItem_Click(System::Object^  sender, System::EventArgs^  e) {
		MessageBox::Show(HELPTEXT, "Software Integrity Tester Instructions");
	}

	/*
	*	When the Filter Menu Option is Selected:
	*
	*	Create a new filtersettings form and display it.
	*/
	private: System::Void filterByWeaknessToolStripMenuItem_Click(System::Object^  sender, System::EventArgs^  e) {
		FilterSettings^ fs = gcnew FilterSettings(weaknessList);
		fs->Show();
	}

	/*
	*	When the settings button is clicked:
	*	
	*	Show the userSettings form (which is hidden when the close button is clicked
	*/
	private: System::Void settingsBtn_Click(System::Object^  sender, System::EventArgs^  e) {
		//UserSettings^ us = gcnew UserSettings();
		userSettingsUI->Show();
	}

	/*
	*	When the main form is loaded:
	*
	*	Check to see if there is a file that remembered the last settings. If there was, set the path, if not, create a file.
	*/
	private: System::Void MainForm_Load(System::Object^  sender, System::EventArgs^  e) {
		if (!System::IO::Directory::Exists(settingsPath)) {
			System::IO::Directory::CreateDirectory(settingsPath); //before program opens, creates a user settings directory on desktop
		}
		if (!System::IO::File::Exists(location))
		{
			System::IO::File::CreateText(location)->Close(); //creates file for output location if there was no file beforehand
		}
		else {
			System::IO::StreamReader^ sr = gcnew System::IO::StreamReader(location);
			userSettingsUI->setPath(sr->ReadLine()); //sets the path to the last saved path if there was a file previously
			sr->Close();
		}
		//still need file for the filters, what was checked and unchecked before a user last exited
		//same process, just create another file, still have to figure out how to check what was checked and then write it into a file
	}

	/*
	*	When the main form is about to close:
	*
	*	Save the settings and path before closing.
	*/
	private: System::Void MainForm_FormClosed(System::Object^  sender, System::Windows::Forms::FormClosedEventArgs^  e) {
		System::IO::StreamWriter^ sw = gcnew System::IO::StreamWriter(location, false);
		sw->Write(userSettingsUI->getPath()); //overwrites the location file so that whatever outputpath was last used gets saved in the file
		sw->Close();
		//again, still need filter settings to be written/saved
	}


	private: System::Windows::Forms::ContextMenuStrip^  contextMenuStrip1;
	private: System::Windows::Forms::ToolStripMenuItem^  removeSelectedFilesToolStripMenuItem;
	private: System::Windows::Forms::MenuStrip^  menuStrip1;
	private: System::Windows::Forms::ToolStripMenuItem^  filterToolStripMenuItem;
	private: System::Windows::Forms::ToolStripMenuItem^  helpToolStripMenuItem;
	private: System::Windows::Forms::Button^  openButton;
	private: System::Windows::Forms::Button^  runButton;
	private: System::Windows::Forms::Label^  versionLabel;
	private: System::Windows::Forms::ListBox^  fileList;
	private: System::Windows::Forms::OpenFileDialog^  openFileDialog1;
	private: System::ComponentModel::IContainer^  components;
	private: System::Windows::Forms::Button^  settingsBtn;

#pragma region Windows Form Designer generated code
			 /// <summary>
			 /// Required method for Designer support - do not modify
			 /// the contents of this method with the code editor.
			 /// </summary>
			 void InitializeComponent(void)
			 {
				 this->components = (gcnew System::ComponentModel::Container());
				 System::ComponentModel::ComponentResourceManager^  resources = (gcnew System::ComponentModel::ComponentResourceManager(MainForm::typeid));
				 this->menuStrip1 = (gcnew System::Windows::Forms::MenuStrip());
				 this->filterToolStripMenuItem = (gcnew System::Windows::Forms::ToolStripMenuItem());
				 this->filterByWeaknessToolStripMenuItem = (gcnew System::Windows::Forms::ToolStripMenuItem());
				 this->helpToolStripMenuItem = (gcnew System::Windows::Forms::ToolStripMenuItem());
				 this->aboutToolStripMenuItem = (gcnew System::Windows::Forms::ToolStripMenuItem());
				 this->openButton = (gcnew System::Windows::Forms::Button());
				 this->runButton = (gcnew System::Windows::Forms::Button());
				 this->versionLabel = (gcnew System::Windows::Forms::Label());
				 this->fileList = (gcnew System::Windows::Forms::ListBox());
				 this->contextMenuStrip1 = (gcnew System::Windows::Forms::ContextMenuStrip(this->components));
				 this->removeSelectedFilesToolStripMenuItem = (gcnew System::Windows::Forms::ToolStripMenuItem());
				 this->openFileDialog1 = (gcnew System::Windows::Forms::OpenFileDialog());
				 this->settingsBtn = (gcnew System::Windows::Forms::Button());
				 this->menuStrip1->SuspendLayout();
				 this->contextMenuStrip1->SuspendLayout();
				 this->SuspendLayout();
				 // 
				 // menuStrip1
				 // 
				 this->menuStrip1->ImageScalingSize = System::Drawing::Size(20, 20);
				 this->menuStrip1->Items->AddRange(gcnew cli::array< System::Windows::Forms::ToolStripItem^  >(2) {
					 this->filterToolStripMenuItem,
						 this->helpToolStripMenuItem
				 });
				 this->menuStrip1->Location = System::Drawing::Point(0, 0);
				 this->menuStrip1->Name = L"menuStrip1";
				 this->menuStrip1->Padding = System::Windows::Forms::Padding(8, 4, 0, 4);
				 this->menuStrip1->Size = System::Drawing::Size(704, 44);
				 this->menuStrip1->TabIndex = 0;
				 this->menuStrip1->Text = L"menuStrip1";
				 // 
				 // filterToolStripMenuItem
				 // 
				 this->filterToolStripMenuItem->DropDownItems->AddRange(gcnew cli::array< System::Windows::Forms::ToolStripItem^  >(1) { this->filterByWeaknessToolStripMenuItem });
				 this->filterToolStripMenuItem->Name = L"filterToolStripMenuItem";
				 this->filterToolStripMenuItem->Size = System::Drawing::Size(80, 36);
				 this->filterToolStripMenuItem->Text = L"Filter";
				 // 
				 // filterByWeaknessToolStripMenuItem
				 // 
				 this->filterByWeaknessToolStripMenuItem->Name = L"filterByWeaknessToolStripMenuItem";
				 this->filterByWeaknessToolStripMenuItem->Size = System::Drawing::Size(323, 38);
				 this->filterByWeaknessToolStripMenuItem->Text = L"Filter by weakness...";
				 this->filterByWeaknessToolStripMenuItem->Click += gcnew System::EventHandler(this, &MainForm::filterByWeaknessToolStripMenuItem_Click);
				 // 
				 // helpToolStripMenuItem
				 // 
				 this->helpToolStripMenuItem->DropDownItems->AddRange(gcnew cli::array< System::Windows::Forms::ToolStripItem^  >(1) { this->aboutToolStripMenuItem });
				 this->helpToolStripMenuItem->Name = L"helpToolStripMenuItem";
				 this->helpToolStripMenuItem->Size = System::Drawing::Size(77, 36);
				 this->helpToolStripMenuItem->Text = L"Help";
				 // 
				 // aboutToolStripMenuItem
				 // 
				 this->aboutToolStripMenuItem->Name = L"aboutToolStripMenuItem";
				 this->aboutToolStripMenuItem->Size = System::Drawing::Size(250, 38);
				 this->aboutToolStripMenuItem->Text = L"How to use...";
				 this->aboutToolStripMenuItem->Click += gcnew System::EventHandler(this, &MainForm::aboutToolStripMenuItem_Click);
				 // 
				 // openButton
				 // 
				 this->openButton->Location = System::Drawing::Point(18, 552);
				 this->openButton->Margin = System::Windows::Forms::Padding(4);
				 this->openButton->Name = L"openButton";
				 this->openButton->Size = System::Drawing::Size(180, 94);
				 this->openButton->TabIndex = 1;
				 this->openButton->Text = L"Open";
				 this->openButton->UseVisualStyleBackColor = true;
				 this->openButton->Click += gcnew System::EventHandler(this, &MainForm::openButton_Click);
				 // 
				 // runButton
				 // 
				 this->runButton->Location = System::Drawing::Point(206, 552);
				 this->runButton->Margin = System::Windows::Forms::Padding(4);
				 this->runButton->Name = L"runButton";
				 this->runButton->Size = System::Drawing::Size(180, 94);
				 this->runButton->TabIndex = 2;
				 this->runButton->Text = L"Run";
				 this->runButton->UseVisualStyleBackColor = true;
				 this->runButton->Click += gcnew System::EventHandler(this, &MainForm::runButton_Click);
				 // 
				 // versionLabel
				 // 
				 this->versionLabel->AutoSize = true;
				 this->versionLabel->Location = System::Drawing::Point(594, 662);
				 this->versionLabel->Margin = System::Windows::Forms::Padding(4, 0, 4, 0);
				 this->versionLabel->Name = L"versionLabel";
				 this->versionLabel->Size = System::Drawing::Size(85, 25);
				 this->versionLabel->TabIndex = 4;
				 this->versionLabel->Text = L"Version";
				 // 
				 // fileList
				 // 
				 this->fileList->ContextMenuStrip = this->contextMenuStrip1;
				 this->fileList->Font = (gcnew System::Drawing::Font(L"Calibri Light", 12));
				 this->fileList->FormattingEnabled = true;
				 this->fileList->HorizontalScrollbar = true;
				 this->fileList->ItemHeight = 39;
				 this->fileList->Location = System::Drawing::Point(18, 65);
				 this->fileList->Margin = System::Windows::Forms::Padding(4);
				 this->fileList->Name = L"fileList";
				 this->fileList->SelectionMode = System::Windows::Forms::SelectionMode::MultiExtended;
				 this->fileList->Size = System::Drawing::Size(684, 355);
				 this->fileList->TabIndex = 5;
				 this->fileList->SelectedIndexChanged += gcnew System::EventHandler(this, &MainForm::fileList_SelectedIndexChanged);
				 // 
				 // contextMenuStrip1
				 // 
				 this->contextMenuStrip1->ImageScalingSize = System::Drawing::Size(32, 32);
				 this->contextMenuStrip1->Items->AddRange(gcnew cli::array< System::Windows::Forms::ToolStripItem^  >(1) { this->removeSelectedFilesToolStripMenuItem });
				 this->contextMenuStrip1->Name = L"contextMenuStrip1";
				 this->contextMenuStrip1->Size = System::Drawing::Size(329, 40);
				 // 
				 // removeSelectedFilesToolStripMenuItem
				 // 
				 this->removeSelectedFilesToolStripMenuItem->Name = L"removeSelectedFilesToolStripMenuItem";
				 this->removeSelectedFilesToolStripMenuItem->Size = System::Drawing::Size(328, 36);
				 this->removeSelectedFilesToolStripMenuItem->Text = L"Remove Selected Files";
				 this->removeSelectedFilesToolStripMenuItem->Click += gcnew System::EventHandler(this, &MainForm::removeSelectedFilesToolStripMenuItem_Click);
				 // 
				 // openFileDialog1
				 // 
				 this->openFileDialog1->FileName = L"openFileDialog1";
				 // 
				 // settingsBtn
				 // 
				 this->settingsBtn->Location = System::Drawing::Point(528, 552);
				 this->settingsBtn->Margin = System::Windows::Forms::Padding(6);
				 this->settingsBtn->Name = L"settingsBtn";
				 this->settingsBtn->Size = System::Drawing::Size(178, 94);
				 this->settingsBtn->TabIndex = 7;
				 this->settingsBtn->Text = L"Settings";
				 this->settingsBtn->UseVisualStyleBackColor = true;
				 this->settingsBtn->Click += gcnew System::EventHandler(this, &MainForm::settingsBtn_Click);
				 // 
				 // MainForm
				 // 
				 this->AutoScaleDimensions = System::Drawing::SizeF(12, 25);
				 this->AutoScaleMode = System::Windows::Forms::AutoScaleMode::Font;
				 this->ClientSize = System::Drawing::Size(704, 623);
				 this->Controls->Add(this->settingsBtn);
				 this->Controls->Add(this->fileList);
				 this->Controls->Add(this->versionLabel);
				 this->Controls->Add(this->runButton);
				 this->Controls->Add(this->openButton);
				 this->Controls->Add(this->menuStrip1);
				 this->Icon = (cli::safe_cast<System::Drawing::Icon^>(resources->GetObject(L"$this.Icon")));
				 this->MainMenuStrip = this->menuStrip1;
				 this->Margin = System::Windows::Forms::Padding(4);
				 this->MaximizeBox = false;
				 this->MaximumSize = System::Drawing::Size(730, 694);
				 this->MinimumSize = System::Drawing::Size(730, 694);
				 this->Name = L"MainForm";
				 this->Text = L"Software Integrity Tester";
				 this->FormClosed += gcnew System::Windows::Forms::FormClosedEventHandler(this, &MainForm::MainForm_FormClosed);
				 this->Load += gcnew System::EventHandler(this, &MainForm::MainForm_Load);
				 this->Shown += gcnew System::EventHandler(this, &MainForm::MainForm_Shown);
				 this->menuStrip1->ResumeLayout(false);
				 this->menuStrip1->PerformLayout();
				 this->contextMenuStrip1->ResumeLayout(false);
				 this->ResumeLayout(false);
				 this->PerformLayout();

			 }
#pragma endregion
private: System::Void fileList_SelectedIndexChanged(System::Object^  sender, System::EventArgs^  e) {
}
};
}
