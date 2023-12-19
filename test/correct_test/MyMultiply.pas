program MyMultiply;
    var sum, isEven, inputa, inputb, inputc;
    procedure isEvenNum(x);
    begin
        if odd x+1 then
            isEven :=1
        else
            isEven :=0
    end;

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

    begin
        read(inputa, inputb, inputc);
        call myMultiply(inputa * inputb + inputc, inputc);
        write(isEven)
    end
