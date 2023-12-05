package inter;

public class Block extends Stmt{

    Stmt proc;
    Stmt body;

    public Block(Stmt proc, Stmt body) {
        this.proc = proc;
        this.body = body;
    }
}
