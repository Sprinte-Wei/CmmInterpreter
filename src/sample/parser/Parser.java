package sample.parser;

import sample.Main;
import sample.lexer.Token;
import sample.lexer.TokenType;

import java.io.*;
import java.util.*;

public class Parser {
    private String start;//开始符号
    private ArrayList<String> ends = new ArrayList<>();//终结符号
    private ArrayList<String> notends = new ArrayList<>();//非终结符号
    private ArrayList<Production> productions = new ArrayList<>();//产生式
    private ArrayList<ArrayList<State>> sTable = new ArrayList<>();//状态表
    private ArrayList<SpecificState> states = new ArrayList<>();//具体的状态

    public Parser()
    {
        getLinesFromResource();
        createSTable();
        printAll();
    }

    private void createSTable()
    {
        SpecificState specificState = new SpecificState();
        ArrayList<String> tokens = new ArrayList<>();
        tokens.add(start);
        tokens.add("0");
        specificState.state.add(tokens);
        specificState.setFinish(true);
        specificState.setPreState(0);
        specificState.setToken("END");
        thisState(specificState);
    }

    private void thisState(SpecificState s)//形成当前状态  未考虑同一状态内相同的情况
    {
        for(int i = 0; i < s.state.size(); i++)
        {
            if(s.state.get(i).size() > 1) {
                String first = s.state.get(i).get(0);
                if(!ends.contains(first) && !s.find.contains(first))
                {
                    s.find.add(first);
                    s.setFinish(false);
                    for(int j = 0; j < productions.size(); j++)
                    {
                        if(productions.get(j).tokens.get(0).equals(first))
                        {
                            ArrayList<String> tokens = new ArrayList<>();
                            for(int k = 1; k < productions.get(j).tokens.size(); k++)
                            {
                                tokens.add(productions.get(j).tokens.get(k));
                            }
                            tokens.add(Integer.toString(j));
                            s.state.add(tokens);
                        }
                    }
                }
            }
        }
        if(s.isFinish())
        {
            boolean unRepeat = true;
            int line = 0;
            for(int i = 0; i < states.size(); i++) {
                if(states.get(i).equal(s))
                {
                    line = i;
                    unRepeat = false;
                }
            }
            if(unRepeat)
            {
                exSTable();
                line = sTable.size()-1;
                for(int i = 0; i < s.state.size(); i++)
                {
                    ArrayList<String> curTokens = s.state.get(i);
                    if(curTokens.size() == 1)
                    {
                        if(curTokens.get(0).equals("0"))
                        {
                            sTable.get(line).get(sTable.get(line).size()-1).setState(0);
                        }
                        else {
                            for (int j = 0; j < sTable.get(line).size(); j++) {
                                State curState = sTable.get(line).get(j);
                                curState.setState(Integer.parseInt(curTokens.get(0)));
                                curState.setR(true);
                            }
                        }
                    }
                }
                states.add(s);
            }
            String token = s.getToken();
            int symbol;
            if(token.equals("END"))
            {
                symbol = ends.size() + notends.size();
            }
            else if(ends.contains(token))
            {
                symbol = ends.indexOf(token);
            }
            else
            {
                symbol = ends.size() + notends.indexOf(token);
            }
            State tempState = sTable.get(s.getPreState()).get(symbol);
            //if(!tempState.isR()) {//全部归约
                tempState.setR(false);
                tempState.setState(line);
            //}

            if(unRepeat) nextState(s,sTable.size()-1);
        }
        else
        {
            s.setFinish(true);
            thisState(s);
        }
    }

    private void nextState(SpecificState s, int pre)//寻找下一状态  未考虑同一状态内相同的情况
    {
        ArrayList<String> tokens = new ArrayList<>();
        for(int i = 0; i < s.state.size(); i++)
        {
            if(s.state.get(i).size() > 1) {
                String token = s.state.get(i).get(0);
                if (!tokens.contains(token)) {
                    tokens.add(token);
                }
            }
        }
        for(int i = 0; i < tokens.size(); i++)
        {
            SpecificState specificState = new SpecificState();
            specificState.setFinish(true);
            specificState.setPreState(pre);
            specificState.setToken(tokens.get(i));

            for(int j = 0; j < s.state.size(); j++)
            {
                ArrayList<String> curStrings = s.state.get(j);
                if(tokens.get(i).equals(curStrings.get(0)))
                {
                    ArrayList<String> curTokens = new ArrayList<>();
                    for(int k = 1; k < curStrings.size(); k++)
                    {
                        curTokens.add(curStrings.get(k));
                    }
                    specificState.state.add(curTokens);
                }
            }

            thisState(specificState);
        }
    }

