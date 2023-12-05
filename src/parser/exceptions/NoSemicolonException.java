package parser.exceptions;

import lexer.Tag;
import lexer.Word;

public final class NoSemicolonException extends ChMissingException{
    public NoSemicolonException() {
        super("缺少引号\";\"。");
    }

    public static void main(String[] args) {
        NoSemicolonException e = new NoSemicolonException();
        e.PrintExceptionMessage();
        ProgStartException e1 = new ProgStartException();
        e1.setError_token(new Word("Program", Tag.PROGRAM));
        e1.PrintExceptionMessage();
    }

}
