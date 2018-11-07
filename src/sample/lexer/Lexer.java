package sample.lexer;


import java.util.ArrayList;
import java.util.List;

public class Lexer {

    //最后分析得到的token序列
    private List<Token> tokens;
    //当前行
    private int currentLine = 0;
    //当前行的当前位置
    private int currentCharPosition = 0;
    //是否处于多行注释状态
    private boolean isInMultipleLineNotation = false;

    //这三个变量用于处理多行注释，本该使用局部变量，因没想起来使用局部变量的解决方案，就用了类变量
    private StringBuilder stringBuilder;
    private int multipleNotationStartLine = 0;
    private int multipleNotationsStart = 0;


    private List<String> lines;
    private String line;

    public Lexer(List<String> lines){
        this.lines = lines;
        tokens = new ArrayList<>();
    }

    public List<Token> getTokens() {
        for(int i = 0; i < lines.size(); i++){
            currentCharPosition = 0;
            currentLine = i + 1;
            line = lines.get(i);
            try{
                do{
                    //如果想一行报多个错，可以将try写到while里面，但是这样存在歧义
                    nextToken();
                }while (currentCharPosition < line.length());
                /*while (currentCharPosition != line.length()){
                    //如果想一行报多个错，可以将try写到while里面，但是这样存在歧义
                    nextToken();
                }*/
            }
            catch (LexicalException e){
                System.out.println(e.getMessage());
            }
        }
        return tokens;
    }

    private void nextToken() throws LexicalException {
        if(isInMultipleLineNotation){
            readMultipleLineNotation();
            return;
        }
        if(currentCharPosition >= line.length()){
            return;
        }
        char currentChar = line.charAt(currentCharPosition++);
        if(isBlankChar(currentChar)){ }
        else if(canGenerateToken(currentChar)){
            tokens.add(new Token(TokenHelper.getSingleCharTokenType(currentChar),
                    String.valueOf(currentChar), currentLine, currentCharPosition-1));
        }
        else if(doubleValid(currentChar)){
            if(currentCharPosition == line.length() || line.charAt(currentCharPosition) != currentChar){
                tokens.add(new Token(TokenHelper.getSingleCharAndDoubleTokenType(currentChar, true),
                        String.valueOf(currentChar), currentLine, currentCharPosition-1));
            }
            else{
                StringBuilder s = new StringBuilder();
                s.append(currentChar);
                s.append(currentChar);
                tokens.add(new Token(TokenHelper.getSingleCharAndDoubleTokenType(currentChar, false),
                        s.toString(), currentLine, currentCharPosition-1));
                currentCharPosition++;
            }
        }
        else if(currentChar == '>'){
            if(currentCharPosition == line.length() || line.charAt(currentCharPosition) != '='){
                tokens.add(new Token(TokenType.GREATER_THAN,
                        String.valueOf(currentChar), currentLine, currentCharPosition-1));
            }
            else{
                tokens.add(new Token(TokenType.GREAT_THAN_OR_EQUAL,
                        ">=", currentLine, currentCharPosition-1));
                currentCharPosition++;
            }
        }
        else if(currentChar == '<'){
            if(currentCharPosition == line.length() || line.charAt(currentCharPosition) != '='){
                tokens.add(new Token(TokenType.LESS_THAN,
                        String.valueOf(currentChar), currentLine, currentCharPosition-1));
            }
            else{
                tokens.add(new Token(TokenType.LESS_THAN_OR_EQUAL,
                        "<=", currentLine, currentCharPosition-1));
                currentCharPosition++;
            }
        }
        else if(currentChar == '!'){
            if(currentCharPosition == line.length() || line.charAt(currentCharPosition) != '='){
                tokens.add(new Token(TokenType.LOGICAL_NOT,
                        "!", currentLine, currentCharPosition-1));
            }
            else{
                tokens.add(new Token(TokenType.NOT_EQUAL,
                        "!=", currentLine, currentCharPosition-1));
                currentCharPosition++;
            }
        }
        else if(currentChar == '/'){
            if(currentCharPosition == line.length()){
                tokens.add(new Token(TokenType.DIVIDE,
                        "/", currentLine, currentCharPosition-1));
            }
            else if(line.charAt(currentCharPosition) == '/'){
                tokens.add(readSingleLineNotation());
            }
            else if(line.charAt(currentCharPosition) == '*'){
                isInMultipleLineNotation = true;
                multipleNotationStartLine = currentLine;
                multipleNotationsStart = currentCharPosition - 1;
                currentCharPosition++;
                if(stringBuilder == null){
                    stringBuilder = new StringBuilder();
                }
                readMultipleLineNotation();
            }
            else{
                tokens.add(new Token(TokenType.DIVIDE,
                        "/", currentLine, currentCharPosition-1));
            }
        }
        else if(currentChar == '-'){
            if(currentCharPosition != line.length() && line.charAt(currentCharPosition) == '-'){
                tokens.add((new Token(TokenType.AUTO_DECREMENT, "--", currentLine, currentCharPosition-1)));
                currentCharPosition++;
            }
            else{
                tokens.add(new Token(TokenType.MINUS, "-", currentLine, currentCharPosition-1));
            }
            /*if(currentCharPosition == line.length()){
                tokens.add(new Token(TokenType.MINUS, "-", currentLine, currentCharPosition-1));
            }
            else if(line.charAt(currentCharPosition) == '-'){
                tokens.add((new Token(TokenType.AUTO_DECREMENT, "--", currentLine, currentCharPosition-1)));
                currentCharPosition++;
            }
            else if(canReadAsNegativeSymbol()){
                tokens.add(readNumber());
            }
            else{
                tokens.add(new Token(TokenType.MINUS, "-", currentLine, currentCharPosition-1));
            }*/
        }
        else if(isLetter(currentChar)){
            tokens.add(readIdentifier());
        }
        else if(currentChar == '\''){
            if(currentCharPosition + 1 >= line.length() || line.charAt(currentCharPosition+1) != '\''){
                throw new LexicalException(currentLine, currentCharPosition, "Illegal character.");
            }
            tokens.add(new Token(TokenType.CHARACTER,
                    String.valueOf(line.charAt(currentCharPosition)), currentLine, currentCharPosition-1));
            currentCharPosition = currentCharPosition + 2;
        }
        else if(currentChar == '\"'){
            tokens.add(readString());
        }
        else if(isLegalCharInNumber(currentChar)){
            tokens.add(readNumber());
        }
        else{
            throw new LexicalException(currentLine, currentCharPosition, "Illegal identifier.");
        }

    }

