package sample.parser;

public class SyntaxException extends Exception{
    public int location;
    public int line;

    public SyntaxException(int line, int location, String s) {
        super("Syntax Error At line " + line + " location " + location + " !("+ s + ")");
        this.location = location;
        this.line = line;
    }

    public SyntaxException(String s)
    {
        super(s);
    }

}
