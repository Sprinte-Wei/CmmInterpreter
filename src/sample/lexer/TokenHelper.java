package sample.lexer;


public class TokenHelper {

    public static TokenType getKeyTokenType(String input){
        switch (input){
            case "int":
                return TokenType.INT;
            case "double":
                return TokenType.DOUBLE;
            case "char":
                return TokenType.CHAR;
            case "bool":
                return TokenType.BOOL;
            case "void":
                return TokenType.VOID;
            case "read":
                return TokenType.READ;
            case "write":
                return TokenType.WRITE;
            case "while":
                return TokenType.WHILE;
            case "for":
                return TokenType.FOR;
            case "if":
                return TokenType.IF;
            case "else":
                return TokenType.ELSE;
            case "switch":
                return TokenType.SWITCH;
            case "case":
                return TokenType.CASE;
            case "default":
                return TokenType.DEFAULT;
            case "true":
                return TokenType.TRUE;
            case "false":
                return TokenType.FALSE;
            case "break":
                return TokenType.BREAK;
            case "return":
                return TokenType.RETURN;
            default:
                return null;
        }
    }

    public static TokenType getSingleCharTokenType(char c){
        switch (c){
            case ',':
                return TokenType.COMMA;
            case ';':
                return TokenType.SEMICOLON;
            case '(':
                return TokenType.LEFT_PARENTHESIS;
            case ')':
                return TokenType.RIGHT_PARENTHESIS;
            case '{':
                return TokenType.LEFT_BRACE;
            case '}':
                return TokenType.RIGHT_BRACE;
            case '*':
                return TokenType.MULTIPLY;
            case ':':
                return TokenType.COLON;
            case '[':
                return TokenType.LEFT_BRACKET;
            case ']':
                return TokenType.RIGHT_BRACKET;
            default:
                return null;
        }
    }

    public static TokenType getSingleCharAndDoubleTokenType(char c, boolean isSingle){
        switch (c){
            case '+':
                return isSingle ? TokenType.PLUS : TokenType.AUTO_INCREMENT;
            case '=':
                return isSingle ? TokenType.ASSIGN : TokenType.EQUAL;
            default:
                return null;
        }
    }


}