    /**
     * 识别标识符：变量名+函数名，由字母数字下划线构成，只能以字母开头，不能以下划线结尾
     *
     *
     * @return Token
     */
    private Token readIdentifier() throws LexicalException{
        int begin = currentCharPosition - 1;
        StringBuilder s = new StringBuilder();
        s.append(line.charAt(begin));
        while(true){
            if(currentCharPosition == line.length()){
                if(isLegalIdentifier(s.toString())){
                    if(isKeySymbol(s.toString())){
                        return new Token(TokenHelper.getKeyTokenType(s.toString()),
                                s.toString(), currentLine, begin);
                    }
                    return new Token(TokenType.IDENTIFIER, s.toString(), currentLine, begin);
                }
                throw new LexicalException(currentLine, currentCharPosition, "Illeagal identifier.");
            }
            if(!isLegalCharInIdentifier(line.charAt(currentCharPosition)) && isLegalIdentifier(s.toString())){
                if(isKeySymbol(s.toString())){
                    return new Token(TokenHelper.getKeyTokenType(s.toString()),
                            s.toString(), currentLine, begin);
                }
                return new Token(TokenType.IDENTIFIER, s.toString(), currentLine, begin);
            }
            else if(!isLegalCharInIdentifier(line.charAt(currentCharPosition))){
                throw new LexicalException(currentLine, currentCharPosition, "Illeagal identifier.");
            }
            else{
                s.append(line.charAt(currentCharPosition++));
            }
        }
    }

    /**
     * 识别字符串：目前没有加识别转义字符的功能
     *
     *
     * @return Token
     */
    private Token readString() throws LexicalException{
        int begin = currentCharPosition - 1;
        StringBuilder s = new StringBuilder();
        while (true){
            if(currentCharPosition == line.length()){
                throw new LexicalException(currentLine, currentCharPosition, "Quotes are not closed.");
            }
            else if(line.charAt(currentCharPosition) == '\"'){
                currentCharPosition++;
                return new Token(TokenType.CONSTANT_STRING, s.toString(), currentLine, begin);
            }
            else{
                s.append(line.charAt(currentCharPosition++));
            }
        }
    }

    /**
     * 识别单行注释
     *
     *
     * @return Token
     */
    private Token readSingleLineNotation() {
        int begin = currentCharPosition - 1;
        currentCharPosition++;
        StringBuilder s = new StringBuilder();
        while (true){
            if(currentCharPosition == line.length()){
                return new Token(TokenType.SINGLE_LINE_NOTATION,
                        s.toString(), currentLine, begin);
            }
            else{
                s.append(line.charAt(currentCharPosition++));
            }
        }
    }

