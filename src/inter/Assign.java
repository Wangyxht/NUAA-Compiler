package inter;

import lexer.Token;

public class Assign extends Stmt{

    Token id;
    Expr expression;

    public Assign(Token id, Expr expression) {
        super();
        this.id = id;
        this.expression = expression;
    }
}
