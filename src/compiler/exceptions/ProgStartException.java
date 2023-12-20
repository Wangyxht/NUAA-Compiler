package compiler.exceptions;

public final class ProgStartException extends ParserException{
    public ProgStartException() {
        super("PASCAL未找到程序入口\"program\"，\"program\"是否漏写或错写？");
    }
}
