package compiler.main;

import compiler.exceptions.parserExceptions.*;
import lexer.Tag;
import lexer.Token;

import java.util.HashMap;

/**
 * <b>单例</b>
 * 异常列表
 * 通过当前分析块、当前分析词法单元、当前错误词法单元进行异常映射
 * 用双嵌套哈希表维护
 */
public class ParserExceptionList {

    private static final HashMap<String, HashMap<String, ParserException>>
            exception_str_list = new HashMap<>();
    private static final HashMap<String, HashMap<Tag, ParserException>>
            exception_tag_list = new HashMap<>();

    // 构建基于字符串的异常哈希表
    static {
        HashMap<String, ParserException> progStrList = new HashMap<>();
        progStrList.put(";", new NoSemicolonException());
        exception_str_list.put("prog", progStrList);

        HashMap<String, ParserException> constStrList = new HashMap<>();
        constStrList.put(",", new NoCommaException());
        constStrList.put(";", new NoSemicolonException());
        exception_str_list.put("condecl", constStrList);

        HashMap<String, ParserException> varStrList = new HashMap<>();
        varStrList.put(",", new NoCommaException());
        varStrList.put(";", new NoSemicolonException());
        exception_str_list.put("var", constStrList);

        HashMap<String, ParserException> procStrList = new HashMap<>();
        procStrList.put("(", new NoLeftParenthesisException());
        procStrList.put(")", new NoRightParenthesisException());
        procStrList.put(";", new NoSemicolonException());
        procStrList.put(",", new NoCommaException());
        exception_str_list.put("proc", procStrList);

        HashMap<String, ParserException> statementStrList = new HashMap<>();
        statementStrList.put(";", new NoSemicolonException());
        statementStrList.put("(", new NoLeftParenthesisException());
        statementStrList.put(")", new NoRightParenthesisException());
        statementStrList.put(",", new NoCommaException());
        exception_str_list.put("statement", statementStrList);

        HashMap<String, ParserException> statementstStrList = new HashMap<>();
        statementstStrList.put(";", new NoSemicolonException());
        exception_str_list.put("statements", statementstStrList);

        HashMap<String, ParserException> expStrList = new HashMap<>();
        expStrList.put("(", new NoLeftParenthesisException());
        expStrList.put(")", new NoRightParenthesisException());
        exception_str_list.put("expression", expStrList);
    }

    // 构建基于词法单元标签的异常哈希表
    static {
        HashMap<Tag, ParserException> progTagList = new HashMap<>();
        progTagList.put(Tag.PROGRAM, new ProgStartException());
        progTagList.put(Tag.ID, new ProgIDException());
        exception_tag_list.put("prog", progTagList);

        HashMap<Tag, ParserException> blockTagList = new HashMap<>();
        blockTagList.put(Tag.BEGIN, new NoBeginException());
        exception_tag_list.put("block",blockTagList);


        HashMap<Tag, ParserException> bodyTagList = new HashMap<>();
        bodyTagList.put(Tag.BEGIN, new NoBeginException());
        exception_tag_list.put("body", bodyTagList);

        HashMap<Tag, ParserException> constTagList = new HashMap<>();
        constTagList.put(Tag.ASSIGN, new InvalidConstDeclare());
        constTagList.put(Tag.ID, new InvalidConstDeclare());
        constTagList.put(Tag.INTEGER ,new InvalidConstDeclare());
        exception_tag_list.put("condecl", constTagList);

        HashMap<Tag, ParserException> varTagList = new HashMap<>();
        varTagList.put(Tag.ID, new InvalidVarDeclare());
        exception_tag_list.put("var", varTagList);

        HashMap<Tag, ParserException> procTagList = new HashMap<>();
        procTagList.put(Tag.ID, new InvalidArgsException());
        exception_tag_list.put("proc", procTagList);

        HashMap<Tag, ParserException> statementTagList = new HashMap<>();
        statementTagList.put(Tag.ASSIGN, new InvalidAssign());
        statementTagList.put(Tag.THEN, new NoThenException());
        statementTagList.put(Tag.DO, new NoDoException());
        exception_tag_list.put("statement", statementTagList);

        HashMap<Tag, ParserException> expTagList = new HashMap<>();
        expTagList.put(Tag.ID, new InvalidExpression());
        expTagList.put(Tag.INTEGER, new InvalidExpression());
        exception_tag_list.put("expression", expTagList);

        HashMap<Tag, ParserException> lexpTagList = new HashMap<>();
        lexpTagList.put(Tag.LOP ,new InvalidLogicalExpr());
        exception_tag_list.put("lexp", lexpTagList);

        HashMap<Tag, ParserException> callTagList = new HashMap<>();
        callTagList.put(Tag.ID, new InvalidCall());
        exception_tag_list.put("call", callTagList);

        HashMap<Tag, ParserException> readTagList = new HashMap<>();
        readTagList .put(Tag.ID, new ReadIDException());
        exception_tag_list.put("read", readTagList);
    }

    private ParserExceptionList(){}

    static public ParserException GetParserException(String analyse_block, String correct_str, Token error_token){
        return getParserException(error_token, exception_str_list.get(analyse_block).get(correct_str));
    }

    static public ParserException GetParserException(String analyse_block, Tag correct_tag, Token error_token){
        return getParserException(error_token, exception_tag_list.get(analyse_block).get(correct_tag));
    }

    private static ParserException getParserException(Token error_token, ParserException e) {
        if (e instanceof ChMissingException){
            e.setError_token(null);
        } else {
            e.setError_token(error_token);
        }
        e.SetLocator();
        return e;
    }
}
