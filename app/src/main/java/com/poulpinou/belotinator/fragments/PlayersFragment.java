package com.poulpinou.belotinator.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.poulpinou.belotinator.R;
import com.poulpinou.belotinator.core.Player;
import com.poulpinou.belotinator.databinding.FragmentPlayersBinding;

public class PlayersFragment extends Fragment {

    private FragmentPlayersBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        this.setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        this.binding = FragmentPlayersBinding.inflate(inflater, container, false);
        this.binding.fabAddPlayer.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.create_new_player);

            final EditText input = new EditText(getActivity());
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            input.setGravity(Gravity.CENTER);
            input.setId(0);
            builder.setView(input);
            builder.setPositiveButton(R.string.confirm, (dialog, which) -> {
                if(this.getActivity() != null && !input.getText().toString().isEmpty()){
                    LinearLayout layout = this.getActivity().findViewById(R.id.players_layout);
                    ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    layout.addView(this.createPlayerButton(new Player(input.getText().toString()), Player.PLAYERS_LIST.size() - 1, params));
                }
            });
            builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.cancel());
            AlertDialog alert = builder.create();
            alert.show();
        });
        return this.binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(this.getActivity() == null)
            return;

        LinearLayout layout = this.getActivity().findViewById(R.id.players_layout);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        //TODO Fixer les ids qui risquent de faire des conflits.
        int id = 0;
        for(Player player : Player.PLAYERS_LIST){
            layout.addView(this.createPlayerButton(player, id, params));
            id++;
        }
    }

    private Button createPlayerButton(Player player, int id, ViewGroup.LayoutParams params){
        Button playerButton = new Button(this.getActivity());
        playerButton.setLayoutParams(params);
        playerButton.setText(player.getName());
        playerButton.setId(id);
        playerButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        playerButton.setOnClickListener(view -> {
            PlayerStatsFragment.selectedPlayer = player;
            NavHostFragment.findNavController(PlayersFragment.this).navigate(R.id.action_PlayersFragment_to_PlayerStatsFragment);
        });
        return playerButton;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        this.binding = null;
    }
}