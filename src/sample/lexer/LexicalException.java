package sample.lexer;

public class LexicalException extends Exception {

    public int line;

    public int location;

    public LexicalException(int line, int location, String message){
        super("Invalid Input At line " + line + ", location " + location + "!(LexicalException: " + message + ")");
        this.line = line;
        this.location = location;
    }

}

