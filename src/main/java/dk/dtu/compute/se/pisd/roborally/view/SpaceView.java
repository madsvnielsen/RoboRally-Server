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
package dk.dtu.compute.se.pisd.roborally.view;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.model.*;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class SpaceView extends StackPane implements ViewObserver {

    // Update this value to change the size of the spaces
    // Size should be no smaller than 45
    final static int spaceSize = 45; // 60; // 75;
    final public static int SPACE_HEIGHT = spaceSize;
    final public static int SPACE_WIDTH = spaceSize;

    public final Space space;

    public SpaceView(@NotNull Space space) {
        this.space = space;
        // XXX the following styling should better be done with styles
        this.setPrefWidth(SPACE_WIDTH);
        this.setMinWidth(SPACE_WIDTH);
        this.setMaxWidth(SPACE_WIDTH);

        this.setPrefHeight(SPACE_HEIGHT);
        this.setMinHeight(SPACE_HEIGHT);
        this.setMaxHeight(SPACE_HEIGHT);

        // Create an ImageView object and set its image to the desired picture
        Image image = new Image("File:src/main/java/dk/dtu/compute/se/pisd/roborally/Sprites/Tile.png");
        ImageView imageView = new ImageView(image);

        // Set the background of the SpaceView to the ImageView object
        this.setBackground(new Background(new BackgroundImage(imageView.getImage(), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, new BackgroundSize(SPACE_WIDTH+1, SPACE_HEIGHT+1, false, false, false, false))));

        // This space view should listen to changes of the space
        space.attach(this);
        update(space);
    }

    private void updatePlayer() {

        Player player = space.getPlayer();
        if (player != null) {
            Circle playerGFX = new Circle();
            playerGFX.setRadius(SPACE_WIDTH/2);
            Image robots = new Image("File:src/main/java/dk/dtu/compute/se/pisd/roborally/Sprites/robo_figur.png");
            try {
                if (player.getPlayerNum() <= 4) {
                    playerGFX.setFill(new ImagePattern(robots, -player.getPlayerNum() + 1, 0, 4, 2, true));
                } else {
                    playerGFX.setFill(new ImagePattern(robots, -player.getPlayerNum() + 1, 1, 4, 2, true));
                }
            }
            catch (Exception e) {
                playerGFX.setFill(Color.MEDIUMPURPLE);
            }

            playerGFX.setRotate((90*player.getHeading().ordinal())%360);

            this.getChildren().add(playerGFX);
        }

    }


    private void drawFieldObjects(){
        ArrayList<FieldObject> walls = space.findObjectsOfType(Wall.class);
        Conveyor conveyor = (Conveyor)space.findObjectOfType(Conveyor.class);
        Gear gear = (Gear) space.findObjectOfType(Gear.class);
        CheckpointField checkpoint = (CheckpointField) space.findObjectOfType(CheckpointField.class);
        StartField startField = (StartField) space.findObjectOfType(StartField.class);
        Laser laserField = (Laser) space.findObjectOfType(Laser.class);
        RebootField rebootField = (RebootField) space.findObjectOfType(RebootField.class);
        //Gears
        if (gear != null){
            Rectangle gearGfx = new Rectangle();
            gearGfx.setWidth(SPACE_WIDTH);
            gearGfx.setHeight(SPACE_HEIGHT);
            if (gear.getDirection() == Direction.RIGHT){
                Image greenGear = new Image("File:src/main/java/dk/dtu/compute/se/pisd/roborally/Sprites/rightGear.png");
                gearGfx.setFill(new ImagePattern(greenGear,0,0,1,1,true));
            } else {
                Image redGear = new Image("File:src/main/java/dk/dtu/compute/se/pisd/roborally/Sprites/leftgear.png");
                gearGfx.setFill(new ImagePattern(redGear,0,0,1,1,true));
            }
            this.getChildren().add(gearGfx);
        }

        //Walls
        if (walls.isEmpty()) {
        } else {
            for (FieldObject fobject : walls) {
                Wall object = (Wall) fobject;
                Image wallSprite = new Image("File:src/main/java/dk/dtu/compute/se/pisd/roborally/Sprites/wall.png");
                Rectangle wallGfx = new Rectangle();
                wallGfx.setWidth(SPACE_WIDTH);
                wallGfx.setHeight(10);
                wallGfx.setFill(new ImagePattern(wallSprite, 0, 0, 1, 1, true));
                switch (object.getDir()) {
                    case SOUTH:
                        wallGfx.setTranslateY(20);
                        break;
                    case NORTH:
                        wallGfx.setTranslateY(-20);
                        break;
                    case EAST:
                        wallGfx.setRotate(90);
                        wallGfx.setTranslateX(20);
                        break;
                    case WEST:
                        wallGfx.setRotate(90);
                        wallGfx.setTranslateX(-20);
                        break;
                }

                this.getChildren().add(wallGfx);
            }
        }

        //Reboot
        if (rebootField !=null){
            Rectangle rebootGFX = new Rectangle();
            rebootGFX.setHeight(SPACE_HEIGHT);
            rebootGFX.setWidth(SPACE_WIDTH);
            switch (rebootField.getDirection()) {
                case SOUTH:
                    rebootGFX.setRotate(180);
                    break;
                case NORTH:
                    break;
                case EAST:
                    rebootGFX.setRotate(90);
                    break;
                case WEST:
                    rebootGFX.setRotate(-90);
                    break;
            }
            Image reboot = new Image("File:src/main/java/dk/dtu/compute/se/pisd/roborally/Sprites/Reboot.jpg");
            rebootGFX.setFill(new ImagePattern(reboot,0,0,1,1,true));
            this.getChildren().add(rebootGFX);
        }

        //Checkpoints
        if(checkpoint != null){
            Circle cpGfx = new Circle();
            cpGfx.setRadius(SPACE_WIDTH*0.5);
            Image checkpoints = new Image("File:src/main/java/dk/dtu/compute/se/pisd/roborally/Sprites/checkpoints.jpg");
            if (checkpoint.getCheckpointNumber() <= 4) {
                cpGfx.setFill(new ImagePattern(checkpoints, -checkpoint.getCheckpointNumber()+1, 0, 4, 2, true));
            } else {
                cpGfx.setFill(new ImagePattern(checkpoints, -checkpoint.getCheckpointNumber()+1, 1, 4, 2, true));
            }
            this.getChildren().add(cpGfx);
        }

        //Start
        if(startField != null){
            Circle startGfx = new Circle();
            startGfx.setRadius(SPACE_WIDTH/2.5);
            Image startfield = new Image("File:src/main/java/dk/dtu/compute/se/pisd/roborally/Sprites/startfield.png");
            startGfx.setFill(new ImagePattern(startfield,0,0,1,1,true));
            this.getChildren().add(startGfx);
        }

        //Conveyor
        if (conveyor != null) {
            Rectangle conveyorGfx = new Rectangle();
            conveyorGfx.setWidth(SPACE_WIDTH-5);
            conveyorGfx.setHeight(SPACE_HEIGHT);
            switch (conveyor.getDirection()) {
                case SOUTH:
                    conveyorGfx.setRotate(180);
                    break;
                case NORTH:
                    break;
                case EAST:
                    conveyorGfx.setRotate(90);
                    break;
                case WEST:
                    conveyorGfx.setRotate(-90);
                    break;
            }
            if (conveyor.getCOLOR().equals(Color.BLUE)) {
                Image blueCon = new Image("File:src/main/java/dk/dtu/compute/se/pisd/roborally/Sprites/blueConveyor.png");
                conveyorGfx.setFill(new ImagePattern(blueCon,0,0,1,1,true));
            }else {
                Image orangeCon = new Image("File:src/main/java/dk/dtu/compute/se/pisd/roborally/Sprites/orangeConveyor.png");
                conveyorGfx.setFill(new ImagePattern(orangeCon,0,0,1,1,true));
            }
            this.getChildren().add(conveyorGfx);

        }
        if (laserField != null){
            Rectangle laserShot = new Rectangle();
            laserShot.setHeight(SPACE_HEIGHT);
            laserShot.setWidth(SPACE_WIDTH);
            switch (laserField.getDirection()) {
                case SOUTH:
                    laserShot.setRotate(180);
                    if (laserField.getTYPE().equals("EMITER")){
                        laserShot.setTranslateY(SPACE_WIDTH*0.15);
                    }
                    break;
                case NORTH:
                    if (laserField.getTYPE().equals("EMITER")){
                        laserShot.setTranslateY(-SPACE_WIDTH*0.15);
                    }
                    break;
                case EAST:
                    laserShot.setRotate(90);
                    if (laserField.getTYPE().equals("EMITER")){
                        laserShot.setTranslateX(SPACE_HEIGHT*0.15);
                    }
                    break;
                case WEST:
                    laserShot.setRotate(-90);
                    if (laserField.getTYPE().equals("EMITER")){
                        laserShot.setTranslateX(-SPACE_HEIGHT*0.15);
                    }
                    break;
            }
            if (laserField.getTYPE().equals("SHOT")) {
                Image laser = new Image("File:src/main/java/dk/dtu/compute/se/pisd/roborally/Sprites/SingleLaser.png");
                laserShot.setFill(new ImagePattern(laser, 0, 0, 1, 1, true));
            } else {
                Image laser = new Image("File:src/main/java/dk/dtu/compute/se/pisd/roborally/Sprites/Emiter.png");
                laserShot.setFill(new ImagePattern(laser, 0, 0, 1, 1, true));
            }
            this.getChildren().add(laserShot);
        }
        //Walls
        /*if( wall != null) {
            Image wallSprite = new Image("File:src/main/java/dk/dtu/compute/se/pisd/roborally/Sprites/wall.png");
            Rectangle wallGfx = new Rectangle();
            wallGfx.setWidth(SPACE_WIDTH);
            wallGfx.setHeight(SPACE_WIDTH*0.222);
            wallGfx.setFill(new ImagePattern(wallSprite,0,0,1,1,true));

            switch (wall.getDir()) {
                case SOUTH:
                    wallGfx.setTranslateY(SPACE_WIDTH*0.45);
                    break;
                case NORTH:
                    wallGfx.setTranslateY(-SPACE_WIDTH*0.45);
                    break;
                case EAST:
                    wallGfx.setRotate(90);
                    wallGfx.setTranslateX(SPACE_WIDTH*0.45);
                    break;
                case WEST:
                    wallGfx.setRotate(90);
                    wallGfx.setTranslateX(-SPACE_WIDTH*0.45);
                    break;
            }

            this.getChildren().add(wallGfx);
        }*/
        /*
        PushPanel pushPanel = (PushPanel) space.findObjectOfType(PushPanel.class);
        if (pushPanel != null) {
            Rectangle pushpannelGfx = new Rectangle();
            pushpannelGfx.setWidth(25);
            pushpannelGfx.setHeight(47);
            switch (pushPanel.getDirection()) {
                case SOUTH:
                    pushpannelGfx.setTranslateY(20);
                    break;
                case NORTH:
                    pushpannelGfx.setTranslateY(-20);
                    break;
                case EAST:
                    pushpannelGfx.setRotate(90);
                    pushpannelGfx.setTranslateX(20);
                    break;
                case WEST:
                    pushpannelGfx.setRotate(90);
                    pushpannelGfx.setTranslateX(-20);
                    break;
            }
            if (conveyor.getColor().equals(Color.BLUE)) {
                pushpannelGfx.setFill(Color.ROYALBLUE);
            }else {
                pushpannelGfx.setFill(Color.FORESTGREEN);
            }
            this.getChildren().add(pushpannelGfx);
            this.getChildren().add(pushpannelGfx);

        }
         */
    }




    /**
     * Update view
     * <p>
     * This method updates the graphical view of the space.
     * It redraws the objects on the field and the players.
     * Note that the method deletes graphical objects on the space and redraws.
     */
    @Override
    public void updateView(Subject subject) {
        this.getChildren().clear();

        drawFieldObjects();

        if (subject == this.space) {
            updatePlayer();
        }

    }

}
