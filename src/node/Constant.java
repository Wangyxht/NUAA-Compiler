package node;

import lexer.Number;

public class Constant extends Expr{
    public int val;
    public Constant(Number num) {
        super(num);
        this.val = num.getVal();
    }
}
