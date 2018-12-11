package sample.parser;

import sample.lexer.Token;

public class TreeNode {
    public int parent;
    public String content;
    //public Token token;
    //public boolean isToken = false;

    public TreeNode(int i, String s)
    {
        parent = i;
        content = s;
    }
}
