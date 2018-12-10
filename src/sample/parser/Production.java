package sample.parser;

import java.util.ArrayList;

public class Production {
    public ArrayList<String> tokens = new ArrayList<>();

    public Production(String s[])
    {
        for(int i = 0; i < s.length; i++)
        {
            tokens.add(s[i]);
        }
    }
}
