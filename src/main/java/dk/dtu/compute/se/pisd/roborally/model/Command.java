/*
 *  This file is part of the initial project provided for the
 *  course "Project in Software Development (02362)" held at
 *  DTU Compute at the Technical University of Denmark.
 *
 *  Copyright (C) 2019, 2020: Ekkart Kindler, ekki@dtu.dk
 *
 *  This software is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; version 2 of the License.
 *
 *  This project is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this project; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package dk.dtu.compute.se.pisd.roborally.model;

import com.google.gson.annotations.Expose;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public enum Command {

    // This is a very simplistic way of realizing different commands.

    FORWARD("Move 1"),
    RIGHT("Turn Right"),
    LEFT("Turn Left"),
    FAST_FORWARD("Move 2"),
    MOVE_THREE("Move 3"),
    UTURN("Do a u-turn"),
    POWERUP("Power Up"),
    MOVE_BACK("Back Up"),
    AGAIN("Repeat last card"),
    CHOOSETURN("Turn left or right", RIGHT, LEFT),
    SPAM("Spam")
    ;

    @Expose
    final public String displayName;

    final private List<Command> options;

    Command(String displayName, Command... options) {
        this.displayName = displayName;
        this.options = Collections.unmodifiableList(Arrays.asList(options));
    }


    /**
     * Returns whether the command requires user interaction
     * <p>
     * Returns if the command requires the user to select between different commands upon execution
     * @return Whether the command is interactive
     */
    public boolean isInteractive() {
        return !options.isEmpty();
    }

    /**
     * Returns command options
     * <p>
     * Returns the options the user can choose between upon the execution of the command
     * @return List of options (commands)
     */
    public List<Command> getOptions() {
        return options;
    }


}
