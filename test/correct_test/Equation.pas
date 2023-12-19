program EquationTest;
    var answer1, inputa, inputb, inputc;
    procedure equationHasAnswer(input1, input2, input3);
        var D;
        procedure delta(a, b, c);
            begin
                D := b * b - 4 * a * c;
                write(D);
                if D >= 0 then
                    answer1 := 1
                else
                    answer1 := 0
            end
        begin
            call delta(input1, input2, input3);
            write(D)
        end
    begin
        read(inputa, inputb, inputc);
        call equationHasAnswer(inputa, inputb, inputc);
        write(answer1)
    end