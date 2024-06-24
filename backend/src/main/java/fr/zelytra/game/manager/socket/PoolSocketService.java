package fr.zelytra.game.manager.socket;

import fr.zelytra.game.manager.message.MessageType;
import fr.zelytra.game.manager.message.SocketMessage;
import fr.zelytra.game.pool.*;
import fr.zelytra.user.UserEntity;
import fr.zelytra.user.UserService;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.websocket.Session;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@ApplicationScoped
public class PoolSocketService {

    @Inject
    UserService userService;

    private final ConcurrentMap<String, PoolParty> games = new ConcurrentHashMap<>();

    /**
     * Creates a new PoolParty for the given user.
     *
     * @param username      the username of the user creating the party
     * @param socketSession the WebSocket session of the user
     * @return the created PoolParty or null if the user is not found
     */
    public PoolParty createParty(String username, Session socketSession) {
        UserEntity userEntity = userService.getUserByName(username);

        if (userEntity == null) {
            Log.warn("[createParty][N/A] User not found: " + username);
            return null;
        }

        PoolPlayer user = new PoolPlayer(userEntity, socketSession);

        if (isPlayerInPool(username)) {
            Log.warn("[createParty][N/A] User already in pool: " + username);
            PoolParty poolParty = getPoolPartyByPlayer(username);
            removePlayerFromPool(user, poolParty.getUuid());
        }

        PoolParty poolParty = new PoolParty(user);
        games.put(poolParty.getUuid(), poolParty);
        Log.info("[createParty][" + poolParty.getUuid() + "] Created: " + username);
        return poolParty;
    }

    /**
     * Allows a user to join an existing pool or create a new one if no session ID is given.
     *
     * @param username      the username of the user attempting to join a pool
     * @param sessionID     the session ID of the pool the player wants to join
     * @param socketSession the WebSocket session of the user
     */
    public void joinPool(String username, String sessionID, Session socketSession) {
        Log.info("[joinPool][N/A] User: " + username);
        UserEntity user = userService.getUserByName(username);

        if (user == null) {
            Log.warn("[joinPool][N/A] User not found: " + username);
            return;
        }

        PoolPlayer poolPlayer = new PoolPlayer(user, socketSession);

        if (sessionID.isBlank() || sessionID.isEmpty()) {
            PoolParty poolParty = createParty(username, socketSession);
            SocketMessage<PoolParty> socketMessage = new SocketMessage<>(MessageType.UPDATE_POOL_DATA,poolParty);
            socketMessage.sendDataToPlayer(poolPlayer.getSocketSession());
            return;
        } else if (isPlayerInPool(username)) {
            Log.warn("[joinPool][N/A] User already in pool: " + username);
            PoolParty poolParty = getPoolPartyByPlayer(username);
            removePlayerFromPool(poolPlayer, poolParty.getUuid());
        } else if (!games.containsKey(sessionID)) {
            Log.warn("[joinPool][N/A] Pool not found: " + sessionID);
            return;
        }

        PoolParty poolParty = games.get(sessionID);
        poolParty.getPlayers().add(poolPlayer);
        Log.info("[joinPool][" + poolParty.getUuid() + "] Joined: " + username);
        broadcastPoolDataToParty(poolParty);
    }

    /**
     * Retrieves a player from the pool.
     *
     * @param username the username of the user to retrieve
     * @return the PoolPlayer if found, null otherwise
     */
    public PoolPlayer getPlayerFromPool(String username) {
        Log.info("[getPlayerFromPool][N/A] User: " + username);

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

        Log.warn("[getPlayerFromPool][N/A] User not found: " + username);
        return null;
    }

