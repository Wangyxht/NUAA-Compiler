package node;

public class Block extends Stmt{

    public Integer varNum;

    Stmt proc;

    Stmt body;

    public Block(Integer varNum ,Stmt proc, Stmt body) {
        this.varNum = varNum;
        this.proc = proc;
        this.body = body;
    }
}
