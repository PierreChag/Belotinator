package com.poulpinou.belotinator.core;

public enum RoundType {

    CLUB("club", 1, 162, 1),
    DIAMOND("diamond", 1, 162, 1),
    HEART("heart", 1, 162, 1),
    SPADE("spade", 1, 162, 1),
    WITHOUT_TRUMP("without_trump", 4, 130, 0),
    ALL_TRUMP("all_trump", 3, 258, 4);

    private final String name;
    private final int scoreMultiplier, pointsPerRound, beloteMaxNumber;

    RoundType(String name, int scoreMultiplier, int pointsPerRound, int beloteMaxNumber){
        this.name = name;
        this.scoreMultiplier = scoreMultiplier;
        this.pointsPerRound = pointsPerRound;
        this.beloteMaxNumber = beloteMaxNumber;
    }

    public String getName(){
        return this.name;
    }

    public int getPointsPerRound(){
        return this.pointsPerRound;
    }

    public int getMultiplier(){
        return this.scoreMultiplier;
    }

    public int getBeloteMaxNumber(){
        return this.beloteMaxNumber;
    }

    public static RoundType getRoundType(String name){
        RoundType[] allRoundTypes = RoundType.values();
        for (RoundType roundType : allRoundTypes) {
            if (roundType.getName().equals(name)) {
                return roundType;
            }
        }
        return CLUB;
    }
}
