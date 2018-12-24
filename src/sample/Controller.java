package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javax.swing.filechooser.FileFilter;
import javax.swing.*;
import javafx.embed.swing.SwingNode;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import sample.lexer.Lexer;
import sample.lexer.Token;
import sample.lexer.TokenType;
import sample.parser.Parser;
import sample.parser.SyntaxException;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import sample.parser.TreeNode;

import java.awt.*;
import java.awt.MenuItem;
import java.awt.TextArea;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    private MenuItem newMenuItem;
    @FXML
    private TreeView<String> tv;
    @FXML
    private ListView<String> lexerList;
    @FXML
    private Pane filePane;
    @FXML
    private javafx.scene.control.TextArea editArea;
    @FXML
    private TextField fileN;
    @FXML
    private javafx.scene.control.TextArea Terminal;
    @FXML
    private javafx.scene.control.TextArea errorText;
    @FXML
    private Button btn_getImage;
    @FXML
    private javafx.scene.control.TextArea lineCountTextArea;

    private final static JFileTree FILETREE = new JFileTree(
            new JFileTree.ExtensionFilter("lnk"));
    final SwingNode swingNode = new SwingNode();

    private File currentFile;
    ObservableList<String> list = FXCollections.observableArrayList();

    private int lineCount = 1;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        createSwingContent(swingNode);
        filePane.getChildren().add(swingNode);
        FILETREE.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    String str = "", fileName = "";
                    StringBuilder text = new StringBuilder();
                    File file = FILETREE.getSelectFile();
                    currentFile = file;
                    fileName = file.getName();
                    Terminal.setText("");
                    if (file.isFile()) {
                        if (fileName.endsWith(".cmm")
                                || fileName.endsWith(".CMM")
                                || fileName.endsWith(".txt")
                                || fileName.endsWith(".TXT")
                                || fileName.endsWith(".java")) {
                            try {
                                FileReader file_reader = new FileReader(file);
                                BufferedReader in = new BufferedReader(
                                        file_reader);
                                while ((str = in.readLine()) != null)
                                    text.append(str + '\n');
                                fileN.setText(fileName);
                                editArea.setText(text.toString());
                                in.close();
                                file_reader.close();
                            } catch (IOException e2) {
                            }
                        }
                    }
                }
            }
        });

    }

    private Parser parser = new Parser();

    private void createSwingContent(final SwingNode swingNode) {
        SwingUtilities.invokeLater(() -> {
            swingNode.setContent(FILETREE);
        });
    }


    private void setChildren(TreeItem<String> treeNode, int index) {
        if (parser.tree.get(index).children.size() == 0) {
            return;
        } else {
            for (int i = 0; i < parser.tree.get(index).children.size(); i++) {
                TreeItem<String> item = new TreeItem<String>(parser.tree.get(parser.tree.get(index).children.get(i)).content);
                treeNode.getChildren().add(item);
                setChildren(item, parser.tree.get(index).children.get(i));
            }
        }
    }

    private List<String> getList(String s){
        return Arrays.asList(s.split("\n"));
    }

    public void setBtn_run() {
        Terminal.setText("");
        errorText.setText("");
        list = FXCollections.observableArrayList();
        tv.setRoot(null);

        Lexer l = new Lexer(getList(editArea.getText()));
        List<Token> tokens = l.getTokens();
        if (l.isError) {
            Terminal.appendText("-------------------------------|    词法运行出错    |-------------------------------");
            errorText.appendText(l.errorCode.toString());
            return;
        } else {
            Terminal.appendText("-------------------------------|    词法运行完成    |-------------------------------");
            for (int i = 0; i < tokens.size(); i++) {
                Token token = tokens.get(i);
                if (token.getType().equals(TokenType.SINGLE_LINE_NOTATION) || token.getType().equals(TokenType.MULTIPLE_LINE_NOTATION)) {
                    tokens.remove(token);
                    i--;
                }
            }
            for (Token tk : tokens) {
                list.add("Type: " + tk.getType() + ", Value: " + tk.getValue());
            }
            lexerList.setItems(list);

            try {
                parser.readTokens(tokens);
                Terminal.appendText("\n-------------------------------|    语法运行完成    |-------------------------------");
                TreeItem<String> root = new TreeItem<>(parser.tree.get(parser.tree.size() - 1).content);
                tv.setRoot(root);
                root.setExpanded(false);
                setChildren(root, parser.tree.size() - 1);
            } catch (SyntaxException e) {
                Terminal.appendText("\n-------------------------------|    语法运行出错    |-------------------------------");
                errorText.appendText(e.getMessage());
            }
        }
    }
    String dotFormat = "";
    public void setBtn_getImage(){
        sample.parser.TreeNode tn = parser.tree.get(parser.tree.size() - 1);
        setImage(tn,parser.tree.size() - 1);
        createDotGraph(dotFormat, "GrammerTree");
    }

    public void createDotGraph(String dotFormat,String fileName)
    {
        sample.GraphViz gv=new GraphViz();
        gv.addln(gv.start_graph());
        gv.addln("edge [fontname=\"FangSong\"];");
        gv.addln("node [shape=box, fontname=\"FangSong\" size=\"20,20\"];");
        gv.add(dotFormat);
        gv.addln(gv.end_graph());
        String type = "jpg";
        gv.decreaseDpi();
        gv.decreaseDpi();
        File out = new File(fileName+"."+ type);
        gv.writeGraphToFile( gv.getGraph( gv.getDotSource(), type ), out );
        Terminal.appendText("\n-------------------------------|    图片导出完成    |-------------------------------");
    }

    private void setImage(sample.parser.TreeNode treeNode, int index){
        if (parser.tree.get(index).children.size() == 0) {
            return;
        } else {
            for (int i = 0; i < parser.tree.get(index).children.size(); i++) {
                dotFormat += "\""+parser.tree.get(index).content+"["+ index +"]\"" + "->" + "\""+parser.tree.get(parser.tree.get(index).children.get(i)).content + "["+parser.tree.get(index).children.get(i)+"]\"" + ";";
                TreeNode newTreeNode = parser.tree.get(parser.tree.get(index).children.get(i));
                setImage(newTreeNode, parser.tree.get(index).children.get(i));
            }
        }

    }
}
