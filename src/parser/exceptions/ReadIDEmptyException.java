package parser.exceptions;

public final class ReadIDEmptyException extends ParserException{

    public ReadIDEmptyException() {
        super("Read参数不可为空。");
    }
}
