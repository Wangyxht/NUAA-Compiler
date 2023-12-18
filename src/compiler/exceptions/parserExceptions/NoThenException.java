package compiler.exceptions.parserExceptions;

public final class NoThenException extends ChMissingException{
    public NoThenException() {
        super("if语句块内缺少then。");
    }
}
