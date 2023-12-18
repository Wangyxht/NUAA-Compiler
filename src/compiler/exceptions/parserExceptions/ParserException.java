package compiler.exceptions.parserExceptions;

import lexer.Lexer;
import lexer.Number;
import lexer.Token;
import lexer.Word;

/**
 * 语法分析器异常基类，所有具体语法分析异常类继承自此类
 * 当语法分析器抛出异常时，执行恐慌模式或自动恢复模式。
 */
public abstract class ParserException extends Exception{

    /** 异常个数 */
    static private int exceptions_num = 0;
    /** 异常所在行数 */
    protected long line;
    /** 异常所在列数 */
    protected long col;
    /** 异常所属词法单元 */
    protected Token error_token;
    /** 异常具体信息 */
    protected String detailMessage;


    public ParserException(String detailMessage) {
        super("Syntax Error");
        exceptions_num ++;
        this.detailMessage = detailMessage;
    }

    public void SetLocator(){
        setLine(Lexer.getLine());
        setCol(Lexer.getCol());
    }

    void setLine(long line) {
        this.line = line;
    }

    void setCol(long col) {
        this.col = col;
    }

    public void setError_token(Token error_token) {
        this.error_token = error_token;
    }

    public void PrintExceptionMessage(){
        if(error_token instanceof Word){
            System.out.format("%c[31m [%s]:   位于第%d行%d列\t %-20s \t %-40s %c[0m\r\n",
                    27, getMessage(), line, col,"'"+((Word) error_token).getContent()+"'", detailMessage, 27);

        } else if(error_token instanceof Number){
            System.out.format("%c[31m [%s]:   位于第%d行%d列\t %-20s \t %-40s %c[0m\r\n",
                    27, getMessage(), line, col,  "'" +((Number) error_token).getVal()+"'", detailMessage, 27);
        } else{
            System.out.format("%c[31m [%s]:   位于第%d行%d列\t %-20s \t %-40s %c[0m\r\n",
                    27, getMessage(), line, col, "'Tag:"+error_token.getTag()+"'", detailMessage, 27);
        }
    }
}
