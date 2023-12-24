package symbols;

public class ProcedureInf extends SymbolInf {

    /** 嵌套深度 **/
    int depth;
    /** 过程代码地址 **/
    int addr;
    /** 过程分配空间大小 **/
    int size;
    /** 形参个数 */
    int paraNum;
    /** 活动记录基础空间（不包括形式单元与局部变量与运算栈）*/
    static public final int basicSize = 3;

    public ProcedureInf(int depth, int addr, int size, int paraNum) {
        super(IDType.PROC_ID);
        this.depth = depth;
        this.addr = addr;
        this.size = size;
        this.paraNum = paraNum;
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
