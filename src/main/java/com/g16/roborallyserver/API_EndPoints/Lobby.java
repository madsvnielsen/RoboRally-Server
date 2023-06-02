package com.g16.roborallyserver.API_EndPoints;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.g16.roborallyserver.sessionUtils.GameSession;
import com.g16.roborallyserver.sessionUtils.GameSessionManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequestMapping(value = "/lobby", produces="application/json")
@RestController
public class Lobby {
    GameSessionManager gsm = new GameSessionManager();


    @GetMapping(value = "")
    public ResponseEntity<?> lobbies() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        List<GameSession> gameSessions = gsm.getGameSessions();
        Map<String, Integer> lobbyPlayerCounts = new HashMap<>();

        for (GameSession session : gameSessions) {
            int playerCount = gsm.playerCount(session.getGameID());
            lobbyPlayerCounts.put(session.getGameID(), playerCount);
        }

        String jsonResult = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(lobbyPlayerCounts);

        return new ResponseEntity<>(jsonResult, HttpStatus.OK);
    }



    @GetMapping(value = "/{gameID}")
    public GameSession lobby(String gameID){
        Optional<GameSession> queryResponse = gsm.getGameSessions().stream().filter(gs -> gs.gameID.equals(gameID)).findFirst();
        return queryResponse.orElse(null);
    }
}
