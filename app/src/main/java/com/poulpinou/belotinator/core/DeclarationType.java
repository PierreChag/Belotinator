package com.poulpinou.belotinator.core;

import androidx.annotation.NonNull;

import com.poulpinou.belotinator.R;

public enum DeclarationType {

    BELOTE("belote", 20, R.drawable.declaration_belote),
    TIERCE("tierce", 20, R.drawable.declaration_string_3),
    QUARTE("quarte", 50, R.drawable.declaration_string_4),
    QUINTE("quinte", 100, R.drawable.declaration_string_5),
    SQUARE_SEVEN("square_seven", 0, R.drawable.declaration_square_7),
    SQUARE_EIGHT("square_eight", 0, R.drawable.declaration_square_8),
    SQUARE_NINE("square_nine", 150, R.drawable.declaration_square_9),
    SQUARE_TEN("square_ten", 100, R.drawable.declaration_square_10),
    SQUARE_JACK("square_jack", 200, R.drawable.declaration_square_v),
    SQUARE_QUEEN("square_queen", 100, R.drawable.declaration_square_d),
    SQUARE_KING("square_king", 100, R.drawable.declaration_square_r),
    SQUARE_ACE("square_ace", 100, R.drawable.declaration_square_as);

    private final String name;
    private final int value;
    private final int idIcon;

    DeclarationType(String name, int value, int idIcon){
        this.name = name;
        this.value = value;
        this.idIcon = idIcon;
    }

    public String getName(){
        return this.name;
    }

    @NonNull
    @Override
    public String toString()  {
        return this.getName();
    }

    /**
     * @param type can modify the value of a declaration.
     * @return the current value in points of this declaration.
     */
    public int getValue(RoundType type){
        if(type == RoundType.WITHOUT_TRUMP){
            if(this == SQUARE_NINE) return 0;
            if(this == SQUARE_JACK) return 100;
        }
        return this.value;
    }

    /**
     * @return the corresponding resource id.
     */
    public int getIdIcon(){
        return this.idIcon;
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

    /**
     * @return an index value specific to each of the 12 DeclarationTypes.
     */
    public int getId(){
        for(int i = 0; i < DeclarationType.values().length; i++){
            if(this == DeclarationType.values()[i])
                return i;
        }
        return 0;
    }
}