    /**
     * Checks if a player is already in a pool.
     *
     * @param username the username of the user to check
     * @return true if the user is in a pool, false otherwise
     */
    public boolean isPlayerInPool(String username) {
        Log.info("[isPlayerInPool][N/A] User: " + username);

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
     * @return the PoolParty if found, null otherwise
     */
    public PoolParty getPoolPartyByPlayer(String username) {
        Log.info("[getPoolPartyByPlayer][N/A] User: " + username);

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

        Log.warn("[getPoolPartyByPlayer][N/A] Pool not found for user: " + username);
        return null;
    }

    /**
     * Retrieves the pool party of a specific player by their socket session ID.
     *
     * @param socketSessionId the socket session ID of the player
     * @return the PoolPlayer if found, null otherwise
     */
    public PoolPlayer getPlayerBySocketSessionId(String socketSessionId) {
        Log.info("[getPlayerBySocketSessionId][N/A] User: " + socketSessionId);

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

        Log.warn("[getPlayerBySocketSessionId][N/A] Player not found for user: " + socketSessionId);
        return null;
    }

    /**
     * Removes a player from a pool party. If the pool party becomes empty, it deletes the pool party.
     *
     * @param user      the PoolPlayer to remove
     * @param sessionID the session ID of the pool party
     * @return true if the player was removed or the pool party was deleted, false otherwise
     */
    public boolean removePlayerFromPool(PoolPlayer user, String sessionID) {
        Log.info("[removePlayerFromPool][" + sessionID + "] User: " + user.getUsername());

        if (!games.containsKey(sessionID)) {
            Log.warn("[removePlayerFromPool][" + sessionID + "] Pool not found: " + sessionID);
            return false;
        }

        PoolParty poolParty = games.get(sessionID);
        PoolPlayer foundedPlayer = getPlayerFromPool(user.getAuthUsername());

        boolean removed = poolParty.removePlayer(foundedPlayer);
        broadcastPoolDataToParty(poolParty);
        if (removed) {
            Log.info("[removePlayerFromPool][" + sessionID + "] Removed user: " + user.getUsername());
            if (poolParty.getPlayers().isEmpty()) {
                games.remove(sessionID);
                Log.info("[removePlayerFromPool][" + sessionID + "] Pool deleted");
            }
            return true;
        } else {
            Log.warn("[removePlayerFromPool][" + sessionID + "] User not in pool: " + user.getUsername());
            return false;
        }
    }

    /**
     * Handles the event when a player closes their connection.
     *
     * @param socketSessionId the socket session ID of the player
     */
    public void playerClosedConnection(String socketSessionId) {
        Log.info("[playerClosedConnection][N/A] User: " + socketSessionId);
        PoolPlayer poolPlayer = getPlayerBySocketSessionId(socketSessionId);

        if (poolPlayer == null) {
            Log.warn("[playerClosedConnection][N/A] User not found: " + socketSessionId);
            return;
        }

        PoolParty poolParty = getPoolPartyByPlayer(poolPlayer.getUsername());

        if (poolParty == null) {
            Log.warn("[playerClosedConnection][N/A] Party not found: " + poolPlayer.getAuthUsername());
            return;
        }

        removePlayerFromPool(poolPlayer, poolParty.getUuid());
        Log.info("[playerClosedConnection][N/A] User " + poolPlayer.getAuthUsername() + " closed connection");
    }

    /**
     * Sets the game rules for a specific pool party.
     *
     * @param gameRules       the game rules to set
     * @param socketSessionId the socket session ID of the player setting the rules
     */
    public void setRule(GameRules gameRules, String socketSessionId) {
        PoolPlayer poolPlayer = getPlayerBySocketSessionId(socketSessionId);
        PoolParty poolParty = getPoolPartyByPlayer(poolPlayer.getUsername());
        poolParty.setRules(gameRules);
        broadcastPoolDataToParty(poolParty);
        Log.info("[setRule][" + poolParty.getUuid() + "] User: " + poolPlayer.getUsername() + " set rule: " + gameRules);
    }

    /**
     * Sets the game status for a specific pool party.
     *
     * @param gameStatus       the game rules to set
     * @param socketSessionId the socket session ID of the player setting the rules
     */
    public void setStatus(GameStatus gameStatus, String socketSessionId) {
        PoolPlayer poolPlayer = getPlayerBySocketSessionId(socketSessionId);
        PoolParty poolParty = getPoolPartyByPlayer(poolPlayer.getUsername());
        poolParty.setState(gameStatus);
        broadcastPoolDataToParty(poolParty);
        Log.info("[setStatus][" + poolParty.getUuid() + "] User: " + poolPlayer.getUsername() + " set status: " + gameStatus);
    }

    /**
     * Sets the teams for a specific pool party.
     *
     * @param team       the teams of the party
     * @param socketSessionId the socket session ID of the player setting the rules
     */
    public void setPlayersTeam(PoolTeam team, String socketSessionId) {
        PoolPlayer poolPlayer = getPlayerBySocketSessionId(socketSessionId);
        PoolParty poolParty = getPoolPartyByPlayer(poolPlayer.getUsername());
        poolParty.setTeams(team);
        broadcastPoolDataToParty(poolParty);
        Log.info("[setPlayersTeam][" + poolParty.getUuid() + "] User: " + poolPlayer.getUsername() + " set new teams !");
    }

    /**
     * Retrieves all active pool parties.
     *
     * @return a map of session IDs to PoolParty instances
     */
    public ConcurrentMap<String, PoolParty> getGames() {
        return games;
    }

    /**
     * Broadcasts the pool data to all players in the party.
     *
     * @param poolParty the pool party whose data is to be broadcast
     */
    public void broadcastPoolDataToParty(PoolParty poolParty) {
        if (poolParty == null) {
            Log.warn("[broadcastPoolDataToParty][N/A] Pool party is null");
            return;
        }

        for (PoolPlayer player : poolParty.getPlayers()) {
            SocketMessage<PoolParty> socketMessage = new SocketMessage<>(MessageType.UPDATE_POOL_DATA, poolParty);
            socketMessage.sendDataToPlayer(player.getSocketSession());
            Log.info("[broadcastPoolDataToParty][" + poolParty.getUuid() + "] Data sent to: " + player.getUsername());
        }

        // Ensure the game owner also receives the data
        SocketMessage<PoolParty> socketMessage = new SocketMessage<>(MessageType.UPDATE_POOL_DATA, poolParty);
        socketMessage.sendDataToPlayer(poolParty.getGameOwner().getSocketSession());
        Log.info("[broadcastPoolDataToParty][" + poolParty.getUuid() + "] Data sent to game owner: " + poolParty.getGameOwner().getUsername());
    }

}
