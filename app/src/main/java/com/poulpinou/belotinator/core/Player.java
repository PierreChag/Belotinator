package com.poulpinou.belotinator.core;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.poulpinou.belotinator.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.UUID;

public class Player {

    public static final ArrayList<Player> PLAYERS_LIST = loadPlayersList();
    private final UUID uuid;
    private final String name;

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
