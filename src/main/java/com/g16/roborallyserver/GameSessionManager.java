package com.g16.roborallyserver;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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

    public static void addGameSession(GameSession gs){
        gameSessions.add(gs);
    }
}
