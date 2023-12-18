package compiler.exceptions.parserExceptions;

public final class InvalidConstDeclare extends ParserException{
    public InvalidConstDeclare() {
        super("非法的常量定义，请注意常量定义格式。");
    }
}
