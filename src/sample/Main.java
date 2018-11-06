package sample;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sample.lexer.Lexer;
import sample.lexer.Token;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("CmmInterpreter");
        primaryStage.setScene(new Scene(root, 1200, 900));
        primaryStage.setResizable(false);
        primaryStage.show();
    }


    public static void main(String[] args) {
        //launch(args);

        Lexer l = new Lexer(getLinesFromResource());
        List<Token> tokens = l.getTokens();
        for(Token token : tokens){
            System.out.println(token);
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
