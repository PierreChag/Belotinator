package com.poulpinou.belotinator.core;

import android.util.Pair;

import java.util.ArrayList;

public class Round {

    public static final int POINTS_CAPOT = 90;

    private final Belote belote;
    private Player startingPlayer, leaderPlayer;
    private RoundType type;
    private int rawPointsA, rawPointsB, declarationPointsA, declarationPointsB, finalPointsA, finalPointsB;
    private final ArrayList<Pair<Player,DeclarationType>> declarationList = new ArrayList<>();

    public Round(Belote belote, Player startingPlayer, Player leaderPlayer, RoundType type){
        this.belote = belote;
        this.startingPlayer = startingPlayer;
        this.leaderPlayer = leaderPlayer;
        this.type = type;
    }

    public void editStartingPlayer(Player player){
        this.startingPlayer = player;
    }

    public void editLeaderPlayer(Player player){
        this.leaderPlayer = player;
    }

    public void editRoundType(RoundType type){
        this.type = type;
    }

    public void addDeclaration(Player player, DeclarationType declarationType){
        this.declarationList.add(new Pair<>(player, declarationType));
        if(this.belote.playerIsEquipA(player)) {
            this.declarationPointsA += declarationType.getValue(this.type);
        }else{
            this.declarationPointsB += declarationType.getValue(this.type);
        }
    }

    public void removeDeclaration(int index){
        Pair<Player,DeclarationType> declaration = this.declarationList.get(index);
        if(this.belote.playerIsEquipA(declaration.first)) {
            this.declarationPointsA -= declaration.second.getValue(this.type);
        }else{
            this.declarationPointsB -= declaration.second.getValue(this.type);
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
        if(this.belote.playerIsEquipA(this.leaderPlayer)) {
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

        this.finalPointsA *= this.type.getMultiplier();
        this.finalPointsB *= this.type.getMultiplier();
    }
}
