package symbols;

/**
 * 符号表项抽象类
 * 维护符号表的基本信息，对于不同类型的符号表进行存取操作
 */
public abstract class SymbolsInf {

    /** 符号表项类型 **/
    IDType type;

    public SymbolsInf(IDType type) {
        this.type = type;
    }
}
