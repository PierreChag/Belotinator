package com.poulpinou.belotinator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
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

    //TODO setLeader et setFirst à chaque modification (et recalculer les scores) et verrouiller les 2 edit text.
    private static int SCREEN_HEIGHT = 0;
    private int emptyPlayerCount;
    private Belote beloteGame;
    private Round newRound = new Round();
    private DeclarationType newDeclaration = null;

    private EditText editTextRawPointsA, editTextRawPointsB;
    private TextView textViewFinalPointsA, textViewFinalPointsB;

    private void setScreenHeight() {
        if(this.getContext() != null){
            Display display = ((WindowManager) this.getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            SCREEN_HEIGHT = size.y;
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
            this.editTextRawPointsA = activity.findViewById(R.id.raw_points_A);
            this.editTextRawPointsB = activity.findViewById(R.id.raw_points_B);
            this.textViewFinalPointsA = activity.findViewById(R.id.final_points_A);
            this.textViewFinalPointsB = activity.findViewById(R.id.final_points_B);
            this.setupRawPointsEditText(this.editTextRawPointsA, this.editTextRawPointsB, true, this.textViewFinalPointsA, this.textViewFinalPointsB);
            this.setupRawPointsEditText(this.editTextRawPointsB, this.editTextRawPointsA, false, this.textViewFinalPointsA, this.textViewFinalPointsB);
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
            spinner.setOnItemSelectedListener(this);
        }
    }

    private void setupRoundTypeButton(ImageButton imageButton, RoundType roundType){
        imageButton.setOnClickListener(v -> {
            if (getActivity() != null && roundType != newRound.getType()){
                if (newRound.getType() != null){
                    ImageButton oldButton = getActivity().findViewById(getButtonRoundTypeId(newRound.getType()));
                    oldButton.setImageResource(newRound.getType().getIdRoundTypeImageUnselected());
                }
                newRound.setRoundType(roundType);
                ImageButton oldButton = getActivity().findViewById(getButtonRoundTypeId(roundType));
                oldButton.setImageResource(roundType.getIdRoundTypeImageShadow());
                this.updatePoints(getActivity());
            }
        });
    }

    private void setupDeclarationsButton(TextView button){
        button.setOnClickListener(v -> {
            FragmentActivity activity = getActivity();
            if (activity != null){
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                LayoutInflater inflater = requireActivity().getLayoutInflater();
                View modifiedView = inflater.inflate(R.layout.dialog_declaration_choice, null);
                builder.setView(modifiedView);

                GridView grid = modifiedView.findViewById(R.id.declaration_grid);
                grid.setAdapter(new DeclarationType.AllDeclarationsAdapter(activity));
                grid.setOnItemClickListener((adapterView, view, position, id) -> {
                    view.setBackgroundColor(this.getResources().getColor(R.color.light_grey, null));
                    if(newDeclaration != null){
                        ImageView previousSelectedView = (ImageView) grid.getChildAt(newDeclaration.getId());
                        previousSelectedView.setSelected(false);
                        previousSelectedView.setBackgroundColor(this.getResources().getColor(R.color.white, null));
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
                        LinearLayout layoutB = getActivity().findViewById(R.id.layout_declarations_b);
                        ImageView iv = getDeclarationImageView(activity, newDeclaration);
                        iv.setId(newRound.addDeclaration(spinnerPlayer.getSelectedItemPosition(), newDeclaration));
                        this.updateFinalPoints(activity);
                        if(Belote.isEquipA(spinnerPlayer.getSelectedItemPosition())){
                            LinearLayout layoutA = getActivity().findViewById(R.id.layout_declarations_a);
                            iv.setBackgroundColor(activity.getResources().getColor(R.color.equip_A, null));
                            layoutA.addView(iv, layoutA.indexOfChild(layoutB));
                        }else{
                            iv.setBackgroundColor(activity.getResources().getColor(R.color.equip_B, null));
                            layoutB.addView(iv);
                        }
                        newDeclaration = null;
                    }
                    dialog.dismiss();
                });
                builder.setNegativeButton(R.string.cancel, (dialog, id) -> dialog.cancel());
                builder.show();
            }
        });
    }

    private void setupRemoveDeclarations(LinearLayout linearLayout) {
        linearLayout.setOnClickListener(v -> {
            Activity activity = this.getActivity();
            if(activity != null){
                DeclarationType.OneDeclarationAdapter adapter = new DeclarationType.OneDeclarationAdapter(
                        activity,
                        android.R.layout.select_dialog_item,
                        android.R.id.text1,
                        newRound.getDeclarationList().stream().map((Round.PlayerDeclaration declaration) -> declaration.getPlayerName(beloteGame)).toArray(String[]::new),
                        newRound.getDeclarationList().stream().mapToInt(declaration -> declaration.getDeclarationType().getIdIcon()).toArray());
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle(R.string.remove_declaration);
                builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item ) {
                                View declarationView = activity.findViewById(newRound.getDeclarationViewId(item));
                                ((ViewGroup) declarationView.getParent()).removeView(declarationView);
                                newRound.removeDeclaration(item);
                                updateFinalPoints(activity);
                                dialog.dismiss();
                            }
                        });
                builder.setNegativeButton(R.string.cancel, (dialog, id) -> dialog.cancel());
                builder.show();
            }
        });
    }

    private void setupRawPointsEditText(EditText editText, EditText otherEquipEditText, boolean equipA, TextView viewEquipA, TextView viewEquipB) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                int points;
                try{
                    points = Integer.parseInt(s.toString());
                }catch(NumberFormatException e) {
                    s.clear();
                    return;
                }
                if(newRound == null) return;
                if(points > newRound.getType().getPointsPerRound()) {
                    points = newRound.getType().getPointsPerRound();
                    s.clear();
                    s.append(String.valueOf(points));
                }
                if(newRound.getRawPoints(equipA) != points && getActivity() != null){
                    newRound.setPointsEquip(equipA, points);
                    otherEquipEditText.setText(String.valueOf(newRound.getRawPoints(!equipA)));
                    updateFinalPoints(getActivity());
                }
            }
        });
        //Retracts the keyboard when the edit text is not selected.
        editText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus && getActivity() != null) {
                InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });
        this.switchEditText(editText, false);
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
            if(parent.getId() == R.id.spinner_new_leader){
                //onItemSelected event for the spinners used to set the leader player.
                this.newRound.setLeaderPlayer(pos);
                this.updatePoints(this.getActivity());

            }else if(parent.getId() == R.id.spinner_new_first){
                //onItemSelected event for the spinners used to set the first player.
                this.newRound.setStartingPlayer(pos);
                this.updatePoints(this.getActivity());

            }else if(parent.getId() == R.id.spinner_playerA_equipA
                    || parent.getId() == R.id.spinner_playerB_equipA
                    || parent.getId() == R.id.spinner_playerA_equipB
                    || parent.getId() == R.id.spinner_playerB_equipB){
                //onItemSelected event for the spinners used to set the player at the start of the Belote Game.
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

    public void updatePoints(@NonNull Context context){
        if(this.newRound.canAddPoints()){
            switchEditText(this.editTextRawPointsA, true);
            switchEditText(this.editTextRawPointsB, true);
        }else{
            switchEditText(this.editTextRawPointsA, false);
            switchEditText(this.editTextRawPointsB, false);
            this.newRound.clearPoints();
        }
        this.updateFinalPoints(context);
    }

    /**
     * Computes the finalPoints with the current parameters and updates the 2 TextView showing the final Points.
     */
    public void updateFinalPoints(@NonNull Context context){
        if(this.newRound.canAddPoints()){
            this.newRound.computesFinalPoints();
            this.textViewFinalPointsA.setText(String.valueOf(newRound.getFinalPoints(true)));
            this.textViewFinalPointsB.setText(String.valueOf(newRound.getFinalPoints(false)));
            if(newRound.winnerIsEquipA()){
                this.textViewFinalPointsA.setBackgroundColor(context.getResources().getColor(R.color.equip_A, null));
                this.textViewFinalPointsB.setBackgroundColor(0x00000000);
            }else{
                this.textViewFinalPointsA.setBackgroundColor(0x00000000);
                this.textViewFinalPointsB.setBackgroundColor(context.getResources().getColor(R.color.equip_B, null));
            }
        }else{
            this.textViewFinalPointsA.setText("0");
            this.textViewFinalPointsA.setBackgroundColor(0x00000000);
            this.textViewFinalPointsB.setText("0");
            this.textViewFinalPointsB.setBackgroundColor(0x00000000);
        }
    }

    /**
     * Inactivates or activates the given EditText.
     * @param editText instance to modify.
     * @param activated is the state the EditText should take.
     */
    public void switchEditText(EditText editText, boolean activated){
        if(editText != null){
            if(activated){
                if(!editText.isEnabled()){
                    editText.setFocusableInTouchMode(true);
                    editText.setEnabled(true);
                    editText.setCursorVisible(true);
                }
            }else{
                if(editText.isEnabled()) {
                    editText.setFocusable(false);
                    editText.setEnabled(false);
                    editText.setCursorVisible(false);
                    editText.getText().clear();
                }
            }
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