package com.poulpinou.belotinator.core;

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

    public UUID getUuid() {
        return this.uuid;
    }

    public String getName(){
        return this.name;
    }

    /**
     * Check first if a json file containing the list of player exists.
     * Create one if needed with the new player saved.
     * Replace the existing one with the new list containing the new player.
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
        FileHandler.saveJSONStringToFile(FileHandler.PLAYERS_LIST_FILE_NAME, playersArrayJson.toString());
    }

    public static ArrayList<Player> loadPlayersList(){
        ArrayList<Player> list = new ArrayList<>();
        String fileToString = FileHandler.getJSONStringFromFile(FileHandler.PLAYERS_LIST_FILE_NAME);
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
}
