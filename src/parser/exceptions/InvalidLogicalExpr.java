package parser.exceptions;

public final class InvalidLogicalExpr extends ParserException {

    public InvalidLogicalExpr() {
        super("非法的逻辑表达式。");
    }
}
