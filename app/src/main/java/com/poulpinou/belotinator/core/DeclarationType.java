package com.poulpinou.belotinator.core;

public enum DeclarationType {

    BELOTE("belote", 20),
    TIERCE("tierce", 20),
    QUARTE("quarte", 50),
    QUINTE("quinte", 100),
    SQUARE_NINE("square_ace", 0),
    SQUARE_JACK("square_queen", 100),
    SQUARE_QUEEN("square_queen", 100),
    SQUARE_KING("square_king", 100),
    SQUARE_TEN("square_ten", 100),
    SQUARE_ACE("square_ace", 100);

    private final String name;
    private final int value;

    DeclarationType(String name, int value){
        this.name = name;
        this.value = value;
    }

    public String getName(){
        return this.name;
    }

    public int getValue(RoundType type){
        if(type == RoundType.ALL_TRUMP){
            if(this == SQUARE_NINE) return 150;
            if(this == SQUARE_JACK) return 200;
        }
        return this.value;
    }

    public static DeclarationType getDeclarationType(String name){
        DeclarationType[] allDeclarationTypes = DeclarationType.values();
        for (DeclarationType declarationType : allDeclarationTypes) {
            if (declarationType.getName().equals(name)) {
                return declarationType;
            }
        }
        return BELOTE;
    }
}
