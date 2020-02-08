package com.java.rule40;

public class Main {
    public static void main(String[] args){
        Game game = new Game();
        System.out.println(game.getScore(5,'$'));
        System.out.println(game.getScore(new Game.Card(5,'$')));
    }
}
