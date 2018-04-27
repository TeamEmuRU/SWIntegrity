with Ada.Text_IO, Ada.Unchecked_Deallocation; use Ada.Text_IO,Ada.Unchecked_Deallocation;
procedure Hello is
type Vector     is array (Integer range <>) of Float;
type Vector_Ref is access Vector;
--hello this is a test comment
procedure Free is new Ada.Unchecked_Deallocation(Object => Vector, Name => Vector_Ref);
A: Vector_Ref;
B: Vector_Ref;
V : Vector;
begin
   A := new Vector(1..10);
   B := A;
   Free(A);
   V:=B;
   Put_Line ("Hello WORLD!");
   i:Integer;
   i:=0;
end Hello;