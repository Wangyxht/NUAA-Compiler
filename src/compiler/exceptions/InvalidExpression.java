package compiler.exceptions;

public final class InvalidExpression extends ParserException{
    public InvalidExpression() {
        super("非法的表达式，请检查表达式是否正确。");
    }
}
