package parser.exceptions;

public final class NoThenException extends ChMissingException{
    public NoThenException() {
        super("if语句块内缺少then。");
    }
}
