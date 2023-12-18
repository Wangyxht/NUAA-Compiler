package node;

public class Block extends Stmt{

    public Integer varNum;

    public Integer bodyEntry;

    Stmt proc;

    Stmt body;

    public Block(Integer bodyEntry, Integer varNum ,Stmt proc, Stmt body) {
        this.bodyEntry = bodyEntry;
        this.varNum = varNum;
        this.proc = proc;
        this.body = body;
    }
}
