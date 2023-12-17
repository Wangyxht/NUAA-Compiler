package parser.main;

import interpreter.Interpreter;
import pCode.*;
import node.*;
import lexer.Number;
import lexer.*;
import parser.exceptions.*;
import symbols.*;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;

/**
 *
 */
public class Parser {

    /**
     * 词法分析器
     * @see Lexer
     */
    private final Lexer lexer;
    /**
     * 代码解释器
     * @see Interpreter
     */
    private final Interpreter interpreter = new Interpreter();
    /**
     * 当前识别过程栈，用于查询异常表。
     * @see ParserException
     */
    private final Stack<String> stmt_stack = new Stack<>();
    /**
     * 根符号表，用于记录顶层符号表
     */
    private final Symbols root_table = new Symbols(null);
    /**
     * 前置符号表，用于记录前置分析的符号表
     */
    private Symbols prev_table = null;
    /**
     * 当前符号表，用于记录当前分析的符号表
     */
    private Symbols cur_table = root_table;
    /**
     * 当前词法单元
     */
    private Token token;
    /**
     * 代码区，用于存放生成的pCode代码序列
     */
    private pCodeArea codeArea = new pCodeArea();

    Parser(Lexer lexer) {
        this.lexer = lexer;
        ScanToken();
    }

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        System.out.print("请输入待编译文件的文件路径：");
        String fileDir = scanner.nextLine();
        Lexer lexer_test = new Lexer(fileDir, false);
        Parser parser = new Parser(lexer_test);
        parser.prog();
        parser.codeArea.displayCode();
        return;
    }

    /**
     * 严格比较，若不匹配则抛出异常提示
     *
     * @param str 请求匹配字符串
     * @throws Exception 不匹配异常
     */
    private void StrictMatch(String str) throws Exception {
        if (!(token instanceof Word) || !((Word) token).getContent().equals(str)) {
            throw ParserExceptionList.
                    GetParserException(stmt_stack.peek(), str, token);
        }
    }

    /**
     * 严格比较，若不匹配则抛出异常提示
     *
     * @param tag 请求匹配类型
     * @throws Exception 不匹配异常
     */
    private void StrictMatch(Tag tag) throws Exception {
        if (!token.getTag().equals(tag)) {
            throw ParserExceptionList.
                    GetParserException(stmt_stack.peek(), tag, token);
        }
    }

    private boolean Match(Tag... tags) {
        for (var tag : tags) {
            if (token.getTag().equals(tag)) {
                return true;
            }
        }
        return false;
    }

    private boolean Match(String... strs) {
        for (var str : strs) {
            if (token instanceof Word && ((Word) token).getContent().equals(str)) {
                return true;
            }
        }
        return false;
    }

    private void ScanToken() {
        try {
            token = lexer.AnalyseToken();
//            if (token.getTag().equals(Tag.EOF)) {
//                System.exit(1);
//            }
        } catch (Exception e) {
            System.out.println((char) 27 + "[31m" + e.getMessage() + (char) 27 + "[0m");
        }
    }

    private Stmt prog() throws Exception {
        int depth = 1;// 嵌套深度，初始值为1
        try {
            stmt_stack.add("prog");
            StrictMatch(Tag.PROGRAM);
            ScanToken();
            StrictMatch(Tag.ID);
            ScanToken();
            StrictMatch(";");
            ScanToken();

        } catch (NoSemicolonException e) { // 缺失引号异常
            e.PrintExceptionMessage();
        } catch (ParserException e) { // 其他语法异常(恐慌模式)
            e.PrintExceptionMessage();
            while (!Match(";") && !Match(Tag.VAR, Tag.CONST, Tag.PROCEDURE)) {
                ScanToken();
            }
            try {
                StrictMatch(";");
                ScanToken();
            } catch (NoSemicolonException e1) {
                e1.PrintExceptionMessage();
            }
        }
        // 生成跳转到主程序代码
        codeArea.generateCode(pCodeType.JMP, null, null);
        var x = block(depth, 0);
        stmt_stack.pop();
        return x;
    }

    /**
     * @return
     * @throws
     */
    private Stmt block(int depth, int paraNum) throws Exception {
        stmt_stack.add("block");
        // 变量计数器
        int varNum = 0;
        // 过程
        Stmt proc_ = null;

        while (!Match(Tag.BEGIN)) {
            try {
                switch (token.getTag()) {
                    case CONST -> condecl();
                    case VAR -> varNum = vardecl(depth);
                    case PROCEDURE -> proc_ = proc(depth);
                    default -> throw new BlockStartException();
                }
            } catch (BlockStartException e) {
                e.SetLocator();
                e.setError_token(token);
                e.PrintExceptionMessage();
                while (!Match(";") && !Match(Tag.BEGIN)) {
                    ScanToken();
                }
                ScanToken();
            }
        }
        // 如果分析的是主程序，回填主程序地址
        if(cur_table == root_table) codeArea.backPatch(0, codeArea.getNextCodeAddr());

        // 分析语句块
        var body_ = body(depth, paraNum + varNum);
        stmt_stack.pop();
        return new Block(varNum, proc_, body_);
    }

    private void condecl() throws Exception {
        stmt_stack.add("condecl");
        StrictMatch(Tag.CONST);
        ScanToken();
        try {
            _const();
        } catch (NoSemicolonException e) {
            while (!Match(Tag.VAR, Tag.BEGIN)) {
                ScanToken();
            }
        } catch (ParserException e) {
            e.PrintExceptionMessage();
            while (!Match(",", ";") && !Match(Tag.VAR, Tag.BEGIN)) {
                ScanToken();
            }
        }

        while (Match(",")) {
            try {
                ScanToken();
                _const();
            } catch (NoSemicolonException e) {
                while (!Match(Tag.VAR, Tag.BEGIN)) {
                    ScanToken();
                }
            } catch (ParserException e) {
                e.PrintExceptionMessage();
                while (!Match(",", ";") && !Match(Tag.VAR, Tag.BEGIN)) {
                    ScanToken();
                }
            }
        }
        try {
            StrictMatch(";");
        } catch (NoSemicolonException e) {
            e.PrintExceptionMessage();
            stmt_stack.pop();
            return;
        }
        ScanToken();
        stmt_stack.pop();
    }

    private void _const() throws Exception {
        // 读取常量定义ID
        StrictMatch(Tag.ID);
        var ID_name = ((Word) token).getContent();

        // 读取:=
        ScanToken();
        StrictMatch(Tag.ASSIGN);

        // 读取数字
        ScanToken();
        StrictMatch(Tag.INTEGER);
        var ID_val = ((Number) token).getVal();

        // 填入符号表
        cur_table.putSymbol(ID_name, new ConstInf(ID_val));

        // 扫描分隔符
        ScanToken();
        if (!Match(",", ";") && !Match(Tag.VAR, Tag.BEGIN)) StrictMatch(",");
        if (Match(Tag.VAR, Tag.BEGIN)) StrictMatch(";");
    }

    private int vardecl(int depth) throws Exception {
        stmt_stack.push("var");
        StrictMatch(Tag.VAR);
        int variableNum = 0; //过程变量计数器
        try {
            // 读取变量定义ID
            ScanToken();
            StrictMatch(Tag.ID);
            var ID_name = ((Word) token).getContent();

            // 填入符号表
            cur_table.putSymbol(ID_name, new VariableInf(depth, variableNum));
            variableNum++;

            // 读取分隔符
            ScanToken();
            if (!Match(",", ";") && !Match(Tag.BEGIN)) StrictMatch(",");
            if (Match(Tag.BEGIN)) StrictMatch(";");

        } catch (NoSemicolonException e) { // 缺失分号异常
            while (!Match(Tag.BEGIN)) {
                ScanToken();
            }
        } catch (ParserException e) { // 其他异常
            e.PrintExceptionMessage();
            while (!Match(",", ";") && !Match(Tag.BEGIN)) {
                ScanToken();
            }
        }

        while (Match(",")) {
            try {
                // 读取变量定义ID
                ScanToken();
                StrictMatch(Tag.ID);
                var ID_name = ((Word) token).getContent();
                // 填入符号表
                cur_table.putSymbol(ID_name, new VariableInf(depth, variableNum));
                variableNum++;
                // 读取分隔符
                ScanToken();
                if (!Match(",", ";")) StrictMatch(",");

            } catch (NoSemicolonException e) { // 缺失逗号异常
                while (!Match(Tag.BEGIN)) {
                    ScanToken();
                }
            } catch (ParserException e) { // 其他异常
                e.PrintExceptionMessage();
                while (!Match(",", ";") && !Match(Tag.BEGIN)) {
                    ScanToken();
                }
            }
        }

        // 读取分号
        try {
            StrictMatch(";");
        } catch (NoSemicolonException e) { // 缺失分号异常
            e.PrintExceptionMessage();
            stmt_stack.pop();
            return variableNum;
        }

        ScanToken();
        stmt_stack.pop();
        return variableNum;
    }

    /**
     * <h3>对当前的一个过程proc进行分析 </h3>
     * <p>产生式:&lt;proc> →
     * <lu>
     * <li>&lt;proc> → procedure &lt;id>（[&lt;id>{,&lt;id>}]）;&lt;block>{;&lt;proc>}</li>
     * </lu>
     * </p>
     *
     * @return Stmt 过程 proc
     * @throws Exception
     */
    private Stmt proc(int depth) throws Exception {
        stmt_stack.push("proc");
        Stmt x = null;

        // 读取PROCEDURE关键字
        StrictMatch(Tag.PROCEDURE);
        ScanToken();

        // 读取过程名与左括号
        String ID_name = null;
        try {
            // 读取过程名
            if (!Match(Tag.ID)) throw new ProgIDException();
            ID_name = ((Word) token).getContent();
            ScanToken();

            // 读取左括号
            StrictMatch("(");
            ScanToken();

        } catch (ProgIDException e) { // 过程名异常
            e.setError_token(token);
            e.SetLocator();
            e.PrintExceptionMessage();
            while (!Match("(", ",", ")")) {
                ScanToken();
            }
        } catch (ParserException e) { // 其他异常
            e.PrintExceptionMessage();
            while (!Match("(", ",", ")")) {
                ScanToken();
            }
        }

        // 读取过程的参数
        int paraNum = 0; // 参数计数器
        if ((Match(Tag.ID) || Match(",")) && !Match(")")) { // 有参声明
            if (Match(Tag.ID)){ ScanToken(); paraNum++;}
            while (Match(",")) {
                try {
                    ScanToken();
                    StrictMatch(Tag.ID);
                    var paraName = ((Word)token).getContent();
                    paraNum++;
                    ScanToken();
                    if (!Match(")", ",")) throw new InvalidArgsException();
                } catch (ParserException e) {
                    e.setError_token(token);
                    e.SetLocator();
                    e.PrintExceptionMessage();
                    while (!Match(",", ")", ";")) {
                        ScanToken();
                    }
                    if (Match(")", ";")) break;
                }
            }
        }

        // 匹配右括号与结束符号
        try {
            StrictMatch(")");
            ScanToken();
            StrictMatch(";");
            ScanToken();
        } catch (NoSemicolonException e) {
            e.PrintExceptionMessage();
        } catch (NoRightParenthesisException e) {
            e.PrintExceptionMessage();
            while (!Match(";")) {
                ScanToken();
            }
            ScanToken();
        }
        // 记录过程名以及相关数据到表项
        cur_table.putSymbol(ID_name,
                new ProcedureInf(depth, codeArea.getNextCodeAddr(), -1));
        codeArea.generateCode(pCodeType.INT, null, null);

        // 新建符号表
        prev_table = cur_table;
        cur_table = new Symbols(prev_table);

        // 读取语句块, 生成过程代码
        Block proc_block = (Block) block(depth + 1, paraNum);
        int procSize = proc_block.varNum + paraNum;

        // 返回上级表项
        cur_table = prev_table;
        prev_table = cur_table.getPrev();

        // 填入符号表
        cur_table.setProcedureSize(ID_name, procSize);
        // 回填空间增量到LIT
        codeArea.backPatch(
                cur_table.getProcedure(ID_name).getAddr(),
                cur_table.getProcedure(ID_name).getSize());


        if (Match(";")) {
            while (Match(";")) { // 并列过程
                // 生成过程序列、符号表以及代码
                ScanToken();
                var nextProc = proc(depth);
                x = new Procedure(procSize, proc_block, nextProc);
            }
        } else { // 过程无嵌套
            x = new Procedure(procSize ,proc_block, null);
        }
        stmt_stack.pop();
        return x;
    }

    /**
     * <h3>对当前的一个过程主体body进行分析 </h3>
     * <p>产生式:&lt;body> →
     * <lu>
     * <li>begin &lt;statement>{;&lt;statement>}end</li>
     * </lu>
     * </p>
     *
     * @return Stmt 过程主体 body
     * @throws Exception
     */
    private Stmt body(int depth, int procBasicSize) throws Exception {
        StrictMatch(Tag.BEGIN);
        ScanToken();
        var x = statements(depth, procBasicSize);
        StrictMatch(Tag.END);
        ScanToken();
        return x;
    }

    /**
     * <h3>对当前的多块语句进行分析 </h3>
     *
     * @return StmtSeq 语句序列 statements
     * @throws Exception
     */
    private Stmt statements(int depth, int procBasicSize) throws Exception {
        if (Match(Tag.END)) return null; // 读取到语句块结尾处返回空
        Stmt stmt = null;
        try {
            stmt = statement(depth, procBasicSize);
            if (!Match(Tag.END)) StrictMatch(";");
        } catch (NoSemicolonException e) { // 缺失引号异常
            e.PrintExceptionMessage();
            return new StmtSeq(stmt, statements(depth, procBasicSize));
        } catch (InvalidStatement e) { // 非法语句异常（由statement抛出）
            e.PrintExceptionMessage();
            while (!Match(";") && !Match(Tag.END)) {
                ScanToken();
            }
            if (Match(Tag.END)) {
                ScanToken();
                return null;
            }
        }
        if (!Match(Tag.END)) ScanToken();
        return new StmtSeq(stmt, statements(depth, procBasicSize));
    }

    /**
     * <h3>对当前的一个语句statement进行分析 </h3>
     * <p>产生式:
     * &lt;statement> →
     * <lu>
     * <li>&lt;id> := &lt;exp></li>
     * <li>if &lt;lexp> then &lt;statement>[else &lt;statement>]</li>
     * <li>while &lt;lexp> do &lt;statement></li>
     * <li>write (&lt;exp>{,&lt;exp>})</li></li>
     * <li>call &lt;id>（[&lt;exp>{,&lt;exp>}]）</li>
     * <li>&lt;body></li>
     * <li>read (&lt;id>{，&lt;id>})</li>
     * <li>write (&lt;exp>{,&lt;exp>})</li>
     * </lu>
     * </p>
     *
     * @return Stmt 完整语句statement
     * @throws Exception
     */
    private Stmt statement(int depth, int procBasicSize) throws Exception {
        stmt_stack.push("statement");
        Stmt x;

        switch (token.getTag()) {
            case ID -> {
                var id = token;
                var ID_name = ((Word) token).getContent();
                ScanToken();
                try {
                    StrictMatch(Tag.ASSIGN);
                } catch (InvalidAssign e) {
                    e.PrintExceptionMessage();
                    var e1 = new InvalidStatement();
                    e1.setError_token(token);
                    e1.SetLocator();
                    throw e1;
                }
                ScanToken();
                try {
                    x = new Assign(id, exp(depth, procBasicSize));
                } catch (InvalidExpression e) {
                    e.PrintExceptionMessage();
                    var e1 = new InvalidStatement();
                    e1.SetLocator();
                    e1.setError_token(token);
                    stmt_stack.pop();
                    throw e1;
                }
            }
            case IF -> {
                ScanToken();
                var expression = lexp(depth, procBasicSize);
                try {
                    StrictMatch(Tag.THEN);
                    ScanToken();
                } catch (NoThenException e) {
                    e.PrintExceptionMessage();
                }
                var stmt_if = statement(depth, procBasicSize);
                try {
                    StrictMatch(";");
                    ScanToken();
                } catch (NoSemicolonException e) {
                    e.PrintExceptionMessage();
                }
                if (Match(Tag.ELSE)) {
                    ScanToken();
                    x = new If_with_Else(expression, stmt_if, statement(depth, procBasicSize));
                } else {
                    x = new If(expression, stmt_if);
                }
            }
            case WHILE -> {
                ScanToken();
                var expression = lexp(depth, procBasicSize);
                try {
                    StrictMatch(Tag.DO);
                    ScanToken();
                } catch (NoDoException e) {
                    e.PrintExceptionMessage();
                }
                x = new While(expression, statement(depth, procBasicSize));
            }
            case CALL -> {
                stmt_stack.push("call");
                // 读取方法名
                ScanToken();
                Token id = null;
                try {
                    StrictMatch(Tag.ID);
                    id = token;
                    ScanToken();
                } catch (InvalidCall e) {
                    e.PrintExceptionMessage();
                    while (!Match("(", ")", ";", ",")) {
                        ScanToken();
                    }
                }
                stmt_stack.pop();

                //读取实参列表
                try {
                    StrictMatch("(");
                    ScanToken();
                } catch (NoLeftParenthesisException e) {
                    e.PrintExceptionMessage();
                }
                var args = new ArrayList<Expr>();

                // 有参函数调用
                if (!Match(")")) {
                    AddArgs(args, depth, procBasicSize);

                    while (Match(",")) {
                        ScanToken();
                        AddArgs(args, depth, procBasicSize);
                    }
                }
                try {
                    StrictMatch(")");
                    ScanToken();
                } catch (NoRightParenthesisException e) {
                    e.PrintExceptionMessage();
                }
                x = new Call(id, args);
            }
            case BEGIN -> {
                x = body(depth, procBasicSize);
            }
            case READ -> {
                var ID_list = new ArrayList<Token>();
                try {
                    ScanToken();
                    StrictMatch("(");
                } catch (NoLeftParenthesisException e) {
                    e.PrintExceptionMessage();
                    while (!Match(Tag.ID)) ScanToken();
                }

                ScanToken();
                AddIDs(ID_list);
                while (Match(",")) {
                    ScanToken();
                    AddIDs(ID_list);
                }
                try {
                    StrictMatch(")");
                    ScanToken();
                } catch (NoRightParenthesisException e) {
                    e.PrintExceptionMessage();
                }

                try {
                    if (ID_list.isEmpty()) {
                        var e = new ReadIDEmptyException();
                        e.setError_token(token);
                        e.SetLocator();
                        throw e;
                    }
                } catch (ReadIDEmptyException e) {
                    e.PrintExceptionMessage();
                }

                x = new Read(ID_list);
            }
            case WRITE -> {
                var expressions = new ArrayList<Expr>();
                ScanToken();
                try {
                    StrictMatch("(");
                    ScanToken();
                } catch (NoLeftParenthesisException e) {
                    e.PrintExceptionMessage();
                }

                AddArgs(expressions, depth, procBasicSize);
                while (Match(",")) {
                    ScanToken();
                    AddArgs(expressions, depth, procBasicSize);
                }

                try {
                    StrictMatch(")");
                    ScanToken();
                } catch (NoRightParenthesisException e) {
                    e.PrintExceptionMessage();
                }
                try {
                    if (expressions.isEmpty()) {
                        var e = new WriteExpEmptyException();
                        e.setError_token(token);
                        e.SetLocator();
                        throw e;
                    }
                } catch (WriteExpEmptyException e) {
                    e.PrintExceptionMessage();
                }
                x = new Write(expressions);
            }
            default -> {
                InvalidStatement e = new InvalidStatement();
                e.setError_token(token);
                e.SetLocator();
                stmt_stack.pop();
                throw e;
            }
        }

        stmt_stack.pop();
        return x;
    }

    private void AddArgs(ArrayList<Expr> args, int depth, int procBasicSize) throws Exception {
        try {
            args.add(exp(depth, procBasicSize));
        } catch (InvalidExpression e) {
            e.PrintExceptionMessage();
            while (!Match(",", ")", ";")) {
                ScanToken();
            }
        }
        try {
            if (!Match(")", ";")) StrictMatch(",");
        } catch (NoCommaException e) {
            e.PrintExceptionMessage();
            while (!Match(",", ")", ";")) ScanToken();
        }
    }

    private void AddIDs(ArrayList<Token> ID_list) throws Exception {
        if (Match(")")) return;
        stmt_stack.push("read");
        try {
            StrictMatch(Tag.ID);
            ID_list.add(token);
            ScanToken();
        } catch (ReadIDException e) {
            e.PrintExceptionMessage();
            ID_list.add(null);
            while (!Match(",", ")", ";")) ScanToken();
        }
        stmt_stack.pop();
        try {
            if (!Match(")", ";")) StrictMatch(",");
        } catch (NoCommaException e) {
            e.PrintExceptionMessage();
            while (!Match(",", ")", ";")) ScanToken();
        }
    }

    /**
     * <h3>对当前的一个逻辑表达式lexp进行分析 </h3>
     * <p>产生式:&lt;lexp> →
     * <lu>
     * <li>&lt;exp> &lt;lop> &lt;exp></li>
     * <li>odd &lt;exp></li>
     *
     * </lu>
     * </p>
     *
     * @return Expr 逻辑表达式lexp
     * @throws Exception
     */
    private Expr lexp(int depth, int procBasicSize) throws Exception {
        stmt_stack.push("lexp");
        Expr x;
        Expr expression_left;
        Expr expression_right;
        try {
            if (Match(Tag.ODD)) {
                var op = token;
                ScanToken();
                try {
                    x = new Unary(op, exp(depth, procBasicSize));
                } catch (InvalidExpression e) {
                    stmt_stack.pop();
                    e.PrintExceptionMessage();
                    var e1 = new InvalidLogicalExpr();
                    e1.setError_token(token);
                    e1.SetLocator();
                    throw e1;
                }
            } else {
                try {
                    expression_left = exp(depth, procBasicSize);
                } catch (InvalidExpression e) {
                    stmt_stack.pop();
                    e.PrintExceptionMessage();
                    var e1 = new InvalidLogicalExpr();
                    e1.setError_token(token);
                    e1.SetLocator();
                    throw e1;
                }
                StrictMatch(Tag.LOP);
                var op = token;
                ScanToken();
                try {
                    expression_right = exp(depth, procBasicSize);
                } catch (InvalidExpression e) {
                    stmt_stack.pop();
                    var e1 = new InvalidLogicalExpr();
                    e1.setError_token(token);
                    e1.SetLocator();
                    throw e1;
                }
                x = new Arith(op, expression_left, expression_right);
            }
        } catch (InvalidLogicalExpr e) {
            e.PrintExceptionMessage();
            while (!Match(Tag.DO, Tag.THEN) && !Match(";")) {
                ScanToken();
            }
            if (Match(";")) {
                var e1 = new InvalidStatement();
                e1.SetLocator();
                e1.setError_token(token);
                throw e1;
            }
            stmt_stack.pop();
            return null;
        }

        stmt_stack.pop();
        return x;
    }

    /**
     * <h3>对当前的一个表达式exp进行分析 </h3>
     * <p>产生式:&lt;exp> →
     * <lu>
     * <li>&lt;exp> → [+|-]&lt;term>{&lt;aop>&lt;term>}</li>
     *
     * </lu>
     * </p>
     *
     * @return Expr 逻辑表达式exp
     * @throws Exception
     */
    private Expr exp(int depth, int procBasicSize) throws Exception {
        stmt_stack.push("expression");
        Expr x;
        Token tk;
        if (Match("-") || Match("+")) {
            tk = token;
            ScanToken();
            x = new Unary(tk, term(depth, procBasicSize));
            if (((Word) tk).getContent().equals("-")){
                codeArea.generateCode(pCodeType.LIT, null, 0);
                codeArea.generateCode(pCodeType.OPR, null, OpCode.MINUS.ordinal());
            }
        } else {
            x = new Unary(null, term(depth, procBasicSize));
        }

        while (Match(Tag.AOP)) {
            tk = token;
            ScanToken();
            x = new Arith(tk, x, term(depth, procBasicSize));
            if(((Word) tk).getContent().equals("-")){
                codeArea.generateCode(pCodeType.OPR, null, OpCode.MINUS.ordinal());
            } else {
                codeArea.generateCode(pCodeType.OPR, null, OpCode.PLUS.ordinal());
            }
        }
        stmt_stack.pop();
        return x;
    }

    /**
     * <h3>对当前的一个表达式term进行分析 </h3>
     * <p>产生式:&lt;term> →
     * <lu>
     * <li>&lt;factor>{&lt;mop>&lt;factor>}</li>
     *
     * </lu>
     * </p>
     *
     * @return Expr 表达式term
     * @throws Exception
     */
    private Expr term(int depth, int procBasicSize) throws Exception {
        Expr x = factor(depth, procBasicSize);
        while (Match(Tag.MOP)) {
            var tk = token;
            ScanToken();
            x = new Arith(tk, x, factor(depth, procBasicSize));
            if(((Word) tk).getContent().equals("*")){
                codeArea.generateCode(pCodeType.OPR, null, OpCode.MUTI.ordinal());
            } else {
                codeArea.generateCode(pCodeType.OPR, null, OpCode.DIV.ordinal());
            }

        }
        return x;
    }

    /**
     * <h3>对当前的一个因子factor进行分析 </h3>
     * <p>产生式:&lt;factor> →
     * <lu>
     * <li>&lt;id> </li>
     * <li>&lt;integer></li>
     * <li>(&lt;exp>)</li>
     *
     * </lu>
     * </p>
     *
     * @return Expr 因子factor
     * @throws Exception
     */
    private Expr factor(int depth, int procBasicSize) throws Exception {
        Expr x;
        switch (token.getTag()) {
            case ID ->{
                x = new Expr(token);
                var ID_name = ((Word) token).getContent();
                var symbol = cur_table.getSymbol(ID_name);
                if(symbol == null || symbol instanceof ProcedureInf){
                       // TODO : 未定义的标识符
                }

                // 生成pCode代码
                if(symbol instanceof VariableInf variable) {
                    codeArea.generateCode(pCodeType.LOD,
                            depth - variable.getDepth(),
                            variable.getAddr() + procBasicSize);
                } else if(symbol instanceof ConstInf constVal) {
                    codeArea.generateCode(pCodeType.LIT, null, constVal.getVal());
                }

            }
            case INTEGER -> {
                x = new Constant((Number) token);
                // 生成pCode代码
                codeArea.generateCode(pCodeType.LIT, null, ((Constant) x).val);
            }
            case SPLIT -> { // (<exp>)
                if (Match("(")) {
                    try {
                        ScanToken();
                        x = exp(depth, procBasicSize);
                        StrictMatch(")");
                    } catch (NoRightParenthesisException e) { // 缺失右括号异常
                        e.PrintExceptionMessage();
                        var e_ret = new InvalidExpression();
                        e_ret.setError_token(token);
                        e_ret.SetLocator();
                        throw e_ret;
                    }
                }
                else {
                    ParserException e = new InvalidExpression();
                    e.setError_token(token);
                    e.SetLocator();
                    throw e;
                }
            }
            default -> {
                ParserException e = new InvalidExpression();
                e.setError_token(token);
                e.SetLocator();
                throw e;
            }
        }
        ScanToken();
        return x;
    }

}
