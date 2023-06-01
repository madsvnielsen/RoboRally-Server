package com.g16.roborallyserver.sessionUtils;

public class Connection {

    public String userID;


    public GameSession gameSession;



    public Connection(String userID, GameSession gameSession){
        this.userID = userID;
        this.gameSession = gameSession;
    }


}
