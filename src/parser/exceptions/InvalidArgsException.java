package parser.exceptions;

public final class InvalidArgsException extends ParserException{

    public InvalidArgsException() {
        super("非法的参数声明，请注意参数声明格式正确。");
    }
}
