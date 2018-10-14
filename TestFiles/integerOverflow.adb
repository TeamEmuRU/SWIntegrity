th Ada.Text_IO; use Ada.Text_IO;
 
procedure Overflow is
   A: Integer := Integer'last;
   B: Integer := 99999999999999999999999;
 
begin
   Put_Line("Types defined by the user:" + B );
   New_Line;
 
   Put_Line("Forcing a variable of type Integer to overflow:");
   loop -- endless loop
      A := A + 1; -- line 49 -- this will later raise a CONSTRAINT_ERROR
   end loop;
end Overflow;
