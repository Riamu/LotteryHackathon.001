package com.voltahackathon001.game.cavegeneration;

/*
 * Volta Hackathon
 * Amelia Stead
 * ---------------
 * A cell representing one of two states, on/block or off/empty.
 */

public class Cell {
    private int state; //0: off, 1: on
    private int nextState;

    public Cell() {
        state = 0;
        nextState = 0;
    }

    public int getState() {return state;}
    public void setState(int state) {this.state = state;}
    public int getNextState() {return nextState;}
    public void setNextState(int nextState) {this.nextState = nextState;}

    public void update() {
        state = nextState;
    }

    public String toString() {
        if (state == 0)
            return "-";
        else
            return "X";
    }
}