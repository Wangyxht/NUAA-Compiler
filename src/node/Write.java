package node;

import java.util.ArrayList;

public class Write extends Stmt {
    ArrayList<Expr> expressions;

    public Write(ArrayList<Expr> expressions) {
        this.expressions = expressions;
    }
}
