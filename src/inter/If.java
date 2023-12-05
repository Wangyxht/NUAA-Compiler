package inter;

public class If extends Stmt {

    protected Stmt statement;

    protected Expr expression;

    public If(Expr expression, Stmt statement) {
        super();
        this.expression = expression;
        this.statement = statement;
    }

}
