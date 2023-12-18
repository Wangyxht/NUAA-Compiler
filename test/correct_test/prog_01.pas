program prog01;
    var a,b,c;
    const d := 1;
    procedure test01(x, y);
        begin
            c := d * 100 + x + y;
        end
    begin
        call test01(1, 2);
        write(c);
    end
