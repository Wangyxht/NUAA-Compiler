package lexer;

public class Token {
    private Tag tag;

    public Token(Tag tag) {
        this.tag = tag;

    }

    public Tag getTag() {
        return tag;
    }

    @Override
    public String toString() {
        return "Token{" +
                "tag=" + tag +
                '}';
    }
}
