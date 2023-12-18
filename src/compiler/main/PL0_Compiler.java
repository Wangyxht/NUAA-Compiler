package compiler.main;

import compiler.exceptions.parserExceptions.*;
import interpreter.Interpreter;
import pCode.*;
import node.*;
import lexer.Number;
import lexer.*;
import symbols.*;

import java.util.ArrayList;
import java.util.Stack;

/**
 *
 */
public class PL0_Compiler {

    /**
     * 词法分析器
     * @see Lexer
     */
    private Lexer lexer;
    /**
     * 代码解释器
     * @see Interpreter
     */
    private Interpreter interpreter;
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

    PL0_Compiler() {}

    public static void main(String[] args) throws Exception {
//        Scanner scanner = new Scanner(System.in);
//        System.out.print("请输入待编译文件的文件路径：");
//        String filePath = scanner.nextLine();
        PL0_Compiler PL0Compiler = new PL0_Compiler();
        PL0Compiler.executePL0code("test/correct_test/Fibonacci.pas");
        return;
    }

    /**
     * 编译代码，输出中间代码pCode
     * @param codePath 目标代码路径
     * @throws Exception IO异常、未处理的匹配异常
     */
    public void compileCode(String codePath) throws Exception {
        lexer = new Lexer(codePath, false);
        this.prog();
        codeArea.displayCode();
    }

    /**
     * 编译并且执行PL0代码，调用解释器输出代码运行结果与行为
     */
    public void executePL0code(String codePath) throws Exception {
        compileCode(codePath);
        executePcode();
    }

