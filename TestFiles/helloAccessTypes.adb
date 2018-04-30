with Ada.Text_IO, Ada.Unchecked_Deallocation; use Ada.Text_IO,Ada.Unchecked_Deallocation;
procedure Hello is
type Vector     is array (Integer range <>) of Float;
type Vector_Ref is access Vector;
--hello this is a test comment
procedure Free is new Ada.Unchecked_Deallocation(Object => Vector, Name => Vector_Ref);
VA, VB: Vector_Ref;
V : Vector;
begin
   VA := new Vector(1..10);
   VB := VA;
   Free(VB);
   V:=VB;
   Put_Line ("Hello WORLD!");
   i:Integer;
   i:=0;
end Hello;
