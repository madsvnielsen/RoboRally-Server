package com.g16.roborallyserver.sessionUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;

import dk.dtu.compute.se.pisd.roborally.controller.GameController;

public class GameSession {

    public String getGameID() {
        return gameID;
    }

    public String gameID;

    public String command;


    private boolean isStarted = false;


    private GameController controller = null;

    public GameSession(String gameID){
        this.gameID = gameID;
    }

    public void setIsStarted(boolean isStarted){
        this.isStarted = isStarted;
    }

    @JsonIgnore
    public boolean isStarted(){
        return isStarted;
    }

    public void setController(GameController controller){
        this.controller = controller;
    }

    @JsonIgnore
    public GameController getController(){
        return controller;
    }
}
