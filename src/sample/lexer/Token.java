package sample.lexer;

public class Token {

    private TokenType type;
    private String value;
    private int line;
    private int location;

    public Token(TokenType type, String value, int line, int location) {
        this.type = type;
        this.value = value;
        this.line = line;
        this.location = location;
    }

    public TokenType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public int getLocation(){
        return location;
    }

    public int getLine(){
        return line;
    }

    @Override
    public String toString() {
        return type + " " + value + " " + line + " " + location;
    }

}
