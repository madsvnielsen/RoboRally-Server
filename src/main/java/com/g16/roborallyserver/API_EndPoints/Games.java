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
            return new ResponseEntity<>("You are not authenticated! OMG", HttpStatus.OK);
        }

        if(GameSessionManager.playerCount(gameId) < 2){
            return new ResponseEntity<>("You need to be at least 2 players!", HttpStatus.OK);
        }

        GameSessionManager.startGameSession(gameId, mapName);

        return new ResponseEntity<>("Starting game with map " + mapName, HttpStatus.OK);
    }




}
