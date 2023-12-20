package compiler.exceptions;

public final class ReadIDException extends ParserException{
    public ReadIDException() {
        super("Read参数列表只能为标识符，请确认Read调用格式。");
    }
}
