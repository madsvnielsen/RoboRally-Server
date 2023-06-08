package com.g16.roborallyserver.API_EndPoints;


import com.g16.roborallyserver.sessionUtils.Connection;
import com.g16.roborallyserver.sessionUtils.GameSessionManager;
import dk.dtu.compute.se.pisd.roborally.controller.SaveLoadController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@RequestMapping(value = "/storage", produces="application/json")
@RestController
public class SaveGame {

    private final String savesLocation ="src/main/java/dk/dtu/compute/se/pisd/roborally/Saves";

    @PostMapping(value = "/save/{saveName}")
    public ResponseEntity<String> saveGame(@PathVariable String saveName, @RequestBody String gameJson) {

        File savesDirectory = new File(savesLocation);
        List<File>  saveFiles =  Arrays.stream(savesDirectory.listFiles()).toList();
        if(saveFiles.stream().anyMatch(f -> f.getName().equals(saveName))){
            //Save game already exist!!!
            return new ResponseEntity<>("200", HttpStatus.OK);
        }
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(savesDirectory +"/" + saveName));
            writer.write(gameJson);
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

                GameSessionManager.startGameSessionFromSave(gameID, Objects.requireNonNull(SaveLoadController.deserializeAndLoad(savesLocation + "/" + saveName)));

                //Starting game from save
                return new ResponseEntity<>("100", HttpStatus.OK);

        } catch (IOException ioe){
            return new ResponseEntity<>("500", HttpStatus.OK);
        }
    }
    @PostMapping(value = "/saveHand/{gameID}")
    public ResponseEntity<String> saveHand(@PathVariable String gameID,@RequestParam String uuid, @RequestBody String[] cardHand, @RequestBody String[] program)
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
        conn.setCardHand(cardHand);
        conn.setProgram(program);
        return  new ResponseEntity<>("100", HttpStatus.OK);
    }



}
