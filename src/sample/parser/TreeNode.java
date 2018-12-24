package sample.parser;

import sample.lexer.Token;

import java.util.ArrayList;

public class TreeNode {
    public int parent;
    public String content;
    public ArrayList<Integer> children = new ArrayList<>();
    //public Token token;
    //public boolean isToken = false;

    public TreeNode(int i, String s)
    {
        parent = i;
        content = s;
    }
}
