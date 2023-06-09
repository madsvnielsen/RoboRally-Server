package com.g16.roborallyserver.sessionUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dk.dtu.compute.se.pisd.roborally.model.Player;


/**Connection
 *  A connection object represents a players connection with a game session
 */

public class Connection {


    /**
     * Unique user id to identify users
     */
    public String userID;


    /**
     * Game session with which the connection is associated
     */
    public GameSession gameSession;


    private boolean isHost = false;

    private Player playerRobot;

    /**
     * program, saveProgram and cardHand are used to save the state of the players cards
     */
    private String[] program;
    private String[] savedProgram;

    private String[] cardHand;


    public void setCardHand(String[] cardHand) {
        this.cardHand = cardHand;
        for(int i = 0; i < cardHand.length; i++){
            playerRobot.getCardField(i).setCard(CommandCardUtil.convertCommand(cardHand[i]));
        }

    }


    @JsonIgnore
    public boolean isDoneProgramming() {
        return isProgramming;
    }

    public void setDoneProgramming(boolean programming) {
        isProgramming = programming;
    }

    private boolean isProgramming = false;


    private boolean hasExecutedCards = false;



    public Connection(String userID, GameSession gameSession){
        this.userID = userID;
        this.gameSession = gameSession;
    }


    @JsonIgnore
    public boolean isHasExecutedCards() {
        return hasExecutedCards;
    }

    public void setHasExecutedCards(boolean hasExecutedCards) {
        this.hasExecutedCards = hasExecutedCards;
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
    public void setSavedProgram(String[] savedProgram){
        this.savedProgram = savedProgram;
        for(int i = 0; i < savedProgram.length; i++){
            playerRobot.getProgramField(i).setCard(CommandCardUtil.convertCommand(savedProgram[i]));
        }
    }

}
