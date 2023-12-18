package compiler.exceptions.parserExceptions;

public final class NoLeftParenthesisException extends ChMissingException{
    public NoLeftParenthesisException() {
        super("缺失左括号\"(\"。");
    }
}
