#include <iostream>
#include <cstdio>
#include <string>

#include <windows.h>
#include <sql.h>
#include <sqlext.h>
#include <sqltypes.h> 

using namespace std;    // to save us having to type std::

const int MAX_CHAR = 1024;

int main ( )
{
   SQLCHAR   SQLStmt[MAX_CHAR];
   char      strSQL[MAX_CHAR];
   char   chrTemp;

   SQLVARCHAR rtnFirstName[50];
   SQLVARCHAR rtnLastName[50];
   SQLVARCHAR rtnAddress[30];
   SQLVARCHAR rtnCity[30];
   SQLVARCHAR rtnState[3];
   SQLDOUBLE  rtnSalary;
   SQLVARCHAR rtnGender[1];
   SQLINTEGER rtnAge;

   // Get a handle to the database

   SQLHENV EnvironmentHandle;
   RETCODE retcode = SQLAllocHandle( SQL_HANDLE_ENV, SQL_NULL_HANDLE, &EnvironmentHandle );

   // Set the SQL environment flags

   retcode = SQLSetEnvAttr( EnvironmentHandle, SQL_ATTR_ODBC_VERSION, (SQLPOINTER) SQL_OV_ODBC3, SQL_IS_INTEGER );

   // create handle to the SQL database

   SQLHDBC ConnHandle;
   retcode = SQLAllocHandle( SQL_HANDLE_DBC, EnvironmentHandle, &ConnHandle );

   // Open the database using a System DSN

   retcode = SQLDriverConnect(ConnHandle, 
   NULL, 
   (SQLCHAR*)"DSN=PRG411;UID=myUser;PWD=myPass;", 
   SQL_NTS,
   NULL, 
   SQL_NTS, 
   NULL, 
   SQL_DRIVER_NOPROMPT);
   if (!retcode) 
   {
      cout << "SQLConnect() Failed";
   }
   else
   {
      // create a SQL Statement variable

      SQLHSTMT StatementHandle;
      retcode = SQLAllocHandle(SQL_HANDLE_STMT, ConnHandle, &StatementHandle);

      // Part 1: Create the Employee table (Database)

      do
      {
         cout << "Create the new table? ";
         cin >> chrTemp;
      } while (cin.fail());

      if (chrTemp == 'y' || chrTemp == 'Y')
      {
         strcpy((char *) SQLStmt, "CREATE TABLE [dbo].[Employee]([pkEmployeeID] [int] IDENTITY(1,1) NOT NULL,[FirstName] [varchar](50) NOT NULL,[LastName] [varchar](50) NOT NULL,[Address] [varchar](30) NOT NULL,[City] [varchar](30) NOT NULL,[State] [varchar](3) NOT NULL, [Salary] [double] NOT NULL,[Gender] [varchar](1) NOT NULL,  [Age] [int] NOT NULL, CONSTRAINT [PK_Employee] PRIMARY KEY CLUSTERED ([pkEmployeeID] ASC)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]) ON [PRIMARY]");
         retcode = SQLExecDirect(StatementHandle, SQLStmt, SQL_NTS);
      }

      // Part 2: Hardcode records into the table

      do
      {
         cout << "Add records to the table? ";
         cin >> chrTemp;
      } while (cin.fail());

      if (chrTemp == 'y' || chrTemp == 'Y')
      {
         strcpy((char *) SQLStmt, "INSERT INTO employee([FirstName], [LastName], [Address], [City], [State], [Salary], [Gender],[Age]) VALUES ('Mike','Slentz','123 Torrey Dr.','North Clairmont','CA', 48000.00 ,'M',34)");
         retcode = SQLExecDirect(StatementHandle, SQLStmt, SQL_NTS);

         strcpy((char *) SQLStmt, "INSERT INTO employee([FirstName], [LastName], [Address], [City], [State], [Salary], [Gender],[Age]) VALUES ('Sue','Vander Hayden','46 East West St.','San Diego','CA', 36000.00 ,'F',28)");
         retcode = SQLExecDirect(StatementHandle, SQLStmt, SQL_NTS);

         strcpy((char *) SQLStmt, "INSERT INTO employee([FirstName], [LastName], [Address], [City], [State], [Salary], [Gender],[Age]) VALUES ('Sharon','Stonewall','756 West Olive Garden Way','Plymouth','MA', 56000.00 ,'F',58)");
         retcode = SQLExecDirect(StatementHandle, SQLStmt, SQL_NTS);

         strcpy((char *) SQLStmt, "INSERT INTO employee([FirstName], [LastName], [Address], [City], [State], [Salary], [Gender],[Age]) VALUES ('James','Bartholemew','777 Praying Way','Falls Church','VA', 51000.00 ,'M',45)");
         retcode = SQLExecDirect(StatementHandle, SQLStmt, SQL_NTS);

         strcpy((char *) SQLStmt, "INSERT INTO employee([FirstName], [LastName], [Address], [City], [State], [Salary], [Gender],[Age]) VALUES ('Joe','Smith','111 North 43rd Ave','Peoria','AZ', 44000.00 ,'M', 40)");
         retcode = SQLExecDirect(StatementHandle, SQLStmt, SQL_NTS);

         strcpy((char *) SQLStmt, "INSERT INTO employee([FirstName], [LastName], [Address], [City], [State], [Salary], [Gender],[Age]) VALUES ('Michael','Smith','20344 North Swan Park','Phoenix','AZ', 24000.00 ,'M', 40)");
         retcode = SQLExecDirect(StatementHandle, SQLStmt, SQL_NTS);

         strcpy((char *) SQLStmt, "INSERT INTO employee([FirstName], [LastName], [Address], [City], [State], [Salary], [Gender],[Age]) VALUES ('Jennifer','Jones','123 West North Ave','Flagstaff','AZ', 40000.00 ,'F', 40)");
         retcode = SQLExecDirect(StatementHandle, SQLStmt, SQL_NTS);

         strcpy((char *) SQLStmt, "INSERT INTO employee([FirstName], [LastName], [Address], [City], [State], [Salary], [Gender],[Age]) VALUES ('Cora','York','33rd Park Way Drive','Mayville','MI', 30000.00 ,'F', 61)");
         retcode = SQLExecDirect(StatementHandle, SQLStmt, SQL_NTS);

         strcpy((char *) SQLStmt, "INSERT INTO employee([FirstName], [LastName], [Address], [City], [State], [Salary], [Gender],[Age]) VALUES ('Tom','Jefferson','234 Friendship Way','Battle Creek','MI', 41000.00 ,'M', 31)");
         retcode = SQLExecDirect(StatementHandle, SQLStmt, SQL_NTS);
      }

      // Part 3 & 4: Searchs based on criteria

      do
      {
         cout << "1. Display all records in the database" << endl;
         cout << "2. Display all records with age greater than 40" << endl;
         cout << "3. Display all records with salary over $30K" << endl;
         cout << "4. Exit" << endl << endl;

         do
         {
            cout << "Please enter a selection: ";
            cin >> chrTemp;
         } while (cin.fail());

         if (chrTemp == '1')
         {
            strcpy((char *) SQLStmt, "SELECT [FirstName], [LastName], [Address], [City], [State], [Salary], [Gender],[Age] FROM EMPLOYEE");
         }
         else if (chrTemp == '2')
         {
            strcpy((char *) SQLStmt, "SELECT [FirstName], [LastName], [Address], [City], [State], [Salary], [Gender],[Age] FROM EMPLOYEE WHERE [AGE] > 40");
         }
         else if (chrTemp == '3')
         {
            strcpy((char *) SQLStmt, "SELECT [FirstName], [LastName], [Address], [City], [State], [Salary], [Gender],[Age] FROM EMPLOYEE WHERE [Salary] > 30000");
         }

         if (chrTemp == '1'  || chrTemp == '2' || chrTemp == '3')
         {
            retcode = SQLExecDirect(StatementHandle, SQLStmt, SQL_NTS);

            SQLBindCol(StatementHandle, 1, SQL_C_CHAR, &rtnFirstName, sizeof(rtnFirstName), NULL );
            SQLBindCol(StatementHandle, 2, SQL_C_CHAR, &rtnLastName, sizeof(rtnLastName), NULL );
            SQLBindCol(StatementHandle, 3, SQL_C_CHAR, &rtnAddress, sizeof(rtnAddress), NULL );
            SQLBindCol(StatementHandle, 4, SQL_C_CHAR, &rtnCity, sizeof(rtnCity), NULL );
            SQLBindCol(StatementHandle, 5, SQL_C_CHAR, &rtnState, sizeof(rtnState), NULL );
            SQLBindCol(StatementHandle, 6, SQL_C_DOUBLE, &rtnSalary, sizeof(rtnSalary), NULL );
            SQLBindCol(StatementHandle, 7, SQL_C_CHAR, &rtnGender, sizeof(rtnGender), NULL );
            SQLBindCol(StatementHandle, 8, SQL_C_NUMERIC, &rtnAge, sizeof(rtnAge), NULL );

            for(;;) 
            {
               retcode = SQLFetch(StatementHandle);
               if (retcode == SQL_NO_DATA_FOUND) break;

               cout << rtnFirstName << " " << rtnLastName << " " << rtnAddress << " " << rtnCity << " " << rtnState << " " << rtnSalary << " " << rtnGender << "" << rtnAge << endl;
            }
         }
      } while (chrTemp != '4');

      SQLFreeStmt(StatementHandle, SQL_CLOSE );
      SQLFreeConnect(ConnHandle);
      SQLFreeEnv(EnvironmentHandle);

      printf( "Done.\n" );
   }

   return 0;
}
