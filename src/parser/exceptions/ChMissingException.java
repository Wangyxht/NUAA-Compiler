package parser.exceptions;


import lexer.Lexer;

/**
 * 字符缺失异常，当程序缺失某字符时抛出继承此基类的异常
 */
public abstract class ChMissingException extends ParserException{
    public ChMissingException(String detailMessage) {
        super(detailMessage);
    }

    @Override
    public void SetLocator(){
        setLine(Lexer.getPreLine());
        setCol(Lexer.getPreCol());
    }
    @Override
    public void PrintExceptionMessage() {
        System.out.format("%c[31m [%s]:   位于第%d行%d列\t %-20s \t %-40s %c[0m\r\n",
                27, getMessage(), line, col, "' '", detailMessage, 27);
    }
}
