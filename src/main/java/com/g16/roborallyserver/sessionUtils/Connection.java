package com.g16.roborallyserver.sessionUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dk.dtu.compute.se.pisd.roborally.model.CommandCardField;
import dk.dtu.compute.se.pisd.roborally.model.Player;


public class Connection {

    public String userID;


    public GameSession gameSession;


    private boolean isHost = false;

    private Player playerRobot;



    private String[] program;

    @JsonIgnore
    public boolean isDoneProgramming() {
        return isProgramming;
    }

    public void setDoneProgramming(boolean programming) {
        isProgramming = programming;
    }

    private boolean isProgramming = false;



    public Connection(String userID, GameSession gameSession){
        this.userID = userID;
        this.gameSession = gameSession;
    }

    @JsonIgnore
    public boolean isHost() {
        return isHost;
    }

    public void setHost(boolean host) {
        isHost = host;
    }

    public void setPlayerToken(Player playerRobot){
        this.playerRobot = playerRobot;
    }

    @JsonIgnore
    public Player getPlayerRobot() {
        return playerRobot;
    }

    @JsonIgnore
    public String[] getProgram() {
        return program;
    }

    public void setProgram(String[] program) {
        this.program = program;
    }

}
