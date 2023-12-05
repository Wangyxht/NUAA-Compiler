package parser.exceptions;

public final class InvalidStatement extends ParserException{
    public InvalidStatement() {
        super("非法的语句，请检查该语句是否正确。");
    }

}
