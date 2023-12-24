package symbols;
import interpreter.Interpreter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class Symbols {
    /** 符号表,用哈希表抽象表示 */
    HashMap<String, SymbolInf> table = new HashMap<>();
    /** 前置符号表 */
    Symbols prev;
    /** 下一个插入数据相对于基地址位置 */
    int variableAddr = 4;

    ArrayList<String> errorSymbol = new ArrayList<>();

    public Symbols(Symbols prev) {
        this.prev = prev;
    }

    public SymbolInf getSymbol(String symbol_name) throws SymbolsNotDeclareException {
        if(errorSymbol.contains(symbol_name)){
            throw new SymbolsNotDeclareException(symbol_name);
        }
        var cur_table = this;
        while(cur_table != null){
            SymbolInf symbol = cur_table.table.get(symbol_name);
            if(symbol != null) return symbol;
            cur_table = cur_table.prev;
        }

        throw new SymbolsNotDeclareException(symbol_name);
    }

    private SymbolInf getSymbolNoException(String symbol_name) {
        var cur_table = this;
        while(cur_table != null){
            SymbolInf symbol = cur_table.table.get(symbol_name);
            if(symbol != null) return symbol;
            cur_table = cur_table.prev;
        }
        return null;
    }

    public ProcedureInf getProcedure(String symbol_name){
        if(symbol_name == null) return new ProcedureInf(-1, -1, -1, -1);
        if(symbol_name.equals("")) return null;
        try{
            SymbolInf symbolInf = getSymbol(symbol_name);
            return symbolInf instanceof ProcedureInf ? (ProcedureInf) symbolInf : null;
        } catch (SymbolsNotDeclareException e) {
            errorSymbol.add(symbol_name);
            e.PrintExceptionMessage();
            return new ProcedureInf(-1, -1, -1, -1);
        }

    }

    public VariableInf getVariable(String symbol_name) {
        try{
            SymbolInf symbolInf = getSymbol(symbol_name);
            if(!(symbolInf instanceof VariableInf)) throw new SymbolNotVariableException(symbol_name);
            return (VariableInf)symbolInf;
        } catch (SymbolsNotDeclareException e){
            errorSymbol.add(symbol_name);
            e.PrintExceptionMessage();
            return new VariableInf(-1, -1);
        } catch (SymbolNotVariableException e) {
            e.PrintExceptionMessage();
            return new VariableInf(-1, -1);
        }

    }

    public Integer getConstVal(String symbol_name) {
        try{
            SymbolInf symbolInf = getSymbol(symbol_name);
            return symbolInf instanceof ConstInf ? ((ConstInf) symbolInf).val : null;
        } catch (SymbolsNotDeclareException e){
            errorSymbol.add(symbol_name);
            e.PrintExceptionMessage();
            return Integer.MAX_VALUE;
        }

    }

    public void putSymbol(String symbol_name, SymbolInf symbol){
        boolean isProc = true;
        var symbol_check = getSymbolNoException(symbol_name);
        if(!(symbol_check instanceof ProcedureInf)) isProc = false;

        try {
            if (table.get(symbol_name) != null || isProc) {
                throw new SymbolReDeclareException(symbol_name);
            }

            if (symbol instanceof VariableInf variableInf) {
                variableInf.addr = variableAddr++;
            }
            table.put(symbol_name, symbol);
        } catch (SymbolReDeclareException e){
            e.PrintExceptionMessage();
        }
    }

    public void checkParaNum(String symbol_name, Integer paraNum){
        try{
            var procInf = getProcedure(symbol_name);
            if(procInf.paraNum == -1) return;
            if(paraNum.equals(procInf.paraNum)) return;
            throw new SymbolCallException(symbol_name);
        } catch (SymbolCallException e) {
            e.PrintExceptionMessage();
        }
    }

    public void setProcedureSize(String symbol_name, int size){
        if(getProcedure(symbol_name) != null){
            var procedureInf = table.get(symbol_name);
            if(!(procedureInf instanceof ProcedureInf)) return;
            ((ProcedureInf) procedureInf).size = size + ProcedureInf.basicSize;
        }
    }

    public void setProcedureAddr(String symbol_name, int addr){
        var procedureInf = getProcedure(symbol_name);
        if(procedureInf != null){
            procedureInf.addr = addr;
        }
    }

    public Symbols getPrev() {
        return prev;
    }
}
