program progTest;
    var hasAnswer, sum, isEven, inputa, inputb, inputc;
    procedure equationHasAnswer(input1, input2, input3);
        var D;
        procedure delta(a, b, c);
            begin
                D := b * b - 4 * a * c;
                write(D);
                if D >= 0 then
                    hasAnswer := 1
                else
                    hasAnswer := 0
            end
        begin
            call delta(input1, input2, input3);
            write(D)
        end

    procedure fibonacci(n);
        begin
            if n <= 2
                fibn := 1
            else
            begin
                t := 0;
                call fibonacci(n-1)
                t := t + fibn;
                call fibonacci(n-2)
                t := t + fibn;
                fibn := t
            end
        end

    procedure myMultiply(m, k);
    var i, sum;
    begin
        i := 0;
        sum := 0;
        while i < k do
        begin
            sum := sum + m;
            i := i + 1
        end;
        write(sum);
        call isEvenNum(sum)
    end

    procedure isEvenNum(x);
    begin
        if odd x+1 then
            isEven :=1
        else
            isEven :=0
    end

    begin
        read(inputa, inputb, inputc);
        call equationHasAnswer(inputa, inputb, inputc);
        write(hasAnswer);

        read(inputa);
        call fibonacci(inputa);
        write(fibn);

        read(inputa, inputb, inputc);
        call myMultiply(inputa * inputb + inputc, inputc);
        write(isEven)
    end