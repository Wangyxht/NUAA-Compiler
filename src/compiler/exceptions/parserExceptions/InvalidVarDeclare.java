package compiler.exceptions.parserExceptions;

public final class InvalidVarDeclare extends ParserException{
    public InvalidVarDeclare() {
        super("非法的变量定义，请注意变量定义格式。");
    }
}
