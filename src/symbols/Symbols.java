package symbols;
import interpreter.Interpreter;

import java.util.HashMap;

public class Symbols {
    /** 单个符号表 */
    HashMap<String, SymbolInf> table = new HashMap<>();
    /** 前置符号表 */
    Symbols prev;

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

    public ProcedureInf getProcedure(String symbol_name){
        SymbolInf symbolInf = getSymbol(symbol_name);
        return symbolInf instanceof ProcedureInf ? (ProcedureInf) symbolInf : null;
    }

    public VariableInf getVariable(String symbol_name) {
        SymbolInf symbolInf = getSymbol(symbol_name);
        return symbolInf instanceof VariableInf ? (VariableInf) symbolInf : null;
    }

    public Integer getConstVal(String symbol_name) {
        SymbolInf symbolInf = getSymbol(symbol_name);
        return symbolInf instanceof ConstInf ? ((ConstInf) symbolInf).val : null;
    }

    public void putSymbol(String symbol_name, SymbolInf symbol){
        table.put(symbol_name, symbol);
    }

    public void setProcedureSize(String symbol_name, int size){
        if(table.get(symbol_name) != null){
            var procedureInf = table.get(symbol_name);
            if(!(procedureInf instanceof ProcedureInf)) return;
            ((ProcedureInf) procedureInf).size = size + ProcedureInf.basicSize;
        }
    }

    public Symbols getPrev() {
        return prev;
    }
}
