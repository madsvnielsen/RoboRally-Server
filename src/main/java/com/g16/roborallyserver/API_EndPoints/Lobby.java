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

/** Lobby
 * Controller for managing all endpoints related to getting information about a lobby.
 * A lobby represents an instance of a game.
 */

@RequestMapping(value = "/lobby", produces="application/json")
@RestController
public class Lobby {

    /** Get lobbies
     * json object of a dictionary of lobbies, where the key is the lobby name
     * and the value is the player count
     * @return json object containing a dictionary of lobbies
     */
    @GetMapping(value = "")
    public ResponseEntity<?> lobbies() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        List<GameSession> gameSessions = GameSessionManager.getGameSessions();
        Map<String, Integer> lobbyPlayerCounts = new HashMap<>();

        for (GameSession session : gameSessions) {
            int playerCount = GameSessionManager.playerCount(session.getGameID());
            lobbyPlayerCounts.put(session.getGameID(), playerCount);
        }

        String jsonResult = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(lobbyPlayerCounts);

        return new ResponseEntity<>(jsonResult, HttpStatus.OK);
    }


}
