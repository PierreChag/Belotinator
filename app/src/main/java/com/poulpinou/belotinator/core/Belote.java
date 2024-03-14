package com.poulpinou.belotinator.core;

import android.content.Context;

import androidx.annotation.NonNull;

import com.poulpinou.belotinator.R;
import com.poulpinou.belotinator.core.Utils.Result;

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
    private final Player playerATeamA, playerBTeamA, playerATeamB, playerBTeamB;
    private final int victoryPoints;
    private Result beloteResult = Result.IN_PROGRESS;
    private int teamAPoints, teamBPoints;
    private final ArrayList<Round> roundsList = new ArrayList<>();

    /**
     * Creates a Belote instance.
     * @param dateInMillis When the belote game happened in milliseconds
     * @param playerATeamA Player A in team A (id: 1)
     * @param playerATeamB Player A in team B (id: 2)
     * @param playerBTeamA Player B in team A (id: 3)
     * @param playerBTeamB Player B in team B (id: 4)
     * @param victoryPoints Points required to win the game
     */
    public Belote(long dateInMillis, Player playerATeamA, Player playerATeamB, Player playerBTeamA, Player playerBTeamB, int victoryPoints){
        this.dateInMillis = dateInMillis;
        this.playerATeamA = playerATeamA;
        this.playerATeamB = playerATeamB;
        this.playerBTeamA = playerBTeamA;
        this.playerBTeamB = playerBTeamB;
        this.victoryPoints = victoryPoints;
        BELOTES_LIST.add(this);
    }

    /**
     * Adds the round in input in the Belote round list, computes the total points, and save to json.
     * @param newRound to be added.
     * @return the number of previous rounds that need to be updated because of dispute resolution.
     */
    public int addRound(Round newRound){
        this.roundsList.add(newRound);
        this.teamAPoints += newRound.getFinalPoints(true);
        this.teamBPoints += newRound.getFinalPoints(false);
        int roundsToUpdate = this.resolvePreviousDisputes(newRound.getWinner());
        if(this.getTeamAPoints() > this.getTeamBPoints() && this.getTeamAPoints() >= this.victoryPoints){
            this.beloteResult = Result.TEAM_A_WON;
            this.computeStats();
        }
        if(this.getTeamBPoints() > this.getTeamAPoints() && this.getTeamBPoints() >= this.victoryPoints){
            this.beloteResult = Result.TEAM_B_WON;
            this.computeStats();
        }
        this.saveBelote();
        return roundsToUpdate;
    }

    /**
     *
     * @param roundResult is the result of the Round.
     * @return the number of previous Rounds that need to be updated because of dispute resolution.
     */
    private int resolvePreviousDisputes(Result roundResult){
        if(!roundResult.isFinished()){
            return 0;
        }
        boolean winnerIsTeamA = (roundResult == Result.TEAM_A_WON);
        int roundUpdated = 0;
        int points = 0;
        for(int i = this.getRoundsList().size() - 1; i >= 0; i--){
            Round round = this.getRoundsList().get(i);
            if(round.isInDispute()){
                points += round.resolveDispute(winnerIsTeamA);
                roundUpdated++;
            }else{
                break;
            }
        }
        if(winnerIsTeamA){
            this.teamAPoints += points;
        }else {
            this.teamBPoints += points;
        }
        return roundUpdated;
    }

    /**
     * @return the list of Rounds of this belote.
     */
    public ArrayList<Round> getRoundsList() {
        return this.roundsList;
    }

    /**
     * @return the current total points of Team A
     */
    public int getTeamAPoints(){
        return this.teamAPoints;
    }

    /**
     * @return the current total points of Team B
     */
    public int getTeamBPoints(){
        return this.teamBPoints;
    }

    /**
     * @return True if the Belote game is finished, false otherwise.
     */
    public boolean isFinished(){
        return this.beloteResult.isFinished();
    }

    /**
     * @return the current Result of this Belote.
     */
    public Utils.Result getBeloteResult(){
        return this.beloteResult;
    }

    /**
     * @param context used to get "no player selected" string.
     * @return An ArrayList starting with the "no player selected" string, then the list of 4 player names in the following order:
     * - 0: ?
     * - 1: playerA TeamA
     * - 2: playerA TeamB
     * - 3: playerB TeamA
     * - 4: playerB TeamB
     */
    public ArrayList<String> getStringPlayerList(@NonNull Context context){
        ArrayList<String> list = new ArrayList<>();
        list.add(context.getString(R.string.empty));
        list.add(this.playerATeamA.getName());
        list.add(this.playerATeamB.getName());
        list.add(this.playerBTeamA.getName());
        list.add(this.playerBTeamB.getName());
        return list;
    }

    /**
     * @param id of the player:
     * <p>- 1: playerA TeamA
     * <p>- 2: playerA TeamB
     * <p>- 3: playerB TeamA
     * <p>- 4: playerB TeamB
     * @return the instance of the corresponding player.
     */
    public Player getPlayerFromId(int id){
        return switch (id) {
            default -> this.playerATeamA;
            case 2 -> this.playerATeamB;
            case 3 -> this.playerBTeamA;
            case 4 -> this.playerBTeamB;
        };
    }

    /**
     * @param playerId of the player:
     * <p>- 1: playerA TeamA
     * <p>- 2: playerA TeamB
     * <p>- 3: playerB TeamA
     * <p>- 4: playerB TeamB
     * @return the player UUID of the teammate.
     */
    public UUID getTeammatePlayerUUID(int playerId){
        return this.getPlayerFromId(this.getTeammatePlayerId(playerId)).getUuid();
    }

    /**
     * @param playerId of the player:
     * <p>- 1: playerA TeamA
     * <p>- 2: playerA TeamB
     * <p>- 3: playerB TeamA
     * <p>- 4: playerB TeamB
     * @return the player id of the teammate.
     */
    public int getTeammatePlayerId(int playerId){
        if(playerId == 1) return 3;
        if(playerId == 2) return 4;
        if(playerId == 3) return 1;
        return 2;
    }

    /**
     * @param playerId of the current round starting player:
     * <p>- 1: playerA TeamA
     * <p>- 2: playerA TeamB
     * <p>- 3: playerB TeamA
     * <p>- 4: playerB TeamB
     * @return the player id of the starting player for the next round.
     */
    public int getNextStartingPlayer(int playerId){
        return playerId == 4 ? 1 : playerId + 1;
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
        ArrayList<String> filesToString = Storage.getAllJSONStringFromDirectory(Storage.getBelotesDirectory());
        for(String fileToString : filesToString){
            loadBelote(fileToString);//TODO load data from previously DONE belotes.
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
                    Player.getPlayerFromUUID(UUID.fromString(beloteJSON.getString("playerAEquipB"))),
                    Player.getPlayerFromUUID(UUID.fromString(beloteJSON.getString("playerBEquipA"))),
                    Player.getPlayerFromUUID(UUID.fromString(beloteJSON.getString("playerBEquipB"))),
                    beloteJSON.getInt("victoryPoints")
            );
            loadedBelote.teamAPoints = beloteJSON.getInt("equipAPoints");
            loadedBelote.teamBPoints = beloteJSON.getInt("equipBPoints");

            //TODO Compatibility code for previous versions.
            if(beloteJSON.has("done")){
                if(beloteJSON.getBoolean("done")){
                    loadedBelote.beloteResult = loadedBelote.teamAPoints > loadedBelote.teamBPoints ? Result.TEAM_A_WON : Result.TEAM_B_WON;
                }else{
                    loadedBelote.beloteResult = loadedBelote.teamAPoints > loadedBelote.victoryPoints ? Result.EQUALITY : Result.IN_PROGRESS;
                }
                beloteJSON.remove("done");
                beloteJSON.put("result", loadedBelote.beloteResult.getName());
                // Saves the modification
                Storage.saveJSONStringToFile(Storage.getBelotesDirectory(), loadedBelote.getFileName(), beloteJSON.toString());
            }
            loadedBelote.beloteResult = Result.fromId(beloteJSON.getString("result"));

            // looping through all rounds
            JSONArray roundsArrayJson = beloteJSON.getJSONArray("roundsList");
            for(int i = 0; i < roundsArrayJson.length(); i++) {
                Round round = Round.loadJSON(roundsArrayJson.getJSONObject(i));
                if(round == null){
                    continue;
                }
                loadedBelote.roundsList.add(round);
            }
            if(loadedBelote.isFinished()){
                loadedBelote.computeStats();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Save the data from the different rounds in each player stats.
     */
    public void computeStats(){
        this.playerATeamA.addDataBelote(this, 1);
        this.playerATeamB.addDataBelote(this, 2);
        this.playerBTeamA.addDataBelote(this, 3);
        this.playerBTeamB.addDataBelote(this, 4);
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
            thisBeloteJson.put("playerAEquipA", this.playerATeamA.getUuid().toString());
            thisBeloteJson.put("playerBEquipA", this.playerBTeamA.getUuid().toString());
            thisBeloteJson.put("playerAEquipB", this.playerATeamB.getUuid().toString());
            thisBeloteJson.put("playerBEquipB", this.playerBTeamB.getUuid().toString());
            thisBeloteJson.put("victoryPoints", this.victoryPoints);
            thisBeloteJson.put("result", this.beloteResult.getName());
            thisBeloteJson.put("equipAPoints", this.teamAPoints);
            thisBeloteJson.put("equipBPoints", this.teamBPoints);
            JSONArray roundsArrayJson = new JSONArray();
            for (Round round : this.roundsList){
                roundsArrayJson.put(round.getJson());
            }
            thisBeloteJson.put("roundsList", roundsArrayJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Storage.saveJSONStringToFile(Storage.getBelotesDirectory(), this.getFileName(), thisBeloteJson.toString());
    }

    public void deleteBelote() {
        Storage.deleteFile(Storage.getBelotesDirectory(), this.getFileName());
        BELOTES_LIST.remove(this);
    }

    private String getFileName(){
        return this.getDateInShortString() + ".json";
    }
}