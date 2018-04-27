-- Joseph Antaki
-- Considers the performance of two medical tests on a list of patients, and
-- decides which test, if either, is more accurate.
-- Read a total number of patients N, then read data of N patients, each on its
-- own line. Output probabilities of test correctness, and the better test.

with Ada.Text_IO, Ada.Integer_Text_IO, Ada.Float_Text_IO;
use Ada.Text_IO, Ada.Integer_Text_IO, Ada.Float_Text_IO;

procedure medical is
-- main procedure. Prompts the user for input, then prints final results.
        D_pos1_prob, D_pos2_prob, H_neg1_prob, H_neg2_prob: Float;
        total_patients: Positive;
   procedure crunch_numbers(p1, p2, p3, p4: out Float) is
   -- handles all numerics: array filling, counting and calculating.
           subtype ListSize is Positive range 1..total_patients;
           subtype DataSize is Positive range 1..3;
           subtype PossibleValues is Integer range 0..1;
           type PatientArray is array(ListSize, DataSize) of PossibleValues;
           patients: PatientArray;
           pos1_counter, pos2_counter, D_pos1_counter, D_pos2_counter, H_neg1_counter,
                H_neg2_counter: Float := 0.0;
        procedure fill_array is
        -- populate the 2D array with the user's input, ignore spaces in between numbers.
                delimiter: Character;
        begin
                for i in ListSize loop
                        get(delimiter);
                        get(delimiter);
                        for j in DataSize loop
                                get(patients(i, j));
                                exit when j = DataSize'Last;
                                get(delimiter);
                        end loop;
                end loop;
        end fill_array;

        procedure tabulate is
        -- count up all patients matching the criteria we're interested in
	 begin
                for i in ListSize loop
                        if patients(i, 2) = 1 then
                                pos1_counter := pos1_counter + 1.0;
                                if patients(i, 1) = 1 then
                                        D_pos1_counter := D_pos1_counter + 1.0;
                                end if;
                        elsif patients(i, 1) = 0 then -- patient is negative for test 1.
                                H_neg1_counter := H_neg1_counter + 1.0;
                        end if;
                        if patients(i, 3) = 1 then
                                pos2_counter := pos2_counter + 1.0;
                                if patients(i, 1) = 1 then
                                        D_pos2_counter := D_pos2_counter + 1.0;
                                end if;
                        elsif patients(i, 1) = 0 then -- patient is negative for test 2.
                                H_neg2_counter := H_neg2_counter + 1.0;
                        end if;
                end loop;
        end tabulate;

        procedure calculate (prob1: out Float; prob2: out Float; prob3: out Float;
                prob4: out Float) is
        -- compute individual probabilities and write them to variables.
        begin
                prob1 := D_pos1_counter / pos1_counter;
                prob2 := D_pos2_counter / pos2_counter;
                prob3 := H_neg1_counter / (float(total_patients)-pos1_counter);
                prob4 := H_neg2_counter / (float(total_patients)-pos2_counter);
        end calculate;
    begin -- crunch_numbers
        fill_array;
        tabulate;
        calculate(p1, p2, p3, p4);
    end crunch_numbers;
begin -- medical
   put("How many patients are there? ");
        get(total_patients);
        put("Input patient data one line at a time.");
        New_Line;
        crunch_numbers(D_pos1_prob, D_pos2_prob, H_neg1_prob, H_neg2_prob);
        put("P(D | Pos1) = ");
        put(D_pos1_prob, Exp => 0, Fore => 0, Aft => 2);
        New_Line;
        put("P(D | Pos2) = ");
        put(D_pos2_prob, Exp => 0, Fore => 0, Aft => 2);
        New_Line;
        put("P(H | Neg1) = ");
        put(H_neg1_prob, Exp => 0, Fore => 0, Aft => 2);
        New_Line;
        put("P(H | Neg2) = ");
        put(H_neg2_prob, Exp => 0, Fore => 0, Aft => 2);
        New_Line;

        if D_pos1_prob > D_pos2_prob and H_neg1_prob > H_neg2_prob then
                put("Test 1");
        elsif D_pos2_prob > D_pos1_prob and H_neg2_prob > H_neg1_prob then
                put("Test 2");
        else
                put("Neither test");
        end if;
        put(" is better.");
exception
        when Data_Error | Constraint_Error =>
                Skip_Line;
                put_line("Error: invalid input");
                medical;
end medical; 