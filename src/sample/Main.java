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
        launch(args);
        System.exit(0);
    }


}
