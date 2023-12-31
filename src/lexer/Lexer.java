package lexer;

import java.io.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Objects;
import java.util.Scanner;

/**
 * 词法分析类
 * 通过输入文件路径将代码拆分为词法单元，生成txt文件。
 *
 * @author Wangyx
 * @since 1.0
 */
public class Lexer {
    /**
     * 代码输入流
     */
    private InputStreamReader codeReader = null;
    /**
     * 词法分析输出流
     */
    private OutputStreamWriter codeAnalyseWriter = null;
    /**
     * 缓冲区大小，默认为1024
     */
    private int bufferSize = 1024;
    /**
     * 输入缓冲区
     */
    private final char[] buffer = new char[bufferSize];
    /**
     * 缓冲区指针
     */
    private int buffer_ptr = 0;
    /**
     * 缓冲区当前字符
     */
    private char ch;
    /**
     * 行定位器
     */
    private static long line = 1;
    /**
     * 列定位器
     */
    private static long col = 1;

    private static long preLine = 0;

    private static long preCol = 0;
    /**
     * 文件读取状态
     */
    private boolean file_end = false;
    /**
     * 是否输出到文本文件,默认输出
     */
    private boolean txt_output = true;
    /**
     * 关键字哈希表
     */
    private static final Hashtable<String, Word>
            reserve_words = initReserveWords();

