package parser.exceptions;

public final class InvalidAssign extends ParserException {
    public InvalidAssign() {
        super("非法的赋值语句，请检查赋值语句是否正确。");
    }
}
