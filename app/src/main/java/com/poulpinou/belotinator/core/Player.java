package com.poulpinou.belotinator.core;

import java.util.UUID;

public class Player {

    private UUID uuid;
    private String name;

    //String dir = Environment.getExternalStorageDirectory()+File.separator+"nameOfYourFile.extension"

    public Player (String name){
        this.name = name;
        this.uuid = UUID.randomUUID();
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public String getName(){
        return this.name;
    }
}
