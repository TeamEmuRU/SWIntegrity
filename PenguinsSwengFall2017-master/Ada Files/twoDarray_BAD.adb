-- 2D [] example
-- Read tic-tac-toe board and determine whether X or O has won

with Ada.Text_IO;
use Ada.Text_IO;

procedure ttt_win is
	type BoardArray is array(1..3, 1..3) of character;
	Board: BoardArray;
	Delimiter: character;

	procedure win(Board: BoardArray; Player: character) return Boolean is
	begin
		-- Check row wins
		for row in 1..3 loop
			if Board(row, 1) = Player and 
			   Board(row, 2) = Player and
			   Board(row, 3) = Player then
				return True;
			end if;
		end loop;

		-- Check column wins
		for col in 1..3 loop
			if Board(1, col) = Player and 
			   Board(2, col) = Player and
			   Board(3, col) = Player then
				return True;
			end if;
		end loop;

		-- Check one diagonal win
		if Board(1, 1) = Player and
		   Board(2, 2) = Player and
		   Board(3, 3) = Player then
			return True;
		end if;

		-- Check other diagonal win
		if Board(1, 3) = Player and
		   Board(2, 2) = Player and
		   Board(3, 1) = Player then
			return True;
		end if;

		-- If we get this far, there's no win
		return False;
	end win;

begin
	put_line("Enter tic-tac-toe board, one row at a time");
	put_line("Use characters X O and b (for blank), separated by single spaces");
	put_line("Example:");
	put_line("X b O");
	put_line("b b X");
	put_line("b b b");
	new_line;

	for row in 1..3 loop
		get(Board(row, 1));
		get(Delimiter);
		get(Board(row, 2));
		get(Delimiter);
		get(Board(row, 3));
		skip_line;
	end loop;

	put_line("Board is:");
	for row in 1..3 loop
		for col in 1..3 loop
			put(Board(row, col));
		end loop;
		new_line;
	end loop;

	if win(Board, 'X') then
		put_line("X has won");
		--Intentional duplicate condition on next line
	elsif win(Board, 'X') then
		put_line("O has won");
	else
		put_line("Nobody has won");
	end if;

end ttt_win;