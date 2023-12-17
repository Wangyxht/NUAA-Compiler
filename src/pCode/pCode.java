package pCode;

public class pCode {
    /**
     * 三地址指令类型
     * @see pCodeType
     */
    public pCodeType codeType;
    /**
     * 第一操作码 L
     * 代表调用层与说明层的层差值
     * @see pCodeType
     */
    public Integer L;
    /**
     * 第二操作码 a
     * 代表位移量（相对地址）
     * @see pCodeType
     */
    public Integer a;

    public pCode(pCodeType instructionType, Integer l, Integer a) {
        this.codeType = instructionType;
        this.L = l;
        this.a = a;
    }

    @Override
    public String toString() {
        return  codeType + " " + L + " " + a + "\r\n";
    }
}
