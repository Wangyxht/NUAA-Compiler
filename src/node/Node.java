package node;

import lexer.Lexer;

public abstract class Node {
    /**
     * 节点标签总数,用于三地址代码生成
     */
    static int labels = 0;
    /**
     * 行定位器，用于报错
     */
    long lexLine = 0;
    /**
     * 列定位器，用于报错
     */
    long lexCol = 0;

    Node() {
        lexLine = Lexer.getLine();
        lexCol = Lexer.getCol();
    }

}
