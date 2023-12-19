package symbols;

import lexer.Lexer;
import lexer.Number;
import lexer.Word;

public abstract class SymbolsException extends Exception{
    /** 异常个数 */
    static public int exceptions_num = 0;
    /** 异常具体信息 */
    protected String detailMessage;
    /** 异常标识符 */
    protected String errorSymbol;
    /** 异常所在行数 */
    protected long line;
    /** 异常所在列数 */
    protected long col;
    public SymbolsException(String detailMessage, String errorSymbol) {
        super("Symbol Error");
        exceptions_num ++;
        this.detailMessage = detailMessage;
        this.errorSymbol = errorSymbol;
        line = Lexer.getLine();
        col = Lexer.getCol();
    }

    public void PrintExceptionMessage(){
        System.out.format("%c[31m [%s]:   位于第%d行%d列\t %-20s \t %-40s %c[0m\r\n",
                    27, getMessage(), line, col,"'"+ errorSymbol  +"'", detailMessage, 27);
    }
}
