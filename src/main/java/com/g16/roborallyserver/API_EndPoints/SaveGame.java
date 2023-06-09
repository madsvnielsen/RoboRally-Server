package com.g16.roborallyserver.API_EndPoints;


import com.g16.roborallyserver.sessionUtils.Connection;
import com.g16.roborallyserver.sessionUtils.GameSessionManager;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.controller.SaveLoadController;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.*;

@RequestMapping(value = "/storage", produces="application/json")
@RestController
public class SaveGame {

    private final String savesLocation ="src/main/java/dk/dtu/compute/se/pisd/roborally/Saves";

    @PostMapping(value = "/save/{saveName}")
    public ResponseEntity<String> saveGame(@PathVariable String saveName, @RequestParam String gameID,  @RequestBody String gameJson) {
        GameController gc = SaveLoadController.deserializeString(gameJson);
        for(Player savedP : gc.board.getPlayers()){
            Optional<Connection> matchingConnection = GameSessionManager.getPlayerConnections(gameID).stream().filter(
                    p->p.getPlayerRobot().getName().equals(savedP.getName())
            ).findFirst();
            if(matchingConnection.isPresent()){
                savedP.setCards(matchingConnection.get().getPlayerRobot().getCards());
                savedP.setProgram(matchingConnection.get().getPlayerRobot().getProgram());
            }
        }
        String finalJson = SaveLoadController.serializeGameController(gc);
        File savesDirectory = new File(savesLocation);
        List<File>  saveFiles =  Arrays.stream(savesDirectory.listFiles()).toList();
        /*if(saveFiles.stream().anyMatch(f -> f.getName().equals(saveName))){
            //Save game already exist!!!
            return new ResponseEntity<>("200", HttpStatus.OK);
        }*/
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(savesDirectory +"/" + saveName));
            writer.write(finalJson);
            writer.close();
            return new ResponseEntity<>("100", HttpStatus.OK);
        } catch (IOException ioe){
            return new ResponseEntity<>("500", HttpStatus.OK);
        }
    }

    @GetMapping(value = "/load/{saveName}")
    public ResponseEntity<String> loadGame(@PathVariable String saveName,@RequestParam String gameID, @RequestParam String uuid ) {

        File savesDirectory = new File(savesLocation);
        List<File>  saveFiles =  Arrays.stream(savesDirectory.listFiles()).toList();
        if(!saveFiles.stream().anyMatch(f -> f.getName().equals(saveName))){
            //Save game doesn't exist!!!
            return new ResponseEntity<>("200", HttpStatus.OK);
        }
        try {
            BufferedReader br = new BufferedReader(new FileReader(savesDirectory +"/" + saveName));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            String everything = sb.toString();
            br.close();

                if(!GameSessionManager.gameExists(gameID)){
                    return new ResponseEntity<>("Game doesn't exist!", HttpStatus.OK);
                }

                if(!GameSessionManager.isAuthenticatedAsHost(gameID, uuid)){
                    //You are not authenticated!
                    return new ResponseEntity<>("200", HttpStatus.OK);
                }

                if(GameSessionManager.playerCount(gameID) < 2){
                    //You need to be at least 2 players!
                    return new ResponseEntity<>("300", HttpStatus.OK);
                }
                GameController savedController = SaveLoadController.deserializeAndLoad(savesLocation + "/" + saveName);


            assert savedController != null;
            if(GameSessionManager.playerCount(gameID) != savedController.board.getPlayersNumber()){
                    //Invalid amount of players!!!!!!
                    return new ResponseEntity<>("600", HttpStatus.OK);
                }


                GameSessionManager.startGameSessionFromSave(gameID, savedController);

                //Starting game from save
                return new ResponseEntity<>("100", HttpStatus.OK);

        } catch (IOException ioe){
            return new ResponseEntity<>("500", HttpStatus.OK);
        }
    }
    @PostMapping(value = "/saveHand/{gameID}")
    public ResponseEntity<String> saveHand(@PathVariable String gameID,@RequestParam String uuid, @RequestBody String[][] requestBody)
    {
        if(!GameSessionManager.gameExists(gameID)){
            return new ResponseEntity<>("Game doesn't exist!", HttpStatus.OK);
        }

        if(!GameSessionManager.isAuthenticated(gameID, uuid)){
            //You are not authenticated!
            return new ResponseEntity<>("200", HttpStatus.OK);
        }

        Connection conn = GameSessionManager.getPlayerConnection(gameID, uuid);

        if(conn == null){
            //You are not authenticated in this game!
            return new ResponseEntity<>("200", HttpStatus.OK);
        }
        String[] cardHand = requestBody[0];
        String[] program = requestBody[1];
        conn.setCardHand(cardHand);
        conn.setSavedProgram(program);
        return  new ResponseEntity<>("100", HttpStatus.OK);
    }

    @GetMapping(value = "/")
    public ResponseEntity<?> getStorageList() {
        File mapDirectory = new File(savesLocation);
        File[] maps =  mapDirectory.listFiles();
        List<String> mapNames = new ArrayList<String>();
        if(maps ==  null){
            return new ResponseEntity<>("No saves", HttpStatus.OK);
        }
        for(File f : maps){
            mapNames.add(f.getName());
        }
        return new ResponseEntity<>(mapNames, HttpStatus.OK);

    }



}
