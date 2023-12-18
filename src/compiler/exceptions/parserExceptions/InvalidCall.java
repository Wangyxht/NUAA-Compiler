package compiler.exceptions.parserExceptions;

public final class InvalidCall extends ParserException{
    public InvalidCall() {
        super("非法的函数名调用。");
    }
}
