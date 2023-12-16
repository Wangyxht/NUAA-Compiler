package symbols;

public class VariableInf extends SymbolInf {

    /** 活动记录基本信息占用空间 */
    static final int actRecordInfSize = 3;
    /** 变量值 */
    int val;
    /** 嵌套深度 */
    int depth;
    /** 分配地址:相对于本过程基地址的偏移量  */
    int addr;

    public VariableInf(int val, int depth, int addr){
        super(IDType.VAR_ID);
        this.val = val;
        this.depth = depth;
        this.addr = addr;
    }

    public VariableInf(int depth){
        super(IDType.VAR_ID);
    }
}
