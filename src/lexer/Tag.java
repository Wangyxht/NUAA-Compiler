package lexer;

/**
 * <b>Tag为所有词法单元对应的枚举类型</b>
 * @author Wangyx
 * @since 1.0
 */
public enum Tag {
    /**<b>id：标识符</b>*/
    ID,
    /**<b>integer：整型数字</b>*/
    INTEGER,
    /**<b>LOP -> =| <> | < | <= | >=</b>*/
    LOP,
    /**<b>AOP -> + | -</b>*/
    AOP,
    /**<b>MOP -> * | /</b>*/
    MOP,
    /**<b>ASSIGN -> :=</b>*/
    ASSIGN,
    /** <b>SPLIT -> { | } | [ | ] | ( | ) | , | ; </b> */
    SPLIT,
    /** <b>EOF:文件末尾</b> */
    EOF,
    // 以下枚举类型均为关键字
    PROGRAM, VAR, CONST, PROCEDURE, BEGIN, END,
    IF, THEN, ELSE, WHILE, DO, CALL, READ, WRITE, ODD,

}
