package sample.parser;

public class State {
    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public boolean isR() {
        return isR;
    }

    public void setR(boolean r) {
        isR = r;
    }

    public String getError() { return error; }

    public void setError(String s) {
        error = s;
    }

    private int state = -1;//状态，不规约表示跳转到第几个状态，规约表示第几个产生式，负数表示错误，0代表acc
    private boolean isR = false;//是否为规约
    private String error = "";//报错信息
}
