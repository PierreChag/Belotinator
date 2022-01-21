package com.poulpinou.belotinator.core;

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
     * @param playerAEquipA Player A in equip A
     * @param playerBEquipA Player B in equip A
     * @param playerAEquipB Player A in equip B
     * @param playerBEquipB Player B in equip B
     * @param victoryPoints Points required to win the game
     */
    public Belote(long dateInMillis, Player playerAEquipA, Player playerBEquipA, Player playerAEquipB, Player playerBEquipB, int victoryPoints){
        this.dateInMillis = dateInMillis;
        this.playerAEquipA = playerAEquipA;
        this.playerBEquipA = playerBEquipA;
        this.playerAEquipB = playerAEquipB;
        this.playerBEquipB = playerBEquipB;
        this.victoryPoints = victoryPoints;
        this.saveBelote();
    }

    public boolean playerIsEquipA(Player player){
        return player == this.playerAEquipA || player == this.playerAEquipB;
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
}