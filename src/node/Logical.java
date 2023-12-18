package node;

import lexer.Token;

import java.util.ArrayList;

public class Logical extends Arith{

    ArrayList<Integer> trueList = new ArrayList<>();

    ArrayList<Integer> falseList = new ArrayList<>();

    public Logical(Token op, Expr expression_l, Expr expression_r) {
        super(op, expression_l, expression_r);
    }

    public void addTrueList(Integer addr){
        trueList.add(addr);
    }

    public void addFalseList(Integer addr){
        falseList.add(addr);
    }
}