    /**
     * 识别多行注释，和其他方法不是很一致，无返回值
     *
     *
     *
     */
    private void readMultipleLineNotation() throws LexicalException{
        while (isInMultipleLineNotation){
            if(currentCharPosition <= line.length() - 2){
                if(line.charAt(currentCharPosition) == '/' && line.charAt(currentCharPosition+1) == '*'){
                    throw new LexicalException(currentLine, currentCharPosition+1, "The multiple line notation cannot contain \"/*\".");
                }
                else if(line.charAt(currentCharPosition) == '*' && line.charAt(currentCharPosition+1) == '/'){
                    tokens.add(new Token(TokenType.MULTIPLE_LINE_NOTATION, stringBuilder.toString(), multipleNotationStartLine, multipleNotationsStart));
                    stringBuilder.setLength(0);
                    isInMultipleLineNotation = false;
                    currentCharPosition = currentCharPosition + 2;
                    return;
                }
                else{
                    stringBuilder.append(line.charAt(currentCharPosition++));
                }
            }
            else{
                if(currentCharPosition != line.length()){
                    stringBuilder.append(line.charAt(currentCharPosition++));
                }
                stringBuilder.append('\n');
                if(currentLine == lines.size()){
                    throw new LexicalException(currentLine, currentCharPosition+1, "The multiple line notation is not closed.");
                }
                break;
            }
        }
    }

    /**
     * 识别数字：有int和double两种，没有加科学记数法，没有float
     *
     *
     * @return Token
     */
    private Token readNumber() throws LexicalException{
        int begin = currentCharPosition - 1;
        StringBuilder s = new StringBuilder();
        s.append(line.charAt(begin));
        while (true){
            if((currentCharPosition == line.length() || isLegalCharFollowNumber(line.charAt(currentCharPosition))) && isLegalNum(s.toString())){
                if(s.toString().contains(".")){
                    return new Token(TokenType.NUMBER_DOUBLE, s.toString(), currentLine, begin);
                }
                return new Token(TokenType.NUMBER_INT, s.toString(), currentLine, begin);
            }
            else if(currentCharPosition == line.length() || isLegalCharFollowNumber(line.charAt(currentCharPosition))){
                throw new LexicalException(currentLine, currentCharPosition, "Illeagal number or illegal identifier.");
            }
            else if(!isRightStepOfNum(s.toString()) && !isLegalNum(s.toString())){
                throw new LexicalException(currentLine, currentCharPosition, "Illeagal number or illegal identifier.");
            }
            else {
                s.append(line.charAt(currentCharPosition++));
            }
        }
    }

    private boolean isLegalCharInIdentifier(char c){
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9') || c == '_';
    }

    private boolean isLegalCharInNumber(char c){
        return (c >= '0' && c <= '9') || c == '-' || c == '.';
    }

    private boolean isLegalNum(String s){
        String regex1 = "-{0,1}[1-9]([0-9]*\\.{0,1}[0-9]+){0,1}"; //匹配整数64616  浮点数8161.15
        String regex2 = "-{0,1}0{0,1}(\\.[0-9]+){0,1}"; //匹配0   浮点数0.53215  及0.0
        return (s.matches(regex1) || s.matches(regex2));
    }

    private boolean isRightStepOfNum(String s){
        String regex1 = "-{0,1}[1-9][0-9]*\\.";
        String regex2 = "-{0,1}0{0,1}\\.";
        return (s.equals("-") || s.matches(regex1) || s.matches(regex2));
    }

    /**
     * 识别正确的标识符：有字母、数字、下划线组成，必须以字母开头，不能以下划线结尾
     *
     * @param input String
     *
     * @return boolean
     */
    private boolean isLegalIdentifier(String input) {
        return (input.matches("^\\w+$") && !input.endsWith("_")
                && isLetter(input.charAt(0)));
    }

    /**
     * 识别保留字
     *
     * @param str String
     *
     * @return boolean
     */
    private boolean isKeySymbol(String str) {
        return (str.equals("if") || str.equals("else")
                || str.equals("read") || str.equals("write")
                || str.equals("while") || str.equals("for")
                || str.equals("switch") || str.equals("case")
                || str.equals("default") || str.equals("int")
                || str.equals("double") || str.equals("char")
                || str.equals("bool") || str.equals("void")
                || str.equals("false") || str.equals("true")
                || str.equals("string"));
    }

    /**
     * 用于单字符token
     *
     * @param c char
     *
     * @return boolean
     */
    private boolean canGenerateToken(char c){
        return(c == ',' || c == ';' || c == '(' || c == ')' || c == '{' || c == '}' || c == '*' || c == ':' || c == '[' || c == ']' || c == '%');
    }

    /**
     * 用于字符 + =
     *
     * @param c char
     *
     * @return boolean
     */
    private boolean doubleValid(char c){
        return(c == '+' || c == '=' || c == '&' || c == '|');
    }

    private boolean isLetter(char c){
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }

    private boolean isBlankChar(char c){
        return c == ' ' || c == '\t';
    }

    private boolean isLegalCharFollowNumber(char c){
        return c == '+' || c == '-' || c == '*' || c == '/' || c == ',' || c == ';' || c == ')' || c == ':' || c =='=' || c == '!' || c == ']' || c == '%' ||
                c == '>' || c == '<' || c == '&' || c == '|' || c == '}' ||
                isBlankChar(c);
    }

    /**
     * 判断负号和减号
     *
     *
     * @return boolean
     */
    private boolean canReadAsNegativeSymbol(){
        return isLegalCharInNumber(line.charAt(currentCharPosition));
    }

}