    private void exSTable()
    {
        ArrayList<State> stateArrayList = new ArrayList<>();
        for(int i = 0; i < ends.size() + notends.size() + 1; i++)
        {
            State state = new State();
            stateArrayList.add(state);
        }
        sTable.add(stateArrayList);
    }

    private void getLinesFromResource()//获取文法信息
    {
        StringBuilder s = new StringBuilder();
        File file = new File(Main.class.getResource("/Grammar.txt").getPath());
        BufferedReader reader = null;
        try {
            FileInputStream in = new FileInputStream(file);
            reader = new BufferedReader(new InputStreamReader(in,"UTF-8"));
            String tempString;

            start = reader.readLine();
            String[] temp = reader.readLine().split(" ");
            for(int i = 0; i < temp.length; i++)
            {
                ends.add(temp[i]);
            }

            temp = reader.readLine().split(" ");
            for(int i = 0; i < temp.length; i++)
            {
                notends.add(temp[i]);
            }

            temp = new String[]{"S", start};
            Production production = new Production(temp);
            productions.add(production);
            while ((tempString = reader.readLine()) != null) {
                temp = tempString.split(" ");
                production = new Production(temp);
                productions.add(production);
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
    }

    private void printAll()
    {
        for(int i = 0; i < sTable.size(); i++)
        {
            ArrayList<State> s = sTable.get(i);
            System.out.print(i);
            for(int j = 0; j < s.size(); j++)
            {
                System.out.print(s.get(j).isR());
                System.out.print(s.get(j).getState());
                System.out.print(" ");
            }
            System.out.println();
        }
    }

    public void readTokens(List<Token> tokens)throws SyntaxException
    {
        //建立token状态栈
        Stack<String> stringStack = new Stack<>();
        stringStack.push("0");

        //建立token队列
        Queue<Token> tokenQueue = new LinkedList<>();
        for(int i = 0; i < tokens.size(); i++)
        {
            Token t = tokens.get(i);
            tokenQueue.offer(t);
        }
        tokenQueue.offer(new Token(TokenType.END,"", tokens.size(),(tokens.get(tokens.size()-1)).getLocation() + (tokens.get(tokens.size()-1).getValue()).length()));//补上句子结尾
        //开始读取
        int Vn = -1;//记录非终结符号，用于递归
        while (tokenQueue.peek() != null)
        {
            //获得状态，即表的行数
            int state = Integer.parseInt(stringStack.peek());

            //获得符号，即表的列数
            int symbol;
            if(Vn > 0) symbol = Vn;
            else {
                Token temp = tokenQueue.peek();
                TokenType type = temp.getType();
                if(type.toString().equals("END"))
                {
                    symbol = ends.size() + notends.size();
                }
                else symbol = ends.indexOf(type.toString());
            }

            //通过表内容来进行处理
            State nextState = sTable.get(state).get(symbol);
            if(nextState.getState() == -1) throw new SyntaxException(tokenQueue.peek().getLine(),tokenQueue.peek().getLocation(), tokenQueue.peek().getValue());//输入形式错误
            else if(nextState.getState() == 0) return;
            else if(nextState.isR())//规约状态
            {
                for(int i = 0; i < (productions.get(nextState.getState()).tokens.size()-1) * 2; i++)
                {
                    stringStack.pop();//归约弹出应该的符号和状态
                }
                Vn = ends.size() + notends.indexOf(productions.get(nextState.getState()).tokens.get(0));
            }
            else//移入状态
            {
                if(Vn > 0) stringStack.push(Integer.toString(Vn));
                else stringStack.push(tokenQueue.poll().getValue());
                if(tokenQueue.size() == 40)
                    Vn = 1;
                stringStack.push(Integer.toString(nextState.getState()));
                Vn = -1;
            }
        }
    }
}
