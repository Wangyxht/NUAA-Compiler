package compiler.exceptions.parserExceptions;

public final class ReadIDEmptyException extends ParserException{
    public ReadIDEmptyException() {
        super("Read参数不可为空。");
    }
}
