package inter;

public class If_with_Else extends If {

    private Stmt statement_else;

    public If_with_Else(Expr expression, Stmt statement_if, Stmt statement_else) {
        super(expression, statement_if);
        this.statement_else = statement_else;
    }
}
