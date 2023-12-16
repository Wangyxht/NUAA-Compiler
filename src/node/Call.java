package node;

import lexer.Token;

import java.util.ArrayList;

public class Call extends Stmt {

    private Token ID;

    private ArrayList<Expr> args;

    public Call(Token ID, ArrayList<Expr> args) {
        super();
        this.ID = ID;
        this.args = args;
    }
}
