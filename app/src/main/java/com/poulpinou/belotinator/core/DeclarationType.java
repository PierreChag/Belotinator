package com.poulpinou.belotinator.core;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

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

    public int getValue(RoundType type){
        if(type == RoundType.WITHOUT_TRUMP){
            if(this == SQUARE_NINE) return 0;
            if(this == SQUARE_JACK) return 100;
        }
        return this.value;
    }

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

    @NonNull
    @Override
    public String toString()  {
        return this.getName();
    }

    public int getId(){
        for(int i = 0; i < DeclarationType.values().length; i++){
            if(this == DeclarationType.values()[i])
                return i;
        }
        return 0;
    }

    public static class DeclarationAdapter extends BaseAdapter {
        private final Context context;

        public DeclarationAdapter(Context c){
            this.context = c;
        }

        @Override
        public int getCount() {
            return DeclarationType.values().length;
        }

        @Override
        public Object getItem(int position) {
            return DeclarationType.values()[position].getIdIcon();
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView = new ImageView(this.context);
            imageView.setImageResource(DeclarationType.values()[position].getIdIcon());
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            imageView.setAdjustViewBounds(true);
            imageView.setPadding(5, 5, 5, 5);
            return imageView;
        }

    }
}
