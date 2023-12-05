package lexer;

/**
 * <b>Word类用于管理标识符、关键字以及运算符</b>
 *
 * @see Token
 */
public class Word extends Token {
    String contend = null;

    public Word(String contend, Tag tag) {
        super(tag);
        this.contend = contend;
    }

    public String getContent() {
        return contend;
    }

    @Override
    public String toString() {
        return "Word{" +
                "contend='" + contend + '\'' +
                '}';
    }
}