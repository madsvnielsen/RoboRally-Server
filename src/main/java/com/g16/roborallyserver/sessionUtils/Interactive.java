package com.g16.roborallyserver.sessionUtils;

/** Interactive
 *  Represents a chosen command by a player who used an "interactive" card
 */


public class Interactive {

    private int userID;

    private String  step;

    private boolean chosen;

    private String command;

    public Interactive(int userID, String step, boolean chosen, String command){
        this.userID = userID;
        this.step = step;
        this.chosen = chosen;
        this.command = command;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getStep() {
        return step;
    }

    public void setStep(String step) {
        this.step = step;
    }

    public boolean isChosen() {
        return chosen;
    }

    public void setChosen(boolean chosen) {
        this.chosen = chosen;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }


}
