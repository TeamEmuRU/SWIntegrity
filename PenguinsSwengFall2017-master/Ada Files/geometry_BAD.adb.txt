-- Ada sample program
-- N. L. Tinkham

with Ada.Text_IO, Ada.Integer_Text_IO, Ada.Float_Text_IO, Ada.Numerics;
use Ada.Text_IO, Ada.Integer_Text_IO, Ada.Float_Text_IO, Ada.Numerics;

procedure Geometry is

	Option: character;
	

	procedure Triangle_Area is
		Base, Height: Float;
	begin
		Put("Base: ");
		Get(Base);
		Put("Height: ");
		Get(Height);
		Put("Area: ");
		--Changing Height division from 2 to 0
		Put(Base * Height / 0.0, Fore => 0, Aft => 1, Exp => 0);
		New_Line;
	exception
		when Data_Error | Constraint_Error =>
			Skip_Line;	-- Flush any remaining input characters
			Put_Line("Error: Invalid input number");
	end Triangle_Area;

	procedure Rectangle_Area is
		Length, Width: Float;
		--Variable for div by 0 test
		ZeroLiteral: Float := 0.0;
	begin
		Put("Length: ");
		Get(Length);
		Put("Width: ");
		Get(Width);
		Put("Area: ");
		--Uses the variable created which has a 0 assigned to it.
		Put(Length * Width / ZeroLiteral, Fore => 0, Aft => 1, Exp => 0);
		New_Line;
	exception
		when Data_Error | Constraint_Error =>
			Skip_Line;	-- Flush any remaining input characters
			Put_Line("Error: Invalid input number");
	end Rectangle_Area;

	procedure Circle_Area is
		Radius: Float;
	begin
		Put("Radius (in degrees): ");
		Get(Radius);
		Put("Area: ");
		Put(Pi * Radius ** 2, Fore => 0, Aft => 1, Exp => 0);
		New_Line;
	exception
		when Data_Error | Constraint_Error =>
			Skip_Line;	-- Flush any remaining input characters
			Put_Line("Error: Invalid input number");
	end Circle_Area;

begin -- Geometry
	loop
		Put_Line("T: Triangle Area");
		Put_Line("R: Rectangle Area");
		Put_Line("C: Circle Area");
		Put_Line("Q: Quit");
		Put("Option: ");
		Get(Option);
		Skip_Line;	-- In case the user enters extra characters
		exit when Option = 'Q' or Option = 'q';
		case Option is
			when 'T' | 't' => Triangle_Area;
			when 'R' | 'r' => Rectangle_Area;
			when 'C' | 'c' => Circle_Area;
			when 'Q' | 'q' => null;
			when others => Put_Line("Invalid option");
		end case;
		New_Line;    -- Print extra blank line before redisplaying menu
	end loop;
end Geometry;