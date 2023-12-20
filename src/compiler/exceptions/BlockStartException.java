package compiler.exceptions;

public final class BlockStartException extends ParserException {
    public BlockStartException() {
        super("block语句块错误，请确认block语句中只能存在变量定义、常量定义或者主体过程");
    }
}
