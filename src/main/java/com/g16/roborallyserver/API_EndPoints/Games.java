package com.g16.roborallyserver.API_EndPoints;
import com.g16.roborallyserver.sessionUtils.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


/**
 * Games
 *Is a controller that manages all endpoints related to
 * getting or sending information to or from game sessions.
 */


@RequestMapping(value = "/game", produces="application/json")
@RestController
public class Games {

    /**
     * Join game
     * Creates a connection, assigns the connection an UUID and
     * assigns the connection to a game session given by the gameID path variable.
     * @param gameId id of the game to be joined
     * @return ResponseEntity with error message or the uuid associated with the connection
     */
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


    /**
     * Creates a new game session with the given gameID and a new connection and
     * assigns the connection to the created game session.
     * @param gameId id of the game to be joined
     * @return ResponseEntity with error message or the uuid associated with the connection
     */
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

    /**
     * Get Player count
     * Returns the amount of connections associated with a game session
     * @param gameId game session id
     * @return error message or amount of connections associated with a game session
     */
    @GetMapping(value = "/playerCount/{gameId}")
    public ResponseEntity<?> playerCount(@PathVariable String gameId) {
        if(!GameSessionManager.gameExists(gameId)){
            return new ResponseEntity<>("Game doesn't exist!", HttpStatus.OK);
        }
        return new ResponseEntity<>(GameSessionManager.playerCount(gameId), HttpStatus.OK);
    }

    /** Start game session
     * Starts game session given by the parameter gameId and sets the map.
     * The UUID is required to authenticate the host.
     * @param gameId existing game session id
     * @param mapName map name to be loaded
     * @param uuid uuid of caller, to check if user is hosting
     * @return status message
     */
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

    /** Get game state
     * Returns the state of the game after initialization. Can be used by clients to initialize local boards.
     * @param gameId game session id
     * @param uuid user id
     * @return json string of serialized gamecontroller
     */
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

    /**Get player token
     * Gives the name of the robot that a player i associated with
     * @param gameId game session id
     * @param uuid user id
     * @return status code or name of robot
     */
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

    /** Is game stared
     * Returns whether the game is started or not
     * @param gameId game session id
     * @return boolean if game is started or error message
     */
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
        int i = 0;
        int playerNum = conn.getPlayerRobot().getPlayerNum();
        for (String str: program) {
            System.out.println(str);
           if (str.equals("Turn left or right")){
               conn.gameSession.appendInteractives(new Interactive(playerNum, String.valueOf(i), false, "test"));
           }
        }
        conn.setProgram(program);

        return new ResponseEntity<>(conn.gameSession.isStarted(), HttpStatus.OK);
    }

    /** Submit the chosen command
     * When executing an interactive card, the client must submit the command, so it can be relayed to other players.
     * @param gameId game session id
     * @param uuid user id
     * @param comm command choice as string
     * @return status code
     */
    @PostMapping(value = "/interactive/{gameId}")
    public ResponseEntity<?> submitInteractive(@PathVariable String gameId, @RequestParam String uuid,  @RequestBody String comm) {
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
        Interactive inter = new Interactive(Integer.parseInt(info[0]), info[1], done, info[3]);

        conn.gameSession.appendInteractives(inter);

        return new ResponseEntity<>("100", HttpStatus.OK);
    }

    /** Get list of (to be) submitted interactions
     * Gets a list of (to be) submitted interactions during the current round
     * @param gameId game session id
     * @param uuid user id
     * @return json serialized Interactive object or status message
     */
    @GetMapping(value = "/interactive/{gameId}")
    public ResponseEntity<?> getInteractive(@PathVariable String gameId, @RequestParam String uuid) {
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

        return new ResponseEntity<>(conn.gameSession.getInteractives(), HttpStatus.OK);
    }

    /** Get programmed cards this round
     * Returns list of strings of chosen commands by other players if all players are done programming.
     * @param gameId id of game session
     * @param uuid id of user
     * @return List of strings of chosen commands or status message
     */
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
            //Not all players have submitted their programs!!
            return new ResponseEntity<>(new String[]{"500"}, HttpStatus.OK);
        }
        for (Connection player : players) {
            connection = player;
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
            conn.gameSession.clearAll();
        }

        return new ResponseEntity<>(cards, HttpStatus.OK);
    }
}
