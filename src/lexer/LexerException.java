package lexer;
import java.util.HashMap;

/**
 * 词法分析器异常类，记录所有词法分析过程当中的异常错误
 * <ul>
 *     <li>001 存在非法字符</li>
 *     <li>002 存在意外的符号" : "</li>
 *     <li>003 存在非法的ID，数字不能位于标识符开头</li>
 * </ul>
 */
public class LexerException extends Exception{
    static final HashMap<Integer, String> exception_list
            = new HashMap<>();
    Integer exception_num;
    static {
        exception_list.put(1,"[X]001: 存在非法字符。");
        exception_list.put(2,"[X]002: 存在意外的符号\" : \",是否输入\"  := \"?");
        exception_list.put(3,"[X]003: 存在非法的ID，数字不能位于标识符开头。");
    }

    /**
     * 词法分析器异常构造函数
     * <ul>
     *     <li>001 存在非法字符</li>
     *     <li>002 存在意外的符号" : "</li>
     *     <li>003 存在非法的ID，数字不能位于标识符开头</li>
     * </ul>
     * @param exception_num 异常代码
     * @param error_ch 异常字符
     * @param line 异常行数
     */
    public LexerException(int exception_num, char error_ch, long line) {
        super("位于第"+line+"行：\""+error_ch+"\" --"+exception_list.get(exception_num));
        this.exception_num = exception_num;
    }
}
