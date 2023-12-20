package compiler.exceptions;

public final class InvalidCall extends ParserException{
    public InvalidCall() {
        super("非法的函数名调用。");
    }
}
