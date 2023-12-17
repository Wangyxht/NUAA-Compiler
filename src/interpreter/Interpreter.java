package interpreter;

import pCode.*;

import java.util.ArrayList;

/**
 * 代码解释器，用于解释执行代码
 */
public class Interpreter {
    /**
     * 运行时刻栈区
     */
    ControlStack controlStack = new ControlStack();
    /**
     * 代码区
     */
    pCodeArea codeArea = null;
    /** 程序地址指针，存放下一条要执行的指令地址 */
    Integer pc = 0;
    /** 帧指针,指向当前活动记录基地址 */
    Integer sp = 0;
    /** 栈指针，指向当前活动记录顶层地址 */
    Integer top = 0;
    /** 指令寄存器I(虚拟):存放当前要执行的代码 */
    pCode pcode;

    public Interpreter() {}

    /**
     * 执行当前的一条代码并且调整各寄存器与指针
     */
    public void executeCode(){
        switch (pcode.codeType){
            case INT -> {

            }
            case LIT -> {
                controlStack.stack.add(pcode.a);
            }
            case LOD -> {

            }
            default -> {

            }
        }
    }


}
