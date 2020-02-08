package com.java.rule40;

public class Game {
    private int rank;
    private char suit;
    public static Card card;

    public static class Card{
        private int rank;
        private char suit;

        public Card(int rank,char suit){
            this.rank = rank;
            this.suit = suit;
        }
    }

    public int getScore(Card card){
        int suitScore;
        switch (card.suit){
            case '$':
                suitScore = 5;
                break;
            case '#':
                suitScore = 7;
                break;
            default:
                suitScore = 0;
        }
        return card.rank+suitScore;
    }

    public int getScore(int rank,char suit){
        int suitScore;
        switch (suit){
            case '$':
                suitScore = 5;
                break;
            case '#':
                suitScore = 7;
                break;
            default:
                suitScore = 0;
        }
        return rank+suitScore;
    }
}
