package sample;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sample.lexer.Lexer;
import sample.lexer.Token;
import sample.lexer.TokenType;
import sample.parser.Parser;
import sample.parser.SyntaxException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("CmmInterpreter");
        primaryStage.setScene(new Scene(root, 1200, 700));
        primaryStage.setResizable(false);
        primaryStage.show();
    }


    public static void main(String[] args) {
        //launch(args);

        Lexer l = new Lexer(getLinesFromResource());
        List<Token> tokens = l.getTokens();
        for(int i = 0; i < tokens.size(); i++){
            Token token = tokens.get(i);
            if(token.getType().equals(TokenType.SINGLE_LINE_NOTATION)||token.getType().equals(TokenType.MULTIPLE_LINE_NOTATION))
            {
                tokens.remove(token);
                i--;
            }

            //System.out.println(token);
        }
        Parser parser = new Parser();
        /*List<Token> tokens_ = new ArrayList<>();
        Token token1 = new Token(TokenType.LEFT_PARENTHESIS,"(",0,0);
        Token token2 = new Token(TokenType.NUMBER_INT,"1",0,1);
        Token token3 = new Token(TokenType.PLUS,"+",0,2);
        Token token4 = new Token(TokenType.NUMBER_INT,"1",0,3);
        Token token5 = new Token(TokenType.RIGHT_PARENTHESIS,")",0,4);
        Token token6 = new Token(TokenType.PLUS,"+",0,5);
        Token token7 = new Token(TokenType.NUMBER_INT,"1",0,6);
        tokens_.add(token1);
        tokens_.add(token2);
        tokens_.add(token3);
        tokens_.add(token4);
        tokens_.add(token5);
        tokens_.add(token6);
        tokens_.add(token7);测试*/
        try {
            parser.readTokens(tokens);
        } catch (SyntaxException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

    private static List<String> getLinesFromResource(){
        List<String> lines = new ArrayList<>();
        StringBuilder s = new StringBuilder();
        File file = new File(Main.class.getResource("/11.txt").getPath());
        BufferedReader reader = null;
        try {
            FileInputStream in = new FileInputStream(file);
            reader = new BufferedReader(new InputStreamReader(in,"UTF-8"));
            String tempString;
            while ((tempString = reader.readLine()) != null) {
                lines.add(tempString);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        return lines;
    }
}
