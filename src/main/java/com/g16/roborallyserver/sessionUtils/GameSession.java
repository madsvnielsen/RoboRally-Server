package com.g16.roborallyserver.sessionUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import java.util.ArrayList;

/**
 * GameSession
 * A gameSession object represents a game session
 */
public class GameSession {

    public final String gameID;


    /**
     * List to store the choices made by players who used "Interactive" cards
     */
    public final ArrayList<Interactive> Interactives = new ArrayList<>();

    private boolean isStarted = false;


    /**
     * Associated gameController
     */
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
    public String getGameID() {
        return gameID;
    }

    @JsonIgnore
    public void appendInteractives(Interactive interactive){
        Interactives.add(interactive);
    }

    public ArrayList<Interactive> getInteractives() {
        return Interactives;
    }

    public void clearAll(){
        Interactives.clear();
    }

}
