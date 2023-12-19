program fibonacciTest;
    var fibn;
    procedure fibonacci(n);
        var t;
        begin
            if n <= 2 then
                fibn := 1
            else
            begin
                t := 0;
                call fibonacci(n-1);
                t := t + fibn;
                call fibonacci(n-2);
                t := t + fibn;
                fibn := t
            end
        end
    begin
        fibn := 0;
        call fibonacci(10);
        write(fibn)
    end