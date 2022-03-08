package com.poulpinou.belotinator.fragments.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.poulpinou.belotinator.core.DeclarationType;

public class AllDeclarationsAdapter extends BaseAdapter {
    private final Context context;

    public AllDeclarationsAdapter(Context c){
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

