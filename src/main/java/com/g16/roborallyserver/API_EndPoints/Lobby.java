package com.g16.roborallyserver.API_EndPoints;

import com.g16.roborallyserver.sessionUtils.GameSession;
import com.g16.roborallyserver.sessionUtils.GameSessionManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RequestMapping(value = "/lobby", produces="application/json")
@RestController
public class Lobby {
    GameSessionManager gsm = new GameSessionManager();
    @GetMapping(value ="")
    public ResponseEntity<?> lobbies(){
        return new ResponseEntity<>(gsm.getGameSessions(), HttpStatus.OK);
    }

    @GetMapping(value = "/{gameID}")
    public GameSession lobby(String gameID){
        Optional<GameSession> queryResponse = gsm.getGameSessions().stream().filter(gs -> gs.gameID.equals(gameID)).findFirst();
        return queryResponse.orElse(null);
    }
}
