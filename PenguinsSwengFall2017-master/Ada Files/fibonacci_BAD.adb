-- Ada sample program
-- N. L. Tinkham

with Ada.Text_IO, Ada.Integer_Text_IO;
use Ada.Text_IO, Ada.Integer_Text_IO;

procedure Fibonacci is
-- Read Max_Value, then print the numbers of the Fibonacci sequence that are
-- less than or equal to Max_Value.

	Max_Value: Integer;		-- Print numbers up to this value
	Previous: Integer;		-- Previous number in the sequence
	Current: Integer;		-- Current number in the sequence
	Next: Integer;			-- Next number in the sequence
begin
	-- Read a positive integer for Max_Value;
	-- repeat if user enters 0 or a negative number,
	-- until a positive number is entered.

	loop
		Put("Ending value for Fib numbers: ");
		Get(Max_Value);
		exit when Max_Value >= 1;
		Put_Line("Please enter a positive number.");
	end loop;

	procedure trackThisFunction is	--test weakness 2
	/0	--tests weakness 3
	
	-- Calculate and print Fibonacci sequence values

	--Commented out the assignment to previous, which will cause in error when it
	--is used in the put statement.
	--Previous := 1;
	Current := 1;
	Put(Previous);
	New_Line;
	
	procedure trackFunction2 is --tests weakness 2
	
	procedure track3 is --tests weakness 3
	
	while Current <= Max_Value loop
		Put(Current);
		New_Line;
		Next := Previous + Current;
		Previous := Current;
		Current := Next;
		track3;
	end loop;
end Fibonacci;