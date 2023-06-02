package com.g16.roborallyserver.sessionUtils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GameSessionManager {

    static List<GameSession> gameSessions = new ArrayList<>();

    public static boolean gameExists(String gameID){
        return gameSessions.stream().anyMatch(gs -> gs.gameID.equals(gameID));
    }

    public static GameSession getGame(String gameID){
        Optional<GameSession> queryResponse = gameSessions.stream().filter(gs -> gs.gameID.equals(gameID)).findFirst();
        return queryResponse.orElse(null);

    }

    public List<GameSession> getGameSessions(){
        return gameSessions;
    }

    public static void addGameSession(GameSession gs){
        gameSessions.add(gs);
    }
}
