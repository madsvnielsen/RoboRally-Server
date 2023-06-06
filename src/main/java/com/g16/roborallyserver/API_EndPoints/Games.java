package com.g16.roborallyserver.API_EndPoints;

import com.g16.roborallyserver.sessionUtils.Connection;
import com.g16.roborallyserver.sessionUtils.ConnectionManager;
import com.g16.roborallyserver.sessionUtils.GameSession;
import com.g16.roborallyserver.sessionUtils.GameSessionManager;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<?> isStarted(@PathVariable String gameId, @RequestParam String uuid) {
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


        return new ResponseEntity<>(conn.gameSession.isStarted(), HttpStatus.OK);
    }


}
