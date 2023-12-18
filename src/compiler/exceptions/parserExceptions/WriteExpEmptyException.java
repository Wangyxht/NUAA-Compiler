package compiler.exceptions.parserExceptions;

public class WriteExpEmptyException extends ParserException{
    public WriteExpEmptyException() {
        super("Write参数不可为空。");
    }
}
