package symbols;

public class SymbolsNotDeclareException extends SymbolsException {

    public SymbolsNotDeclareException(String errorSymbol) {
        super("未定义的标识符", errorSymbol);
    }
}