    /**
     * 词法分析器类构造函数
     *
     * @param fileDir 输入代码路径
     */
    public Lexer(String fileDir, boolean txt_output) {
        try {
            var codeFileInput = new FileInputStream(fileDir);
            codeReader = new InputStreamReader(codeFileInput);
            loadCode();// 将字符首次导入到缓冲区
            this.txt_output = txt_output;
            if (this.txt_output) {
                var codeFileOutput = new FileOutputStream("code_lex_analyse.txt");
                codeAnalyseWriter = new OutputStreamWriter(codeFileOutput);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }

    }

    /**
     * 生成并且初始化所有保留字
     *
     * @return 返回所有保留字的基于保留字符串的哈希表
     */
    private static Hashtable<String, Word> initReserveWords() {
        var reserve_words = new Hashtable<String, Word>();
        reserve_words.put("program", new Word("program", Tag.PROGRAM));
        reserve_words.put("const", new Word("const", Tag.CONST));
        reserve_words.put("var", new Word("var", Tag.VAR));
        reserve_words.put("procedure", new Word("procedure", Tag.PROCEDURE));
        reserve_words.put("begin", new Word("begin", Tag.BEGIN));
        reserve_words.put("end", new Word("end", Tag.END));
        reserve_words.put("if", new Word("if", Tag.IF));
        reserve_words.put("then", new Word("then", Tag.THEN));
        reserve_words.put("else", new Word("else", Tag.ELSE));
        reserve_words.put("while", new Word("while", Tag.WHILE));
        reserve_words.put("do", new Word("do", Tag.DO));
        reserve_words.put("call", new Word("call", Tag.CALL));
        reserve_words.put("read", new Word("read", Tag.READ));
        reserve_words.put("write", new Word("write", Tag.WRITE));
        reserve_words.put("odd", new Word("odd", Tag.ODD));
        return reserve_words;
    }

    /**
     * 加载代码文本到缓冲区
     */
    private void loadCode() {
        try {
            if (file_end) throw new Exception("重复读取，文件读取已经完毕");
            int len;
            len = codeReader.read(buffer);
            if (len < bufferSize) {
                file_end = true;
                buffer[len] = '\0';
            } else file_end = false;
            buffer_ptr = 0;
            ch = buffer[buffer_ptr++];
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

    }

    /**
     * 读取缓冲区的一个字符到ch
     */
    private void readChar() {
        try {
            if (buffer_ptr == bufferSize && !file_end) loadCode();
            else ch = buffer[buffer_ptr++];
            ++col;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 从当前指针位置分析下一个词法单元，不包括空白字符与注释
     *
     * @return 返回词法单元类型及其属性
     * @throws Exception <br>e1:非法字符<br>e2:未补全的 := 符号
     */
    public Token analyseToken() throws Exception {
        preCol = col;
        preLine = line;
        // 是空白字符
        while (Character.isWhitespace(ch)) {
            if (ch == '\n') {
                line = line + 1;
                col = 0;
                if (txt_output) codeAnalyseWriter.write("\r\n");
            }
            readChar();
        }
        // 判断是否位于文件末尾
        if (ch == '\0') {
            return new Token(Tag.EOF);
        }
        // 判断是否为数字
        else if (Character.isDigit(ch)) {
            String cur_num = "";
            StringBuilder stringBuilder = new StringBuilder(cur_num);
            while (Character.isDigit(ch)) {
                stringBuilder.append(ch);
                readChar();
            }
            if (Character.isUpperCase(ch) || Character.isLowerCase(ch))
                throw new LexerException(3, ch, line, col);
            cur_num = stringBuilder.toString();
            return new Number(Integer.parseInt(cur_num));
        }
        // 判断是否为合法的保留字或为合法的id
        else if (Character.isUpperCase(ch) || Character.isLowerCase(ch)) {// 首个字符为字母
            String cur_word = "";
            StringBuilder stringBuilder = new StringBuilder(cur_word);
            while (Character.isUpperCase(ch) || Character.isLowerCase(ch) || Character.isDigit(ch)) {
                stringBuilder.append(ch);
                readChar();
            }
            cur_word = stringBuilder.toString();
            var words_token = reserve_words.get(cur_word);
            if (words_token != null) {
                return words_token;
            } else {
                return new Word(cur_word, Tag.ID);
            }
        }
        // 判断是否为合法的符号
        Token token_symbol;
        switch (ch) {
            case '=' -> {
                token_symbol = new Word("=", Tag.LOP);
                readChar();
            }
            case '<' -> {
                readChar(); // 继续读取缓冲区下一个字符
                if (ch == '=') {
                    token_symbol = new Word("<=", Tag.LOP);
                    readChar();
                } else if (ch == '>') {
                    token_symbol = new Word("<>", Tag.LOP);
                    readChar();
                } else token_symbol = new Word("<", Tag.LOP);
            }
            case '>' -> {
                readChar();// 继续读取缓冲区下一个字符
                if (ch == '=') {
                    token_symbol = new Word(">=", Tag.LOP);
                    readChar();
                } else token_symbol = new Word(">", Tag.LOP);
            }
            case '+' -> {
                token_symbol = new Word("+", Tag.AOP);
                readChar();
            }
            case '-' -> {
                token_symbol = new Word("-", Tag.AOP);
                readChar();
            }
            case '*' -> {
                token_symbol = new Word("*", Tag.MOP);
                readChar();
            }
            case '/' -> {
                token_symbol = new Word("/", Tag.MOP);
                readChar();
            }
            case ':' -> {
                readChar();
                if (ch == '=') {
                    token_symbol = new Word(":=", Tag.ASSIGN);
                    readChar();
                } else throw new LexerException(2, ':', line, col - 1);
            }
            // 分隔符
            case '(', ')', ',', ';' -> {
                token_symbol = new Word(String.valueOf(ch), Tag.SPLIT);
                readChar();
            }
            // 非法字符
            default -> throw new LexerException(1, ch, line, col);
        }
        return token_symbol;
    }

    private void writeToken(Token token) throws IOException {
        if (txt_output) {
            String token_out_str;
            if (Objects.requireNonNull(token.getTag()) == Tag.INTEGER) {
                var token_num = (Number) token;
                token_out_str = "< "
                        + token_num.getTag()
                        + " "
                        + token_num.getVal()
                        + " >"
                        + ' ';
                codeAnalyseWriter.write(token_out_str);
            } else if (Objects.requireNonNull(token.getTag()) != Tag.EOF) {
                var token_word = (Word) token;
                token_out_str = "< "
                        + token_word.getTag()
                        + " "
                        + token_word.getContent()
                        + " >"
                        + ' ';
                codeAnalyseWriter.write(token_out_str);
            }
        }
    }

    /**
     * @return
     * @throws Exception
     */
    public ArrayList<Token> analyseCode() throws Exception {
        var TokenList = new ArrayList<Token>();

        try {
            while (ch != '\0') {
                var token = analyseToken();
                TokenList.add(token);
                if (txt_output) writeToken(token);
            }
        } catch (LexerException e) {
            System.out.println((char) 27 + "[31m" + e.getMessage() + (char) 27 + "[0m");
            txt_output = false;
            codeAnalyseWriter.close();
            switch (e.exception_code) {
                case 1 -> {
                    readChar();
                }
                case 2 -> {
                }
                case 3 -> {
                    readChar();
                    while (Character.isUpperCase(ch) ||
                            Character.isLowerCase(ch) ||
                            Character.isDigit(ch)) {
                        readChar();
                    }
                }
            }
            analyseCode();
        }finally {
            codeAnalyseWriter.close();
            codeReader.close();
        }
        return TokenList;
    }

    public void setTxt_output(boolean txt_output) {
        this.txt_output = txt_output;
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    public static long getLine() {
        return line;
    }

    public static long getCol() {
        return col;
    }

    public static long getPreLine() {
        return preLine;
    }

    public static long getPreCol() {
        return preCol;
    }

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        System.out.print("请输入待编译文件的文件路径：");
        String fileDir = scanner.nextLine();
        Lexer lexer_test = new Lexer(fileDir, true);
        try {
            var token_test_list = lexer_test.analyseCode();
            System.out.println(token_test_list);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}