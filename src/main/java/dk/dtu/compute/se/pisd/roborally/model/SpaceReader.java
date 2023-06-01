package dk.dtu.compute.se.pisd.roborally.model;

import javafx.scene.paint.Color;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
/**
 * Space Reader
 * <p>
 * Class that reads a map file and places down objects on the board
 *
 */

public class SpaceReader {
    String fileName;

    public SpaceReader(String fileName){
        this.fileName = fileName;
    }

    /**
     * Initializes the map by reading a map file and places down objects to the board
     * @param board the board where the objects are placed
     */

    public void initMap(Board board){
        try{
            File map = new File(fileName);
            Scanner myReader = new Scanner(map);
            int lineNum = 0;
            while (myReader.hasNextLine()) {
                lineNum += 1;
                String data = myReader.nextLine();
                String[] result = data.split(";");
                Space space = board.getSpace(0,0);
                if (!result[0].equals("#")) {
                    space = board.getSpace(Integer.parseInt(result[1]), Integer.parseInt(result[2]));
                }
                switch (result[0]){
                    case "#":
                        break;
                    case "P":
                        space.addObjects(new StartField());
                        break;
                    case "Wall":
                        space.addObjects(new Wall(Heading.valueOf(result[3])));
                        break;
                    case "CheckPoint":
                        CheckpointField newCheckpoint = new CheckpointField(Integer.parseInt(result[3]));
                        space.addObjects(newCheckpoint);
                        board.addCheckpoint(newCheckpoint);
                        break;
                    case "BlueCon":
                        space.addObjects(new Conveyor(Color.BLUE,Heading.valueOf(result[3])));
                        break;
                    case "GreenCon":
                        space.addObjects(new Conveyor(Color.GREEN,Heading.valueOf(result[3])));
                        break;
                    case "Gear":
                        space.addObjects(new Gear(Direction.valueOf(result[3])));
                        break;
                    case "Laser":
                        space.addObjects(new Laser(Heading.valueOf(result[3]), result[4]));
                        break;
                    case "Reboot":
                        space.addObjects(new RebootField(Heading.valueOf(result[3]),Integer.parseInt(result[1]),Integer.parseInt(result[2])));
                        break;
                    default:
                        System.out.println("Unknown object in " + fileName);
                        System.out.println("On line " +lineNum);
                        break;
                }
            }
        } catch (FileNotFoundException e){
            System.out.println("File not found");
        }
    }

    /**
     * Initializes the player by placing them on the start fields
     * @param board the board where the players are placed
     * @param player the player which receives a space from the map file
     * @param playerNum the players number which determines their order
     */
    public void initPlayers(Board board, Player player, int playerNum){
        try{
            String data = Files.readAllLines(Paths.get(fileName)).get(playerNum);
            String[] result = data.split(";");

            player.setSpace(board.getSpace(Integer.parseInt(result[1]), Integer.parseInt(result[2])));
        } catch (FileNotFoundException e){
            System.out.println("File not found");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
