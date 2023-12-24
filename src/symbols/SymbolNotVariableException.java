package symbols;

public class SymbolNotVariableException extends  SymbolsException{
    public SymbolNotVariableException(String errorSymbol) {
        super("请确认该标识符为变量而非过程或常量。", errorSymbol);
    }
}
