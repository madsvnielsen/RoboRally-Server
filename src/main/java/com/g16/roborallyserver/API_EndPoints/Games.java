package com.g16.roborallyserver.API_EndPoints;

import com.g16.roborallyserver.sessionUtils.*;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.model.Command;
import dk.dtu.compute.se.pisd.roborally.model.CommandCard;
import dk.dtu.compute.se.pisd.roborally.model.CommandCardField;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


@RequestMapping(value = "/game", produces="application/json")
@RestController
public class Games {

    @Autowired
    private HttpServletRequest request;

    @GetMapping(value = "/{gameId}")
    public String getTestData(@PathVariable int gameId) {
        return "You wrote " + gameId;
    }

    @GetMapping(value = "/join/{gameId}")
    public ResponseEntity<?> joinGame(@PathVariable String gameId) {

        if(!GameSessionManager.gameExists(gameId)){
            return new ResponseEntity<>("Game does not exist!", HttpStatus.OK);
        }

        String userId = UUID.randomUUID().toString();

        GameSession gs = GameSessionManager.getGame(gameId);
        Connection conn = new Connection(userId, gs);

        ConnectionManager.addConnection(conn);

        return new ResponseEntity<Connection>(conn, HttpStatus.OK);
    }



    @GetMapping(value = "/host/{gameId}")
    public ResponseEntity<?> hostGame(@PathVariable String gameId) {
        if(GameSessionManager.gameExists(gameId)){
            return new ResponseEntity<>("Game already exists!", HttpStatus.OK);
        }
        GameSession gs = new GameSession(gameId);
        GameSessionManager.addGameSession(gs);
        String userId = UUID.randomUUID().toString();
        Connection conn = new Connection(userId, gs);
        conn.setHost(true);
        ConnectionManager.addConnection(conn);

        return new ResponseEntity<>(conn, HttpStatus.OK);
    }

    @GetMapping(value = "/playerCount/{gameId}")
    public ResponseEntity<?> playerCount(@PathVariable String gameId) {
        if(!GameSessionManager.gameExists(gameId)){
            return new ResponseEntity<>("Game doesn't exist!", HttpStatus.OK);
        }
        return new ResponseEntity<>(GameSessionManager.playerCount(gameId), HttpStatus.OK);
    }

    @GetMapping(value = "/start/{gameId}")
    public ResponseEntity<String> startGame(@PathVariable String gameId, @RequestParam String mapName, @RequestParam String uuid) {
        if(!GameSessionManager.gameExists(gameId)){
            return new ResponseEntity<>("Game doesn't exist!", HttpStatus.OK);
        }

        if(!GameSessionManager.isAuthenticatedAsHost(gameId, uuid)){
            //You are not authenticated!
            return new ResponseEntity<>("200", HttpStatus.OK);
        }

        if(GameSessionManager.playerCount(gameId) < 2){
            //You need to be at least 2 players!
            return new ResponseEntity<>("300", HttpStatus.OK);
        }

        GameSessionManager.startGameSession(gameId, mapName);



        //Starting game with map
        return new ResponseEntity<>("100", HttpStatus.OK);
    }

    @GetMapping(value = "/update/{gameId}")
    public ResponseEntity<String> gameUpdate(@PathVariable String gameId, @RequestParam String uuid) {
        if(!GameSessionManager.gameExists(gameId)){
            return new ResponseEntity<>("Game doesn't exist!", HttpStatus.OK);
        }

        if(!GameSessionManager.isAuthenticated(gameId, uuid)){
            //You are not authenticated!
            return new ResponseEntity<>("200", HttpStatus.OK);
        }

        String jsonSerialized = GameSessionManager.getGameState(gameId);

        //Starting game with map
        return new ResponseEntity<>(jsonSerialized, HttpStatus.OK);
    }

    @GetMapping(value = "/playertoken/{gameId}")
    public ResponseEntity<String> playerToken(@PathVariable String gameId, @RequestParam String uuid) {
        if(!GameSessionManager.gameExists(gameId)){
            return new ResponseEntity<>("Game doesn't exist!", HttpStatus.OK);
        }

        if(!GameSessionManager.isAuthenticated(gameId, uuid)){
            //You are not authenticated!
            return new ResponseEntity<>("200", HttpStatus.OK);
        }

        Connection conn = GameSessionManager.getPlayerConnection(gameId, uuid);

        if(conn == null){
            //You are not authenticated in this game!
            return new ResponseEntity<>("200", HttpStatus.OK);
        }
        //Get player token

        return new ResponseEntity<>(conn.getPlayerRobot().getName(), HttpStatus.OK);
    }

    @GetMapping(value = "/isstarted/{gameId}")
    public ResponseEntity<?> isStarted(@PathVariable String gameId) {
        if(!GameSessionManager.gameExists(gameId)){
            return new ResponseEntity<>("Game doesn't exist!", HttpStatus.OK);
        }


        GameSession session = GameSessionManager.getGameSession(gameId);



        return new ResponseEntity<>(session.isStarted(), HttpStatus.OK);
    }

