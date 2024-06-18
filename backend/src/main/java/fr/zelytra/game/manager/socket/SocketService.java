package fr.zelytra.game.manager.socket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fr.zelytra.game.manager.message.MessageType;
import fr.zelytra.game.manager.message.SocketMessage;
import fr.zelytra.game.pool.PoolParty;
import fr.zelytra.game.pool.PoolPlayer;
import fr.zelytra.user.UserEntity;
import fr.zelytra.user.UserService;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.websocket.Session;

import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@ApplicationScoped
public class SocketService {

    @Inject
    UserService userService;

    private final ConcurrentMap<String, PoolParty> games = new ConcurrentHashMap<>();

    public PoolParty createParty(String username, Session socketSession) {
        UserEntity userEntity = userService.getUserByName(username);

        if (userEntity == null) {
            Log.warn("[createParty] User not found: " + username);
            return null;
        }

        PoolPlayer user = new PoolPlayer(userEntity, socketSession);

        if (isPlayerInPool(username)) {
            Log.warn("[createParty] User already in pool: " + username);
            PoolParty poolParty = getPoolPartyByPlayer(username);
            removePlayerFromPool(user, poolParty.getUuid());
        }

        PoolParty poolParty = new PoolParty(user);
        games.put(poolParty.getUuid(), poolParty);
        Log.info("[createParty] Created: " + username + " [" + poolParty.getUuid() + "]");
        return poolParty;
    }

    /**
     * Allows a user to join an existing pool or create new one if no session id gave
     *
     * @param username  the username of the user attempting to join a pool
     * @param sessionID the sessionId the player want to join
     */
    public void joinPool(String username, String sessionID, Session socketSession) {
        Log.info("[joinPool] User: " + username);
        UserEntity user = userService.getUserByName(username);

        if (user == null) {
            Log.warn("[joinPool] User not found: " + username);
            return;
        }

        PoolPlayer poolPlayer = new PoolPlayer(user, socketSession);

        if (sessionID.isBlank() || sessionID.isEmpty()) {
            PoolParty poolParty = createParty(username, socketSession);
            sendDataToPlayer(poolPlayer.getSocketSession(), MessageType.UPDATE_POOL_DATA,poolParty);
            return;
        } else if (isPlayerInPool(username)) {
            Log.warn("[joinPool] User already in pool: " + username);
            PoolParty poolParty = getPoolPartyByPlayer(username);
            removePlayerFromPool(poolPlayer, poolParty.getUuid());
        } else if (!games.containsKey(sessionID)) {
            Log.warn("[joinPool] Pool not found: " + sessionID);
            return;
        }

        PoolParty poolParty = games.get(sessionID);
        poolParty.getPlayers().add(poolPlayer);
        Log.info("[joinPool] Joined: " + username + " [" + poolParty.getUuid() + "]");
        sendDataToPlayer(poolPlayer.getSocketSession(), MessageType.UPDATE_POOL_DATA, poolParty);
    }

    /**
     * Retrieves a player from the pool.
     *
     * @param username the username of the user to retrieve
     * @return the user entity if found, null otherwise
     */
    public PoolPlayer getPlayerFromPool(String username) {
        Log.info("[getPlayerFromPool] User: " + username);

        for (PoolParty poolParty : games.values()) {
            if (poolParty.getGameOwner().getAuthUsername().equals(username)) {
                return poolParty.getGameOwner();
            }
            for (PoolPlayer player : poolParty.getPlayers()) {
                if (player.getUsername().equals(username)) {
                    return player;
                }
            }
        }

        Log.warn("[getPlayerFromPool] User not found: " + username);
        return null;
    }

