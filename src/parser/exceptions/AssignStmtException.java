package parser.exceptions;

public final class AssignStmtException extends ParserException{
    public AssignStmtException() {
        super("非法的赋值表达式，请确认赋值表达式格式");
    }
}
