package compiler.exceptions;

public final class ProgIDException extends ParserException{
    public ProgIDException() {
        super("错误的程序标识符。");
    }
}
