package interpreter;

import pCode.*;
import java.util.Scanner;

/**
 * 代码解释器，用于解释执行pCode代码
 */
public class Interpreter {
    /** 运行时刻栈，用数组抽象表示*/
    int[] controlStack;
    /**
     * 代码区
     */
    pCodeArea codeArea;
    /** 程序地址指针，存放下一条要执行的指令地址 */
    Integer pc = 0;
    /** 帧指针,指向当前活动记录基地址 */
    Integer sp = 1;
    /** 栈指针，指向当前活动记录顶层地址 */
    Integer top = 1;
    /** 指令寄存器I(虚拟):存放当前要执行的代码 */
    pCode pcode;
    /** 屏幕输入流，用于输入数据 */
    Scanner scanner = new Scanner(System.in);

    public Interpreter(pCodeArea codeArea) {
        this.codeArea = codeArea;
        // 分配40kB内存空间
        this.controlStack = new int[10000];
        // 设置主程序栈帧
        controlStack[sp] = -1; // 返回地址
        controlStack[sp + 1] = 0;// 动态链
        controlStack[sp + 2] = 1;// 静态链
        pcode = codeArea.getPcode(pc);
        pc++;
    }

    /**
     * 执行传入的所有pCode语句序列
     */
    public void executeCodes(){
        while(true){ // PC寄存器非结束标志执行代码
            executeCode();
            if(pc == -1) break;
            pcode = codeArea.getPcode(pc);
            pc++;
        }
    }

    /**
     * 执行当前的一条pCode并且调整各寄存器与指针
     */
    private void executeCode(){
        switch (pcode.codeType){
            case INT -> {
                top = top + pcode.a - 1;
            }
            case LIT -> {
                controlStack[++top] = pcode.a;
            }
            case LOD ->{
                int index = searchAccessLink(pcode.L, pcode.a);
                controlStack[++top] = controlStack[index];
            }
            case STO -> {
                int index = searchAccessLink(pcode.L, pcode.a);
                controlStack[index] = controlStack[top--];
            }
            case JMP ->{
                pc = pcode.a;
            }
            case JPC -> {
                pc = controlStack[top] == 0 ?
                        pcode.a :
                        pc ;
            }
            case RED -> {
                controlStack[++top] = scanner.nextInt();
            }
            case CAL -> {
                // 设置返回地址
                 controlStack[top + 1] = pc;
                // 设置动态链（保存sp）
                controlStack[top + 2] = sp;
                // 设置静态链
                if(pcode.L.equals(0)){ // 如果调用者和被调用者层次差为0
                    controlStack[top + 3] = controlStack[sp + 2];

                } else {
                    int index = searchAccessLink(pcode.L, 3);
                    controlStack[top + 3] = controlStack[index];
                }
                pc = pcode.a;
                sp = top + 1;
                top = sp;
            }
            case RET -> {
                // 恢复top指针
                top = sp - 1;
                // 恢复pc寄存器
                pc = controlStack[sp];
                // 恢复sp指针
                sp = controlStack[sp + 1];

            }
            case PARAM -> {
                 controlStack[top + pcode.a] = controlStack[top];
                 top -- ;
            }
            case WRT -> {
                System.out.print(controlStack[top]);
            }
            case OPR -> {
                operate(pcode.a);
            }
        }
    }

    /**
     * 根据层次差与相对地址查询某变量的位置空间
     * @param L 层差
     * @param a 偏移量
     * @return 该变量的栈指针
     */
    private int searchAccessLink(int L, int a){
        int l = L;
        int link_sp = sp;
        while(l != 0){
            link_sp = controlStack[link_sp + 2];
            l--;
        }
        return link_sp + a - 1;
    }

    /**
     * 用于解析OPR指令的一项操作
     * @param operator 操作代码
     */
    private void operate(int operator){
        var pCodeType = OpCode.getOp(operator);
        int num_lower = controlStack[top - 1];
        int num_upper = controlStack[top];
        switch (pCodeType){
            case PLUS -> controlStack[-- top] = num_lower + num_upper;
            case MINUS -> controlStack[-- top] = num_lower - num_upper;
            case MUTI -> controlStack[-- top] = num_lower * num_upper;
            case DIV -> controlStack[-- top] = num_lower / num_upper;
            case OPPOSITE -> controlStack[top] = - num_upper;
            case ODD -> controlStack[top] = num_upper % 2;
            case EQ -> controlStack[-- top] = num_lower == num_upper ? 1 : 0;
            case NEQ -> controlStack[-- top] = num_lower != num_upper ? 1 : 0;
            case GT -> controlStack[-- top] = num_lower > num_upper ? 1 : 0;
            case GE -> controlStack[-- top] = num_lower >= num_upper ? 1 : 0;
            case LT -> controlStack[-- top] = num_lower < num_upper ? 1 : 0;
            case LE -> controlStack[-- top] = num_lower <= num_upper ? 1 : 0;
            case NEWLINE -> System.out.print("\r\n");
        }
    }
}
