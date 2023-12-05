package parser.exceptions;

public final class NoCommaException extends ChMissingException{
    public NoCommaException() {
        super("缺少逗号\",\"。");
    }
}
