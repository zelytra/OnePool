package fr.zelytra.game.manager.socket;

import fr.zelytra.game.pool.PoolParty;
import fr.zelytra.user.UserEntity;
import fr.zelytra.user.UserService;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@ApplicationScoped
public class SocketService {

    @Inject
    UserService userService;

    private final ConcurrentMap<String, PoolParty> games = new ConcurrentHashMap<>();

    public PoolParty createParty(String username) {
        UserEntity user = userService.getUserByName(username);

        if (user == null) {
            Log.warn("[createParty] User not found: " + username);
            return null;
        }

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
    public void joinPool(String username, String sessionID) {
        Log.info("[joinPool] User: " + username);
        UserEntity user = userService.getUserByName(username);

        if (user == null) {
            Log.warn("[joinPool] User not found: " + username);
            return;
        }

        if (sessionID.isBlank() || sessionID.isEmpty()) {
            createParty(username);
            return;
        } else if (isPlayerInPool(username)) {
            Log.warn("[joinPool] User already in pool: " + username);
            PoolParty poolParty = getPoolPartyByPlayer(username);
            removePlayerFromPool(user, poolParty.getUuid());
        } else if (!games.containsKey(sessionID)) {
            Log.warn("[joinPool] Pool not found: " + sessionID);
            return;
        }

        PoolParty poolParty = games.get(sessionID);
        poolParty.getPlayers().add(user);
        Log.info("[joinPool] Joined: " + username + " [" + poolParty.getUuid() + "]");
    }

    /**
     * Retrieves a player from the pool.
     *
     * @param username the username of the user to retrieve
     * @return the user entity if found, null otherwise
     */
    public UserEntity getPlayerFromPool(String username) {
        Log.info("[getPlayerFromPool] User: " + username);

        for (PoolParty poolParty : games.values()) {
            if (poolParty.getGameOwner().getAuthUsername().equals(username)) {
                return poolParty.getGameOwner();
            }
            for (UserEntity player : poolParty.getPlayers()) {
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
            for (UserEntity player : poolParty.getPlayers()) {
                if (player.getUsername().equals(username)) {
                    return poolParty;
                }
            }
        }

        Log.warn("[getPoolPartyByPlayer] Pool not found for user: " + username);
        return null;
    }

    /**
     * Removes a player from a pool party. If the pool party becomes empty, it deletes the pool party.
     *
     * @param user      the username of the player to remove
     * @param sessionID the session ID of the pool party
     * @return true if the player was removed or the pool party was deleted, false otherwise
     */
    public boolean removePlayerFromPool(UserEntity user, String sessionID) {
        Log.info("[removePlayerFromPool] User: " + user.getUsername() + " from Pool: " + sessionID);

        if (!games.containsKey(sessionID)) {
            Log.warn("[removePlayerFromPool] Pool not found: " + sessionID);
            return false;
        }

        PoolParty poolParty = games.get(sessionID);

        boolean removed = poolParty.removePlayer(user);
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

    public ConcurrentMap<String, PoolParty> getGames() {
        return games;
    }
}
