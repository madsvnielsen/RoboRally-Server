package com.g16.roborallyserver.sessionUtils;

import dk.dtu.compute.se.pisd.roborally.model.Command;
import dk.dtu.compute.se.pisd.roborally.model.CommandCard;


/**
 * Command Card utilities
 * Helper functions for processing input of command cards represented by strings.
 */

public class CommandCardUtil {


    /** Convert command
     * Creates a CommandCard based of the name of the desired command
     * @param sCom display name of command
     * @return CommandCard
     */
    public static CommandCard convertCommand(String sCom){

        return switch (sCom) {
            case "Move 1" -> new CommandCard(Command.FORWARD);
            case "Move 2" -> new CommandCard(Command.FAST_FORWARD);
            case "Move 3" -> new CommandCard(Command.MOVE_THREE);
            case "Turn Right" -> new CommandCard(Command.RIGHT);
            case "Turn Left" -> new CommandCard(Command.LEFT);
            case "Do a u-turn" -> new CommandCard(Command.UTURN);
            case "Power Up" -> new CommandCard(Command.POWERUP);
            case "Back Up" -> new CommandCard(Command.MOVE_BACK);
            case "Repeat last card" -> new CommandCard(Command.AGAIN);
            case "Turn left or right" -> new CommandCard(Command.CHOOSETURN);
            case "Spam" -> new CommandCard(Command.SPAM);
            //case "wait" -> new CommandCard(Command.WAIT);
            default -> null;
        };
    }
}
