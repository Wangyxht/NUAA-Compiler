package node;

public class Procedure extends Stmt{

    /** 某语句块 */
    Stmt block;
    /** 递归过程段 */
    Stmt proc;

    public Procedure(Stmt block, Stmt proc) {
        super();
        this.block = block;
        this.proc = proc;
    }
}
