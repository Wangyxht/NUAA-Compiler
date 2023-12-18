package pCode;

public enum OpCode {
    PLUS,
    MINUS,
    MUTI,
    DIV,
    OPPOSITE,
    ODD,
    EQ,
    NEQ,
    LT,
    GT,
    LE,
    GE,
    NEWLINE;

    static public OpCode getOp(int index){
        for(var opcode : OpCode.values()){
            if(opcode.ordinal() == index) return opcode;
        }
        return null;
    }
}
