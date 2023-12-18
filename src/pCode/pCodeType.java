package pCode;

public enum pCodeType {
    /**LIT 0 ,a 取常量a放入数据栈栈顶*/
    LIT,
    /**OPR 0 ,a 执行运算，a表示执行某种运算*/
    OPR,
    /**LOD L ,a 取变量（相对地址为a，层差为L）放到数据栈的栈顶*/
    LOD,
    /**STO L ,a 将数据栈栈顶的内容存入变量（相对地址为a，层次差为L）*/
    STO,
    /**CAL L ,a 调用过程（转子指令）（入口地址为a，层次差为L）*/
    CAL,
    /**INT 0 ,a 数据栈栈顶指针增加a*/
    INT,
    /**JMP 0 ,a无条件转移到地址为a的指令*/
    JMP,
    /**JPC 0 ,a 条件转移指令，转移到地址为a的指令*/
    JPC,
    /**RED L ,a 读数据并存入变量（相对地址为a，层次差为L）*/
    RED,
    /** WRT 0 ,0 将栈顶内容输出 */
    WRT,
    /** PARAM 0，a  传递参数，将参数传递给下一个活动记录偏移量为a的位置处*/
    PARAM,
    /** RET 0, 0 返回调用者，退栈 */
    RET,
}
