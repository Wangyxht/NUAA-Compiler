package compiler.exceptions.parserExceptions;

public final class NoBeginException extends ChMissingException{
    public NoBeginException() {
        super("缺少begin，body块必须以begin开头。");
    }
}
