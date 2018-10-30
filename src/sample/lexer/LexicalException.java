package sample.lexer;

public class LexicalException extends Exception {

    public int line;

    public int location;

    public LexicalException(int line, int location){
        super("Invalid Number Input At line " + line + ", location " + location + "!(LexicalException)");
        this.line = line;
        this.location = location;
    }

}

