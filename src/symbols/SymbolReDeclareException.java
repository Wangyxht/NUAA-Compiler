package symbols;

public class SymbolReDeclareException extends SymbolsException{
    public SymbolReDeclareException(String errorSymbol) {
        super("重复定义的标识符。", errorSymbol);
    }
}
