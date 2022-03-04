package com.poulpinou.belotinator.core;

import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Round {

    public static final int POINTS_CAPOT = 90;

    private RoundType type = null;
    private int startingPlayerId = 0, leaderPlayerId = 0;
    private int rawPointsA, rawPointsB, declarationPointsA, declarationPointsB, finalPointsA, finalPointsB;
    private final ArrayList<PlayerDeclaration> declarationList = new ArrayList<>();

    /**
     * Creates a new instance of Round but all the basic parameters must be initialized manually (RoundType, StartingPlayer and LeaderPlayer).
     */
    public Round(){}

    /**
     * Initializes the basic parameter startingPlayerId.
     * @param playerId in {1, 2, 3, 4}.
     */
    public void setStartingPlayer(int playerId){
        this.startingPlayerId = playerId;
    }

    /**
     * Initializes the basic parameter leaderPlayerId.
     * @param playerId in {1, 2, 3, 4}.
     */
    public void setLeaderPlayer(int playerId){
        this.leaderPlayerId = playerId;
    }

    /**
     * @param belote used to retrieve the player list.
     * @return the name of the Leader of this round.
     */
    public String getLeaderPlayerName(Belote belote){
        return belote.getPlayerFromId(this.leaderPlayerId).getName();
    }


    public boolean leaderIsEquipA() {
        return Belote.isEquipA(this.leaderPlayerId);
    }

    /**
     * Initialize the basic parameter type.
     * @param type is the RoundType used to computes the points.
     */
    public void setRoundType(RoundType type){
        this.type = type;
    }

    /**
     * @return the RoundType of this round.
     */
    public RoundType getType(){
        return this.type;
    }

    /**
     * @param equipA is True for Equip A, False for Equip B.
     * @return the raw Points of the corresponding equip.
     */
    public int getRawPoints(boolean equipA){
        return equipA ? this.rawPointsA : this.rawPointsB;
    }

    /**
     * @param equipA is True for Equip A, False for Equip B.
     * @return the final Points of the corresponding equip.
     */
    public int getFinalPoints(boolean equipA) {
        return equipA ? this.finalPointsA : this.finalPointsB;
    }

    public boolean isInDispute(){
        return this.finalPointsA == this.finalPointsB;
    }

    /**
     * @return the list of declarations.
     */
    public ArrayList<PlayerDeclaration> getDeclarationList() {
        return this.declarationList;
    }

    /**
     * @return True if the basic parameters are initialized (RoundType, StartingPlayer and LeaderPlayer).
     */
    public boolean canAddPoints(){
        return this.type != null && this.startingPlayerId != 0 && this.leaderPlayerId != 0;
    }

    /**
     * @return True if the basic parameters are initialized (RoundType, StartingPlayer and LeaderPlayer) and the score is correct.
     */
    public boolean canBeSaved(){
        return this.canAddPoints() && this.rawPointsA + this.rawPointsB == this.type.getPointsPerRound();
    }

    /**
     * Adds the declaration to the declaration list, and computes the points.
     * @param playerId in {1, 2, 3, 4}.
     * @param declarationType of the declaration.
     * @return the viewId generated for the corresponding declaration.
     */
    public int addDeclaration(int playerId, DeclarationType declarationType){
        int id = View.generateViewId();
        this.declarationList.add(new PlayerDeclaration(playerId, declarationType, id));
        if(Belote.isEquipA(playerId)) {
            this.declarationPointsA += declarationType.getValue(this.type);
        }else{
            this.declarationPointsB += declarationType.getValue(this.type);
        }
        return id;
    }

    /**
     * Removes the declaration from the list and computes the points without this declaration.
     * @param index the position of the declaration in the declarationList.
     */
    public void removeDeclaration(int index){
        PlayerDeclaration declaration = this.declarationList.get(index);
        if(declaration.isEquipA()) {
            this.declarationPointsA -= declaration.getDeclarationType().getValue(this.type);
        }else{
            this.declarationPointsB -= declaration.getDeclarationType().getValue(this.type);
        }
        this.declarationList.remove(index);
    }

    /**
     * @param index of the declaration in the list
     * @return the corresponding view id.
     */
    public int getDeclarationViewId(int index){
        return this.declarationList.get(index).getViewId();
    }

    /**
     * Computes the points (rawPoints without declaration, declarationPoints and finalPoints with multiplier).
     * @param equipA True if points in parameters are equipA's points.
     * @param points Points won during this round.
     */
    public void setPointsEquip(boolean equipA, int points){
        if(this.type == null) return;
        this.rawPointsA = equipA ? points : this.type.getPointsPerRound() - points;
        this.rawPointsB = equipA ? this.type.getPointsPerRound() - points : points;
        this.computesFinalPoints();
    }

    /**
     * Computes the final Points using raw Points and declaration Points:
     * - final Points take the value 0 if both raw Points are 0.
     * - firstly, computes the sum of the raw Points and the declaration Points.
     * - then applies the multiplies of the roundType.
     */
    public void computesFinalPoints(){
        if(this.rawPointsA == 0 && this.rawPointsB == 0){
            this.finalPointsA = 0;
            this.finalPointsB = 0;
            return;
        }
        this.finalPointsA = this.rawPointsA + this.declarationPointsA;
        this.finalPointsB = this.rawPointsB + this.declarationPointsB;
        if(this.leaderIsEquipA()) {
            if(this.finalPointsB > this.finalPointsA) {
                this.finalPointsA = 0;
                this.finalPointsB = this.type.getPointsPerRound() + this.declarationPointsA + this.declarationPointsB;
            }
        }else{
            if(this.finalPointsB < this.finalPointsA){
                this.finalPointsA = this.type.getPointsPerRound() + this.declarationPointsA + this.declarationPointsB;
                this.finalPointsB = 0;
            }
        }
        if(this.rawPointsA == 0) this.finalPointsB += POINTS_CAPOT;
        if(this.rawPointsB == 0) this.finalPointsA += POINTS_CAPOT;

        //The multiplier is only applied in the end
        this.finalPointsA *= this.type.getMultiplier();
        this.finalPointsB *= this.type.getMultiplier();
    }

    /**
     * @return an integer corresponding to the winner.
     * <p>- Equip A: -1
     * <p>- Equality: 0
     * <p>- Equip B: 1
     */
    public int getWinner(){
        if(this.finalPointsA == this.finalPointsB) return 0;
        return this.finalPointsA > this.finalPointsB ? -1 : 1;
    }

    /**
     * Create a JSONObject used to save this Round in json.
     * @return a JSONObject containing the parameters:
     * <p>- type
     * <p>- startingPlayerId
     * <p>- leaderPlayerId
     * <p>- rawPointsA
     * <p>- rawPointsB
     * <p>- finalPointsA
     * <p>- finalPointsB
     * <p>- a JSONArray declarationsList containing all declarations
     */
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

    /**
     * Set raw Points and final Points to 0.
     */
    public void clearPoints() {
        this.rawPointsA = 0;
        this.rawPointsB = 0;
        this.finalPointsA = 0;
        this.finalPointsB = 0;
    }

    public static class PlayerDeclaration{

        private final int playerId;
        private final DeclarationType declarationType;
        private final int viewId;

        /**
         * Object used to store each declarations during a round with its player.
         * @param playerId in {1, 2, 3, 4}.
         * @param declarationType of the declaration.
         */
        public PlayerDeclaration(int playerId, DeclarationType declarationType, int viewId){
            this.playerId = playerId;
            this.declarationType = declarationType;
            this.viewId = viewId;
        }

        /**
         * @return the DeclarationType.
         */
        public DeclarationType getDeclarationType(){
            return this.declarationType;
        }

        /**
         * @return the playerId in {1, 2, 3, 4}.
         */
        public int getPlayerId(){
            return this.playerId;
        }

        /**
         * @param belote game to get the player names.
         * @return the name of the declaration owner in the given Belote game.
         */
        public String getPlayerName(Belote belote){
            return belote.getPlayerFromId(this.playerId).getName();
        }

        /**
         * @return the view id of the declaration.
         */
        public int getViewId() {
            return this.viewId;
        }

        /**
         * @return True if this declaration was made by the equip A.
         */
        public boolean isEquipA(){
            return Belote.isEquipA(this.getPlayerId());
        }
    }
}
