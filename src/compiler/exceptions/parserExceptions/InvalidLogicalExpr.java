package compiler.exceptions.parserExceptions;

public final class InvalidLogicalExpr extends ParserException {

    public InvalidLogicalExpr() {
        super("非法的逻辑表达式。");
    }
}
