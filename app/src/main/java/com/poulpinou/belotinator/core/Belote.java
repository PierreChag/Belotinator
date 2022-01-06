package com.poulpinou.belotinator.core;

import java.util.Date;

public class Belote {

    private final Date date;
    private final Player playerAEquipA, playerBEquipA, playerAEquipB, playerBEquipB;
    private final int victoryPoints;
    private int equipAPoints, equipBPoints;

    public Belote(Date date, Player playerAEquipA, Player playerBEquipA, Player playerAEquipB, Player playerBEquipB, int victoryPoints){
        this.date = date;
        this.playerAEquipA = playerAEquipA;
        this.playerBEquipA = playerBEquipA;
        this.playerAEquipB = playerAEquipB;
        this.playerBEquipB = playerBEquipB;
        this.victoryPoints = victoryPoints;
    }

    public boolean playerIsEquipA(Player player){
        return player == this.playerAEquipA || player == this.playerAEquipB;
    }

}