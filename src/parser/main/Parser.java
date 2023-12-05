package parser.main;

import inter.*;
import lexer.Number;
import lexer.*;
import parser.exceptions.*;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;

/**
 *
 */
public class Parser {

    /**
     * 词法分析器
     *
     * @see Lexer
     */
    private final Lexer lexer;
    private final Stack<String> stmt_stack = new Stack<>();
    /**
     * 当前词法单元
     */
    private Token token;

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
    }

    private void StrictMatch(String str) throws Exception {
        if (!(token instanceof Word) || !((Word) token).getContent().equals(str)) {
            throw ParserExceptionList.
                    GetParserException(stmt_stack.peek(), str, token);
        }
    }

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
            if (token.getTag().equals(Tag.EOF)) {
                System.exit(1);
            }
        } catch (Exception e) {
            System.out.println((char) 27 + "[31m" + e.getMessage() + (char) 27 + "[0m");
        }
    }

    private Stmt prog() throws Exception {
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
        var x = block();
        stmt_stack.pop();
        return x;
    }

    /**
     * @return
     * @throws
     */
    private Stmt block() throws Exception {
        stmt_stack.add("block");
        Stmt proc_ = null;

        while (!Match(Tag.BEGIN)) {
            try {
                switch (token.getTag()) {
                    case CONST -> condecl();
                    case VAR -> vardecl();
                    case PROCEDURE -> proc_ = proc();
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

        try {
            StrictMatch(Tag.BEGIN);
            ScanToken();
        } catch (NoBeginException e) {
            e.PrintExceptionMessage();
        }

        var stmts_ = statements();
        stmt_stack.pop();
        return new Block(proc_, stmts_);
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
        StrictMatch(Tag.ID);
        ScanToken();
        StrictMatch(Tag.ASSIGN);
        ScanToken();
        StrictMatch(Tag.INTEGER);
        ScanToken();
        if (!Match(",", ";") && !Match(Tag.VAR, Tag.BEGIN)) StrictMatch(",");
        if (Match(Tag.VAR, Tag.BEGIN)) StrictMatch(";");
    }

    private void vardecl() throws Exception {
        stmt_stack.push("var");
        StrictMatch(Tag.VAR);
        try {
            ScanToken();
            StrictMatch(Tag.ID);
            ScanToken();
            if (!Match(",", ";") && !Match(Tag.BEGIN)) StrictMatch(",");
            if (Match(Tag.BEGIN)) StrictMatch(";");
        } catch (NoSemicolonException e) {
            while (!Match(Tag.BEGIN)) {
                ScanToken();
            }
        } catch (ParserException e) {
            e.PrintExceptionMessage();
            while (!Match(",", ";") && !Match(Tag.BEGIN)) {
                ScanToken();
            }
        }

        while (Match(",")) {
            try {
                ScanToken();
                StrictMatch(Tag.ID);
                ScanToken();
                if (!Match(",", ";")) StrictMatch(",");

            } catch (NoSemicolonException e) {
                while (!Match(Tag.BEGIN)) {
                    ScanToken();
                }
            } catch (ParserException e) {
                e.PrintExceptionMessage();
                while (!Match(",", ";") && !Match(Tag.BEGIN)) {
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
    private Stmt proc() throws Exception {
        stmt_stack.push("proc");
        Stmt x = null;
        StrictMatch(Tag.PROCEDURE);
        ScanToken();
        try {
            if (!Match(Tag.ID)) throw new ProgIDException();
            ScanToken();
            StrictMatch("(");
            ScanToken();
        } catch (ProgIDException e) {
            e.setError_token(token);
            e.SetLocator();
            e.PrintExceptionMessage();
            while (!Match("(", ",", ")")) {
                ScanToken();
            }

        } catch (ParserException e) {
            e.PrintExceptionMessage();
            while (!Match("(", ",", ")")) {
                ScanToken();
            }
        }


        if (Match(")")) { // 无参声明
            ScanToken();
        } else if (Match(Tag.ID) || Match(",")) { // 有参声明
            if (Match(Tag.ID)) ScanToken();
            while (Match(",")) {
                try {
                    ScanToken();
                    StrictMatch(Tag.ID);
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


        var proc_block = block();

        if (Match(";")) {
            while (Match(";")) {
                x = new Procedure(proc_block, proc());
                ScanToken();
            }
        } else {
            x = new Procedure(proc_block, null);
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
    private Stmt body() throws Exception {
        StrictMatch(Tag.BEGIN);
        ScanToken();
        return statements();

    }

    /**
     * <h3>对当前的多块语句进行分析 </h3>
     *
     * @return StmtSeq 语句序列 statements
     * @throws Exception
     */
    private Stmt statements() throws Exception {
        if (Match(Tag.END)) {
            ScanToken();
            return null;
        } else {
            Stmt stmt = null;
            try {
                stmt = statement();
                if (!Match(Tag.END)) StrictMatch(";");
            } catch (NoSemicolonException e) { // 缺失引号异常
                e.PrintExceptionMessage();
                return new StmtSeq(stmt, statements());
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
            return new StmtSeq(stmt, statements());
        }
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
    private Stmt statement() throws Exception {
        stmt_stack.push("statement");
        Stmt x = null;

        switch (token.getTag()) {
            case ID -> {
                var id = token;
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
                    x = new Assign(id, exp());
                } catch (InvalidExpression e){
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
                var expression = lexp();
                try {
                    StrictMatch(Tag.THEN);
                    ScanToken();
                } catch (NoThenException e) {
                    e.PrintExceptionMessage();
                }
                var stmt_if = statement();
                try {
                    StrictMatch(";");
                    ScanToken();
                } catch (NoSemicolonException e) {
                    e.PrintExceptionMessage();
                }
                if (Match(Tag.ELSE)) {
                    ScanToken();
                    x = new If_with_Else(expression, stmt_if, statement());
                } else {
                    x = new If(expression, stmt_if);
                }
            }
            case WHILE -> {
                ScanToken();
                var expression = lexp();
                try {
                    StrictMatch(Tag.DO);
                    ScanToken();
                } catch (NoDoException e) {
                    e.PrintExceptionMessage();
                }
                x = new While(expression, statement());
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
                if(!Match(")")){
                    AddArgs(args);

                    while (Match(",")) {
                        ScanToken();
                        AddArgs(args);
                    }
                }
                try{
                    StrictMatch(")");
                    ScanToken();
                } catch (NoRightParenthesisException e){
                    e.PrintExceptionMessage();
                }
                x = new Call(id, args);
            }
            case BEGIN -> {
                x = body();
            }
            case READ -> {
                var ID_list = new ArrayList<Token>();
                try{
                    ScanToken();
                    StrictMatch("(");
                } catch (NoLeftParenthesisException e){
                    e.PrintExceptionMessage();
                    while(!Match(Tag.ID)) ScanToken();
                }

                ScanToken();
                AddIDs(ID_list);
                while (Match(",")) {
                    ScanToken();
                    AddIDs(ID_list);
                }
                try{
                    StrictMatch(")");
                    ScanToken();
                } catch (NoRightParenthesisException e){
                    e.PrintExceptionMessage();
                }

                try{
                    if(ID_list.isEmpty()){
                        var e = new ReadIDEmptyException();
                        e.setError_token(token);
                        e.SetLocator();
                        throw e;
                    }
                } catch (ReadIDEmptyException e){
                    e.PrintExceptionMessage();
                }

                x = new Read(ID_list);
            }
            case WRITE -> {
                var expressions = new ArrayList<Expr>();
                ScanToken();
                try{
                    StrictMatch("(");
                    ScanToken();
                } catch (NoLeftParenthesisException e){
                    e.PrintExceptionMessage();
                }

                AddArgs(expressions);
                while (Match(",")) {
                    ScanToken();
                    AddArgs(expressions);
                }

                try{
                    StrictMatch(")");
                    ScanToken();
                } catch (NoRightParenthesisException e) {
                    e.PrintExceptionMessage();
                }
                try{
                    if(expressions.isEmpty()){
                        var e = new WriteExpEmptyException();
                        e.setError_token(token);
                        e.SetLocator();
                        throw e;
                    }
                } catch (WriteExpEmptyException e){
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

    private void AddArgs(ArrayList<Expr> args) throws Exception {
        try{
            args.add(exp());
        } catch (InvalidExpression e){
            e.PrintExceptionMessage();
            while(!Match(",", ")", ";")){
                ScanToken();
            }
        }
        try{
            if(!Match(")",";")) StrictMatch(",");
        } catch (NoCommaException e){
            e.PrintExceptionMessage();
            while(!Match(",", ")", ";")) ScanToken();
        }
    }

    private void AddIDs(ArrayList<Token> ID_list) throws Exception{
        if(Match(")")) return;
        stmt_stack.push("read");
        try{
            StrictMatch(Tag.ID);
            ID_list.add(token);
            ScanToken();
        } catch (ReadIDException e){
            e.PrintExceptionMessage();
            ID_list.add(null);
            while (!Match(",", ")", ";")) ScanToken();
        }
        stmt_stack.pop();
        try{
            if(!Match(")", ";")) StrictMatch(",");
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
    private Expr lexp() throws Exception {
        stmt_stack.push("lexp");
        Expr x = null;
        Expr expression_left = null;
        Expr expression_right = null;
        try {
            if (Match(Tag.ODD)) {
                var op = token;
                ScanToken();
                try {
                    x = new Unary(op, exp());
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
                    expression_left = exp();
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
                    expression_right = exp();
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
    private Expr exp() throws Exception {
        stmt_stack.push("expression");
        Expr x;
        Token tk;
        if (Match("-") || Match("+")) {
            tk = token;
            ScanToken();
        } else {
            tk = new Word(null, Tag.AOP);
        }

        x = new Unary(tk, term());

        while (Match(Tag.AOP)) {
            tk = token;
            ScanToken();
            x = new Arith(tk, x, term());
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
    private Expr term() throws Exception {
        Expr x = factor();

        while (Match(Tag.MOP)) {
            var tk = token;
            ScanToken();
            x = new Arith(tk, x, factor());
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
    private Expr factor() throws Exception {
        Expr x;

        switch (token.getTag()) {
            case ID -> x = new Expr(token);
            case INTEGER -> x = new Constant((Number) token);
            case SPLIT -> {
                if (Match("(")) {
                    try {
                        ScanToken();
                        x = exp();
                        StrictMatch(")");
                    } catch (NoRightParenthesisException e) {
                        e.PrintExceptionMessage();
                        var e_ret = new InvalidExpression();
                        e_ret.setError_token(token);
                        e_ret.SetLocator();
                        throw e_ret;
                    }
                } else {
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
