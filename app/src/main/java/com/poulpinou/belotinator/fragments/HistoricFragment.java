package com.poulpinou.belotinator.fragments;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.poulpinou.belotinator.R;
import com.poulpinou.belotinator.core.Belote;
import com.poulpinou.belotinator.core.Utils;

public class HistoricFragment extends Fragment {

    //Components
    private LinearLayout layoutHistoric;

    // Required empty public constructor
    public HistoricFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_historic, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Activity activity = this.getActivity();
        if (activity != null) {
            //Put components into variables
            this.layoutHistoric = activity.findViewById(R.id.layout_historic);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        this.layoutHistoric.removeAllViews();
        Activity activity = this.getActivity();
        if(activity != null){
            this.addOngoingBelotes(activity);
        }
    }

    private void addOngoingBelotes(@NonNull Activity activity) {
        for(Belote belote : Belote.BELOTES_LIST){
            this.layoutHistoric.addView(this.createOngoingBeloteView(activity, belote));
        }
    }

    private View createOngoingBeloteView(Activity activity, Belote belote){
        int tenDPinPX = Utils.getPixelFromDp(10);
        int fiveDPinPX = Utils.getPixelFromDp(5);

        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams paramA = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1f);
        paramA.setMargins(tenDPinPX, 0, fiveDPinPX, 0);
        LinearLayout.LayoutParams paramB = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1f);
        paramB.setMargins(fiveDPinPX, 0, tenDPinPX, 0);

        //Main layout
        LinearLayout mainLayout = new LinearLayout(activity);
        LinearLayout.LayoutParams paramMainLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        paramMainLayout.setMargins(0, 0, 0, tenDPinPX);
        mainLayout.setLayoutParams(paramMainLayout);
        mainLayout.setPadding(0, 0, 0, tenDPinPX);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setBackgroundColor(activity.getResources().getColor(R.color.light_grey, null));

        //Name of the belote game : the date.
        TextView textBeloteTitle = new TextView(activity);
        textBeloteTitle.setLayoutParams(param);
        textBeloteTitle.setText(belote.getDateInLongString());
        textBeloteTitle.setTextColor(activity.getResources().getColor(R.color.dark_grey, null));
        textBeloteTitle.setGravity(Gravity.CENTER);
        textBeloteTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP,24);
        textBeloteTitle.setPadding(0, fiveDPinPX, 0, fiveDPinPX);
        mainLayout.addView(textBeloteTitle);

        //First horizontal layout that contains the two teams
        LinearLayout layoutPlayers = new LinearLayout(activity);
        layoutPlayers.setLayoutParams(param);
        layoutPlayers.setOrientation(LinearLayout.HORIZONTAL);
        layoutPlayers.setGravity(Gravity.CENTER);
        mainLayout.addView(layoutPlayers);

        //Equip A name
        TextView textLeaderA = new TextView(activity);
        textLeaderA.setLayoutParams(paramA);
        textLeaderA.setText(this.getString(R.string.equip_label, belote.getPlayerFromId(1).getName(), belote.getPlayerFromId(3).getName()));
        textLeaderA.setGravity(Gravity.CENTER);
        textLeaderA.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
        textLeaderA.setPadding(0, fiveDPinPX, 0, fiveDPinPX);
        textLeaderA.setTextColor(activity.getResources().getColor(R.color.equip_A, null));
        textLeaderA.setBackgroundColor(activity.getResources().getColor(R.color.dark_grey, null));
        layoutPlayers.addView(textLeaderA);

        //Equip B name
        TextView textLeaderB = new TextView(activity);
        textLeaderB.setLayoutParams(paramB);
        textLeaderB.setText(this.getString(R.string.equip_label, belote.getPlayerFromId(2).getName(), belote.getPlayerFromId(4).getName()));
        textLeaderB.setGravity(Gravity.CENTER);
        textLeaderB.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
        textLeaderB.setPadding(0, fiveDPinPX, 0, fiveDPinPX);
        textLeaderB.setTextColor(activity.getResources().getColor(R.color.equip_B, null));
        textLeaderB.setBackgroundColor(activity.getResources().getColor(R.color.dark_grey, null));
        layoutPlayers.addView(textLeaderB);

        //Second horizontal layout that contains the two scores
        LinearLayout layoutScores = new LinearLayout(activity);
        layoutScores.setLayoutParams(param);
        layoutScores.setOrientation(LinearLayout.HORIZONTAL);
        layoutScores.setGravity(Gravity.CENTER);
        mainLayout.addView(layoutScores);

        //Equip A score
        TextView textScoreA = new TextView(activity);
        textScoreA.setLayoutParams(paramA);
        textScoreA.setText(String.valueOf(belote.getEquipAPoints()));
        textScoreA.setGravity(Gravity.CENTER);
        textScoreA.setTextSize(TypedValue.COMPLEX_UNIT_SP,30);
        textLeaderB.setPadding(0, fiveDPinPX, 0, fiveDPinPX);
        layoutScores.addView(textScoreA);

        //Equip B score
        TextView textScoreB = new TextView(activity);
        textScoreB.setLayoutParams(paramB);
        textScoreB.setText(String.valueOf(belote.getEquipBPoints()));
        textScoreB.setGravity(Gravity.CENTER);
        textScoreB.setTextSize(TypedValue.COMPLEX_UNIT_SP,30);
        textLeaderB.setPadding(0, fiveDPinPX, 0, fiveDPinPX);
        layoutScores.addView(textScoreB);

        if(belote.isDone()){
            if(belote.getWinner() == -1){
                textScoreA.setTextColor(activity.getResources().getColor(R.color.black, null));
                textScoreA.setBackgroundColor(activity.getResources().getColor(R.color.equip_A, null));
                textScoreB.setTextColor(activity.getResources().getColor(R.color.equip_B, null));
                textScoreB.setBackgroundColor(activity.getResources().getColor(R.color.white, null));
            }else{
                textScoreA.setTextColor(activity.getResources().getColor(R.color.equip_A, null));
                textScoreA.setBackgroundColor(activity.getResources().getColor(R.color.white, null));
                textScoreB.setTextColor(activity.getResources().getColor(R.color.black, null));
                textScoreB.setBackgroundColor(activity.getResources().getColor(R.color.equip_B, null));
            }
        }else{
            textScoreA.setTextColor(activity.getResources().getColor(R.color.equip_A, null));
            textScoreA.setBackgroundColor(activity.getResources().getColor(R.color.white, null));
            textScoreB.setTextColor(activity.getResources().getColor(R.color.equip_B, null));
            textScoreB.setBackgroundColor(activity.getResources().getColor(R.color.white, null));
        }

        //OnClickListener that opens the Belote Game
        mainLayout.setOnClickListener(v -> {
            BeloteFragment.selectedBelote = belote;
            NavHostFragment.findNavController(HistoricFragment.this).navigate(R.id.action_HistoricFragment_to_BeloteFragment);
        });
        return mainLayout;
    }
}