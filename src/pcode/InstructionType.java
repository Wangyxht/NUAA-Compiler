package pcode;

/**
 * 指令类型枚举类型，定义所有指令类型的标识
 */
public enum InstructionType {
    LIT,    //LIT 0 ,a 取常量a放入数据栈栈顶
    OPR,    //OPR 0 ,a 执行运算，a表示执行某种运算
    LOD,    //LOD L ,a 取变量（相对地址为a，层差为L）放到数据栈的栈顶
    STO,    //STO L ,a 将数据栈栈顶的内容存入变量（相对地址为a，层次差为L）
    CAL,    //CAL L ,a 调用过程（转子指令）（入口地址为a，层次差为L）
    INT,    //INT 0 ,a 数据栈栈顶指针增加a
    JMP,    //JMP 0 ,a无条件转移到地址为a的指令
    JPC,    //JPC 0 ,a 条件转移指令，转移到地址为a的指令
    RED,    //RED L ,a 读数据并存入变量（相对地址为a，层次差为L）
    ERT,    //WRT 0 ,0 将栈顶内容输出
}
