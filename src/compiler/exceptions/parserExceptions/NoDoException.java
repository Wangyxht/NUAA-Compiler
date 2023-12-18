package compiler.exceptions.parserExceptions;

public final class NoDoException extends ChMissingException{
    public NoDoException() {
        super("while语句块内缺少do");
    }
}
