package sample.parser;

import java.util.ArrayList;

public class SpecificState {
    public ArrayList<java.util.ArrayList<String>> state = new ArrayList<>();//表达式
    public ArrayList<String> find = new ArrayList<>();//寻找过的first集
    private boolean isFinish;//判断当前状态生成完毕
    private int preState;//上一个状态，用于生成表
    private String token;//接受到的符号，用于生成表

    public boolean isFinish() {
        return isFinish;
    }

    public void setFinish(boolean finish) {
        isFinish = finish;
    }

    public int getPreState() {
        return preState;
    }

    public void setPreState(int preState) {
        this.preState = preState;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean equal(SpecificState s)//判断两个状态是否相同
    {
        if(state.size() != s.state.size()) return false;
        for (int i = 0; i < state.size(); i++)
        {
            ArrayList<java.util.ArrayList<String>> sState = s.state;
            for(int j = 0; j < state.get(i).size(); j++)
            {
                if(!state.get(i).get(j).equals(sState.get(i).get(j))) return false;
            }
        }
        return  true;
    }
}
