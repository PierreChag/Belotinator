package com.poulpinou.belotinator.core;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Round {

    public static final int POINTS_CAPOT = 90;

    private RoundType type;
    private int startingPlayerId, leaderPlayerId;
    private int rawPointsA, rawPointsB, declarationPointsA, declarationPointsB, finalPointsA, finalPointsB;
    private final ArrayList<PlayerDeclaration> declarationList = new ArrayList<>();

    public Round(int startingPlayerId, int leaderPlayerId, RoundType type){
        this.startingPlayerId = startingPlayerId;
        this.leaderPlayerId = leaderPlayerId;
        this.type = type;
    }

    public void editStartingPlayer(int playerId){
        this.startingPlayerId = playerId;
    }

    public void editLeaderPlayer(int playerId){
        this.leaderPlayerId = playerId;
    }

    public void editRoundType(RoundType type){
        this.type = type;
    }

    public void addDeclaration(int playerId, DeclarationType declarationType){
        this.declarationList.add(new PlayerDeclaration(playerId, declarationType));
        if(PlayerConstruct.isEquipA(playerId)) {
            this.declarationPointsA += declarationType.getValue(this.type);
        }else{
            this.declarationPointsB += declarationType.getValue(this.type);
        }
    }

    public void removeDeclaration(int index){
        PlayerDeclaration declaration = this.declarationList.get(index);
        if(declaration.isEquipA()) {
            this.declarationPointsA -= declaration.getDeclarationType().getValue(this.type);
        }else{
            this.declarationPointsB -= declaration.getDeclarationType().getValue(this.type);
        }
        this.declarationList.remove(index);
    }

    public void setPointsEquip(boolean equipA, int points){
        this.rawPointsA = equipA ? points : this.type.getPointsPerRound() - points;
        this.rawPointsB = equipA ? this.type.getPointsPerRound() - points : points;

        this.finalPointsA = this.rawPointsA + this.declarationPointsA;
        this.finalPointsB = this.rawPointsB + this.declarationPointsB;
        if(this.rawPointsA == 0) this.finalPointsB += POINTS_CAPOT;
        if(this.rawPointsB == 0) this.finalPointsA += POINTS_CAPOT;
        if(PlayerConstruct.isEquipA(this.leaderPlayerId)) {
            if(this.finalPointsB > this.finalPointsA) {
                this.finalPointsA = 0;
                this.finalPointsB += this.declarationPointsA;
            }
        }else{
            if(this.finalPointsB < this.finalPointsA){
                this.finalPointsA += this.declarationPointsB;
                this.finalPointsB = 0;
            }
        }

        //The multiplier is only applied in the end
        this.finalPointsA *= this.type.getMultiplier();
        this.finalPointsB *= this.type.getMultiplier();
    }

    public JSONObject getJson(){
        JSONObject thisRoundJson = new JSONObject();
        try {
            thisRoundJson.put("type", this.type.getName());
            thisRoundJson.put("startingPlayerId", this.startingPlayerId);
            thisRoundJson.put("leaderPlayerId", this.leaderPlayerId);
            thisRoundJson.put("rawPointsA", this.rawPointsA);
            thisRoundJson.put("rawPointsB", this.rawPointsB);
            thisRoundJson.put("finalPointsA", this.finalPointsA);
            thisRoundJson.put("finalPointsB", this.finalPointsB);
            JSONArray declarationArrayJson = new JSONArray();
            for (PlayerDeclaration playerDeclaration : this.declarationList){
                JSONObject thisDeclarationJson = new JSONObject();
                thisDeclarationJson.put("player", playerDeclaration.getPlayerId());
                thisDeclarationJson.put("type", playerDeclaration.getDeclarationType().getName());
                declarationArrayJson.put(thisDeclarationJson);
            }
            thisRoundJson.put("declarationsList", declarationArrayJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return thisRoundJson;
    }

    public static class PlayerDeclaration{

        private final int playerId;
        private final DeclarationType declarationType;

        public PlayerDeclaration(int playerId, DeclarationType declarationType){
            this.playerId = playerId;
            this.declarationType = declarationType;
        }

        public DeclarationType getDeclarationType(){
            return this.declarationType;
        }

        public int getPlayerId(){
            return this.playerId;
        }

        public boolean isEquipA(){
            return PlayerConstruct.isEquipA(this.getPlayerId());
        }
    }

    public enum PlayerConstruct{

        PLAYER_A_EQUIP_A(0),
        PLAYER_B_EQUIP_A(1),
        PLAYER_A_EQUIP_B(2),
        PLAYER_B_EQUIP_B(3);

        private final int id;

        PlayerConstruct(int id){
            this.id = id;
        }

        public int getId(){
            return this.id;
        }

        public boolean isEquipA(){
            return isEquipA(this.getId());
        }

        public static boolean isEquipA(int playerId){
            return playerId <= 1;
        }
    }
}
