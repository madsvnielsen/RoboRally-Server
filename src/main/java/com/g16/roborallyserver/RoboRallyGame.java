package com.g16.roborallyserver;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

import javax.sql.DataSource;
import java.util.UUID;


@RequestMapping(value = "/game", produces="application/json")
@RestController
public class RoboRallyGame {

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

        return new ResponseEntity<>("{\"userID\":\"" + userId + "\"}", HttpStatus.OK);
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
        ConnectionManager.addConnection(conn);

        return new ResponseEntity<>(conn, HttpStatus.OK);
    }


}
