package com.poulpinou.belotinator.core;

import com.poulpinou.belotinator.R;

public enum RoundType {

    CLUB("club", 0, 1, 162, R.drawable.club, R.drawable.club_unselected, R.drawable.club_shadow),
    DIAMOND("diamond", 1, 1, 162,  R.drawable.diamond, R.drawable.diamond_unselected, R.drawable.diamond_shadow),
    HEART("heart", 2, 1, 162, R.drawable.heart, R.drawable.heart_unselected, R.drawable.heart_shadow),
    SPADE("spade", 3, 1, 162, R.drawable.spade, R.drawable.spade_unselected, R.drawable.spade_shadow),
    WITHOUT_TRUMP("without_trump", 4, 4, 130, R.drawable.without_trump, R.drawable.without_trump_unselected, R.drawable.without_trump_shadow),
    ALL_TRUMP("all_trump", 5, 3, 258, R.drawable.all_trump, R.drawable.all_trump_unselected, R.drawable.all_trump_shadow);

    private final String name;
    private final int scoreMultiplier, id, pointsPerRound, idRoundTypeImage, idRoundTypeImageUnselected, idRoundTypeImageShadow;

    RoundType(String name, int id, int scoreMultiplier, int pointsPerRound, int idRoundTypeImage, int idRoundTypeImageUnselected, int idRoundTypeImageShadow){
        this.name = name;
        this.id = id;
        this.scoreMultiplier = scoreMultiplier;
        this.pointsPerRound = pointsPerRound;
        this.idRoundTypeImage = idRoundTypeImage;
        this.idRoundTypeImageUnselected = idRoundTypeImageUnselected;
        this.idRoundTypeImageShadow = idRoundTypeImageShadow;
    }

    public String getName(){
        return this.name;
    }

    public int getId(){
        return this.id;
    }

    public int getPointsPerRound(){
        return this.pointsPerRound;
    }

    public int getMultiplier(){
        return this.scoreMultiplier;
    }

    public int getIdRoundTypeImage(){
        return this.idRoundTypeImage;
    }

    public int getIdRoundTypeImageUnselected(){
        return this.idRoundTypeImageUnselected;
    }

    public int getIdRoundTypeImageShadow(){
        return this.idRoundTypeImageShadow;
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
