package pcode;

/**
 * 三元式类，一个实例表示一个三元式代码
 */
public class ternary {
    /**
     * 三地址指令类型
     * @see InstructionType
     */
    InstructionType instructionType;
    /**
     * 第一操作码 L
     * @see InstructionType
     */
    Integer L;
    /**
     * 第二操作码 a
     * @see InstructionType
     */
    Integer a;

    public ternary(InstructionType instructionType, Integer l, Integer a) {
        this.instructionType = instructionType;
        L = l;
        this.a = a;
    }
}
