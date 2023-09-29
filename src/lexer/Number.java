package lexer;

/**
 * <b> Number类用于管理所有整型数</b>
 * @see Token
 */
public class Number extends Token {
    /** 整型数字实际值 */
    private int val;

    public Number(int val) {
        super(Tag.INTEGER);
        this.val = val;
    }

    public int getVal() {
        return val;
    }

    @Override
    public String toString() {
        return "Number{" +
                "val=" + val +
                '}';
    }
}
