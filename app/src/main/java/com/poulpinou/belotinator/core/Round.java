package com.poulpinou.belotinator.core;

import android.view.View;

import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Round {

    private RoundType type = null;
    private int startingPlayerId = 0, leaderPlayerId = 0;
    private int rawPointsA, rawPointsB, declarationPointsA, declarationPointsB, finalPointsA, finalPointsB;
    private final ArrayList<PlayerDeclaration> declarationList = new ArrayList<>();
    private boolean isInDispute = false;

    /**
     * Creates a new instance of Round but all the basic parameters must be initialized manually (RoundType, StartingPlayer and LeaderPlayer).
     */
    public Round(){}

    /**
     * @return the id of the starting player:
     * <p>- 1: playerA EquipA
     * <p>- 2: playerA EquipB
     * <p>- 3: playerB EquipA
     * <p>- 4: playerB EquipB
     */
    public int getStartingPlayerId(){
        return this.startingPlayerId;
    }

    /**
     * @return the id of the leader player:
     * <p>- 1: playerA EquipA
     * <p>- 2: playerA EquipB
     * <p>- 3: playerB EquipA
     * <p>- 4: playerB EquipB
     */
    public int getLeaderPlayerId(){
        return this.leaderPlayerId;
    }

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

    /**
     * @return true if the leader is  in equip A.
     */
    public boolean leaderIsEquipA() {
        return Player.isEquipA(this.leaderPlayerId);
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
     * @return the raw Points of the corresponding equip.
     */
    public int getDeclarationPoints(boolean equipA) {
        return equipA ? this.declarationPointsA : this.declarationPointsB;
    }

    /**
     * @param equipA is True for Equip A, False for Equip B.
     * @return the final Points of the corresponding equip.
     */
    public int getFinalPoints(boolean equipA) {
        return equipA ? this.finalPointsA : this.finalPointsB;
    }

    /**
     * @return true if the round is still in dispute.
     */
    public boolean isInDispute(){
        return this.isInDispute;
    }

    /**
     * Checks if the round is in dispute. If yes, set the leader final points to 0.
     */
    public void choseDisputeState() {
        if(this.getWinner() == 0){
            if(this.leaderIsEquipA()){
                this.finalPointsA = 0;
            }else{
                this.finalPointsB = 0;
            }
            this.isInDispute = true;
        }else this.isInDispute = false;
    }

    /**
     * Resolves the dispute state of the round by adding the missing points to the winner of the next round.
     * @param nextWinnerIsEquipA is true if the equip that won the next round is equip A.
     * @return number of points added to the winner score.
     */
    public int resolveDispute(boolean nextWinnerIsEquipA){
        int pointsToAdd = 0;
        if(this.isInDispute){
            pointsToAdd = Math.max(this.finalPointsA, this.finalPointsB);
            if(nextWinnerIsEquipA){
                this.finalPointsA += pointsToAdd;
            }else{
                this.finalPointsB += pointsToAdd;
            }
            this.isInDispute = false;
        }
        return pointsToAdd;
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
        if(Player.isEquipA(playerId)) {
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
        if(this.rawPointsA == 0) this.finalPointsB += Utils.POINTS_CAPOT;
        if(this.rawPointsB == 0) this.finalPointsA += Utils.POINTS_CAPOT;

        //The multiplier is only applied in the end
        this.finalPointsA *= this.type.getMultiplier();
        this.finalPointsB *= this.type.getMultiplier();
        this.choseDisputeState();
    }

    /**
     * @return an integer corresponding to the winner.
     * <p>- Equip A: -1
     * <p>- Equality: 0
     * <p>- Equip B: 1
     */
    public int getWinner(){
        if(this.isInDispute) return 0;
        return this.finalPointsA > this.finalPointsB ? -1 : 1;
    }

    /**
     * @param playerId:
     * <p>- 1: playerA EquipA
     * <p>- 2: playerA EquipB
     * <p>- 3: playerB EquipA
     * <p>- 4: playerB EquipB
     * @return true if the player is in the team that won.
     */
    public boolean wonByPlayer(int playerId){
        return Player.isEquipA(playerId) ? this.getWinner() == -1 : this.getWinner() == 1;
    }

    /**
     * @return true if the round was won thanks to declaration points.
     */
    public boolean wonWithDeclarations(){
        switch (this.getWinner()){
            default:
            case 0:
                return false;
            case -1:
                return this.finalPointsA - this.type.getMultiplier() * (this.declarationPointsA - this.declarationPointsB) <= this.finalPointsB;
            case 1:
                return this.finalPointsB - this.type.getMultiplier() * (this.declarationPointsB - this.declarationPointsA) <= this.finalPointsB;
        }

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
            thisRoundJson.put("isInDispute", this.isInDispute);
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
     * @param roundInJSON the round saved in JSON format.
     * @return a new instance of Round created with the provided data.
     */
    @Nullable
    public static Round loadJSON(JSONObject roundInJSON){
        Round loadedRound = new Round();
        try {
            loadedRound.setRoundType(RoundType.getRoundType(roundInJSON.getString("type")));
            loadedRound.setStartingPlayer(roundInJSON.getInt("startingPlayerId"));
            loadedRound.setLeaderPlayer(roundInJSON.getInt("leaderPlayerId"));
            loadedRound.rawPointsA = roundInJSON.getInt("rawPointsA");
            loadedRound.rawPointsB = roundInJSON.getInt("rawPointsB");
            loadedRound.finalPointsA = roundInJSON.getInt("finalPointsA");
            loadedRound.finalPointsB = roundInJSON.getInt("finalPointsB");
            loadedRound.isInDispute = roundInJSON.getBoolean("isInDispute");
            // looping through all rounds
            JSONArray declarationArrayJson = roundInJSON.getJSONArray("declarationsList");
            for(int i = 0; i < declarationArrayJson.length(); i++) {
                JSONObject thisDeclarationJson = declarationArrayJson.getJSONObject(i);
                PlayerDeclaration playerDeclaration = new PlayerDeclaration(thisDeclarationJson.getInt("player"), DeclarationType.getDeclarationType(thisDeclarationJson.getString("type")));
                loadedRound.declarationList.add(playerDeclaration);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            loadedRound = null;
        }
        return loadedRound;
    }

    /**
     * Set raw Points and final Points to 0.
     */
    public void clearPoints() {
        this.rawPointsA = 0;
        this.rawPointsB = 0;
        this.finalPointsA = 0;
        this.finalPointsB = 0;
        this.isInDispute = false;
    }

    /**
     * @return the minimal number of points the leader needed to obtain in order to win the Round.
     */
    public int getMinimalPointsForLeader() {
        return (this.type.getPointsPerRound() + this.declarationPointsA + this.declarationPointsB) / 2 - this.getDeclarationPoints(this.leaderIsEquipA()) + 1;
    }

    public static class PlayerDeclaration{

        private final int playerId;
        private final DeclarationType declarationType;
        private int viewId;

        /**
         * Object used to store each declarations during a round with its player.
         * @param playerId in {1, 2, 3, 4}.
         * @param declarationType of the declaration.
         */
        public PlayerDeclaration(int playerId, DeclarationType declarationType){
            this.playerId = playerId;
            this.declarationType = declarationType;
        }

        public PlayerDeclaration(int playerId, DeclarationType declarationType, int viewId){
            this(playerId, declarationType);
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
         * @return the view id of the declaration. Return 0 if there is no view associated. //TODO Check les conséquences quand on charge une declaration et donc qu'il n'y pas de viewId.
         */
        public int getViewId() {
            return this.viewId;
        }

        /**
         * @return True if this declaration was made by the equip A.
         */
        public boolean isEquipA(){
            return Player.isEquipA(this.getPlayerId());
        }
    }
}
