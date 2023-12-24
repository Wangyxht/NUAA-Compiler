package symbols;

public class SymbolCallException extends SymbolsException{
    public SymbolCallException(String errorSymbol) {
        super("调用过程参数与过程定义不符。", errorSymbol);
    }
}
