package com.g16.roborallyserver.sessionUtils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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

    public List<GameSession> getGameSessions(){
        return gameSessions;
    }

    public static void addGameSession(GameSession gs){
        gameSessions.add(gs);
    }

    public static int playerCount(String gameID){
        return (int) ConnectionManager.connectionList.stream().filter(player ->
                Objects.equals(player.gameSession.gameID, gameID)).count();
    }

    public static boolean isAuthenticatedAsHost(String gameID, String uuid){
        return ConnectionManager.connectionList.stream().anyMatch(conn ->
                Objects.equals(conn.gameSession.gameID, gameID) && conn.isHost() && conn.userID.equals(uuid));
    }


}
