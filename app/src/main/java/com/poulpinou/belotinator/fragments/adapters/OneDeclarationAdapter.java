package com.poulpinou.belotinator.fragments.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;

import com.poulpinou.belotinator.core.DeclarationType;

public class OneDeclarationAdapter extends ArrayAdapter<String> {

    private final int[] imagesId;

    public OneDeclarationAdapter(@NonNull Context context, int resource, int textViewId, String[] ownersName, int[] imagesId) {
        super(context, resource, textViewId, ownersName);
        this.imagesId = imagesId;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View v = super.getView(position, convertView, parent);
        TextView tv = v.findViewById(android.R.id.text1);
        Drawable drawable = AppCompatResources.getDrawable(v.getContext(), imagesId[position]);
        if (drawable != null) {
            drawable.setBounds(0,0,200,200);
            tv.setCompoundDrawables(drawable, null, null, null);
        }
        //Add margin between image and text (support various screen densities)
        int dp5 = (int) (5 * v.getResources().getDisplayMetrics().density + 0.5f);
        tv.setCompoundDrawablePadding(dp5);
        return v;
    }
}

