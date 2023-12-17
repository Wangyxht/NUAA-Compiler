package node;

public class Procedure extends Stmt{
    /** 活动记录基本信息大小 */
    public Integer procSize;
    /** 某语句块 */
    Stmt block;
    /** 递归过程段 */
    Stmt proc;

    public Procedure(Integer procSize, Stmt block, Stmt proc) {
        this.procSize = procSize;
        this.block = block;
        this.proc = proc;
    }
}
