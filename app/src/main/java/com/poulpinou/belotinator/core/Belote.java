package com.poulpinou.belotinator.core;

import android.content.Context;

import androidx.annotation.NonNull;

import com.poulpinou.belotinator.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class Belote {

    public static final ArrayList<Belote> BELOTES_LIST = new ArrayList<>();
    static {
        loadBelotesList();
    }
    private final long dateInMillis;
    private final Player playerAEquipA, playerBEquipA, playerAEquipB, playerBEquipB;
    private final int victoryPoints;
    private boolean done = false;
    private int equipAPoints, equipBPoints;
    private final ArrayList<Round> roundsList = new ArrayList<>();

    /**
     * Creates a Belote instance.
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
        BELOTES_LIST.add(this);
    }

    /**
     * Adds the round in input in the Belote round list, computes the total points, and save to json.
     * @param newRound to be added.
     */
    public void addRound(Round newRound){
        this.roundsList.add(newRound);
        if(newRound.isInDispute()){
            //TODO in Dispute !!!!!
        }else{
            this.equipAPoints += newRound.getFinalPoints(true);
            this.equipBPoints += newRound.getFinalPoints(false);
        }
        if(this.getEquipAPoints() > this.getEquipBPoints() && this.getEquipAPoints() > this.victoryPoints){
            this.done = true;
        }
        if(this.getEquipBPoints() > this.getEquipAPoints() && this.getEquipBPoints() > this.victoryPoints){
            this.done = true;
        }
        this.saveBelote();
    }

    /**
     * @return the list of Rounds of this belote.
     */
    public ArrayList<Round> getRoundsList() {
        return this.roundsList;
    }

    /**
     * @return the current total points of Equip A
     */
    public int getEquipAPoints(){
        return this.equipAPoints;
    }

    /**
     * @return the current total points of Equip B
     */
    public int getEquipBPoints(){
        return this.equipBPoints;
    }

    /**
     * @return True if the belote game is done.
     */
    public boolean isDone(){
        return this.done;
    }

    /**
     * @return an integer corresponding to the winner.
     * <p>- Not enough points or equality : 0
     * <p>- Equip A: -1
     * <p>- Equip B: 1
     */
    public int getWinner(){
        if(!this.done){
            return 0;
        }
        return (this.getEquipAPoints() > this.getEquipBPoints()) ? -1 : 1;
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
     * <p>- 1: playerA EquipA
     * <p>- 2: playerA EquipB
     * <p>- 3: playerB EquipA
     * <p>- 4: playerB EquipB
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
     * @return The date of creation of this belote game ins String format compact
     */
    public String getDateInShortString(){
        return new SimpleDateFormat(Utils.DATE_FORMAT, Locale.getDefault()).format(new Date(this.dateInMillis));
    }

    /**
     * @return The date of creation of this belote game ins String format
     */
    public String getDateInLongString(){
        return DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT).format(new Date(this.dateInMillis));
    }

    /**
     * Reads the JSON files stored in the folder. Create an instance of Belote for each entry found.
     */
    public static void loadBelotesList(){
        ArrayList<String> filesToString = Utils.getAllJSONStringFromDirectory(Utils.getBelotesDirectory());
        for(String fileToString : filesToString){
            loadBelote(fileToString);
        }
    }

    /**
     * Creates an instance of Belote from the data in parameter, and stores it in the BELOTES_LIST.
     * @param beloteString is the content of the file where the Belote was saved.
     */
    private static void loadBelote(String beloteString) {
        Belote loadedBelote;
        try {
            // Getting data JSON Object
            JSONObject beloteJSON = new JSONObject(beloteString);
            loadedBelote = new Belote(
                    beloteJSON.getLong("date"),
                    Player.getPlayerFromUUID(UUID.fromString(beloteJSON.getString("playerAEquipA"))),
                    Player.getPlayerFromUUID(UUID.fromString(beloteJSON.getString("playerBEquipA"))),
                    Player.getPlayerFromUUID(UUID.fromString(beloteJSON.getString("playerAEquipB"))),
                    Player.getPlayerFromUUID(UUID.fromString(beloteJSON.getString("playerBEquipB"))),
                    beloteJSON.getInt("victoryPoints")
            );
            loadedBelote.done = beloteJSON.getBoolean("done");
            loadedBelote.equipAPoints = beloteJSON.getInt("equipAPoints");
            loadedBelote.equipBPoints = beloteJSON.getInt("equipBPoints");

            // looping through all rounds
            JSONArray roundsArrayJson = beloteJSON.getJSONArray("roundsList");
            for(int i = 0; i < roundsArrayJson.length(); i++) {
                Round round = Round.loadJSON(roundsArrayJson.getJSONObject(i));
                if(round == null){
                    continue;
                }
                loadedBelote.roundsList.add(round);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
            thisBeloteJson.put("done", this.done);
            thisBeloteJson.put("equipAPoints", this.equipAPoints);
            thisBeloteJson.put("equipBPoints", this.equipBPoints);
            JSONArray roundsArrayJson = new JSONArray();
            for (Round round : this.roundsList){
                roundsArrayJson.put(round.getJson());
            }
            thisBeloteJson.put("roundsList", roundsArrayJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Utils.saveJSONStringToFile(Utils.getBelotesDirectory(), this.getFileName(), thisBeloteJson.toString());
    }

    public void deleteBelote() {
        Utils.deleteFile(Utils.getBelotesDirectory(), this.getFileName());
        BELOTES_LIST.remove(this);
    }

    private String getFileName(){
        return this.getDateInShortString() + ".json";
    }
}