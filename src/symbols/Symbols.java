package symbols;
import java.util.HashMap;

public class Symbols {
    /** 单个符号表 */
    HashMap<String, SymbolInf> table = new HashMap<>();
    /** 前置符号表 */
    Symbols prev = null;
    /** 运行栈相对于基地址偏移量 */
    int addr_drift;

    public Symbols(Symbols prev) {
        this.prev = prev;
    }

    public SymbolInf getSymbol(String symbol_name){
        var cur_table = this;
        while(cur_table != null){
            SymbolInf symbol = cur_table.table.get(symbol_name);
            if(symbol != null) return symbol;
            cur_table = prev;
        }
        return null;
    }

    public void putSymbol(String symbol_name ,SymbolInf symbol){
        table.put(symbol_name, symbol);
    }

}
