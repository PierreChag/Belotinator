package com.poulpinou.belotinator;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Bundle;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.poulpinou.belotinator.core.Belote;
import com.poulpinou.belotinator.core.DeclarationType;
import com.poulpinou.belotinator.core.Player;
import com.poulpinou.belotinator.core.Round;
import com.poulpinou.belotinator.core.RoundType;

import java.util.Calendar;

public class BeloteFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private static int SCREEN_HEIGHT = 0;
    private int emptyPlayerCount;
    private Belote beloteGame;
    private Round newRound = new Round();
    private DeclarationType newDeclaration = null;

    private void setScreenHeight() {
        if(this.getContext() != null){
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                Display display = ((WindowManager) this.getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                SCREEN_HEIGHT = size.y;
            }
        }
        SCREEN_HEIGHT = 1000;
    }


    public BeloteFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setScreenHeight();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_belote, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Activity activity = this.getActivity();
        if (activity != null){
            this.setupAllPlayersSpinners(activity.findViewById(R.id.spinner_playerA_equipA));
            this.setupAllPlayersSpinners(activity.findViewById(R.id.spinner_playerB_equipA));
            this.setupAllPlayersSpinners(activity.findViewById(R.id.spinner_playerA_equipB));
            this.setupAllPlayersSpinners(activity.findViewById(R.id.spinner_playerB_equipB));

            for (RoundType roundType : RoundType.values()){
                this.setupRoundTypeButton(activity.findViewById(this.getButtonRoundTypeId(roundType)), roundType);
            }
            this.setupDeclarationsButton(activity.findViewById(R.id.declaration_button));
            this.setupRemoveDeclarations(activity.findViewById(R.id.layout_declarations_a));
        }
    }

    private void setupAllPlayersSpinners(Spinner spinner){
        if (this.getActivity() != null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this.getActivity(), R.layout.centerer_layout, Player.getStringPlayerList(this.getActivity()));
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
            spinner.setOnItemSelectedListener(this);
        }
    }

    private void setupBelotePlayersSpinners(Spinner spinner){
        if (this.getActivity() != null && this.beloteGame != null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this.getActivity(), R.layout.centerer_layout_big, this.beloteGame.getStringPlayerList(this.getActivity()));
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
        }
    }

    private void setupRoundTypeButton(ImageButton imageButton, RoundType roundType){
        imageButton.setOnClickListener(v -> {
            if (getActivity() != null){
                if (newRound.getType() != null){
                    ImageButton oldButton = getActivity().findViewById(getButtonRoundTypeId(newRound.getType()));
                    oldButton.setImageResource(newRound.getType().getIdRoundTypeImageUnselected());
                }
                newRound.setRoundType(roundType);
                ImageButton oldButton = getActivity().findViewById(getButtonRoundTypeId(roundType));
                oldButton.setImageResource(roundType.getIdRoundTypeImageShadow());
            }
        });
    }

    @SuppressLint("InflateParams")
    private void setupDeclarationsButton(TextView button){
        button.setOnClickListener(v -> {
            FragmentActivity activity = getActivity();
            if (activity != null){
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                LayoutInflater inflater = requireActivity().getLayoutInflater();
                View modifiedView = inflater.inflate(R.layout.dialog_declaration, null);
                builder.setView(modifiedView);

                GridView grid = modifiedView.findViewById(R.id.declaration_grid);
                grid.setAdapter(new DeclarationType.DeclarationAdapter(activity));
                grid.setOnItemClickListener((adapterView, view, position, id) -> {
                    view.setBackgroundColor(this.getResources().getColor(R.color.light_grey));
                    if(newDeclaration != null){
                        ImageView previousSelectedView = (ImageView) grid.getChildAt(newDeclaration.getId());
                        previousSelectedView.setSelected(false);
                        previousSelectedView.setBackgroundColor(this.getResources().getColor(R.color.white));
                    }
                    newDeclaration = DeclarationType.values()[position];
                });

                Spinner spinnerPlayer = modifiedView.findViewById(R.id.spinner_player_declaration);
                this.setupBelotePlayersSpinners(spinnerPlayer);

                builder.setTitle(R.string.add_a_declaration);
                builder.setPositiveButton(R.string.confirm, (dialog, id) -> {
                    if(newDeclaration == null || spinnerPlayer.getSelectedItemPosition() == 0){
                        Toast.makeText(activity, R.string.select_first, Toast.LENGTH_LONG).show();
                    }else{
                        dialog.dismiss();
                        newRound.addDeclaration(spinnerPlayer.getSelectedItemPosition(), newDeclaration);
                        LinearLayout layoutB = getActivity().findViewById(R.id.layout_declarations_b);
                        ImageView iv = getDeclarationImageView(activity, newDeclaration);
                        if(Belote.isEquipA(spinnerPlayer.getSelectedItemPosition())){
                            LinearLayout layoutA = getActivity().findViewById(R.id.layout_declarations_a);
                            iv.setBackgroundColor(activity.getResources().getColor(R.color.equip_A));
                            layoutA.addView(iv, layoutA.indexOfChild(layoutB));
                        }else{
                            iv.setBackgroundColor(activity.getResources().getColor(R.color.equip_B));
                            layoutB.addView(iv);
                        }
                        newDeclaration = null;
                    }
                });
                builder.setNegativeButton(R.string.cancel, (dialog, id) -> dialog.dismiss());
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    private void setupRemoveDeclarations(LinearLayout linearLayout) {
        linearLayout.setOnClickListener(v -> {
            Activity activity = this.getActivity();
            if(activity != null){
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle(R.string.remove_declarations);
                builder.setPositiveButton(R.string.confirm, (dialog, which) -> {
                    LinearLayout layoutB = activity.findViewById(R.id.layout_declarations_b);
                    layoutB.removeAllViews();
                    linearLayout.removeViewsInLayout(0, linearLayout.getChildCount() - 1);
                    dialog.dismiss();
                });
                builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.cancel());
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }

    private static ImageView getDeclarationImageView(Context context, DeclarationType declarationType) {
        ImageView iv = new ImageView(context);
        iv.setImageResource(declarationType.getIdIcon());
        iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        int px = Math.round(30 * (metrics.densityDpi / 160f));
        iv.setLayoutParams(new LinearLayout.LayoutParams(px, px));
        return iv;
    }

    private int getButtonRoundTypeId(RoundType roundType){
        switch (roundType){
            default:
            case CLUB:
                return R.id.button_club;
            case DIAMOND:
                return R.id.button_diamond;
            case HEART:
                return R.id.button_heart;
            case SPADE:
                return R.id.button_spade;
            case WITHOUT_TRUMP:
                return R.id.button_without_trump;
            case ALL_TRUMP:
                return R.id.button_all_trump;
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        if(this.getActivity() != null){
            this.emptyPlayerCount = 0;
            this.tryResetSelectedPlayer(this.getActivity(), parent.getId(), R.id.spinner_playerA_equipA, pos);
            this.tryResetSelectedPlayer(this.getActivity(), parent.getId(), R.id.spinner_playerB_equipA, pos);
            this.tryResetSelectedPlayer(this.getActivity(), parent.getId(), R.id.spinner_playerA_equipB, pos);
            this.tryResetSelectedPlayer(this.getActivity(), parent.getId(), R.id.spinner_playerB_equipB, pos);
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
                            Player.getPlayerFromListIndex(spinnerAB.getSelectedItemPosition()),
                            Player.getPlayerFromListIndex(spinnerBA.getSelectedItemPosition()),
                            Player.getPlayerFromListIndex(spinnerBB.getSelectedItemPosition()),
                            Belote.DEFAULT_VICTORY_POINTS);
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this.getActivity(), R.layout.centerer_layout, Player.getStringPlayerList(this.getActivity()));
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                Spinner spinnerFirst = this.getActivity().findViewById(R.id.spinner_new_first);
                Spinner spinnerLeader = this.getActivity().findViewById(R.id.spinner_new_leader);
                this.setupBelotePlayersSpinners(spinnerFirst);
                this.setupBelotePlayersSpinners(spinnerLeader);
                slide(
                        this.getActivity().findViewById(R.id.belote_equip_maker_layout),
                        this.getActivity().findViewById(R.id.belote_equip_summary_layout),
                        this.getActivity().findViewById(R.id.belote_new_round));
            }
        }
    }

    private void tryResetSelectedPlayer(@NonNull FragmentActivity activity, int selectedSpinnerID, int comparedSpinnerID, int pos){
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

    public static void slideIn(View viewIn){
        viewIn.setVisibility(View.VISIBLE);
        TranslateAnimation animIn = new TranslateAnimation(0, 0, -SCREEN_HEIGHT, 0);
        animIn.setDuration(600);
        animIn.setFillAfter(true);
        viewIn.startAnimation(animIn);
    }

    public static void slide(View viewOut, View... viewsIn){
        TranslateAnimation animOut = new TranslateAnimation(0, 0, 0, -SCREEN_HEIGHT);
        animOut.setDuration(600);
        animOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                viewOut.setVisibility(View.GONE);
                for(View v : viewsIn){
                    slideIn(v);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        viewOut.startAnimation(animOut);
    }
}