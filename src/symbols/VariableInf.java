package symbols;

public class VariableInf extends SymbolInf {
    /** 嵌套深度 */
    int depth;
    /** 分配地址:相对于本过程基地址的偏移量  */
    int addr;

    public VariableInf(int depth, int addr){
        super(IDType.VAR_ID);
        this.depth = depth;
        this.addr = addr;
    }

    public VariableInf(int depth){
        super(IDType.VAR_ID);
        this.depth = depth;
    }

    public int getDepth() {
        return depth;
    }

    public int getAddr() {
        return addr;
    }
}
