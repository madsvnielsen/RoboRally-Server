package com.g16.roborallyserver.sessionUtils;

import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.controller.SaveLoadController;
import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import dk.dtu.compute.se.pisd.roborally.RoboRally;
import dk.dtu.compute.se.pisd.roborally.model.*;


import java.util.*;

public class GameSessionManager {

    static List<GameSession> gameSessions = new ArrayList<>();
    final static List<String> PLAYER_COLORS = Arrays.asList("red", "green", "blue", "orange", "grey", "magenta");

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

    public static List<Connection> getPlayerConnections(String gameID){

        return ConnectionManager.connectionList.stream().filter(conn -> Objects.equals(conn.gameSession.gameID, gameID)).toList();
    }

    public static int playerCount(String gameID){
        return (int) ConnectionManager.connectionList.stream().filter(player ->
                Objects.equals(player.gameSession.gameID, gameID)).count();
    }

    public static boolean isAuthenticated(String gameID, String uuid){
        return ConnectionManager.connectionList.stream().anyMatch(conn ->
                Objects.equals(conn.gameSession.gameID, gameID) && conn.userID.equals(uuid));
    }

    public static boolean isAuthenticatedAsHost(String gameID, String uuid){
        return ConnectionManager.connectionList.stream().anyMatch(conn ->
                Objects.equals(conn.gameSession.gameID, gameID) && conn.isHost() && conn.userID.equals(uuid));
    }

    public static GameSession getGameSession(String gameId){
        Optional<GameSession> result = gameSessions.stream().filter(sesh -> Objects.equals(sesh.gameID, gameId)).findFirst();
        return result.orElse(null);
    }

    public static String getGameState(String gameId){
        return SaveLoadController.serializeAndGetString(getGame(gameId).getController());
    }

    public static void startGameSession(String gameId, String map){

        String mapPath = "src/main/java/dk/dtu/compute/se/pisd/roborally/Maps/"+map;
        GameSession targetSession = getGameSession(gameId);
        targetSession.setIsStarted(true);
        Board board = new Board(13,10, mapPath);
        targetSession.setController(new GameController(board));
        SpaceReader spaceReader = new SpaceReader(mapPath);


        int numberOfPlayers = playerCount(gameId);

        ProgrammingDeckInit programmingDeckInit = new ProgrammingDeckInit();
        List<Connection> playerConnections = getPlayerConnections(gameId);

        for (int i = 0; i < numberOfPlayers; i++) {
            Player player = new Player(board, PLAYER_COLORS.get(i),"Player " + (i + 1),i+1, programmingDeckInit.init());
            board.addPlayer(player);
            spaceReader.initPlayers(board,player, i);
            playerConnections.get(i).setPlayerToken(player);
        }

        targetSession.getController().startProgrammingPhase();

    }


}
