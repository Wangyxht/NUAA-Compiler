package inter;

import lexer.Token;

import java.util.ArrayList;

public class Read extends Stmt {
    ArrayList<Token> IDList;

    public Read(ArrayList<Token> IDList) {
        this.IDList = IDList;
    }
}
