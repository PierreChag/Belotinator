package com.poulpinou.belotinator.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.fragment.NavHostFragment;

import com.poulpinou.belotinator.R;
import com.poulpinou.belotinator.core.Belote;
import com.poulpinou.belotinator.core.Utils;
import com.poulpinou.belotinator.databinding.FragmentMainBinding;

public class MainFragment extends Fragment {

    private FragmentMainBinding binding;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.binding = FragmentMainBinding.inflate(inflater, container, false);
        this.binding.fabAddGame.setOnClickListener(view -> NavHostFragment.findNavController(MainFragment.this).navigate(R.id.action_MainFragment_to_BeloteFragment));
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentActivity activity = this.getActivity();
        if(activity != null){
            activity.invalidateMenu();
            activity.addMenuProvider(new MenuProvider() {
                @Override
                public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                    menu.clear();
                    menuInflater.inflate(R.menu.menu_main, menu);
                }

                @Override
                public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                    int id = menuItem.getItemId();
                    if(id == R.id.action_settings) {
                        //TODO Add the action_settings effect !
                        return true;
                    }
                    return false;
                }
            });
        }
        this.binding.buttonPlayers.setOnClickListener(view1 -> NavHostFragment.findNavController(MainFragment.this).navigate(R.id.action_MainFragment_to_PlayersFragment));
        this.binding.buttonHistoric.setOnClickListener(view1 -> NavHostFragment.findNavController(MainFragment.this).navigate(R.id.action_MainFragment_to_HistoricFragment));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        this.binding = null;
    }

    private void addOngoingBelotes(@NonNull Activity activity) {
        for(Belote belote : Belote.BELOTES_LIST){
            if(!belote.isFinished()){
                this.binding.ongoingGames.addView(this.createOngoingBeloteView(activity, belote));
            }
        }
    }

    private View createOngoingBeloteView(Activity activity, Belote belote){
        int tenDPinPX = Utils.getPixelFromDp(10);
        int fiveDPinPX = Utils.getPixelFromDp(5);

        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams weightedParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);

        //Main layout
        LinearLayout mainLayout = new LinearLayout(activity);
        LinearLayout.LayoutParams paramMainLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        paramMainLayout.setMargins(0, 0, 0, tenDPinPX);
        mainLayout.setLayoutParams(paramMainLayout);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setBackgroundColor(activity.getResources().getColor(R.color.white, null));

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

        //Team A name
        TextView textLeaderA = new TextView(activity);
        LinearLayout.LayoutParams paramLeaderA = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1f);
        paramLeaderA.setMargins(tenDPinPX, 0, fiveDPinPX, 0);
        textLeaderA.setLayoutParams(paramLeaderA);
        textLeaderA.setText(this.getString(R.string.team_label, belote.getPlayerFromId(1).getName(), belote.getPlayerFromId(3).getName()));
        textLeaderA.setTextColor(activity.getResources().getColor(R.color.black, null));
        textLeaderA.setBackgroundColor(activity.getResources().getColor(R.color.team_A, null));
        textLeaderA.setGravity(Gravity.CENTER);
        textLeaderA.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
        textLeaderA.setPadding(0, fiveDPinPX, 0, fiveDPinPX);
        layoutPlayers.addView(textLeaderA);

        //Team B name
        TextView textLeaderB = new TextView(activity);
        LinearLayout.LayoutParams paramLeaderB = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1f);
        paramLeaderB.setMargins(fiveDPinPX, 0, tenDPinPX, 0);
        textLeaderB.setLayoutParams(paramLeaderB);
        textLeaderB.setText(this.getString(R.string.team_label, belote.getPlayerFromId(2).getName(), belote.getPlayerFromId(4).getName()));
        textLeaderB.setTextColor(activity.getResources().getColor(R.color.black, null));
        textLeaderB.setBackgroundColor(activity.getResources().getColor(R.color.team_B, null));
        textLeaderB.setGravity(Gravity.CENTER);
        textLeaderB.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
        textLeaderB.setPadding(0, fiveDPinPX, 0, fiveDPinPX);
        layoutPlayers.addView(textLeaderB);

        //Second horizontal layout that contains the two scores
        LinearLayout layoutScores = new LinearLayout(activity);
        layoutScores.setLayoutParams(param);
        layoutScores.setOrientation(LinearLayout.HORIZONTAL);
        layoutScores.setGravity(Gravity.CENTER);
        mainLayout.addView(layoutScores);

        //Team A score
        TextView textScoreA = new TextView(activity);
        textScoreA.setLayoutParams(weightedParam);
        textScoreA.setText(String.valueOf(belote.getTeamAPoints()));
        textScoreA.setTextColor(activity.getResources().getColor(R.color.team_A, null));
        textScoreA.setGravity(Gravity.CENTER);
        textScoreA.setTextSize(TypedValue.COMPLEX_UNIT_SP,30);
        layoutScores.addView(textScoreA);

        //Team B score
        TextView textScoreB = new TextView(activity);
        textScoreB.setLayoutParams(weightedParam);
        textScoreB.setText(String.valueOf(belote.getTeamBPoints()));
        textScoreB.setTextColor(activity.getResources().getColor(R.color.team_B, null));
        textScoreB.setGravity(Gravity.CENTER);
        textScoreB.setTextSize(TypedValue.COMPLEX_UNIT_SP,30);
        layoutScores.addView(textScoreB);

        //OnClickListener that opens the Belote Game
        mainLayout.setOnClickListener(v -> {
            BeloteFragment.selectedBelote = belote;
            NavHostFragment.findNavController(MainFragment.this).navigate(R.id.action_MainFragment_to_BeloteFragment);
        });
        return mainLayout;
    }

    @Override
    public void onResume() {
        super.onResume();
        this.binding.ongoingGames.removeAllViews();
        Activity activity = this.getActivity();
        if(activity != null){
            this.addOngoingBelotes(activity);
        }
    }
}