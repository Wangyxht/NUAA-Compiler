package inter;

public class While extends Stmt {
    private Expr expression;
    private Stmt statement;

    public While(Expr expression, Stmt statement) {
        super();
        this.expression = expression;
        this.statement = statement;
    }
}
