package node;


import lexer.Token;

/**
 * Unary 类用于处理单目的运算符，<br>
 * 同时辅助 term exp 的右递归分析。
 */
public final class Unary extends Expr {
    private Expr expression;

    public Unary(Token op, Expr expression) {
        super(op);
        this.expression = expression;
    }
}
