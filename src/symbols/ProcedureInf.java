package symbols;

public class ProcedureInf extends SymbolInf {

    /** 嵌套深度 **/
    int depth;
    /** 过程代码地址 **/
    int addr;
    /** 过程分配空间大小 **/
    int size;

    public ProcedureInf(int depth, int addr, int size) {
        super(IDType.PROC_ID);
        this.depth = depth;
        this.addr = addr;
        this.size = size;
    }

    public int getDepth() {
        return depth;
    }

    public int getAddr() {
        return addr;
    }

    public int getSize() {
        return size;
    }
}
