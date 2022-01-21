package com.poulpinou.belotinator;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.poulpinou.belotinator.core.Belote;
import com.poulpinou.belotinator.core.Player;

import java.util.Calendar;

public class BeloteFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private int emptyPlayerCount;
    private Belote beloteGame;

    public BeloteFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_belote, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(this.getActivity() != null){
            this.setupPlayerSpinners(this.getActivity().findViewById(R.id.spinner_playerA_equipA));
            this.setupPlayerSpinners(this.getActivity().findViewById(R.id.spinner_playerB_equipA));
            this.setupPlayerSpinners(this.getActivity().findViewById(R.id.spinner_playerA_equipB));
            this.setupPlayerSpinners(this.getActivity().findViewById(R.id.spinner_playerB_equipB));
        }
        if(this.getActivity() != null){
            LinearLayout listItem = this.getActivity().findViewById(R.id.belote_equip_summary_layout);
            listItem.setOnClickListener(v -> slide(v, this.getActivity().findViewById(R.id.belote_equip_maker_layout)));
        }
    }

    private void setupPlayerSpinners(Spinner spinner){
        if (this.getActivity() != null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this.getActivity(), R.layout.centerer_layout, Player.getStringPlayerList(this.getActivity()));
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
            spinner.setOnItemSelectedListener(this);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        if(this.getActivity() != null){
            this.emptyPlayerCount = 0;
            this.tryResetSelectedItem(this.getActivity(), parent.getId(), R.id.spinner_playerA_equipA, pos);
            this.tryResetSelectedItem(this.getActivity(), parent.getId(), R.id.spinner_playerB_equipA, pos);
            this.tryResetSelectedItem(this.getActivity(), parent.getId(), R.id.spinner_playerA_equipB, pos);
            this.tryResetSelectedItem(this.getActivity(), parent.getId(), R.id.spinner_playerB_equipB, pos);
            if(this.emptyPlayerCount == 0){
                Spinner spinnerAA = this.getActivity().findViewById(R.id.spinner_playerA_equipA);
                Spinner spinnerBA = this.getActivity().findViewById(R.id.spinner_playerB_equipA);
                Spinner spinnerAB = this.getActivity().findViewById(R.id.spinner_playerA_equipB);
                Spinner spinnerBB = this.getActivity().findViewById(R.id.spinner_playerB_equipB);
                TextView equipATextView = this.getActivity().findViewById(R.id.equipA_label);
                equipATextView.setText(this.getString(R.string.equip_label, spinnerAA.getSelectedItem().toString(), spinnerBA.getSelectedItem().toString()));
                TextView equipBTextView = this.getActivity().findViewById(R.id.equipB_label);
                equipBTextView.setText(this.getString(R.string.equip_label, spinnerAB.getSelectedItem().toString(), spinnerBB.getSelectedItem().toString()));
                if(this.beloteGame == null){
                    this.beloteGame = new Belote(
                            Calendar.getInstance().getTimeInMillis(),
                            Player.getPlayerFromListIndex(spinnerAA.getSelectedItemPosition()),
                            Player.getPlayerFromListIndex(spinnerBA.getSelectedItemPosition()),
                            Player.getPlayerFromListIndex(spinnerAB.getSelectedItemPosition()),
                            Player.getPlayerFromListIndex(spinnerBB.getSelectedItemPosition()),
                            Belote.DEFAULT_VICTORY_POINTS);
                }
                slide(this.getActivity().findViewById(R.id.belote_equip_maker_layout), this.getActivity().findViewById(R.id.belote_equip_summary_layout));
            }
        }
    }

    private void tryResetSelectedItem(@NonNull FragmentActivity activity, int selectedSpinnerID, int comparedSpinnerID, int pos){
        if(selectedSpinnerID != comparedSpinnerID){
            Spinner spinner = activity.findViewById(comparedSpinnerID);
            int selectedPos = spinner.getSelectedItemPosition();
            if(selectedPos == pos && pos > 0){
                spinner.setSelection(0);
                this.emptyPlayerCount++;
            }else if(selectedPos == 0){
                this.emptyPlayerCount++;
            }
        }else if(pos == 0){
            this.emptyPlayerCount++;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}

    public static void slide(View viewOut, View viewIn){
        TranslateAnimation animOut = new TranslateAnimation(0, 0, 0, -viewOut.getHeight() - ((LinearLayout.LayoutParams) viewOut.getLayoutParams()).topMargin);
        animOut.setDuration(800);
        animOut.setFillAfter(true);
        animOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                viewOut.setVisibility(View.GONE);
                viewIn.setVisibility(View.VISIBLE);
                TranslateAnimation animIn = new TranslateAnimation(0, 0, -viewIn.getHeight() - ((LinearLayout.LayoutParams) viewIn.getLayoutParams()).topMargin, 0);
                animIn.setDuration(800);
                animIn.setFillAfter(true);
                viewIn.startAnimation(animIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        viewOut.startAnimation(animOut);
    }
}