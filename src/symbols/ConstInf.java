package symbols;

public class ConstInf extends SymbolInf {

    /** 常数值 **/
    int val;
    public ConstInf(int val){
        super(IDType.CONST_ID);
        this.val = val;
    }

    public int getVal() {
        return val;
    }
}