    /**
     * 执行Pcode代码，调用解释器输出代码运行结果与行为
     */
    private void executePcode(){
        interpreter  = new Interpreter(codeArea);
        interpreter.executeCodes();
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
     * @param tag 请求匹配类型
     * @throws Exception 不匹配异常
     */
    private void StrictMatch(Tag tag) throws Exception {
        if (!token.getTag().equals(tag)) {
            throw ParserExceptionList.
                    GetParserException(stmt_stack.peek(), tag, token);
        }
    }

    /**
     * 非严格比较，若不匹配则返回false
     * @param tags 请求匹配类型（可以为多个）
     */
    private boolean Match(Tag... tags) {
        for (var tag : tags) {
            if (token.getTag().equals(tag)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 非严格比较，若不匹配则返回false
     * @param strs 请求匹配字符串（可以为多个）
     */
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

    /**
     * 生成一条pCode代码指令
     * @param codeType pCode指令类型
     * @param l 层次差
     * @param a 参数
     */
    private void generateCode(pCodeType codeType, Integer l, Integer a){
        codeArea.generateCode(codeType, l ,a);
    }

    /**
     * 生成一条pCode代码指令
     * @param codeType pCode指令类型
     * @param l 层次差
     * @param opCode 操作类型
     */
    private void generateCode(pCodeType codeType, Integer l, OpCode opCode){
        codeArea.generateCode(codeType, l, opCode.ordinal());
    }

    /**
     * 对目标地址的语句块的a参数进行回填
     * @param addr 回填目标地址
     * @param val 回填值
     */
    private void backPatch(int addr, int val){
        codeArea.backPatch(addr, val);
    }

    /**
     * 对目标地址的语句块的a参数进行回填
     * @param addrList 回填目标地址列表
     * @param val 回填值
     */
    private void backPatch(ArrayList<Integer> addrList, int val) {
        if(addrList.isEmpty()) return;
        for (var addr: addrList) {
            backPatch(addr, val);
        }
    }

    private Stmt prog() throws Exception {
        int depth = 1;// 嵌套深度，初始值为1
        ScanToken();
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
        var x = block(depth, 0, "");
        stmt_stack.pop();
        return x;
    }


    /**
     * 分析一个块block
     * @return Block语句
     * @throws Exception 识别失败异常
     */
    private Stmt block(int depth, int paraNum, String procedureName) throws Exception {
        stmt_stack.add("block");
        int blockJmpAddr = codeArea.getNextCodeAddr();
        generateCode(pCodeType.JMP, null, (Integer)null);
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
        // 回填过程地址
        int procedureEntryAddr = codeArea.getNextCodeAddr();
        backPatch(blockJmpAddr, procedureEntryAddr);
        cur_table.setProcedureAddr(procedureName, procedureEntryAddr);
        // block起始过程空间分配
        generateCode(pCodeType.INT, null, paraNum + varNum + ProcedureInf.basicSize);

        // 分析语句块
        var body_ = body(depth);
        // 生成返回语句
        generateCode(pCodeType.RET, null, (Integer) null);
        stmt_stack.pop();
        return new Block(procedureEntryAddr, paraNum, proc_, body_);
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

    /**
     * 分析常量定义语句块
     *
     * @throws Exception 匹配异常
     */
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

    /**
     * 分析变量定义语句块
     * <p>产生式:&lt;vardecl> →
     * <lu>
     * <li> var &lt;id> {,&lt;id>};
     * </lu>
     * </p>
     * @param depth 嵌套深度
     * @return 变量个数
     * @throws Exception
     */
    private int vardecl(int depth) throws Exception {
        stmt_stack.push("var");
        StrictMatch(Tag.VAR);
        int variableNum = 0; //过程变量计数器
        try {
            // 读取变量定义ID
            ScanToken();
            StrictMatch(Tag.ID);
            var variableName = ((Word) token).getContent();

            // 填入符号表
            cur_table.putSymbol(variableName, new VariableInf(depth));
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
                var variableName = ((Word) token).getContent();
                // 填入符号表
                cur_table.putSymbol(variableName, new VariableInf(depth));
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
     * <p>产生式:&lt;proc>
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
        String procedureName = null;
        try {
            // 读取过程名
            if (!Match(Tag.ID)) throw new ProgIDException();
            procedureName = ((Word) token).getContent();
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

        // 新建符号表
        prev_table = cur_table;
        cur_table = new Symbols(prev_table);

        // 读取过程的参数
        int paraNum = 0; // 参数计数器
        if ((Match(Tag.ID) || Match(",")) && !Match(")")) { // 有参声明
            if (Match(Tag.ID)){ // 读取第一个参数
                var paraName = ((Word)token).getContent();
                paraNum++;
                cur_table.putSymbol(paraName, new VariableInf(depth + 1));
                ScanToken();
            }
            while (Match(",")) { // 读取结束标记
                try {
                    // 读取下一个参数
                    ScanToken();
                    StrictMatch(Tag.ID);
                    var paraName = ((Word)token).getContent();
                    paraNum++;
                    cur_table.putSymbol(paraName, new VariableInf(depth + 1));

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

        // 读取语句块, 生成过程代码
        prev_table.putSymbol(procedureName,
                new ProcedureInf(depth, -1, -1));
        Block proc_block = (Block) block(depth + 1, paraNum, procedureName);
        // 计算程序栈空间
        int procSize = proc_block.varNum + paraNum;
        int procEntry = proc_block.bodyEntry;
        // 返回上级表项
        cur_table = prev_table;
        prev_table = cur_table.getPrev();
        // 记录过程名以及相关数据到表项
        cur_table.setProcedureSize(procedureName, procSize);
        cur_table.setProcedureAddr(procedureName, procEntry);


        if (Match(";")) {
            while (Match(";")) { // 并列过程
                // 生成过程序列、符号表以及代码
                ScanToken();
                var nextProc = proc(depth);
                x = new Procedure(procSize, proc_block, nextProc);
            }
        } else { // 无后继过程
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
    private Stmt body(int depth) throws Exception {
        StrictMatch(Tag.BEGIN);
        ScanToken();
        var x = statements(depth);
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
    private Stmt statements(int depth) throws Exception {
        stmt_stack.push("statements");
        if (Match(Tag.END)) return null; // 读取到语句块结尾处返回空
        Stmt stmt = null;
        try {
            stmt = statement(depth);
            if (!Match(Tag.END)) StrictMatch(";");
        } catch (NoSemicolonException e) { // 缺失引号异常
            e.PrintExceptionMessage();
            stmt_stack.pop();
            return new StmtSeq(stmt, statements(depth));
        } catch (InvalidStatement e) { // 非法语句异常（由statement抛出）
            e.PrintExceptionMessage();
            while (!Match(";") && !Match(Tag.END)) {
                ScanToken();
            }
            if (Match(Tag.END)) {
                ScanToken();
                stmt_stack.pop();
                return null;
            }
        }
        if (!Match(Tag.END)) ScanToken();
        stmt_stack.pop();
        return new StmtSeq(stmt, statements(depth));
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
    private Stmt statement(int depth) throws Exception {
        stmt_stack.push("statement");
        Stmt x;
        switch (token.getTag()) {
            case ID -> {
                var target = token;
                var targetName = ((Word) token).getContent();
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
                    // 分析表达式
                    x = new Assign(target, exp(depth));
                    var symbol = cur_table.getVariable(targetName);
                    // 生成赋值代码
                    generateCode(pCodeType.STO,
                            depth - symbol.getDepth(),
                            symbol.getAddr());

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
                // 分析逻辑表达式
                var expression = lexp(depth);

                // 分析Then关键字
                try {
                    StrictMatch(Tag.THEN);
                    ScanToken();
                } catch (NoThenException e) {
                    e.PrintExceptionMessage();
                }

                // 如果为假，跳转到false入口,此时入口未知
                int ifJpcAddr = codeArea.getNextCodeAddr(); // 获取判断指令地址
                generateCode(pCodeType.JPC, null, (Integer) null);

                // 分析if为true时的语句块
                var stmt_if = statement(depth);
                if (Match(Tag.ELSE)) { // 分析else语句
                    // 产生跳转语句，运行if之后无需运行else
                    int ifStmtOver = codeArea.getNextCodeAddr(); // 获取if语句出口
                    generateCode(pCodeType.JMP, null, (Integer) null);
                    // else类型if逻辑表达式的回填，回填else语句入口
                    backPatch(ifJpcAddr, codeArea.getNextCodeAddr());
                    ScanToken();
                    // 分析else语句块
                    x = new If_with_Else(expression, stmt_if, statement(depth));
                    //在if语句出口处回填下一个语句地址
                    backPatch(ifStmtOver, codeArea.getNextCodeAddr());


                } else { // 无else语句
                    x = new If(expression, stmt_if);
                    // 无else类型if逻辑表达式的回填
                    backPatch(ifJpcAddr, codeArea.getNextCodeAddr());
                }
            }
            case WHILE -> {
                ScanToken();
                // 获取while逻辑表达式代码入口
                int whileEntryAddr = codeArea.getNextCodeAddr();
                // 分析逻辑表达式
                var expression = lexp(depth);
                // 如果为假，跳转到false入口,此时入口未知
                int whileJpcAddr = codeArea.getNextCodeAddr();
                generateCode(pCodeType.JPC, null ,(Integer) null);
                // 分析Do关键字
                try {
                    StrictMatch(Tag.DO);
                    ScanToken();
                } catch (NoDoException e) {
                    e.PrintExceptionMessage();
                }
                // 分析while内嵌语句块
                x = new While(expression, statement(depth));
                // 运行完毕语句块回到逻辑表达式入口地址进行循环
                generateCode(pCodeType.JMP, null, whileEntryAddr);
                // 回填结束位置
                backPatch(whileJpcAddr, codeArea.getNextCodeAddr());

            }
            case CALL -> {
                stmt_stack.push("call");
                // 读取方法名
                ScanToken();
                String procedureName = null;
                Token procedureID = null;
                try {
                    StrictMatch(Tag.ID);
                    procedureID = token;
                    procedureName = ((Word) token).getContent();
                    ScanToken();
                } catch (InvalidCall e) {
                    e.PrintExceptionMessage();
                    while (!Match("(", ")", ";", ",")) {
                        ScanToken();
                    }
                }
                stmt_stack.pop();

                // 匹配左括号
                try {
                    StrictMatch("(");
                    ScanToken();
                } catch (NoLeftParenthesisException e) {
                    e.PrintExceptionMessage();
                }
                var args = new ArrayList<Expr>();

                // 有参函数调用
                if (!Match(")")) {
                    addParams(args, depth, Tag.CALL);
                    while (Match(",")) {
                        ScanToken();
                        addParams(args, depth, Tag.CALL);
                    }
                }

                // 匹配右括号
                try {
                    StrictMatch(")");
                    ScanToken();
                } catch (NoRightParenthesisException e) {
                    e.PrintExceptionMessage();
                }
                x = new Call(procedureID, args);

                // 生成调用代码
                var procedureInf = cur_table.getProcedure(procedureName);
                generateCode(pCodeType.CAL,
                        depth - procedureInf.getDepth(),
                        procedureInf.getAddr());
            }
            case BEGIN -> {
                x = body(depth);
            }
            case READ -> {
                var readList = new ArrayList<Token>();
                // 匹配左括号
                try {
                    ScanToken();
                    StrictMatch("(");
                } catch (NoLeftParenthesisException e) {
                    e.PrintExceptionMessage();
                    while (!Match(Tag.ID)) ScanToken();
                }

                // 匹配参数
                ScanToken();
                addReadTarget(readList, depth);
                while (Match(",")) {
                    ScanToken();
                    addReadTarget(readList, depth);
                }
                try {
                    StrictMatch(")");
                    ScanToken();
                } catch (NoRightParenthesisException e) {
                    e.PrintExceptionMessage();
                }

                try {
                    if (readList.isEmpty()) {
                        var e = new ReadIDEmptyException();
                        e.setError_token(token);
                        e.SetLocator();
                        throw e;
                    }
                } catch (ReadIDEmptyException e) {
                    e.PrintExceptionMessage();
                }

                x = new Read(readList);
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

                addParams(expressions, depth, Tag.WRITE);
                while (Match(",")) {
                    ScanToken();
                    addParams(expressions, depth, Tag.WRITE);
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

    /**
     * 添加一组参数序列即(&lt;id>{，&lt;id>})</li>格式匹配分析
     * @param args 参数组
     * @param depth 嵌套深度
     * @throws Exception 异常
     */
    private void addParams(ArrayList<Expr> args, int depth, Tag tag) throws Exception {
        try {
            args.add(exp(depth));
            // 生成参数传递或参数输出代码
            if (tag.equals(Tag.CALL)) // 调用代码
                generateCode(pCodeType.PARAM, null,
                        args.size() + ProcedureInf.basicSize - 1 );
            else if (tag.equals(Tag.WRITE)) { // 输出代码
                generateCode(pCodeType.WRT, null, (Integer) null);
                generateCode(pCodeType.OPR, null, OpCode.NEWLINE);
            }
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

    /**
     * 分析读入串
     * @param readList
     * @throws Exception
     */
    private void addReadTarget(ArrayList<Token> readList, int depth) throws Exception {
        if (Match(")")) return;
        stmt_stack.push("read");
        try {
            // 匹配一个ID
            StrictMatch(Tag.ID);
            readList.add(token);
            // 获取ID信息
            var targetID = ((Word) token).getContent();
            if(targetID == null){
                // TODO: 报错
            }
            var targetInf = cur_table.getVariable(targetID);
            // 生成读入代码
            generateCode(pCodeType.RED, null, (Integer) null);
            // 送对应存储块
            generateCode(pCodeType.STO, depth - targetInf.getDepth(), targetInf.getAddr());

            ScanToken();
        } catch (ReadIDException e) {
            e.PrintExceptionMessage();
            readList.add(null);
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
    private Expr lexp(int depth) throws Exception {
        stmt_stack.push("lexp");
        Expr x;
        Expr expression_left;
        Expr expression_right;
        try {
            if (Match(Tag.ODD)) {
                var op = token;
                ScanToken();
                try {
                    x = new Unary(op, exp(depth));
                    generateCode(pCodeType.OPR, null, OpCode.ODD);
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
                    expression_left = exp(depth);
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
                    expression_right = exp(depth);
                } catch (InvalidExpression e) {
                    stmt_stack.pop();
                    var e1 = new InvalidLogicalExpr();
                    e1.setError_token(token);
                    e1.SetLocator();
                    throw e1;
                }
                x = new Arith(op, expression_left, expression_right);
                // 生成逻辑表达式代码
                switch (((Word) op).getContent()){
                    case "<>" -> generateCode(pCodeType.OPR, null ,OpCode.NEQ);
                    case "=" -> generateCode(pCodeType.OPR, null, OpCode.EQ);
                    case "<" -> generateCode(pCodeType.OPR, null, OpCode.LT);
                    case "<="-> generateCode(pCodeType.OPR, null, OpCode.LE);
                    case ">" -> generateCode(pCodeType.OPR, null, OpCode.GT);
                    case ">=" -> generateCode(pCodeType.OPR, null, OpCode.GE);
                }
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
    private Expr exp(int depth) throws Exception {
        stmt_stack.push("expression");
        Expr x;
        Token tk;
        if (Match("-") || Match("+")) {
            tk = token;
            ScanToken();
            x = new Unary(tk, term(depth));
            if (((Word) tk).getContent().equals("-")){
                generateCode(pCodeType.OPR, null, OpCode.OPPOSITE);
            }
        } else {
            x = term(depth);
        }

        while (Match(Tag.AOP)) {
            tk = token;
            ScanToken();
            x = new Arith(tk, x, term(depth));
            if(((Word) tk).getContent().equals("-")){
                generateCode(pCodeType.OPR, null, OpCode.MINUS);
            } else {
                generateCode(pCodeType.OPR, null, OpCode.PLUS);
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
    private Expr term(int depth) throws Exception {
        Expr x = factor(depth);
        while (Match(Tag.MOP)) {
            var tk = token;
            ScanToken();
            x = new Arith(tk, x, factor(depth));
            if(((Word) tk).getContent().equals("*")){
                generateCode(pCodeType.OPR, null, OpCode.MUTI);
            } else {
                generateCode(pCodeType.OPR, null, OpCode.DIV);
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
    private Expr factor(int depth) throws Exception {
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
                    generateCode(pCodeType.LOD,
                            depth - variable.getDepth(),
                            variable.getAddr());
                } else if(symbol instanceof ConstInf constVal) {
                    generateCode(pCodeType.LIT, null, constVal.getVal());
                }

            }
            case INTEGER -> {
                x = new Constant((Number) token);
                // 生成pCode代码
                generateCode(pCodeType.LIT, null, ((Constant) x).val);
            }
            case SPLIT -> { // (<exp>)
                if (Match("(")) {
                    try {
                        ScanToken();
                        x = exp(depth);
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
