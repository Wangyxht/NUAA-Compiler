package node;

/**
 * 语句序列类
 * 此类以类似树的形式储存了一系列语句类型
 * statement 储存了本语句
 * next_Stmt 储存了下一语句/语句序列
 */
public class StmtSeq extends Stmt{
    Stmt statement;
    Stmt next_Stmt;

    public StmtSeq(Stmt statement, Stmt next_Stmt) {
        this.statement = statement;
        this.next_Stmt = next_Stmt;
    }
}