    @PostMapping(value = "/program/{gameId}")
    public ResponseEntity<?> submitProgram(@PathVariable String gameId, @RequestParam String uuid,  @RequestBody String[] program) {
        if(!GameSessionManager.gameExists(gameId)){
            return new ResponseEntity<>("Game doesn't exist!", HttpStatus.OK);
        }

        if(!GameSessionManager.isAuthenticated(gameId, uuid)){
            //You are not authenticated!
            return new ResponseEntity<>("200", HttpStatus.OK);
        }

        Connection conn = GameSessionManager.getPlayerConnection(gameId, uuid);

        if(conn == null){
            //You are not authenticated in this game!
            return new ResponseEntity<>("200", HttpStatus.OK);
        }

        conn.setDoneProgramming(true);
        for (String str: program) {
            System.out.println(str);
        }
        conn.setProgram(program);

        return new ResponseEntity<>(conn.gameSession.isStarted(), HttpStatus.OK);
    }

    @PostMapping(value = "/interactive/{gameId}")
    public ResponseEntity<?> submitInteractive(@PathVariable String gameId, @RequestParam String uuid,  @RequestBody String comm, @RequestParam String step) {
        if(!GameSessionManager.gameExists(gameId)){
            return new ResponseEntity<>("Game doesn't exist!", HttpStatus.OK);
        }

        if(!GameSessionManager.isAuthenticated(gameId, uuid)){
            //You are not authenticated!
            return new ResponseEntity<>("200", HttpStatus.OK);
        }

        Connection conn = GameSessionManager.getPlayerConnection(gameId, uuid);

        if(conn == null){
            //You are not authenticated in this game!
            return new ResponseEntity<>("200", HttpStatus.OK);
        }

        String[] info = comm.split(":");
        boolean done;
        done = !info[2].equals("notDone");
        Interactive inter = new Interactive(info[0], info[1], done, info[3]);

        conn.gameSession.appendIteractive(inter);

        return new ResponseEntity<>("100", HttpStatus.OK);
    }

    @GetMapping(value = "/interactive/{gameId}")
    public ResponseEntity<?> getInteractive(@PathVariable String gameId, @RequestParam String uuid, @RequestParam String step) {
        if(!GameSessionManager.gameExists(gameId)){
            return new ResponseEntity<>("Game doesn't exist!", HttpStatus.OK);
        }

        if(!GameSessionManager.isAuthenticated(gameId, uuid)){
            //You are not authenticated!
            return new ResponseEntity<>("200", HttpStatus.OK);
        }

        Connection conn = GameSessionManager.getPlayerConnection(gameId, uuid);

        if(conn == null){
            //You are not authenticated in this game!
            return new ResponseEntity<>("200", HttpStatus.OK);
        }

        Interactive inter = null;

        for (int i = 0; i < conn.gameSession.Interactives.size(); i++){
            if (conn.gameSession.getInteractives().get(i).getStep().equals(step)){
                inter = conn.gameSession.getInteractives().get(i);
            }
        }

        Interactive inter2 = new Interactive(uuid, step, false, "test");

        if (inter == null){
            return new ResponseEntity<Interactive>(inter2, HttpStatus.OK);
        } else {
            return new ResponseEntity<Interactive>(inter, HttpStatus.OK);
        }
    }

    @GetMapping (value= "/getCards/{gameId}")
    public ResponseEntity<?> getProgramedCards(@PathVariable String gameId, @RequestParam String uuid) {
        if (!GameSessionManager.gameExists(gameId)) {
            return new ResponseEntity<>("Game doesn't exist!", HttpStatus.OK);
        }
        if (!GameSessionManager.isAuthenticated(gameId, uuid)) {
            //You are not authenticated!
            return new ResponseEntity<>("200", HttpStatus.OK);
        }
        Connection conn = GameSessionManager.getPlayerConnection(gameId, uuid);


        if (conn == null) {
            //You are not authenticated in this game!
            return new ResponseEntity<>("200", HttpStatus.OK);
        }
        conn.setHasExecutedCards(true);
        List<Connection> players = GameSessionManager.getPlayerConnections(gameId);
        Connection connection;
        List<String> cards = new ArrayList<>();
        if(players.size() != players.stream().filter(Connection::isDoneProgramming).count()){
            return new ResponseEntity<>(new String[]{"500"}, HttpStatus.OK);
        }
        //int increment = 0;
        for (Connection player : players) {
            connection = player;
            /*
            if (connection.isDoneProgramming()) {
                increment++;
            }
             */
            for (int i = 0; i < 5; i++) {
                if (connection.getProgram() != null)
                    cards.add(connection.getProgram()[i]);
                else
                    cards.add("null");
            }
        }
        if(players.stream().filter(Connection::isHasExecutedCards).count() == players.size()){
            players.forEach(p -> {
                p.setHasExecutedCards(false);
                p.setDoneProgramming(false);

            });
        }
        conn.gameSession.clearAll();

        return new ResponseEntity<>(cards, HttpStatus.OK);
    }
}
