package sample.parser;

public class SyntaxException extends Exception{
    public int location;

    public SyntaxException(int location, String s) {
        super("Syntax Error At " + location + "!("+ s + ")");
        this.location = location;
    }

    public SyntaxException(String s)
    {
        super(s);
    }

}
