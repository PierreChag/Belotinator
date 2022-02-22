package com.poulpinou.belotinator.core;

import android.content.Context;

import androidx.annotation.NonNull;

import com.poulpinou.belotinator.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class Belote {

    public static final int DEFAULT_VICTORY_POINTS = 3000;
    private static final String DATE_FORMAT = "dd-MM-yyyy - HH:mm";

    private final long dateInMillis;
    private final Player playerAEquipA, playerBEquipA, playerAEquipB, playerBEquipB;
    private final int victoryPoints;
    private int equipAPoints, equipBPoints;
    private final ArrayList<Round> roundsList = new ArrayList<>();

    /**
     * @param dateInMillis When the belote game happened in milliseconds
     * @param playerAEquipA Player A in equip A (id: 1)
     * @param playerAEquipB Player A in equip B (id: 2)
     * @param playerBEquipA Player B in equip A (id: 3)
     * @param playerBEquipB Player B in equip B (id: 4)
     * @param victoryPoints Points required to win the game
     */
    public Belote(long dateInMillis, Player playerAEquipA, Player playerAEquipB, Player playerBEquipA, Player playerBEquipB, int victoryPoints){
        this.dateInMillis = dateInMillis;
        this.playerAEquipA = playerAEquipA;
        this.playerAEquipB = playerAEquipB;
        this.playerBEquipA = playerBEquipA;
        this.playerBEquipB = playerBEquipB;
        this.victoryPoints = victoryPoints;
        this.saveBelote();
    }

    /**
     * Check first if a json file containing the list of player exists.
     * Create one if needed with the new player saved.
     * Replace the existing one with the new list of players.
     */
    public void saveBelote(){
        JSONObject thisBeloteJson = new JSONObject();
        try {
            thisBeloteJson.put("date", this.dateInMillis);
            thisBeloteJson.put("playerAEquipA", this.playerAEquipA.getUuid().toString());
            thisBeloteJson.put("playerBEquipA", this.playerBEquipA.getUuid().toString());
            thisBeloteJson.put("playerAEquipB", this.playerAEquipB.getUuid().toString());
            thisBeloteJson.put("playerBEquipB", this.playerBEquipB.getUuid().toString());
            thisBeloteJson.put("victoryPoints", this.victoryPoints);
            JSONArray roundsArrayJson = new JSONArray();
            for (Round round : this.roundsList){
                roundsArrayJson.put(round.getJson());
            }
            thisBeloteJson.put("roundsList", roundsArrayJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        FileHandler.saveJSONStringToFile(this.getDateInString() + ".json", thisBeloteJson.toString());
    }

    /**
     * @return The date of creation of this belote game ins String format
     */
    public String getDateInString(){
        return new SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(new Date(this.dateInMillis));
    }

    /**
     * @param context used to get "no player selected" string.
     * @return An ArrayList starting with the "no player selected" string, then the list of 4 player names in the following order:
     * - 0: ?
     * - 1: playerA EquipA
     * - 2: playerA EquipB
     * - 3: playerB EquipA
     * - 4: playerB EquipB
     */
    public ArrayList<String> getStringPlayerList(@NonNull Context context){
        ArrayList<String> list = new ArrayList<>();
        list.add(context.getString(R.string.empty));
        list.add(this.playerAEquipA.getName());
        list.add(this.playerAEquipB.getName());
        list.add(this.playerBEquipA.getName());
        list.add(this.playerBEquipB.getName());
        return list;
    }

    /**
     * @param id of the player:
     * - 1: playerA EquipA
     * - 2: playerA EquipB
     * - 3: playerB EquipA
     * - 4: playerB EquipB
     * @return the instance of the corresponding player.
     */
    public Player getPlayerFromId(int id){
        switch (id){
            default:
            case 1:
                return this.playerAEquipA;
            case 2:
                return this.playerAEquipB;
            case 3:
                return this.playerBEquipA;
            case 4:
                return this.playerBEquipB;
        }
    }

    /**
     * @param playerId:
     * - 1: playerA EquipA
     * - 2: playerA EquipB
     * - 3: playerB EquipA
     * - 4: playerB EquipB
     * @return true if the player is in EquipA, else otherwise.
     */
    public static boolean isEquipA(int playerId){
        return playerId == 1 || playerId == 3;
    }
}