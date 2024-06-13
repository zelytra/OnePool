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
            Log.warn("[SocketService.createParty] User not found: " + username);
            return null;
        }

        PoolParty poolParty = new PoolParty(user);
        games.put(username, poolParty);
        Log.info("Creating party: " + username + " [" + poolParty.getUuid() + "]");
        return poolParty;
    }

    /**
     * Allows a user to join an existing pool or create new one if no session id gave
     *
     * @param username  the username of the user attempting to join a pool
     * @param sessionID the sessionId the player want to join
     */
    public void joinPool(String username, String sessionID) {
        Log.info("User joining pool: " + username);
        UserEntity user = userService.getUserByName(username);

        if (user == null) {
            Log.warn("User not found: " + username);
            return;
        }
        if (sessionID.isBlank() || sessionID.isEmpty()) {
            createParty(username);
        } else if (!games.containsKey(sessionID)) {
            Log.warn("Pool not found: " + username);
        } else {
            PoolParty poolParty = games.get(sessionID);
            poolParty.getPlayers().add(user);
            Log.info("User " + username + " joined pool: " + poolParty.getUuid());
        }
    }

    /**
     * Retrieves a player from the pool.
     *
     * @param username the username of the user to retrieve
     * @return the user entity if found, null otherwise
     */
    public UserEntity getPlayerFromPool(String username) {
        Log.info("Retrieving player from pool: " + username);

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

        Log.warn("User not found in any pool: " + username);
        return null;
    }

    /**
     * Checks if a player is already in a pool.
     *
     * @param username the username of the user to check
     * @return true if the user is in a pool, false otherwise
     */
    public boolean isPlayerInPool(String username) {
        Log.info("Checking if player is in pool: " + username);

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

    public ConcurrentMap<String, PoolParty> getGames() {
        return games;
    }
}
