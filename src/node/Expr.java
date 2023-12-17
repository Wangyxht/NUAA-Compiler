package node;

import lexer.Token;

import java.util.ArrayList;

/**
 *
 */
public class Expr extends Node {

    private Token base_token;

    public Expr(Token base_token) {
        super();
        this.base_token = base_token;
    }
}
