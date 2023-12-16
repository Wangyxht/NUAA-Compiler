package parser.exceptions;

import lexer.Tag;
import lexer.Word;

public final class NoSemicolonException extends ChMissingException{
    public NoSemicolonException() {
        super("缺少引号\";\"。");
    }
}
