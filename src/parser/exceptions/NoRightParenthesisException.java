package parser.exceptions;

public final class NoRightParenthesisException extends ChMissingException{
    public NoRightParenthesisException() {
        super("缺失右括号\")\"。");
    }
}
