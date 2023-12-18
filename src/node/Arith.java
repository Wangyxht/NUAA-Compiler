package node;

import lexer.Token;

public class Arith extends Expr{

    private Expr expression_l;
    private Expr expression_r;

    public Arith(Token op, Expr expression_l, Expr expression_r) {
        super(op);
        this.expression_l = expression_l;
        this.expression_r = expression_r;
    }
}