    /**
     * Checks if a player is already in a pool.
     *
     * @param username the username of the user to check
     * @return true if the user is in a pool, false otherwise
     */
    public boolean isPlayerInPool(String username) {
        Log.info("[isPlayerInPool] User: " + username);

        for (PoolParty poolParty : games.values()) {
            if (poolParty.getGameOwner().getAuthUsername().equals(username)) {
                return true;
            }
            for (UserEntity player : poolParty.getPlayers()) {
                if (player.getUsername().equals(username)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Retrieves the pool party of a specific player.
     *
     * @param username the username of the player
     * @return the pool party if found, null otherwise
     */
    public PoolParty getPoolPartyByPlayer(String username) {
        Log.info("[getPoolPartyByPlayer] User: " + username);

        for (PoolParty poolParty : games.values()) {
            if (poolParty.getGameOwner().getAuthUsername().equals(username)) {
                return poolParty;
            }
            for (PoolPlayer player : poolParty.getPlayers()) {
                if (player.getUsername().equals(username)) {
                    return poolParty;
                }
            }
        }

        Log.warn("[getPoolPartyByPlayer] Pool not found for user: " + username);
        return null;
    }

    /**
     * Retrieves the pool party of a specific player.
     *
     * @param socketSessionId the socket id of the player
     * @return the pool party if found, null otherwise
     */
    public PoolPlayer getPlayerBySocketSessionId(String socketSessionId) {
        Log.info("[getPlayerBySocketSessionId] User: " + socketSessionId);

        for (PoolParty poolParty : games.values()) {
            if (poolParty.getGameOwner().getSocketSession().getId().equals(socketSessionId)) {
                return poolParty.getGameOwner();
            }
            for (PoolPlayer player : poolParty.getPlayers()) {
                if (player.getAuthUsername().equals(socketSessionId)) {
                    return player;
                }
            }
        }

        Log.warn("[getPlayerBySocketSessionId] Player not found for user: " + socketSessionId);
        return null;
    }

    /**
     * Removes a player from a pool party. If the pool party becomes empty, it deletes the pool party.
     *
     * @param user      the username of the player to remove
     * @param sessionID the session ID of the pool party
     * @return true if the player was removed or the pool party was deleted, false otherwise
     */
    public boolean removePlayerFromPool(PoolPlayer user, String sessionID) {
        Log.info("[removePlayerFromPool] User: " + user.getUsername() + " from Pool: " + sessionID);

        if (!games.containsKey(sessionID)) {
            Log.warn("[removePlayerFromPool] Pool not found: " + sessionID);
            return false;
        }

        PoolParty poolParty = games.get(sessionID);
        PoolPlayer foundedPlayer = getPlayerFromPool(user.getAuthUsername());

        boolean removed = poolParty.removePlayer(foundedPlayer);
        if (removed) {
            Log.info("[removePlayerFromPool] Removed user: " + user.getUsername() + " from Pool: " + sessionID);
            if (poolParty.getPlayers().isEmpty()) {
                games.remove(sessionID);
                Log.info("[removePlayerFromPool] Pool deleted: " + sessionID);
            }
            return true;
        } else {
            Log.warn("[removePlayerFromPool] User not in pool: " + user.getUsername() + " for Pool: " + sessionID);
            return false;
        }
    }

    public void playerClosedConnection(String socketSessionId) {
        Log.info("[playerClosedConnection] User: " + socketSessionId);
        PoolPlayer poolPlayer = getPlayerBySocketSessionId(socketSessionId);

        if (poolPlayer == null) {
            Log.warn("[playerClosedConnection] User not found: " + socketSessionId);
            return;
        }

        PoolParty poolParty = getPoolPartyByPlayer(poolPlayer.getUsername());

        if (poolParty == null) {
            Log.warn("[playerClosedConnection] Party not found: " + poolPlayer.getAuthUsername());
            return;
        }

        removePlayerFromPool(poolPlayer, poolParty.getUuid());
        Log.info("[playerClosedConnection] User " + poolPlayer.getAuthUsername() + " closed connection");
    }

    public ConcurrentMap<String, PoolParty> getGames() {
        return games;
    }

    /**
     * Sends a message to a player within a session identified by the WebSocket ID.
     * <p>
     * This method sends a specified data object to a player in a session identified by the WebSocket ID. The message type
     * and data to be broadcast are specified by the parameters. If the session or WebSocket does not exist, it logs an info
     * message and returns without sending any data. It constructs a {@link SocketMessage} with the messageType and data,
     * converts it into JSON format, and then sends this JSON string to the player using their WebSocket.
     * If any error occurs during the JSON conversion or sending, it logs an error or throws an {@link Error} respectively.
     *
     * @param <T>         The type of data to be sent. This allows the method to be used with various types of
     *                    data objects.
     * @param session     The WebSocket to which the data will be sent. This is used to identify the
     *                    player who should receive the message.
     * @param messageType The type of the message to be sent. This helps in identifying the purpose or action of
     *                    the message on the client side.
     * @param data        The data to be sent. This is the actual content of the message being sent to the player.
     *                    The type of this data is generic, allowing for flexibility in what can be sent.
     * @throws Error if there is an issue with converting the {@link SocketMessage} object to a JSON string.
     */
    public <T> void sendDataToPlayer(Session session, MessageType messageType, T data) {
        String json = formatMessage(messageType, data);

        if (session == null || session.getAsyncRemote() == null) {
            Log.error("Failed to get the player socket");
            return;
        }

        // Send the data to the specific WebSocket connection
        session.getAsyncRemote().sendText(json, result -> {
            if (result.isOK()){
                Log.info("[sendDataToPlayer] Type : " + messageType.name());
            }
            if (result.getException() != null) {
                Log.error("Unable to send message to [" + session.getId() + "]: " + result.getException());
            }
        });
    }

    private <T> String formatMessage(MessageType messageType, T data) {
        SocketMessage<T> message = new SocketMessage<>(messageType, data);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // To serialize as ISO-8601 strings
        objectMapper.setTimeZone(TimeZone.getTimeZone("UTC"));
        String json;
        try {
            json = objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            throw new Error(e);
        }
        return json;
    }


}
