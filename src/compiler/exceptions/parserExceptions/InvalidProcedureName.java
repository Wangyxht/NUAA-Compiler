package compiler.exceptions.parserExceptions;

public final class InvalidProcedureName extends ParserException{
    public InvalidProcedureName() {
        super("非法的过程名声明，请确认过程名声明格式。");
    }
}
