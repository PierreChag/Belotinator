package com.poulpinou.belotinator.core;

public enum RoundType {

    CLUB("club", 1, 162, 1, 0, 100),
    DIAMOND("diamond", 1, 162, 1, 0, 100),
    HEART("heart", 1, 162, 1, 0, 100),
    SPADE("spade", 1, 162, 1, 0, 100),
    WITHOUT_TRUMP("without_trump", 4, 130, 0, 0, 100),
    ALL_TRUMP("all_trump", 3, 258, 4, 150, 200);

    private String name;
    private int scoreMultiplier, pointsPerRound, beloteMaxNumber, squareNineValue, squareJackValue;

    RoundType(String name, int scoreMultiplier, int pointsPerRound, int beloteMaxNumber, int squareNineValue, int squareJackValue){
        this.name = name;
        this.scoreMultiplier = scoreMultiplier;
        this.pointsPerRound = pointsPerRound;
        this.beloteMaxNumber = beloteMaxNumber;
        this.squareNineValue = squareNineValue;
        this.squareJackValue = squareJackValue;
    }

}
