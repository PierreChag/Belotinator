package com.poulpinou.belotinator.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

import com.poulpinou.belotinator.R;
import com.poulpinou.belotinator.core.Belote;
import com.poulpinou.belotinator.core.DeclarationType;
import com.poulpinou.belotinator.core.Player;
import com.poulpinou.belotinator.core.Round;
import com.poulpinou.belotinator.core.RoundType;
import com.poulpinou.belotinator.core.Utils;
import com.poulpinou.belotinator.fragments.adapters.AllDeclarationsAdapter;
import com.poulpinou.belotinator.fragments.adapters.OneDeclarationAdapter;

import java.util.ArrayList;
import java.util.Calendar;

public class BeloteFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    //Variables
    private int emptyPlayerCount;
    public static Belote selectedBelote;
    private Round newRound = new Round();
    private DeclarationType newDeclaration = null;

    //Components
    private EditText editTextRawPointsA, editTextRawPointsB;
    private TextView textViewFinalPointsA, textViewFinalPointsB, textViewTotalPointsA, textViewTotalPointsB, textViewPointTips;
    private LinearLayout layoutBelote, layoutNewRound;
    private final ArrayList<LayoutRoundSummary> listLayoutSummary = new ArrayList<>();
    private Spinner spinnerAA, spinnerBA, spinnerAB, spinnerBB;

    //Required empty public constructor
    public BeloteFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_belote, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Activity activity = this.getActivity();
        if (activity != null){
            //Put components into variables
            this.spinnerAA = activity.findViewById(R.id.spinner_playerA_equipA);
            this.spinnerBA = activity.findViewById(R.id.spinner_playerB_equipA);
            this.spinnerAB = activity.findViewById(R.id.spinner_playerA_equipB);
            this.spinnerBB = activity.findViewById(R.id.spinner_playerB_equipB);
            this.editTextRawPointsA = activity.findViewById(R.id.raw_points_A);
            this.editTextRawPointsB = activity.findViewById(R.id.raw_points_B);
            this.textViewFinalPointsA = activity.findViewById(R.id.final_points_A);
            this.textViewFinalPointsB = activity.findViewById(R.id.final_points_B);
            this.textViewTotalPointsA = activity.findViewById(R.id.equipA_points);
            this.textViewTotalPointsB = activity.findViewById(R.id.equipB_points);
            this.textViewPointTips = activity.findViewById(R.id.text_points_tips);
            this.layoutBelote = activity.findViewById(R.id.belote_layout);
            this.layoutNewRound = activity.findViewById(R.id.belote_new_round);

            //Setup components
            this.setupAllPlayersSpinners(activity, this.spinnerAA);
            this.setupAllPlayersSpinners(activity, this.spinnerBA);
            this.setupAllPlayersSpinners(activity, this.spinnerAB);
            this.setupAllPlayersSpinners(activity, this.spinnerBB);
            for (RoundType roundType : RoundType.values()){
                this.setupRoundTypeButton(activity.findViewById(this.getButtonRoundTypeId(roundType)), roundType);
            }
            this.setupDeclarationsButton(activity.findViewById(R.id.declaration_button));
            this.setupRemoveDeclarations(activity.findViewById(R.id.layout_declarations_a));
            this.setupRawPointsEditText(this.editTextRawPointsA, this.editTextRawPointsB, true);
            this.setupRawPointsEditText(this.editTextRawPointsB, this.editTextRawPointsA, false);
            this.setupSaveRoundButton(activity, activity.findViewById(R.id.button_save_round));

            //If the fragment is created from a saved Belote:
            if(selectedBelote != null){
                this.setupEquipLabel(activity);
                this.updateDisplayedScores();
                this.setupNewRoundLayout(activity);
                activity.findViewById(R.id.belote_equip_maker_layout).setVisibility(View.GONE);
                activity.findViewById(R.id.belote_equip_summary_layout).setVisibility(View.VISIBLE);
                activity.findViewById(R.id.belote_equip_score_layout).setVisibility(View.VISIBLE);
                activity.findViewById(R.id.line).setVisibility(View.VISIBLE);
                for(Round round : selectedBelote.getRoundsList()){
                    LayoutRoundSummary summary = new LayoutRoundSummary(activity, round);
                    this.layoutBelote.addView(summary, layoutBelote.getChildCount() - 1);
                    this.listLayoutSummary.add(summary);
                }
                if(selectedBelote.isDone()){
                    this.layoutBelote.addView(this.createWinnerView(activity));
                }else {
                    this.layoutNewRound.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_delete, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_delete) {
            Activity activity = this.getActivity();
            if(activity != null){
                if(selectedBelote == null){
                    activity.onBackPressed();
                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
                    builder.setTitle(R.string.warning);
                    builder.setMessage(R.string.confirm_belote_delete);
                    builder.setPositiveButton(R.string.yes, (dialog, id1) -> {
                        dialog.dismiss();
                        selectedBelote.deleteBelote();
                        activity.onBackPressed();
                    });
                    builder.setNegativeButton("No", (dialog, id12) -> dialog.dismiss());
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupAllPlayersSpinners(@NonNull Activity activity, Spinner spinner){
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this.getActivity(), R.layout.centerer_layout, Player.getStringPlayerList(activity));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }

    private void setupBelotePlayersSpinners(@NonNull Activity activity, Spinner spinner){
        if (selectedBelote != null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, R.layout.centerer_layout_big, selectedBelote.getStringPlayerList(activity));
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
                this.updatePointsNewRound(getActivity());
            }
        });
    }

    private void setupDeclarationsButton(Button button){
        button.setOnClickListener(v -> {
            FragmentActivity activity = getActivity();
            if (activity != null){
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                LayoutInflater inflater = requireActivity().getLayoutInflater();
                View modifiedView = inflater.inflate(R.layout.dialog_declaration_choice, null);
                builder.setView(modifiedView);

                GridView grid = modifiedView.findViewById(R.id.declaration_grid);
                grid.setAdapter(new AllDeclarationsAdapter(activity));
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
                this.setupBelotePlayersSpinners(activity, spinnerPlayer);

                builder.setTitle(R.string.add_a_declaration);
                builder.setPositiveButton(R.string.confirm, (dialog, id) -> {
                    if(newDeclaration == null || spinnerPlayer.getSelectedItemPosition() == 0){
                        Toast.makeText(activity, R.string.select_first, Toast.LENGTH_LONG).show();
                    }else{
                        dialog.dismiss();
                        LinearLayout layoutB = getActivity().findViewById(R.id.layout_declarations_b);
                        ImageView iv = getDeclarationImageView(activity, newDeclaration);
                        iv.setId(newRound.addDeclaration(spinnerPlayer.getSelectedItemPosition(), newDeclaration));
                        this.updateFinalPointsNewRound(activity);
                        if(Player.isEquipA(spinnerPlayer.getSelectedItemPosition())){
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
                OneDeclarationAdapter adapter = new OneDeclarationAdapter(
                        activity,
                        android.R.layout.select_dialog_item,
                        android.R.id.text1,
                        newRound.getDeclarationList().stream().map((Round.PlayerDeclaration declaration) -> declaration.getPlayerName(selectedBelote)).toArray(String[]::new),
                        newRound.getDeclarationList().stream().mapToInt(declaration -> declaration.getDeclarationType().getIdIcon()).toArray());
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle(R.string.remove_declaration);
                builder.setAdapter(adapter, (dialog, item) -> {
                    View declarationView = activity.findViewById(newRound.getDeclarationViewId(item));
                    ((ViewGroup) declarationView.getParent()).removeView(declarationView);
                    newRound.removeDeclaration(item);
                    updateFinalPointsNewRound(activity);
                    dialog.dismiss();
                });
                builder.setNegativeButton(R.string.cancel, (dialog, id) -> dialog.cancel());
                builder.show();
            }
        });
    }

    private void setupSaveRoundButton(@NonNull Activity activity, Button button) {
        button.setOnClickListener(v -> {
            if(this.newRound.canBeSaved()){
                int roundUpdated = selectedBelote.addRound(this.newRound);
                for(int i = 1; i <= roundUpdated; i++){
                    int roundID = selectedBelote.getRoundsList().size() - i - 1;
                    this.listLayoutSummary.get(roundID).updateTeamScore(selectedBelote.getRoundsList().get(roundID));
                }
                this.fadeRoundAndAddNextRound(activity);
                this.updateDisplayedScores();
            }else{
                Toast.makeText(activity, R.string.missing_value_round, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setupEquipLabel(@NonNull Activity activity) {
        if(selectedBelote != null){
            TextView equipATextView = activity.findViewById(R.id.equipA_label);
            equipATextView.setText(this.getString(R.string.equip_label, selectedBelote.getPlayerFromId(1).getName(), selectedBelote.getPlayerFromId(3).getName()));
            TextView equipBTextView = activity.findViewById(R.id.equipB_label);
            equipBTextView.setText(this.getString(R.string.equip_label, selectedBelote.getPlayerFromId(2).getName(), selectedBelote.getPlayerFromId(4).getName()));
        }
    }

    /**
     * Clears all the currently selected value in the new round layout based on the current values in newRound.
     * Thus, newRound must not be reset before calling this method.
     * @param activity the context.
     */
    private void clearNewRoundLayout(@NonNull Activity activity) {
        Spinner spinnerLeader = activity.findViewById(R.id.spinner_new_leader);
        spinnerLeader.setSelection(0);
        ImageButton buttonType = activity.findViewById(this.getButtonRoundTypeId(this.newRound.getType()));
        buttonType.setImageResource(newRound.getType().getIdRoundTypeImageUnselected());
        Spinner spinnerFirst = activity.findViewById(R.id.spinner_new_first);
        spinnerFirst.setSelection(0);
        for(int index = 0; index < this.newRound.getDeclarationList().size(); index++){
            View declarationView = activity.findViewById(this.newRound.getDeclarationViewId(index));
            ((ViewGroup) declarationView.getParent()).removeView(declarationView);
        }
    }

    private void setupRawPointsEditText(EditText editText, EditText otherEquipEditText, boolean equipA) {
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
                    updateFinalPointsNewRound(getActivity());
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
        int px = Utils.getPixelFromDp(30);
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
        Activity activity = this.getActivity();
        if(activity != null){
            if(parent.getId() == R.id.spinner_new_leader){
                //onItemSelected event for the spinners used to set the leader player.
                this.newRound.setLeaderPlayer(pos);
                this.updatePointsNewRound(activity);

            }else if(parent.getId() == R.id.spinner_new_first){
                //onItemSelected event for the spinners used to set the first player.
                this.newRound.setStartingPlayer(pos);
                this.updatePointsNewRound(activity);

            }else if(parent.getId() == R.id.spinner_playerA_equipA
                    || parent.getId() == R.id.spinner_playerB_equipA
                    || parent.getId() == R.id.spinner_playerA_equipB
                    || parent.getId() == R.id.spinner_playerB_equipB){
                //onItemSelected event for the spinners used to set the player at the start of the Belote Game.
                this.emptyPlayerCount = 0;
                this.tryResetSelectedPlayer(parent.getId(), this.spinnerAA, pos);
                this.tryResetSelectedPlayer(parent.getId(), this.spinnerBA, pos);
                this.tryResetSelectedPlayer(parent.getId(), this.spinnerAB, pos);
                this.tryResetSelectedPlayer(parent.getId(), this.spinnerBB, pos);
                if(this.emptyPlayerCount == 0){
                    if(selectedBelote == null){
                        selectedBelote = new Belote(
                                Calendar.getInstance().getTimeInMillis(),
                                Player.getPlayerFromListIndex(this.spinnerAA.getSelectedItemPosition()),
                                Player.getPlayerFromListIndex(this.spinnerAB.getSelectedItemPosition()),
                                Player.getPlayerFromListIndex(this.spinnerBA.getSelectedItemPosition()),
                                Player.getPlayerFromListIndex(this.spinnerBB.getSelectedItemPosition()),
                                Utils.DEFAULT_VICTORY_POINTS);
                        selectedBelote.saveBelote();
                    }
                    this.setupEquipLabel(activity);
                    this.updateDisplayedScores();
                    this.setupNewRoundLayout(activity);
                    slide(
                            activity.findViewById(R.id.belote_equip_maker_layout),
                            activity.findViewById(R.id.belote_equip_summary_layout),
                            activity.findViewById(R.id.belote_equip_score_layout),
                            activity.findViewById(R.id.line),
                            this.layoutNewRound);
                }
            }
        }
    }

    private void setupNewRoundLayout(@NonNull Activity activity){
        ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, R.layout.centerer_layout, Player.getStringPlayerList(activity));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinnerFirst = activity.findViewById(R.id.spinner_new_first);
        Spinner spinnerLeader = activity.findViewById(R.id.spinner_new_leader);
        this.setupBelotePlayersSpinners(activity, spinnerFirst);
        this.setupBelotePlayersSpinners(activity, spinnerLeader);
    }

    /**
     * Sets the current total points in beloteGame in the 2 TextViews.
     */
    private void updateDisplayedScores(){
        this.textViewTotalPointsA.setText(String.valueOf(selectedBelote.getEquipAPoints()));
        this.textViewTotalPointsB.setText(String.valueOf(selectedBelote.getEquipBPoints()));
    }

    private void tryResetSelectedPlayer(int selectedSpinnerID, Spinner comparedSpinner, int pos){
        if(selectedSpinnerID != comparedSpinner.getId()){
            int selectedPos = comparedSpinner.getSelectedItemPosition();
            if(selectedPos == pos && pos > 0){
                comparedSpinner.setSelection(0);
                this.emptyPlayerCount++;
            }else if(selectedPos == 0){
                this.emptyPlayerCount++;
            }
        }else if(pos == 0){
            this.emptyPlayerCount++;
        }
    }

    /**
     * Checks if the current newRound has all the required parameters. If yes, all the user to enter the raw points.
     * Else, locks the editText to enter these values, and clear the current raw points in newRound.
     * Finally, it updates the final points to match the raw points.
     * @param context is the current context.
     */
    public void updatePointsNewRound(@NonNull Context context){
        if(this.newRound.canAddPoints()){
            switchEditText(this.editTextRawPointsA, true);
            switchEditText(this.editTextRawPointsB, true);
        }else{
            switchEditText(this.editTextRawPointsA, false);
            switchEditText(this.editTextRawPointsB, false);
            this.newRound.clearPoints();
        }
        this.updateFinalPointsNewRound(context);
    }

    /**
     * Computes the finalPoints with the current parameters and stores them in newRound.
     * Then updates the 2 TextViews showing the final Points.
     * @param context is the current context.
     */
    public void updateFinalPointsNewRound(@NonNull Context context){
        if(this.newRound.canAddPoints()){
            this.newRound.computesFinalPoints();
            this.textViewFinalPointsA.setText(String.valueOf(newRound.getFinalPoints(true)));
            this.textViewFinalPointsB.setText(String.valueOf(newRound.getFinalPoints(false)));
            switch(newRound.getWinner()){
                case -1:
                    this.textViewFinalPointsA.setBackgroundColor(context.getResources().getColor(R.color.equip_A, null));
                    this.textViewFinalPointsB.setBackgroundColor(0x00000000);
                    break;
                default:
                case 0:
                    this.textViewFinalPointsA.setBackgroundColor(0x00000000);
                    this.textViewFinalPointsB.setBackgroundColor(0x00000000);
                    break;
                case 1:
                    this.textViewFinalPointsA.setBackgroundColor(0x00000000);
                    this.textViewFinalPointsB.setBackgroundColor(context.getResources().getColor(R.color.equip_B, null));
            }
            this.textViewPointTips.setText(this.getString(R.string.point_tips, this.newRound.getLeaderPlayerName(selectedBelote), String.valueOf(this.newRound.getMinimalPointsForLeader())));
        }else{
            this.textViewFinalPointsA.setText("0");
            this.textViewFinalPointsA.setBackgroundColor(0x00000000);
            this.textViewFinalPointsB.setText("0");
            this.textViewFinalPointsB.setBackgroundColor(0x00000000);
            this.textViewPointTips.setText("");
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
        TranslateAnimation animIn = new TranslateAnimation(0, 0, -Utils.getScreenHeight(viewIn.getContext()), 0);
        animIn.setDuration(600);
        animIn.setFillAfter(true);
        viewIn.startAnimation(animIn);
    }

    public static void slide(View viewOut, View... viewsIn){
        TranslateAnimation animOut = new TranslateAnimation(0, 0, 0, -Utils.getScreenHeight(viewOut.getContext()));
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

    public void fadeRoundAndAddNextRound(@NonNull Activity activity){
        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setDuration(600);
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                LayoutRoundSummary summary = new LayoutRoundSummary(activity, newRound);
                listLayoutSummary.add(summary);
                layoutBelote.addView(summary, layoutBelote.getChildCount() - 1);

                Animation fadeIn = new AlphaAnimation(0, 1);
                fadeIn.setDuration(600);

                if(selectedBelote.getWinner() == 0){
                    clearNewRoundLayout(activity);
                    newRound = new Round();
                    updatePointsNewRound(activity);

                    summary.startAnimation(fadeIn);
                    layoutNewRound.startAnimation(fadeIn);
                }else{
                    View winner = createWinnerView(activity);
                    layoutBelote.addView(winner);
                    summary.startAnimation(fadeIn);
                    layoutNewRound.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        this.layoutNewRound.startAnimation(fadeOut);
    }

    /**
     * Generates a view for the winner.
     * @param activity used as Context.
     * @return the generated view.
     */
    private View createWinnerView(Activity activity) {
        int tenDPinPX = Utils.getPixelFromDp(10);

        LinearLayout layoutWinner = new LinearLayout(activity);
        LinearLayout.LayoutParams paramS = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        paramS.setMargins(tenDPinPX, tenDPinPX, tenDPinPX, tenDPinPX);
        layoutWinner.setLayoutParams(paramS);
        layoutWinner.setOrientation(LinearLayout.VERTICAL);
        layoutWinner.setPadding(tenDPinPX, tenDPinPX, tenDPinPX, tenDPinPX);
        layoutWinner.setBackgroundColor(activity.getResources().getColor(R.color.dark_grey, null));
        layoutWinner.setGravity(Gravity.CENTER);

        TextView textWinnerLabel = new TextView(activity);
        textWinnerLabel.setLayoutParams(paramS);
        textWinnerLabel.setText(R.string.winner);
        textWinnerLabel.setTextColor(activity.getResources().getColor(R.color.white, null));
        textWinnerLabel.setGravity(Gravity.CENTER);
        textWinnerLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP,30);
        layoutWinner.addView(textWinnerLabel);

        TextView textWinnerTeam = new TextView(activity);
        textWinnerTeam.setLayoutParams(paramS);
        if(selectedBelote.getWinner() == -1){
            textWinnerTeam.setTextColor(activity.getResources().getColor(R.color.equip_A, null));
            textWinnerTeam.setText(((TextView) activity.findViewById(R.id.equipA_label)).getText());
        }else{
            textWinnerTeam.setTextColor(activity.getResources().getColor(R.color.equip_B, null));
            textWinnerTeam.setText(((TextView) activity.findViewById(R.id.equipB_label)).getText());
        }
        textWinnerTeam.setGravity(Gravity.CENTER);
        textWinnerTeam.setTextSize(TypedValue.COMPLEX_UNIT_SP,40);
        layoutWinner.addView(textWinnerTeam);

        Button buttonClose = new Button(activity);
        buttonClose.setText(R.string.close);
        buttonClose.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        buttonClose.setOnClickListener(v -> activity.onBackPressed());
        layoutWinner.addView(buttonClose);

        return layoutWinner;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        selectedBelote = null;
    }

    @SuppressLint("ViewConstructor")
    public static class LayoutRoundSummary extends LinearLayout {

        private final TextView textPointA;
        private final TextView textPointB;

        public LayoutRoundSummary(Context context, Round round) {
            super(context);

            int tenDPinPX = Utils.getPixelFromDp(10);

            LinearLayout.LayoutParams paramS = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            paramS.setMargins(tenDPinPX, tenDPinPX, tenDPinPX, 0);
            this.setLayoutParams(paramS);
            this.setOrientation(LinearLayout.VERTICAL);
            this.setPadding(tenDPinPX, tenDPinPX, tenDPinPX, 0);
            this.setBackgroundColor(context.getResources().getColor(R.color.dark_grey, null));

            LinearLayout layoutRoundTypeLeader = new LinearLayout(context);
            LinearLayout.LayoutParams paramRTL = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutRoundTypeLeader.setLayoutParams(paramRTL);
            layoutRoundTypeLeader.setOrientation(LinearLayout.HORIZONTAL);
            layoutRoundTypeLeader.setBackgroundColor(context.getResources().getColor(R.color.light_grey, null));
            this.addView(layoutRoundTypeLeader);

            ImageView imageRoundType = new ImageView(context);
            imageRoundType.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, Utils.getPixelFromDp(45), 1f));
            imageRoundType.setAdjustViewBounds(true);
            imageRoundType.setImageResource(round.getType().getIdRoundTypeImage());

            TextView textLeader = new TextView(context);
            textLeader.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1f));
            textLeader.setText(round.getLeaderPlayerName(selectedBelote));
            textLeader.setTextColor(context.getResources().getColor(R.color.black, null));
            textLeader.setGravity(Gravity.CENTER);
            textLeader.setTextSize(TypedValue.COMPLEX_UNIT_SP,24);
            if((round.leaderIsEquipA() && (round.getWinner() != -1)) || (!round.leaderIsEquipA() && (round.getWinner() != 1))){
                textLeader.setPaintFlags(textLeader.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }
            textLeader.setBackgroundColor(context.getResources().getColor(round.leaderIsEquipA() ? R.color.equip_A : R.color.equip_B, null));

            if(round.leaderIsEquipA()){
                layoutRoundTypeLeader.addView(textLeader);
                layoutRoundTypeLeader.addView(imageRoundType);
            }else{
                layoutRoundTypeLeader.addView(imageRoundType);
                layoutRoundTypeLeader.addView(textLeader);
            }

            LinearLayout layoutScore = new LinearLayout(context);
            layoutScore.setLayoutParams(paramRTL);
            layoutScore.setOrientation(LinearLayout.HORIZONTAL);
            this.addView(layoutScore);

            this.textPointA = new TextView(context);
            LinearLayout.LayoutParams paramText = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            this.textPointA.setLayoutParams(paramText);
            this.textPointA.setText(String.valueOf(round.getFinalPoints(true)));
            this.textPointA.setTextColor(context.getResources().getColor(R.color.equip_A, null));
            this.textPointA.setGravity(Gravity.CENTER);
            this.textPointA.setTextSize(TypedValue.COMPLEX_UNIT_SP,40);
            layoutScore.addView(textPointA);

            this.textPointB = new TextView(context);
            this.textPointB.setLayoutParams(paramText);
            this.textPointB.setText(String.valueOf(round.getFinalPoints(false)));
            this.textPointB.setTextColor(context.getResources().getColor(R.color.equip_B, null));
            this.textPointB.setGravity(Gravity.CENTER);
            this.textPointB.setTextSize(TypedValue.COMPLEX_UNIT_SP,40);
            layoutScore.addView(this.textPointB);
        }

        public void updateTeamScore(Round round){
            this.textPointA.setText(String.valueOf(round.getFinalPoints(true)));
            this.textPointB.setText(String.valueOf(round.getFinalPoints(false)));
        }
    }
}