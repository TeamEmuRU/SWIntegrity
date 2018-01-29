-- Ada sample program
-- N. L. Tinkham

with Ada.Text_IO, Ada.Integer_Text_IO;
use Ada.Text_IO, Ada.Integer_Text_IO;

procedure Fibonacci is
-- Read Max_Value, then print the numbers of the Fibonacci sequence that are
-- less than or equal to Max_Value.

	Max_Value:	Integer;	-- Print numbers up to this value
	Previous:	Integer;	-- Previous number in the sequence
	Current:	Integer;	-- Current number in the sequence
	Next:		Integer;	-- Next number in the sequence
begin
	-- Read a positive integer for Max_Value;
	-- repeat if user enters 0 or a negative number,
	-- until a positive number is entered.

	loop
		Put("Ending value for Fibonacci numbers: ");
		Get(Max_Value);
		exit when Max_Value >= 1;
		Put_Line("Please enter a positive number.");
	end loop;

	-- Calculate and print Fibonacci sequence values

	Previous := 1;
	Current := 1;
	Put(Previous);
	New_Line;
	while Current <= Max_Value loop
		Put(Current);
		New_Line;
		Next := Previous + Current;
		Previous := Current;
		Current := Next;
	end loop;
end Fibonacci;