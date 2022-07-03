package com.poulpinou.belotinator.core;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.poulpinou.belotinator.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class Player {

    public static final ArrayList<Player> PLAYERS_LIST = loadPlayersList();
    private final UUID uuid;
    private final String name;
    private final int[] dataColor = new int[7], dataWithoutTrump = new int[7], dataAllTrump = new int[7], colorWinRates = new int[8];
    private final Map<UUID, int[]> dataPlayer = new HashMap<>();
    private int playedGames, wonGames, roundWonLeadFirst, roundTakenLeadFirst, roundWonLeadNotFirst, roundTakenLeadNotFirst, totalDeclarationPoint, squaredJack, roundSavedDeclaration;

    public Player (UUID uuid, String name){
        this.name = name;
        this.uuid = uuid;
    }

    /**
     * Create a new player from his name only : gives him a UUID and save it.
     * @param name The name of the player.
     */
    public Player (String name){
        this(UUID.randomUUID(), name);
        PLAYERS_LIST.add(this);
        savePlayers();
    }

    /**
     * @param playerId:
     * <p>- 1: playerA EquipA
     * <p>- 2: playerA EquipB
     * <p>- 3: playerB EquipA
     * <p>- 4: playerB EquipB
     * @return true if the player is in EquipA, else otherwise.
     */
    public static boolean isEquipA(int playerId){
        return playerId == 1 || playerId == 3;
    }

    /**
     * @return the unique UUID of this player.
     */
    public UUID getUuid() {
        return this.uuid;
    }

    /**
     * @return the name of this player.
     */
    public String getName(){
        return this.name;
    }

    @NonNull
    @Override
    public String toString() {
        return this.name;
    }

    /**
     * Loops in the round of the belote and add all the information to player's data.
     * @param belote game to be added to the stats
     * @param playerId of the player:
     * <p>- 1: playerA EquipA
     * <p>- 2: playerA EquipB
     * <p>- 3: playerB EquipA
     * <p>- 4: playerB EquipB
     */
    public void addDataBelote(Belote belote, int playerId){
        this.playedGames++;
        //We check there is stats for the relationship with the 3 other players in the map.
        for(int i = 1; i <= 4; i++){
            if(i != playerId){
                UUID uuid = belote.getPlayerFromId(1).getUuid();
                if(!this.dataPlayer.containsKey(uuid)){
                    // The int array stored in dataPlayer contains:
                    // [Nb_of_games_played_with, Nb_of_games_won_with, Nb_of_games_first_when_lead, Nb_of_games_first_when_lead_lost, Nb_of_round_stolen_with_declaration]
                    this.dataPlayer.put(uuid, new int[5]);
                }
            }
        }
        int[] arrayTeammate = Objects.requireNonNull(this.dataPlayer.get(belote.getTeammatePlayerUUID(playerId)));
        arrayTeammate[0]++;
        boolean playerIsEquipA = playerId == 1 || playerId == 3;
        if((belote.getWinner() == -1 && playerIsEquipA) || (belote.getWinner() == -1 && !playerIsEquipA)){
            this.wonGames++;
            arrayTeammate[1]++;
        }
        for(Round round : belote.getRoundsList()){
            this.addDataRound(round, playerId);
            if(round.getLeaderPlayerId() == playerId){
                int[] arrayFirst = Objects.requireNonNull(this.dataPlayer.get(belote.getPlayerFromId(round.getStartingPlayerId()).getUuid()));
                arrayFirst[2]++;
                if(!round.wonByPlayer(playerId)){
                    arrayFirst[3]++;
                }
            }
            for(Round.PlayerDeclaration declaration : round.getDeclarationList()){
                if(declaration.getPlayerId() == playerId){
                    if(declaration.getDeclarationType() == DeclarationType.SQUARE_JACK){
                        this.squaredJack++;
                    }
                    this.totalDeclarationPoint += declaration.getDeclarationType().getValue(round.getType());
                }
            }
            if(round.wonWithDeclarations()){
                if(round.wonByPlayer(playerId)){
                    //if(playerId == round.getPlayerMostDeclarationPoints(playerIsEquipA)){
                    //    this.roundSavedDeclaration++;
                    //}
                }else{
                    //if()
                }
            }
        }
    }

    /**
     * Fills the dataArray with the data from the corresponding Round:
     * <p>- 0: Number of played rounds as leader
     * <p>- 1: Number of won rounds as leader
     * <p>- 2: Number of played rounds as supporter
     * <p>- 3: Number of won rounds as supporter
     * <p>- 4: Number of played rounds as defender
     * <p>- 5: Number of won rounds as defender
     * <p>- 6: Average point as leader
     * @param round to be added
     * @param playerID of the player:
     * <p>- 1: playerA EquipA
     * <p>- 2: playerA EquipB
     * <p>- 3: playerB EquipA
     * <p>- 4: playerB EquipB
     */
    private void addDataRound(Round round, int playerID){
        boolean playerIsEquipA = playerID == 1 || playerID == 3;
        boolean playerWonRound = (round.getWinner() == -1 && playerIsEquipA) || (round.getWinner() == -1 && !playerIsEquipA);
        int[] dataArray = this.getDataArrayFromRound(round);
        if(round.getLeaderPlayerId() == playerID){
            dataArray[0]++;
            if(playerWonRound) dataArray[1]++;
            dataArray[6] += round.getFinalPoints(true);
        }else if(round.leaderIsEquipA() != playerIsEquipA){
            dataArray[4]++;
            if(playerWonRound) dataArray[5]++;
        }else{
            dataArray[2]++;
            if(playerWonRound) dataArray[3]++;
        }
        if(round.getLeaderPlayerId() == playerID){
            if(round.getType() != RoundType.WITHOUT_TRUMP && round.getType() != RoundType.ALL_TRUMP){
                this.colorWinRates[round.getType().getId() * 2]++;
                if(playerWonRound) this.colorWinRates[round.getType().getId() * 2 + 1]++;
            }
        }
    }

    /**
     * @param round used to chose the dataArray
     * @return the dataArray storing the data for the round in parameter (COLOR, WITHOUT_TRUMP or ALL_TRUMP)
     */
    private int[] getDataArrayFromRound(Round round){
        if(round.getType() == RoundType.ALL_TRUMP){
            return this.dataAllTrump;
        }
        if(round.getType() == RoundType.WITHOUT_TRUMP){
            return this.dataWithoutTrump;
        }
        return this.dataColor;
    }

    /**
     * Removes the player in parameter from the player list and from the save file.
     * @param player The player to be removed.
     */
    public static void removePlayer(Player player) {
        PLAYERS_LIST.remove(player);
        savePlayers();
    }

    /**
     * Checks first if a json file containing the list of player exists.
     * Creates one if needed with the new player saved.
     * Replaces the existing one with the new list of players.
     */
    public static void savePlayers(){
        JSONArray playersArrayJson = new JSONArray();
        try {
            for (Player player : PLAYERS_LIST){
                JSONObject thisPlayerJson = new JSONObject();
                thisPlayerJson.put("uuid", player.getUuid().toString());
                thisPlayerJson.put("name", player.getName());
                playersArrayJson.put(thisPlayerJson);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Utils.saveJSONStringToFile(Utils.getMainDirectoryFile(), Utils.PLAYERS_LIST_FILE_NAME, playersArrayJson.toString());
    }

    /**
     * Reads the JSON file storing the list of player. Create an instance of Player for each entry found.
     * @return the ArrayList containing the list of all loaded players.
     */
    public static ArrayList<Player> loadPlayersList(){
        ArrayList<Player> list = new ArrayList<>();
        String fileToString = Utils.getJSONStringFromFile(Utils.getMainDirectoryFile(), Utils.PLAYERS_LIST_FILE_NAME);
        if(fileToString != null){
            try {
                // Getting data JSON Array nodes
                JSONArray data  = new JSONArray(fileToString);

                // looping through All nodes
                for (int i = 0; i < data.length(); i++) {
                    JSONObject playerJSON = data.getJSONObject(i);
                    list.add(new Player(UUID.fromString(playerJSON.getString("uuid")), playerJSON.getString("name")));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    /**
     * @param context used to get the resource "no player selected" string.
     * @return an ArrayList starting with the "no player selected" string, then the list of all player names.
     */
    public static ArrayList<String> getStringPlayerList(@NonNull Context context){
        ArrayList<String> list = new ArrayList<>();
        list.add(context.getString(R.string.empty));
        for(Player player : PLAYERS_LIST){
            list.add(player.getName());
        }
        return list;
    }

    /**
     * @param selectedItemPosition the position of the player in the list of player names (one more entry).
     * @return the corresponding instance of Player.
     */
    public static Player getPlayerFromListIndex(int selectedItemPosition) {
        return PLAYERS_LIST.get(selectedItemPosition - 1);
    }

    /**
     * @param uuid the loaded UUID of the player.
     * @return the corresponding instance of Player. Returns null if the player can't be find.
     */
    @Nullable
    public static Player getPlayerFromUUID(UUID uuid) {
        for(Player player : PLAYERS_LIST){
            if(player.getUuid().equals(uuid)){
                return player;
            }
        }
        //TODO Handle the deletion of a player !!
        return PLAYERS_LIST.get(0);
    }
}
