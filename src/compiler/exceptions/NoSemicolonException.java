package compiler.exceptions;

public final class NoSemicolonException extends ChMissingException{
    public NoSemicolonException() {
        super("缺少引号\";\"。");
    }
}
