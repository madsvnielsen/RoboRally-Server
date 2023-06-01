package com.g16.roborallyserver;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Connection {

    public String userId;

    @JsonProperty("gameData")
    private GameSession gameSession;



    public Connection(String userId, GameSession gameSession){
        this.userId = userId;
        this.gameSession = gameSession;
    }


}
